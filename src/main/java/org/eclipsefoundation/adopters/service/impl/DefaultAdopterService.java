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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Adopter;
import org.eclipsefoundation.adopters.model.AdopterList;
import org.eclipsefoundation.adopters.model.Project;
import org.eclipsefoundation.adopters.service.AdopterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@ConfigProperty(name = "eclipse.adopters.path.json", defaultValue = "/tmp/quarkus/adopters.json")
	Path adoptersLocation;
	@ConfigProperty(name = "eclipse.adopters.retry.count", defaultValue = "3")
	int retryCount;
	@ConfigProperty(name = "eclipse.adopters.retry.timeoutInMS", defaultValue = "1000")
	long retryTimeout;

	@Inject
	Jsonb json;

	/**
	 * All updates to the adopters list should be done through the setAdopters
	 * method to ensure thread safety
	 */
	private List<Adopter> adopters;
	private WatchService watcher;
	private Thread watchThread;

	@PostConstruct
	public void init() throws IOException {
		setAdopters(Collections.emptyList());
		// attempt to ensure that the base directory exists for watching
		Path parentDir = adoptersLocation.getParent();
		if (!Files.exists(parentDir)) {
			Files.createDirectory(parentDir);
		}
		// read in the initial file if it exists
		if (Files.exists(adoptersLocation)) {
			LOGGER.debug("Found an adopters file at path {}, reading in", adoptersLocation);
			List<Adopter> initialAdopters = readInAdopters(adoptersLocation, json);
			if (initialAdopters != null) {
				setAdopters(initialAdopters);
			}
		}

		// create and set the watch service
		this.watcher = FileSystems.getDefault().newWatchService();
		// register the adopter location to watch
		LOGGER.debug("Registering file watcher on directory {}", parentDir);
		parentDir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

		// start file watching on a new thread
		this.watchThread = new Thread(this::update);
		this.watchThread.start();
	}

	@PreDestroy
	public void shutdown() {
		// stop the watch thread from running
		this.watchThread.interrupt();
	}

	/**
	 * Update loop, will loop until shutdown is triggered or the service is stopped.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void update() {
		while (true) {
			WatchKey key = null;
			try {
				key = watcher.take();
				// if there was an error while watching for changes, try again
				if (key == null) {
					continue;
				}

				// pass a filtered list of events to be processed
				key.pollEvents().stream().filter(e -> e.kind() != OVERFLOW && e.kind().type() == Path.class)
						.map(e -> (WatchEvent<Path>) e)
						.filter(e -> ((Path) e.context()).getFileName().equals(adoptersLocation.getFileName()))
						.forEach(this::processEvent);
			} catch (@SuppressWarnings("java:S2142") InterruptedException e) {
				LOGGER.debug("Ending watch services, as thread was interrupted and closing");
				try {
					this.watcher.close();
				} catch (IOException e1) {
					LOGGER.error("Error while closing the watch service", e1);
				}
				break;
			} finally {
				// return the key to the watch service
				if (key != null && key.isValid()) {
					key.reset();
				}
			}
		}

	}

	private void processEvent(WatchEvent<Path> event) {
		// clear the adopter list if the event is a deletion event
		if (event.kind() == ENTRY_DELETE) {
			LOGGER.debug("Detected the deletion of adopters file, emptying array");
			setAdopters(Collections.emptyList());
		} else {
			// indicates an update or created file on the given
			// retrieve the new adopters and set them if the read operation was successful
			List<Adopter> newAdopters = readInAdopters(adoptersLocation, json);
			if (newAdopters != null) {
				setAdopters(newAdopters);
			}
		}

	}

	private static List<Adopter> readInAdopters(Path adoptersPath, Jsonb json) {
		LOGGER.debug("Detected an update for adopters file, reading in {}", adoptersPath);
		// get a json processor, and read in the file
		if (Files.exists(adoptersPath)) {
			try (InputStream is = new BufferedInputStream(Files.newInputStream(adoptersPath))) {
				AdopterList al = json.fromJson(is, AdopterList.class);
				return al.getAdopters();
			} catch (IOException e) {
				LOGGER.warn("Error reading file at path: {}\n", adoptersPath, e);
			}
		} else {
			LOGGER.error("Bad file was passed for adopters update, not reading in file: {}", adoptersPath);
		}
		// empty list is valid state, so return null to represent error/no update
		return null;
	}

	@Override
	public List<Adopter> getAdopters() {
		synchronized (this) {
			return new ArrayList<>(adopters);
		}
	}

	public void setAdopters(List<Adopter> adopters) {
		synchronized (this) {
			this.adopters = new ArrayList<>(adopters);
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
                .sorted(Comparator.comparing(a -> a.getName().toLowerCase())).collect(Collectors.toList()));
		return ap;
	}

}
