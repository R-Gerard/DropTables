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

import org.mockito.ArgumentMatcher;

import com.callidusrobotics.droptables.model.ResultEntry;

public class ResultLogMatcher extends ArgumentMatcher<ResultEntry> {
  ResultEntry ref;

  public ResultLogMatcher(ResultEntry ref) {
    this.ref = ref;
  }

  @Override
  public boolean matches(Object argument) {
    if (!(argument instanceof ResultEntry)) {
      return false;
    }

    ResultEntry other = (ResultEntry) argument;
    return ref.data.equals(other.data);
  }
}
