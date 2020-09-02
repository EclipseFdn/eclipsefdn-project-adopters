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


/**
 * A project with information about its adopters (read from the file system)
 * included with the object.
 * 
 * @author Martin Lowe
 *
 */
public class AdoptedProject {

	private String projectId;
	private String name;
	private String url;
	private String logo;
	private List<Adopter> adopters;

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
	 * @return the adopters
	 */
	public List<Adopter> getAdopters() {
		return new ArrayList<>(adopters);
	}

	/**
	 * @param adopters the adopters to set
	 */
	public void setAdopters(List<Adopter> adopters) {
		this.adopters = new ArrayList<>(adopters);
	}
}
