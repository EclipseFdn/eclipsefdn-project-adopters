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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

/**
 * Represents an adopter from the serialized adopter.json file.
 * 
 * @author Martin Lowe
 *
 */
public class Adopter {
	private String name;
	@SerializedName("homepage_url")
	private String homepageUrl;
	private String logo;
	@SerializedName("logo_white")
	private String logoWhite;
	private List<String> projects;

	public Adopter() {
		this.projects = new ArrayList<>();
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
	 * @return the homepageUrl
	 */
	public String getHomepageUrl() {
		return homepageUrl;
	}

	/**
	 * @param homepageUrl the homepageUrl to set
	 */
	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
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
	 * @return the logoWhite
	 */
	public String getLogoWhite() {
		return logoWhite;
	}

	/**
	 * @param logoWhite the logoWhite to set
	 */
	public void setLogoWhite(String logoWhite) {
		this.logoWhite = logoWhite;
	}

	/**
	 * @return the projects
	 */
	@JsonIgnore
	public List<String> getProjects() {
		return new ArrayList<>(projects);
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(List<String> projects) {
		this.projects = new ArrayList<>(projects);
	}

}
