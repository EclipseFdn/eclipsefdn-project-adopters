package org.eclipsefoundation.adopters.model;

import java.util.ArrayList;
import java.util.List;

public class AdopterList {
	private List<Adopter> adopters = new ArrayList<>();

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
