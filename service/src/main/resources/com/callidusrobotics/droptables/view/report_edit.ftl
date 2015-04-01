<#-- @ftlvariable name="" type="com.callidusrobotics.droptables.model.ReportGenerator" -->
<html>
  <head>
    <title>View/Edit Report Generator</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script language="javascript">
      tupleCount = ${report.defaultParameters?keys?size}

      <#if report.id??>
      function doDelete() {
        var id = $("#id").val();

        if (confirm("Really delete this? The action cannot be undone.")) {
          console.log("DELETE /reports/" + id);
          var request = $.ajax({
            url: "/reports/" + id,
            type: "DELETE",
            contentType: "application/json",
            dataType: "json"
          });

          request.always( function(data) { window.location.replace("/reports"); } );
        }
      }
      </#if>

      function doPost() {
        var payload = new Object();
        <#if report.id??>payload.id = $("#id").val();</#if>
        payload.dateCreated = Date.parse( $("#created").val() );
        payload.name = $("#name").val();
        payload.description = $("#description").val();
        payload.author = $("#author").val();
        payload.language = $("#languages").val();
        payload.template = $("#template").val();
        payload.script = $("#script").val();
        payload.defaultParameters = new Object();

        var keys = $( "input[id^='keys']" ).map(function() { return $(this).val(); }).get();
        var types = $( "select[id^='types']" ).map(function() { return $(this).val(); }).get();
        var tips = $( "input[id^='tips']" ).map(function() { return $(this).val(); }).get();
        var vals = $( "input[id^='vals']" ).map(function() { return $(this).val(); }).get();

        for (var i=0; i<keys.length; i++) {
          key = keys[i].trim();
          type = types[i];
          tip = tips[i].trim();
          val = vals[i].trim();

          if (key.length > 0) {
            var param = new Object();
            param.type = type;
            param.tooltip = tip;
            param.values = val.split(';').map(Function.prototype.call, String.prototype.trim);
            payload.defaultParameters[key] = param;
          }
        }

        console.log("POST /reports : " + JSON.stringify(payload));
        var request = $.ajax({
          url: "/reports",
          type: "POST",
          data: JSON.stringify(payload),
          contentType: "application/json",
          dataType: "json"
        });

        request.done( function() {
          alert("Success!");
          window.location.replace("/reports");
        } );

        request.fail( function(data) { alert(data.responseText); } );
      }

      function addTuple() {
        text = "";
        text += "<div id=\"tuple[" + tupleCount + "]\">";
        text += "<b>Key:</b> <input type=\"text\" size=\"15\" id=\"keys[" + tupleCount + "]\" title=\"This is the name of the variable binding in the script engine\" value=\"\" /> ";
        text += "<b>Type:</b> <select id=\"types[" + tupleCount + "]\"><#list getParamTypes(null) as type><option value=\"${type}\">${type}</option></#list></select> ";
        text += "<b>Tooltip:</b> <input type=\"text\" size=\"30\" id=\"tips[" + tupleCount + "]\" title=\"This is the help text to display to users\" value=\"\" /> ";
        text += "<b>Values:</b> <input type=\"text\" size=\"30\" id=\"vals[" + tupleCount + "]\" title=\"A list of one or more default values. Separate values with a semicolon ';'\" value=\"\" /> ";
        text += "<button onclick=\"removeTuple(this)\"> - </button><br>";
        text += "</div>";

        $("#tuples").append(text);
        tupleCount++;
      }

      function removeTuple(obj) {
        $(obj).parent().remove();
      }
    </script>
  </head>
  <body>
    <#include "common/navbar.ftl">
    <#if report.id??><b>ID:</b> <input type="text" size="30" id="id" value="${report.id}" readonly="true" /><br></#if>
    <#if report.id??><b>Date Created:</b> <input type="text" size="30" id="created" value="${report.created?string("yyyy-MM-dd HH:mm:ss")}" readonly="true" /><br></#if>
    <#if report.id??><b>Date Modified:</b> <input type="text" size="30" id="modified" value="${report.modified?string("yyyy-MM-dd HH:mm:ss")}" readonly="true" /><br></#if>
    <b>Name:</b> <input type="text" size="30" id="name" value="${(report.name)!""}" /><br>
    <b>Author:</b> <input type="text" size="30" id="author" value="${(report.author)!""}" /><br>
    <b>Language:</b> <select id="languages"><#list getLanguages() as language><option value="${language}">${language}</option></#list></select><br>
    <b>Description:</b><br>
    <textarea rows="5" cols="80" id="description">${(report.description)!""}</textarea></p><br>
    <p><b>Template:</b><br>
    <textarea rows="50" cols="80" id="template">${(report.template)!""}</textarea></p><br>
    <p><b>Script:</b><br>
    <textarea rows="50" cols="80" id="script">${(report.script)!""}</textarea></p><br>
    <p><b>Default Parameters:</b>
    <button onclick="addTuple()"> + </button>
    <div id="tuples">
      <#list report.defaultParameters?keys as key>
      <#assign paramObj=report.defaultParameters?values[key_index]>
      <div id="tuple[${key_index}]">
        <b>Key:</b> <input type="text" size="15" id="keys[${key_index}]" title="This is the name of the variable binding in the script engine" value="${key}" />
        <b>Type:</b> <select id="types[${key_index}"]><#list getParamTypes(paramObj) as type><option value="${type}">${type}</option></#list></select>
        <b>Tooltip:</b> <input type="text" size="30" id="tips[${key_index}]" title="This is the help text to display to users" value="${paramObj.tooltip}" />
        <b>Values:</b> <input type="text" size="30" id="vals[${key_index}]" title="A list of one or more default values. Separate values with a semicolon ';'" value="${paramObj.values?join("; ")}" />
        <button onclick="removeTuple(this)"> - </button>
      </div>
      </#list>
    </div></p>
    <p>
    <button onclick="doPost()">Save</button>
    <#if report.id??><button onclick="doDelete()">Delete</button></#if>
    </p>
  </body>
</html>
