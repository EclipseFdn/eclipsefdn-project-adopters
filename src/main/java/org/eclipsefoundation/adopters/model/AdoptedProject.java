package org.eclipsefoundation.adopters.model;

import java.util.ArrayList;
import java.util.List;

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
