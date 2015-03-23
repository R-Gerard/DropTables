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

import java.util.Date;
import java.util.HashMap;

import org.bson.types.ObjectId;

import com.callidusrobotics.droptables.model.ReportGenerator.Language;

public class ModelUtil {

  public static ReportGenerator buildReport() {
    ReportGenerator report = new ReportGenerator();
    report.created = buildDate();
    report.modified = buildDate();
    report.id = new ObjectId(buildDate());
    report.language = Language.GROOVY;
    report.script = "";
    report.template = "";
    report.name = "Script" + (int) Math.random() * 10000;
    report.author = "John Doe";
    report.description = "This is a script for unit testing";
    report.binding = new HashMap<String,String>();
    report.binding.put("foo", "bar");

    return report;
  }

  public static ResultEntry buildResult() {
    ResultEntry result = new ResultEntry();
    result.id = new ObjectId(buildDate());
    result.report = buildReport();
    result.created = buildDate();
    result.data = "";
    result.ttl = buildDate();
    result.user = "John Doe";

    return result;
  }

  public static Date buildDate() {
    return new Date((long) Math.random() * Long.MAX_VALUE);
  }

  public static void clearTTL(ResultEntry result) {
    result.ttl = null;
    result.keepForever = true;
  }
}
