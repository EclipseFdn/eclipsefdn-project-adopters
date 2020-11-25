/**
 * ***************************************************************************** Copyright (C) 2020
 * Eclipse Foundation
 *
 * <p>This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 * ****************************************************************************
 */
package org.eclipsefoundation.adopters.response;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
public class CORSFilter implements ContainerResponseFilter {
  @ConfigProperty(name = "quarkus.http.cors.exposed-headers")
  String exposedHeaders;

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {
    responseContext.getHeaders().put("Access-Control-Expose-Headers", Arrays.asList(exposedHeaders));
  }
}
