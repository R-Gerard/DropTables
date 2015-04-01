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

import io.dropwizard.views.View;

import java.util.ArrayList;
import java.util.List;

import com.callidusrobotics.droptables.model.Parameter;
import com.callidusrobotics.droptables.model.ReportGenerator;
import com.callidusrobotics.droptables.model.Parameter.ParamType;
import com.callidusrobotics.droptables.model.ReportGenerator.Language;

/**
 * View class for editing ReportGenerator objects.
 *
 * @author Rusty Gerard
 * @since 0.0.1
 * @see ReportGenerator
 */
public class ReportEditView extends View {
  private final ReportGenerator report;

  public ReportEditView(ReportGenerator report) {
    super("report_edit.ftl");
    this.report = report;
  }

  public ReportGenerator getReport() {
    return report;
  }

  /**
   * Generates a list of available languages, with the current language at the
   * front of the list.
   *
   * @return A list of languages, never null
   * @see ReportGenerator.Language
   */
  public List<String> getLanguages() {
    List<String> result = new ArrayList<String>(Language.values().length);
    for (Language language : Language.values()) {
      result.add(language.toString());
    }
    if (report.getLanguage() != null) {
      result.remove(report.getLanguage().toString());
      result.add(0, report.getLanguage().toString());
    }

    return result;
  }

  /**
   * Generates a list of available parameter types, with the current parameter
   * type at the front of the list.
   *
   * @param parameter
   *          An optional Parameter object to use to order the list, nullable
   * @return A list of types, never null
   * @see Parameter.ParamType
   */
  public List<String> getParamTypes(Parameter parameter) {
    List<String> result = new ArrayList<String>(ParamType.values().length);
    for (ParamType type : ParamType.values()) {
      result.add(type.toString());
    }
    if (parameter != null) {
      result.remove(parameter.getType().toString());
      result.add(0, parameter.getType().toString());
    }

    return result;
  }
}
