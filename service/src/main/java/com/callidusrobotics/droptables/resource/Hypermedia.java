/**
 * Copyright (C) 2015 Rusty Gerard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.callidusrobotics.droptables.resource;

import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable object representing link relations of resources.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 */
public class Hypermedia {
  private final String id;
  private final String href;

  public Hypermedia(String id, String path) {
    this.id = id;
    this.href = UriBuilder.fromPath(path).build(id).toString();
  }

  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  @JsonProperty("href")
  public String getHref() {
    return href;
  }
}
