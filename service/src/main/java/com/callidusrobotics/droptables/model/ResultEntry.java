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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.PrePersist;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexDirection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for caching the results of a ReportGenerator execution.
 *
 * @author Rusty Gerard
 * @since 0.0.2
 * @see ReportGenerator
 */
@Entity("droptables.results")
public class ResultEntry {
  // Entries are deleted after 2 weeks unless "keepForever" is true
  public static final int TTL_SECONDS = 1209600;

  @Id
  ObjectId id;

  @Valid
  @Nullable
  @Reference(ignoreMissing = true)
  ReportGenerator report;

  @Property("keepForever")
  boolean keepForever = false;

  @Nullable
  @Indexed(expireAfterSeconds = TTL_SECONDS)
  @Property("ttl")
  Date ttl;

  @NotNull
  @Indexed(IndexDirection.ASC) // Most recent dates first
  @Property("dateCreated")
  Date created;

  @Valid
  @NotNull
  @Property("user")
  String user = "anonymous";

  @NotNull
  @Property("data")
  String data;

  @Valid
  @NotNull
  @Property("parameters")
  Map<String, String> parameters = Collections.emptyMap();

  public ResultEntry() {}

  public ResultEntry(ReportGenerator report, String user, String data, Map<String,String> parameters) {
    this.report = report;
    this.user = user;
    this.data = data;
    this.parameters  = new LinkedHashMap<String, String>();
    this.parameters.putAll(parameters);
  }

  @PrePersist
  void prePersist() {
    // FIXME: This should use Mongo's currentDate
    if (keepForever) {
      ttl = null;
    } else if (ttl == null) {
      ttl = new Date();
    }

    if (created == null) {
      created = new Date();
    }
  }

  public ObjectId getId() {
    return id;
  }

  @JsonProperty("report")
  public ReportGenerator getReport() {
    return report;
  }

  @JsonProperty("report")
  public void setReport(ReportGenerator report) {
    this.report = report;
  }

  @JsonProperty("keepForever")
  public boolean shouldKeepForever() {
    return keepForever;
  }

  @JsonProperty("keepForever")
  public void setKeepForever(boolean keepForever) {
    this.keepForever = keepForever;
  }

  @JsonProperty("ttl")
  public Date getTtl() {
    if (ttl == null) {
      return null;
    }

    return new Date(ttl.getTime());
  }

  @JsonProperty("ttl")
  public void setTtl(Date ttl) {
    if (ttl != null) {
      this.ttl = new Date(ttl.getTime());
    }
  }

  @JsonProperty("dateCreated")
  public Date getCreated() {
    if (created == null) {
      return null;
    }

    return new Date(created.getTime());
  }

  @JsonProperty("dateCreated")
  public void setCreated(Date created) {
    if (created != null) {
      this.created = new Date(created.getTime());
    }
  }

  @JsonProperty("user")
  public String getUser() {
    return user;
  }

  @JsonProperty("user")
  public void setUser(String user) {
    this.user = user;
  }

  @JsonProperty("data")
  public String getData() {
    return data;
  }

  @JsonProperty("data")
  public void setData(String data) {
    this.data = data;
  }

  @JsonProperty("parameters")
  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  @JsonProperty("parameters")
  public void setParameters(Map<String, String> parameters) {
    this.parameters = new LinkedHashMap<String, String>();
    parameters.putAll(parameters);
  }
}
