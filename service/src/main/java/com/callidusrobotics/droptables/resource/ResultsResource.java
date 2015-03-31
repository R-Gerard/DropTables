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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.callidusrobotics.droptables.configuration.DropTablesConfig;
import com.callidusrobotics.droptables.model.ResultDao;
import com.callidusrobotics.droptables.model.ResultEntry;
import com.callidusrobotics.droptables.view.ResultListView;
import com.callidusrobotics.droptables.view.ResultView;
import com.mongodb.WriteResult;

/**
 * REST APIs to perform CRUD operations on report result objects cached in the
 * database.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 * @see ResultDao
 * @see ResultEntry
 */
@Path("/results/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResultsResource {
  private final ResultDao resultDao;

  public ResultsResource(DropTablesConfig config, Environment env) throws IOException {
    resultDao = new ResultDao(config.getMongoFactory().buildReadWriteDatastore(env));
  }

  /**
   * Generates a View listing ResultEntry objects in the DB.
   *
   * @return The View, never null
   */
  @Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
  @GET
  public ResultListView list() {
    List<ResultEntry> results = resultDao.find().asList();

    return new ResultListView(results);
  }

  /**
   * Generates a View that displays a specified ResultEntry.
   *
   * @param id
   *          The {@link ResultEntry} object to fetch from the database
   * @return The View, never null
   */
  @Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
  @Path("/{id}/")
  @GET
  public ResultView fetch(@Valid @PathParam("id") ObjectId id) {
    ResultEntry result = resultDao.get(id);
    if (result == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    return new ResultView(result);
  }

  /**
   * Updates an existing {@link ResultEntry} object in the database.
   *
   * @param id
   *          The {@link ResultEntry} object to update in the database
   * @param attributes
   *          A map of key-value pairs to update in the object
   * @return Hypermedia reference of the updated object, never null
   */
  @Path("/{id}/")
  @PUT
  public Hypermedia update(@Valid @PathParam("id") ObjectId id, @Valid @NotNull Map<String, String> attributes) {
    ResultEntry entity = resultDao.get(id);
    if (entity == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    if (attributes.containsKey("keepForever")) {
      entity.setKeepForever(Boolean.parseBoolean(attributes.get("keepForever")));
    }

    String resultId = resultDao.save(entity).getId().toString();
    return new Hypermedia(resultId, "/results/{id}");
  }

  /**
   * Deletes the specified ResultEntry.
   *
   * @param id
   *          The {@link ResultEntry} object to fetch from the database and
   *          delete
   * @return The WriteResult, never null
   */
  @Path("/{id}/")
  @DELETE
  public WriteResult delete(@Valid @PathParam("id") ObjectId id) {
    return resultDao.deleteById(id);
  }
}
