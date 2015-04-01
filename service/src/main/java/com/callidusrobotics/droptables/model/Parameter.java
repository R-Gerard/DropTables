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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * POJO for representing default parameters of ReportGenerator objects.
 *
 * @author Rusty Gerard
 * @since 0.0.3
 */
@Embedded
public class Parameter {
  public enum ParamType {
    STRING,
    NUMBER,
    DATE,
    SELECT
  }

  @NotNull
  @Property("type")
  ParamType type = ParamType.STRING;

  @NotNull
  @Property("tooltip")
  String tooltip = "";

  @NotNull
  @Property("values")
  List<String> values = Collections.emptyList();

  public Parameter() {}

  public Parameter(ParamType type, String tooltip, List<String> values) {
    Validate.notNull(type);
    Validate.notNull(tooltip);

    this.type = type;
    this.tooltip = tooltip;
    this.values = new ArrayList<String>(values.size());
    this.values.addAll(values);
  }

  public Parameter(Parameter other) {
    this(other.type, other.tooltip, other.values);
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return super.toString();
    }
  }

  @JsonProperty("type")
  public void setType(ParamType type) {
    Validate.notNull(type);
    this.type = type;
  }

  @JsonProperty("type")
  public ParamType getType() {
    return type;
  }

  @JsonProperty("tooltip")
  public void setTooltip(String tooltip) {
    Validate.notNull(tooltip);
    this.tooltip = tooltip;
  }

  @JsonProperty("tooltip")
  public String getTooltip() {
    return tooltip;
  }

  @JsonProperty("values")
  public void setValues(List<String> values) {
    this.values = new ArrayList<String>(values.size());
    this.values.addAll(values);
  }

  @JsonProperty("values")
  public List<String> getValues() {
    return Collections.unmodifiableList(values);
  }

  @JsonIgnore
  public String getDefaultValue() {
    if (values.isEmpty()) {
      return "";
    }

    return values.get(0);
  }
}
