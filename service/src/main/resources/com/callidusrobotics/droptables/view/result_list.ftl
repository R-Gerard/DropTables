<#-- @ftlvariable name="" type="com.callidusrobotics.droptables.model.ResultEntry" -->
<#assign currentDate = .now>
<html>
  <head>
    <title>Result History</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script language="javascript">
      function doPut(checkbox, id) {
        var payload = new Object();
        payload.keepForever = checkbox.checked;
        console.log("PUT /results/" + id + " : " + JSON.stringify(payload));
        var request = $.ajax({
          url: "/results/" + id,
          type: "PUT",
          data: JSON.stringify(payload),
          contentType: "application/json",
          dataType: "json"
        });

        request.always( function(data) { window.location.replace("/results"); } );
      }

      function doDelete(id) {
        if (confirm("Really delete this? The action cannot be undone.")) {
          console.log("DELETE /results/" + id);
          var request = $.ajax({
            url: "/results/" + id,
            type: "DELETE",
            contentType: "application/json",
            dataType: "json"
          });

          request.always( function(data) { window.location.replace("/results"); } );
        }
      }
    </script>
    <style>
      table {
        width: 100%;
      }

      table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
      }
    </style>
  </head>
  <body>
    <#if results?has_content>
    <table>
      <tr>
        <td><b>REPORT</b></td>
        <td><b>AUTHOR</b></td>
        <td><b><center>PARAMETERS</center></b></td>
        <td><b>EXECUTED BY</b></td>
        <td><b>DATE CREATED</b></td>
        <td colspan="3"><b><center>ACTIONS</center></b></td>
      </tr>
      <#list results as result>
      <tr>
        <#if result.report??>
        <td><a href="/reports/${result.report.id}">${result.report.name}</a></td>
        <td>${result.report.author}</td>
        <#else>
        <td>DELETED</td>
        <td>UNKNOWN</td>
        </#if>
        <td>${prettyPrintParams(result)}</td>
        <td>${result.user}</td>
        <td>${result.created?string("yyyy-MM-dd HH:mm:ss")}</td>
        <td><input type="checkbox" id="check[${result_index}]" title="${prettyPrintTTL(result, currentDate)}" onclick="doPut(this, '${result.id}')" ${result.shouldKeepForever()?string("checked ", "")}/>Keep These Results Forever</td>
        <td><input type="button" id="delete[${result_index}]" value="Delete" onclick="doDelete('${result.id}')" /></td>
        <td><a href="/results/${result.id}">View</a></td>
      </tr>
      </#list>
    </table>
    </#if>
  </body>
</html>
