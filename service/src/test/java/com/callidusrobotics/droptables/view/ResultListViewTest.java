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

package com.callidusrobotics.droptables.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.callidusrobotics.droptables.model.ModelUtil;
import com.callidusrobotics.droptables.model.ResultEntry;

@RunWith(MockitoJUnitRunner.class)
public class ResultListViewTest {
  ResultListView view;
  List<ResultEntry> results;

  FreemarkerViewRenderer renderer;
  OutputStream writer;

  @Before
  public void before() throws Exception {
    results = new ArrayList<ResultEntry>();
    for (int i=0; i<10; i++) {
      ResultEntry result = ModelUtil.buildResult();
      Map<String, String> params = new HashMap<String, String>();
      params.put("foo", "" + (int) (Math.random() * 1000));
      result.setParameters(params);
      results.add(result);
    }
    results.get(0).setReport(null);
    ModelUtil.clearTTL(results.get(1));

    view = new ResultListView(results);
    renderer = new FreemarkerViewRenderer();
    writer = new ByteArrayOutputStream();
  }

  @Test
  public void renderFreeMarkerSuccess() throws Exception {
    // Unit under test
    renderer.render(view, Locale.ENGLISH, writer);

    // Verify results
    String result = writer.toString();

    assertEquals("Exactly 1 ReportLog should have a null TTL", 1, StringUtils.countMatches(result, "These results will not be scheduled for deletion from the database"));
    for (int i=0; i<results.size(); i++) {
      ResultEntry entry = results.get(i);

      assertTrue("Report ID field was not set for #" + i, result.contains(entry.getReport() == null ? "DELETED" : entry.getReport().getId().toString()));
      assertTrue("ID field was not set for #" + i, result.contains(entry.getId().toString()));

      for (Entry<String, String> kv : entry.getParameters().entrySet()) {
        assertTrue("Parameter Key was not set for #" + i, result.contains(kv.getKey()));
        assertTrue("Parameter Val was not set for #" + i, result.contains(kv.getValue()));
      }
    }
  }
}
