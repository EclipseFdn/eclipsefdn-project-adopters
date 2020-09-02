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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Adopter;
import org.eclipsefoundation.adopters.model.AdopterList;
import org.eclipsefoundation.adopters.model.Project;
import org.eclipsefoundation.adopters.service.AdopterService;

import com.google.gson.Gson;

import io.quarkus.cache.CacheResult;
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

	private List<Adopter> adopters;

	@PostConstruct
	public void init() throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Gson gson = new Gson();
		try (InputStream s = cl.getResourceAsStream("adopters.json"); InputStreamReader sr = new InputStreamReader(s)) {
			AdopterList al = gson.fromJson(sr, AdopterList.class);
			this.adopters = al.getAdopters();
		}
	}

	@Override
	public List<Adopter> getAdopters() {
		return new ArrayList<>(adopters);
	}

	@Override
	public List<AdoptedProject> getAdoptedProjects(List<Project> projects) {
		return projects.stream().map(this::getAdoptedProject).filter(a -> !a.getAdopters().isEmpty())
				.collect(Collectors.toList());
	}

	@CacheResult(cacheName = "default")
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
