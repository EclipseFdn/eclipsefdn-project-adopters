/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.service;

import java.util.List;

import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Adopter;
import org.eclipsefoundation.adopters.model.Project;

/**
 * Interface for service to provide information about project adopters.
 * 
 * @author Martin Lowe
 *
 */
public interface AdopterService {

	/**
	 * Get all adopters currently registered within the system.
	 * 
	 * @return list of all known adopters
	 */
	public List<Adopter> getAdopters();

	/**
	 * Discover all adopters for the past projects, wrap the projects in a new
	 * object, and return all of the new wrapped objects.
	 * 
	 * @param projects the Eclipse projects to retrieve projects for
	 * @return the list of wrapped Eclipse projects
	 */
	public List<AdoptedProject> getAdoptedProjects(List<Project> projects);
}
