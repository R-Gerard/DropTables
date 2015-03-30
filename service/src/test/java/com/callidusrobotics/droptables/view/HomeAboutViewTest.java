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

import static org.junit.Assert.assertTrue;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HomeAboutViewTest extends CommonViewTest {
  HomeAboutView view;

  @Before
  public void before() throws Exception {
    view = new HomeAboutView();
    renderer = new FreemarkerViewRenderer();
    writer = new ByteArrayOutputStream();
  }

  @Test
  public void renderFreeMarkerSuccess() throws Exception {
    // Unit under test
    renderer.render(view, Locale.ENGLISH, writer);

    // Verify results
    String result = writer.toString();

    assertTrue("No content was rendered", StringUtils.isNotBlank(result));
  }
}
