/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipsefoundation.adopters.model.Project;

/**
 * Interface for interacting with the PMI Projects API. Used to link Git
 * repos/projects with an Eclipse project to validate committer access.
 * 
 * @author Martin Lowe
 *
 */
@Path("/api/projects")
@RegisterRestClient
public interface ProjectsAPI {

	/**
	 * Retrieves all projects with the given repo URL.
	 * 
	 * @param repoUrl the target repos URL
	 * @return a list of Eclipse Foundation projects.
	 */
	@GET
	@Produces("application/json")
	List<Project> getProject(@QueryParam("page") int page, @QueryParam("pagesize") int pageSize);
}
