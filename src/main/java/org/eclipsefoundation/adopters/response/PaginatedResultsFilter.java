/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.adopters.response;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.LinkHeader;

/**
 * Adds pagination and Link headers to the response by slicing the response
 * entity if its a list entity. This will not dig into complex entities to avoid
 * false positives.
 * 
 * @author Martin Lowe
 *
 */
@Provider
public class PaginatedResultsFilter implements ContainerResponseFilter {
	int defaultPageSize = 10;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		Object entity = responseContext.getEntity();
		// only try and paginate if there are multiple entities
		if (entity instanceof List) {
			List<?> listEntity = (List<?>) entity;
			int page = getRequestedPage(listEntity);
			int lastPage = (int) Math.ceil((double) listEntity.size() / defaultPageSize);
			// set the sliced array as the entity
			responseContext.setEntity(
					listEntity.subList(getArrayLimitedNumber(listEntity, Math.max(0, page - 1) * defaultPageSize),
							getArrayLimitedNumber(listEntity, defaultPageSize * page)));

			// add link headers for paginated page hints
			UriBuilder builder = getUriInfo().getBaseUriBuilder();
			LinkHeader lh = new LinkHeader();
			// add first + last page link headers
			lh.addLink("this page of results", "self", buildHref(builder, page), "");
			lh.addLink("first page of results", "first", buildHref(builder, 1), "");
			lh.addLink("last page of results", "last", buildHref(builder, lastPage), "");
			// add next/prev if needed
			if (page > 1) {
				lh.addLink("previous page of results", "prev", buildHref(builder, page - 1), "");
			}
			if (page < lastPage) {
				lh.addLink("next page of results", "next", buildHref(builder, page + 1), "");
			}
			// set the link header to the response
			responseContext.getHeaders().add("Link", lh);
		}

	}

	/**
	 * Gets the current requested page, rounding down to max if larger than the max
	 * page number, and up if below 1.
	 * 
	 * @param listEntity list entity used to determine the number of pages present
	 *                   for current call.
	 * @return the current page number if set, the last page if greater, or 1 if not
	 *         set or negative.
	 */
	private int getRequestedPage(List<?> listEntity) {
		MultivaluedMap<String, String> params = getUriInfo().getQueryParameters();
		if (params.containsKey("page")) {
			try {
				int page = Integer.parseInt(params.getFirst("page"));
				// use double cast int to allow ceil call to round up for pages
				int maxPage = (int) Math.ceil((double) listEntity.size() / defaultPageSize);
				// get page, with min of 1 and max of last page
				return Math.min(Math.max(1, page), maxPage);
			} catch (NumberFormatException e) {
				// page isn't a number, just return
				return 1;
			}
		}
		return 1;
	}

	/**
	 * Builds an href for a paginated link using the BaseUri UriBuilder from the
	 * UriInfo object, replacing just the page query parameter.
	 * 
	 * @param builder base URI builder from the UriInfo object.
	 * @param page    the page to link to in the returned link
	 * @return fully qualified HREF for the paginated results
	 */
	private String buildHref(UriBuilder builder, int page) {
		return builder.replaceQueryParam("page", page).build().toString();
	}

	/**
	 * Gets an int bound by the size of a list.
	 * 
	 * @param list the list to bind the number by
	 * @param num  the number to check for exceeding bounds.
	 * @return the passed number if its within the size of the given array, 0 if the
	 *         number is negative, and the array size if greater than the maximum
	 *         bounds.
	 */
	private int getArrayLimitedNumber(List<?> list, int num) {
		if (num < 0) {
			return 0;
		} else if (num > list.size()) {
			return list.size();
		}
		return num;
	}

	private UriInfo getUriInfo() {
		return ResteasyContext.getContextData(UriInfo.class);
	}
}
