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

import static org.junit.Assert.*;

import java.util.Date;

import net.sf.json.test.JSONAssert;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultLogTest {
  ResultEntry resultEntry;

  @Before
  public void before() {
    resultEntry = ModelUtil.buildResult();
  }

  @Test
  public void toJsonSuccess() throws Exception {
    resultEntry.id = new ObjectId("54e8f1dbd9a93c9b467d5380");
    resultEntry.report = null;

    String expectedJson = "{\"id\":{\"time\":1424552411000,\"date\":1424552411000,\"timestamp\":1424552411,\"timeSecond\":1424552411,\"machine\":-643220325,\"new\":false,\"inc\":1182618496},\"report\":null,\"keepForever\":false,\"ttl\":0,\"dateCreated\":0,\"user\":\"John Doe\",\"data\":\"\",\"parameters\":{}}";

    // Unit under test
    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(resultEntry);

    // Verify results
    JSONAssert.assertJsonEquals(expectedJson, result);
  }

  @Test
  public void prePersistNewObjectNoTTL() throws Exception {
    resultEntry.keepForever = true;
    resultEntry.created = null;
    resultEntry.ttl = null;

    // Unit under test
    resultEntry.prePersist();

    // Verify results
    assertNotNull(resultEntry.created);
    assertNull(resultEntry.ttl);
  }

  @Test
  public void prePersistNewObjectWithTTL() throws Exception {
    resultEntry.keepForever = false;
    resultEntry.created = null;
    resultEntry.ttl = null;

    // Unit under test
    resultEntry.prePersist();

    // Verify results
    assertNotNull(resultEntry.created);
    assertNotNull(resultEntry.ttl);
  }

  @Test
  public void prePersistModifiedObjectNoTTL() throws Exception {
    Date created = ModelUtil.buildDate();
    resultEntry.keepForever = true;
    resultEntry.created = created;
    resultEntry.ttl = created;

    // Unit under test
    resultEntry.prePersist();

    // Verify results
    assertEquals("dateCreated was modified", created, resultEntry.created);
    assertNull(resultEntry.ttl);
  }

  @Test
  public void prePersistModifiedObjectWithTTL() throws Exception {
    Date created = ModelUtil.buildDate();
    resultEntry.keepForever = false;
    resultEntry.created = created;
    resultEntry.ttl = created;

    // Unit under test
    resultEntry.prePersist();

    // Verify results
    assertEquals("dateCreated was modified", created, resultEntry.created);
    assertEquals("ttl was modified", created, resultEntry.ttl);
  }
}
