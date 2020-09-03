package org.eclipsefoundation.adopters.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * Provides a way to bind NIO paths to MP property values.
 * 
 * @author Martin Lowe
 *
 */
public class PathConverter implements Converter<Path> {
	private static final long serialVersionUID = 1L;

	@Override
	public Path convert(String value) {
		return Paths.get(value);
	}
}