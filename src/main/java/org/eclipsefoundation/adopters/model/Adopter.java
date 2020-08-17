package org.eclipsefoundation.adopters.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.SerializedName;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
