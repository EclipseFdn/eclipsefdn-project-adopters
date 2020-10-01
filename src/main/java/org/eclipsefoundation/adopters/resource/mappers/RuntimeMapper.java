/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.adopters.resource.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catch-all exception mapper to ensure that any error thrown by the service
 * will log the error and quit out safely while limiting the response to a
 * simple error.
 * 
 * @author Martin Lowe
 *
 */
@Provider
public class RuntimeMapper implements ExceptionMapper<RuntimeException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeMapper.class);

	@Override
	public Response toResponse(RuntimeException exception) {
		LOGGER.error(exception.getMessage(), exception);
		// return an empty response with a server error response
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}

}
