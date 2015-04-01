<#-- @ftlvariable name="" type="com.callidusrobotics.droptables.model.ReportGenerator" -->
<html>
  <head>
    <title>Execute Report Generator</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/themes/smoothness/jquery-ui.css" />
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>
    <script language="javascript">
      $(function() {
        <#list report.defaultParameters?keys as key>
        <#assign paramObj=report.defaultParameters?values[key_index]>
        <#if paramObj.type == "DATE">
        $("#param\\[${key}\\]").datepicker({ defaultDate: new Date("${paramObj.getDefaultValue()}") });
        </#if>
        </#list>
      });

      function doPost() {
        var payload = new Object();
        <#list report.defaultParameters?keys as key>
        payload["${key}"] = $("#param\\[${key}\\]").val();
        </#list>

        console.log("POST /reports/" + id + "/results : " + JSON.stringify(payload));
        var request = $.ajax({
          url: "/reports/${report.id}/results",
          type: "POST",
          data: JSON.stringify(payload),
          contentType: "application/json",
          dataType: "json"
        });

        request.done( function(data) {
          console.log("RECEIVED: " + JSON.stringify(data))
          window.location = "/results/" + data["_id"];
        } );

        request.fail( function(data) {
          var newDoc = document.open("text/html", "replace");
          newDoc.write(data.responseText);
          newDoc.close();
        } );
      }
    </script>
  </head>
  <body>
    <#include "common/navbar.ftl">
    <b>ID:</b> <input type="text" size="30" id="id" value="${report.id}" readonly="true" /><br>
    <b>Date Created:</b> <input type="text" size="30" id="created" value="${report.created?string("yyyy-MM-dd HH:mm:ss")}" readonly="true" /><br>
    <b>Date Modified:</b> <input type="text" size="30" id="modified" value="${report.modified?string("yyyy-MM-dd HH:mm:ss")}" readonly="true" /><br>
    <b>Name:</b> <input type="text" size="30" id="name" value="${report.name}" readonly="true" /><br>
    <b>Author:</b> <input type="text" size="30" id="author" value="${report.author}" readonly="true" /><br>
    <b>Language:</b> <input type="text" size="30" id="language" value="${report.language}" readonly="true" /><br><br>
    <b>Description:</b><br>
    <textarea rows="5" cols="80" id="description" readonly="true">${report.description}</textarea></p>
    <p><b>Parameters:</b><br>
    <#list report.defaultParameters?keys as key>
    <#assign paramObj=report.defaultParameters?values[key_index]>
    <#assign tooltip=paramObj.tooltip defaultValue=paramObj.getDefaultValue()>
    <b>${key}:</b>
    <#switch report.defaultParameters?values[key_index].type>
    <#case "STRING">
      <input type="text" size="15" id="param[${key}]" title="${tooltip}" value="${defaultValue}" />
    <#break>
    <#case "NUMBER">
      <input type="number" size="15" id="param[${key}]" title="${tooltip}" value="${defaultValue}" />
    <#break>
    <#case "DATE">
      <input type="text" size="15" id="param[${key}]" title="${tooltip}" value="${defaultValue}" />
    <#break>
    <#case "SELECT">
      <select id="param[${key}]" title="${tooltip}">
        <#list report.defaultParameters?values[key_index].values as value>
        <option value="${value}">${value}</option>
        </#list>
      </select>
    <#break>
    </#switch>
    <br>
    </#list>
    </p>
    <p>
    <button onclick="doPost()">Execute</button>
    </p>
  </body>
</html>
