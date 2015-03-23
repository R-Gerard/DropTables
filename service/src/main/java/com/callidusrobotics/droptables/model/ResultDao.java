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

package com.callidusrobotics.droptables.model;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoCommandException;

/**
 * DAO to manage ResultEntry objects.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 */
public class ResultDao extends BasicDAO<ResultEntry, ObjectId> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResultDao.class);

  public ResultDao(Datastore datastore) {
    super(datastore);

    try {
      datastore.ensureIndexes();
    } catch (MongoCommandException e) {
      LOGGER.error("Unable to ensure indexes: " + e.getMessage());
      LOGGER.debug(ExceptionUtils.getStackTrace(e));
    }
  }
}
