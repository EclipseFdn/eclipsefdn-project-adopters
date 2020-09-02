/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.service.impl;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Adopter;
import org.eclipsefoundation.adopters.model.AdopterList;
import org.eclipsefoundation.adopters.model.Project;
import org.eclipsefoundation.adopters.service.AdopterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.quarkus.runtime.Startup;

/**
 * Retrieves the adopters information from the filesystem on service start, and
 * provides copies to requesting callers.
 * 
 * @author Martin Lowe
 *
 */
@Startup
@ApplicationScoped
public class DefaultAdopterService implements AdopterService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAdopterService.class);

	@ConfigProperty(name = "eclipse.adopters.dirpath", defaultValue = "/tmp/quarkus")
	String adoptersLocation;
	@ConfigProperty(name = "eclipse.adopters.filename", defaultValue = "adopters.json")
	String adoptersName;
	@ConfigProperty(name = "eclipse.adopters.retry.count", defaultValue = "3")
	int retryCount;
	@ConfigProperty(name = "eclipse.adopters.retry.timeoutInMS", defaultValue = "1000")
	long retryTimeout;

	private List<Adopter> adopters;
	private WatchService watcher;

	private ReentrantLock lock;
	private boolean running;

	@PostConstruct
	public void init() throws IOException {
		this.lock = new ReentrantLock();
		this.adopters = new ArrayList<>();
		Path adoptersDir = Paths.get(adoptersLocation);
		Path adoptersFile = Paths.get(adoptersLocation, adoptersName);
		if (Files.exists(adoptersDir)) {
			// read in the initial file if it exists
			if (Files.exists(adoptersFile)) {
				LOGGER.debug("Found an adopters file at path {}, reading in", adoptersFile);
				readInFile(adoptersFile);
			}

			watcher = FileSystems.getDefault().newWatchService();
			// register the adopter location to watch
			LOGGER.debug("Registering file watcher on directory {}", adoptersDir);
			adoptersDir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			// start file watching on a new thread
			new Thread(this::update).start();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@PreDestroy
	public void shutdown() {
		this.running = false;
	}

	/**
	 * Update loop, will loop until shutdown is triggered or the service is stopped.
	 * 
	 * @throws IOException
	 */
	private void update() {
		// soft lock this to a single thread
		if (!this.lock.isLocked()) {
			this.lock.lock();
			try {
				// set the state to running at the start
				this.running = true;
				// watch for running state to change for gentle shutdowns
				while (this.running) {
					WatchKey key = null;
					try {
						key = watcher.take();
						// if there was an error while watching for changes, try again
						if (key == null) {
							continue;
						}

						// pass a filtered list of events to be processed
						@SuppressWarnings("unchecked")
						List<WatchEvent<Path>> events = key.pollEvents().stream()
								.filter(e -> e.kind() != OVERFLOW && e.kind().type() == Path.class)
								.map(e -> (WatchEvent<Path>) e)
								.filter(e -> ((Path) e.context()).getFileName().toString().equals(adoptersName))
								.collect(Collectors.toList());
						processEvents(events);
					} catch (InterruptedException e) {
						// represents shutdown state, so end processing
						Thread.currentThread().interrupt();
					} finally {
						// return the key to the watch service
						if (key != null && key.isValid()) {
							key.reset();
						}
					}
				}
			} finally {
				// release the lock once leaving the current block
				this.lock.unlock();
			}
		}
	}

	private void processEvents(List<WatchEvent<Path>> events) {
		// process each of the queued events
		for (WatchEvent<Path> event : events) {
			// clear the adopter list if the event is a deletion event
			if (event.kind() == ENTRY_DELETE) {
				LOGGER.debug("Detected the deletion of adopters file, emptying array");
				synchronized (this) {
					this.adopters = new ArrayList<>();
				}
			} else {
				// indicates an update or created file on the given
				readInFile(Paths.get(adoptersLocation, event.context().getFileName().toString()));
			}
		}
	}

	private void readInFile(Path adoptersPath) {
		LOGGER.debug("Detected an update for adopters file, reading in {}", adoptersPath);
		Gson gson = new Gson();
		// get a Gson processor, and read in the file
		if (Files.exists(adoptersPath)) {
			try (FileReader fileReader = new FileReader(adoptersPath.toFile());
					BufferedReader sr = new BufferedReader(fileReader)) {
				// read the adopters list from the JSON data
				AdopterList al = gson.fromJson(sr, AdopterList.class);
				synchronized (this) {
					this.adopters = al.getAdopters();
				}
			} catch (IOException e) {
				LOGGER.warn("Error reading file at path: {}\n", adoptersPath, e);
			}
		} else {
			LOGGER.error("Bad file path was passed for adopters update, not reading in file: {}", adoptersPath);
		}
	}

	@Override
	public List<Adopter> getAdopters() {
		synchronized (this) {
			return new ArrayList<>(adopters);
		}
	}

	@Override
	public List<AdoptedProject> getAdoptedProjects(List<Project> projects) {
		return projects.stream().map(this::getAdoptedProject).filter(a -> !a.getAdopters().isEmpty())
				.collect(Collectors.toList());
	}

	private AdoptedProject getAdoptedProject(Project p) {
		AdoptedProject ap = new AdoptedProject();
		ap.setProjectId(p.getProjectId());
		ap.setName(p.getName());
		ap.setLogo(p.getLogo());
		ap.setUrl(p.getUrl());
		ap.setAdopters(getAdopters().stream().filter(a -> a.getProjects().contains(p.getProjectId()))
				.collect(Collectors.toList()));
		return ap;
	}

}
