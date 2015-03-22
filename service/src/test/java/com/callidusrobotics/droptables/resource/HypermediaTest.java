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

import net.sf.json.test.JSONAssert;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HypermediaTest {

  @Test
  public void toJsonSuccess() throws Exception {
    String expectedJson = "{\"_id\":\"123456789\",\"href\":\"/reports/123456789\"}";

    Hypermedia hypermedia = new Hypermedia("123456789", "/reports/{id}");

    // Unit under test
    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(hypermedia);

    // Verify results
    JSONAssert.assertJsonEquals(expectedJson, result);
  }
}
