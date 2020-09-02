/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a project from the Eclipse API.
 * 
 * @author Martin Lowe
 *
 */
public class Project {
	private String projectId;
	private String name;
	private String url;
	private String logo;
	private List<WorkingGroup> workingGroups = new ArrayList<>();

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * @return the workingGroups
	 */
	public List<WorkingGroup> getWorkingGroups() {
		return new ArrayList<>(workingGroups);
	}

	/**
	 * @param workingGroups the workingGroups to set
	 */
	public void setWorkingGroups(List<WorkingGroup> workingGroups) {
		this.workingGroups = new ArrayList<>(workingGroups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(logo, name, projectId, url, workingGroups);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		return Objects.equals(logo, other.logo) && Objects.equals(name, other.name)
				&& Objects.equals(projectId, other.projectId) && Objects.equals(url, other.url)
				&& Objects.equals(workingGroups, other.workingGroups);
	}

	public static class WorkingGroup {
		private String name;
		private String id;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

	}
}
