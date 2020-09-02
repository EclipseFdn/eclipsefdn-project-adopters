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
 * Root object for adopters.json serialized content.
 * 
 * @author Martin Lowe
 *
 */
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
