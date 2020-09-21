/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.resource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Project;
import org.eclipsefoundation.adopters.service.AdopterService;
import org.eclipsefoundation.adopters.service.ProjectService;

/**
 * Retrieves adopted projects along with adopters info for display. This data
 * can be viewed for all projects, a single project, or all projects defined
 * within a working group by the working group ID.
 * 
 * @author Martin Lowe
 *
 */
@Path("")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AdoptersResource {

	@Inject
	ProjectService projectService;
	@Inject
	AdopterService adopterService;

	@GET
	@Path("/projects")
	public Response getAllAdopters(@QueryParam("working_group") String workingGroup) {
		// get cached project list
		List<Project> projects = projectService.getProjects();
		if (workingGroup != null && workingGroup.trim() != null) {
			projects = projects.stream()
					.filter(p -> p.getWorkingGroups().stream().anyMatch(wg -> wg.getId().equals(workingGroup)))
					.collect(Collectors.toList());
		}
		// no projects for working group
		if (projects.isEmpty()) {
			return Response.ok(Collections.emptyList()).build();
		}
		// get the adopted projects, removing non-adopted projects
		List<AdoptedProject> aps = adopterService.getAdoptedProjects(projects);
		if (aps == null) {
			return Response.serverError().build();
		}
		return Response.ok(aps).build();
	}

	@GET
	@Path("/projects/{projectId}")
	public Response getAdoptersForProject(@PathParam("projectId") String projectId) {
		// get cached project list
		List<Project> projects = projectService.getProjects();
		List<Project> filteredProjects = projects.stream().filter(p -> p.getProjectId().equals(projectId))
				.collect(Collectors.toList());
		// no projects for working group
		if (filteredProjects.isEmpty()) {
			return Response.ok(Collections.emptyList()).build();
		}
		// get the adopted projects, removing non-adopted projects
		List<AdoptedProject> aps = adopterService.getAdoptedProjects(filteredProjects);
		if (aps == null) {
			return Response.serverError().build();
		}
		return Response.ok(aps).build();
	}
}
