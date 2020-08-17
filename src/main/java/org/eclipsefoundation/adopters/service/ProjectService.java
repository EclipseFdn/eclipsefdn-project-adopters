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

import org.eclipsefoundation.adopters.model.Project;

/**
 * Intermediate layer between resource and API layers that handles retrieval of
 * all projects and caching of that data for availability purposes.
 * 
 * @author Martin Lowe
 *
 */
public interface ProjectService {

	/**
	 * Retrieves all currently available projects from cache if available, otherwise
	 * going to API to retrieve a fresh copy of the data.
	 * 
	 * @return list of projects available from API.
	 */
	List<Project> getProjects();
}
