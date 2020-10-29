package org.eclipsefoundation.adopters.resource.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.enterprise.inject.Instance;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Return the compiled 404 page when a URL mapping is not found.
 *
 * @author Martin Lowe
 */
@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundMapper.class);

  @ConfigProperty(name = "eclipse.error.location", defaultValue = "/META-INF/resources/404.html")
  Instance<String> errorPageLoc;

  @Override
  public Response toResponse(NotFoundException exception) {
    try (InputStream is = this.getClass().getResourceAsStream(errorPageLoc.get())) {
      return Response.status(404).entity(IOUtils.toString(is, StandardCharsets.UTF_8)).build();
    } catch (IOException e) {
      LOGGER.error("Unable to read in error page at location {}", errorPageLoc.get(), e);
    }
    return Response.status(404).build();
  }
}
