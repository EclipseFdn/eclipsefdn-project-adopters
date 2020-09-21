/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.adopters.config;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Updates JSONB config to use a naming convention when interacting with objects
 * that match the API best practices set by internal documentation.
 * 
 * @author Martin Lowe
 */
@Provider
public class JsonBConfig implements ContextResolver<Jsonb> {

	@Override
	public Jsonb getContext(Class<?> type) {
		JsonbConfig config = new JsonbConfig();

		// following strategy is defined as default by internal API guidelines
		config.withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES)
				.withDateFormat("uuuu-MM-dd'T'HH:mm:ssXXX", null);
		return JsonbBuilder.create(config);
	}
}
