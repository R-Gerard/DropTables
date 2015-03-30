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

import java.util.Date;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.callidusrobotics.droptables.model.ResultEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * View class for listing ResultView objects.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 * @see ResultEntry
 */
public class ResultListView extends View {
  public static final PeriodFormatter PERIOD_FORMTTER = new PeriodFormatterBuilder()
      .appendWeeks()
      .appendSuffix(" weeks ")
      .appendDays()
      .appendSuffix(" days ")
      .appendHours()
      .appendSuffix(" hours ")
      .toFormatter();

  private final List<ResultEntry> results;

  public ResultListView(List<ResultEntry> results) {
    super("result_list.ftl");
    this.results = results;
  }

  public List<ResultEntry> getResults() {
    return results;
  }

  public String prettyPrintParams(ResultEntry result) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(result.getParameters());

    return json;
  }

  public String prettyPrintTTL(ResultEntry result, Date refDate) {
    if (result.getTtl() == null) {
      return "These results will not be scheduled for deletion from the database";
    }

    Period period = new Period(refDate.getTime(), result.getTtl().getTime() + ResultEntry.TTL_SECONDS * 1000);

    return "These results will automatically be deleted in " + PERIOD_FORMTTER.print(period);
  }
}
