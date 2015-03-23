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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.util.GroovyScriptEngine;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import com.callidusrobotics.droptables.model.DocumentDao;
import com.callidusrobotics.droptables.model.ReportDao;
import com.callidusrobotics.droptables.model.ReportGenerator;
import com.callidusrobotics.droptables.model.ResultDao;
import com.callidusrobotics.droptables.model.ResultEntry;
import com.callidusrobotics.droptables.model.ResultLogMatcher;
import com.google.common.collect.ImmutableMap;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@RunWith(MockitoJUnitRunner.class)
public class ReportsResourceTest {
  ReportsResource resource;

  @Mock ReportDao mockReportDao;
  @Mock ResultDao mockResultDao;
  @Mock Datastore mockDatastore;
  @Mock GroovyScriptEngine mockScriptEngine;
  @Mock ObjectId mockId, mockId2, mockId3;
  @Mock DBObject mockObject, mockObject2, mockObject3;
  @Mock Key<ReportGenerator> mockWriteKey;
  @Mock WriteResult mockWriteResult;
  @Mock ReportGenerator mockGroovyReport;
  @Mock Script mockScript;
  @Mock Template mockTemplate;
  @Mock Binding mockBinding;
  @Mock Writable mockWritable;

  static final String CACHE_DIR = "/tmp/droptables-tests/groovy/";
  static final String FILENAME = "mockScript.groovy";

  @Before
  public void before() throws Exception {
    resource = new ReportsResource(mockReportDao, mockResultDao, mockDatastore, mockScriptEngine, CACHE_DIR);

    when(mockObject.get(DocumentDao.DOC_ID)).thenReturn(mockId);
    when(mockObject2.get(DocumentDao.DOC_ID)).thenReturn(mockId2);
    when(mockObject3.get(DocumentDao.DOC_ID)).thenReturn(mockId3);
    when(mockId.toString()).thenReturn("11111");
    when(mockId2.toString()).thenReturn("22222");
    when(mockId3.toString()).thenReturn("33333");

    when(mockGroovyReport.parseScript()).thenReturn(mockScript);
    when(mockGroovyReport.parseTemplate()).thenReturn(mockTemplate);
    when(mockGroovyReport.parseBinding()).thenReturn(mockBinding);
    when(mockGroovyReport.writeScript(CACHE_DIR)).thenReturn(FILENAME);
  }

  @After
  public void after() throws Exception {
    verifyNoMoreInteractions(mockReportDao);
    verifyNoMoreInteractions(mockResultDao);
    verifyNoMoreInteractions(mockDatastore);
    verifyNoMoreInteractions(mockScriptEngine);
    verifyNoMoreInteractions(mockGroovyReport);
  }

  @Test
  public void upsertSuccess() throws Exception {
    when(mockWriteKey.getId()).thenReturn(mockId);
    when(mockReportDao.save(mockGroovyReport)).thenReturn(mockWriteKey);

    // Unit under test
    Hypermedia result = resource.upsert(mockGroovyReport);

    // Verify results
    verify(mockReportDao).save(mockGroovyReport);

    assertEquals(mockId.toString(), result.getId());
    assertEquals("/reports/" + mockId.toString(), result.getHref());
  }

  @Test
  public void executeSuccess() throws Exception {
    String data = "Hello, " + ((int) (Math.random() * 10000)) + "!";
    String logId = "" + (int) (Math.random() * 10000);
    ResultEntry refLog = new ResultEntry();
    refLog.setData(data);
    ResultLogMatcher resultLogMatcher = new ResultLogMatcher(refLog);

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockScriptEngine.run(FILENAME, mockBinding)).thenReturn(new Object());
    when(mockTemplate.make(isA(Map.class))).thenReturn(mockWritable);
    when(mockWritable.toString()).thenReturn(data);
    when(mockResultDao.save(argThat(resultLogMatcher))).thenReturn(new Key<ResultEntry>(ResultEntry.class, logId));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    Hypermedia result = resource.execute(mockId, requestBindings);

