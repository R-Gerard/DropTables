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

import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.callidusrobotics.droptables.configuration.DropTablesConfig;
import com.callidusrobotics.droptables.view.HomeAboutView;
import com.callidusrobotics.droptables.view.HomeView;

/**
 * REST APIs for top-level entrypoints into the app.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HomeResource {

  public HomeResource(DropTablesConfig config, Environment env) {}

  /**
   * Generates a HomeView object.
   *
   * @return The View, never null
   */
  @Produces(MediaType.TEXT_HTML)
  @GET
  public HomeView home() {
    return new HomeView();
  }

  /**
   * Generates a HomeAboutView object.
   *
   * @return The View, never null
   */
  @Produces(MediaType.TEXT_HTML)
  @Path("/about/")
  @GET
  public HomeAboutView about() {
    return new HomeAboutView();
  }
}