    // Verify results
    verify(mockReportDao).get(mockId);
    verify(mockScriptEngine).run(FILENAME, mockBinding);
    verify(mockTemplate).make(isA(Map.class));

    verify(mockGroovyReport).parseScript();
    verify(mockGroovyReport).parseTemplate();
    verify(mockGroovyReport).parseBinding();
    verify(mockGroovyReport).writeScript(CACHE_DIR);

    verify(mockResultDao).save(argThat(resultLogMatcher));

    assertEquals(logId, result.getId());
    assertEquals("/results/" + logId, result.getHref());
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureNotFound() throws Exception {
    when(mockReportDao.get(mockId)).thenReturn(null);

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      // Verify results
      verify(mockReportDao).get(mockId);

      assertEquals(Response.Status.NOT_FOUND, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureScriptExecutionException() throws Exception {
    String message = "Problem executing script";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockScriptEngine.run(FILENAME, mockBinding)).thenThrow(new GroovyRuntimeException(message));
    when(mockTemplate.make(isA(Map.class))).thenReturn(mockWritable);
    when(mockWritable.toString()).thenReturn("Hello, World!");

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);

      verify(mockScriptEngine).run(FILENAME, mockBinding);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureTemplateExecutionException() throws Exception {
    String message = "Problem executing template";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockScriptEngine.run(FILENAME, mockBinding)).thenReturn(new Object());
    when(mockTemplate.make(isA(Map.class))).thenThrow(new GroovyRuntimeException(message));
    when(mockWritable.toString()).thenReturn("Hello, World!");

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);

      verify(mockScriptEngine).run(FILENAME, mockBinding);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureScriptParseError() throws Exception {
    String message = "Failed to parse Groovy script";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockGroovyReport.parseScript()).thenThrow(new GroovyRuntimeException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureTemplatetParseError() throws Exception {
    String message = "Failed to parse template";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockGroovyReport.parseTemplate()).thenThrow(new GroovyRuntimeException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureBindingParseError() throws Exception {
    String message = "Failed to initialize variable bindings";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockGroovyReport.parseBinding()).thenThrow(new GroovyRuntimeException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureDiskIoError() throws Exception {
    String message = "Failed to write file";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockGroovyReport.writeScript(CACHE_DIR)).thenThrow(new IOException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureScriptNpe() throws Exception {
    String message = "Foo is null";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockScriptEngine.run(FILENAME, mockBinding)).thenThrow(new NullPointerException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);
      verify(mockScriptEngine).run(FILENAME, mockBinding);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureScriptUndefinedVariable() throws Exception {
    String message = "No such property: foo";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockScriptEngine.run(FILENAME, mockBinding)).thenThrow(new MissingPropertyException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);
      verify(mockScriptEngine).run(FILENAME, mockBinding);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }

  @Test(expected = WebApplicationException.class)
  public void executeFailureTemplateNpe() throws Exception {
    String message = "Foo is null";

    when(mockReportDao.get(mockId)).thenReturn(mockGroovyReport);
    when(mockTemplate.make(isA(Map.class))).thenThrow(new NullPointerException(message));

    Map<String, String> requestBindings = ImmutableMap.of("foo", "bar");

    // Unit under test
    try {
      resource.execute(mockId, requestBindings);
    } catch (WebApplicationException e) {
      verify(mockReportDao).get(mockId);

      verify(mockGroovyReport).parseScript();
      verify(mockGroovyReport).parseTemplate();
      verify(mockGroovyReport).parseBinding();
      verify(mockGroovyReport).writeScript(CACHE_DIR);
      verify(mockScriptEngine).run(FILENAME, mockBinding);

      assertTrue(e.getCause().getMessage().contains(message));
      assertEquals(Response.Status.BAD_REQUEST, Status.fromStatusCode(e.getResponse().getStatus()));

      throw e;
    }
  }
}
