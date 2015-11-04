<!DOCTYPE html>
<!--
Licensed Materials - Property of IBM
 
IBM Cognos Products: Embeddable Reporting
 
(C) Copyright IBM Corp. 2014
 
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<html lang="en-US">
	<head>
		<title>IBM Embeddable Reporting Demo</title>
		<!-- Include the report style sheet directly since we're using htmlFragment as the output format -->
		<!-- link rel="stylesheet" type="text/css" href="/ba/cre/~/schemas/GlobalReportStyles_10.css" -->
		<script type="text/javascript">
		    var urlRoot = "/ers/v1/";

		    /* load report styles css asynchronously */
			(function(){
				var lnk = document.createElement("link");
				lnk.rel='stylesheet';
				lnk.type='text/css'; 
				lnk.href = urlRoot + '~/schemas/GlobalReportStyles_10.css';
				var h = document.getElementsByTagName("head");
				h[0].appendChild(lnk);
			})();

		    function getXmlHttp() {
		        if (window.XMLHttpRequest) {
		            return new XMLHttpRequest();
		        } else {
		            return new ActiveXObject("Microsoft.XMLHTTP");
		        }
		    }
		
		    var appURL = "";
		    var appName = "";
		    var xRunLocation = '';
		
		    function changeApp() {
		    	var e = document.getElementById("appList");
		        appURL = e.options[e.selectedIndex].value;
		        appName = e.options[e.selectedIndex].text;
		        document.getElementById("banner").innerHTML = "<div style='width:100%'>" + appName + "</div>";
		        getReportList();
		    }
		    
			/**
			 * retrieve the list of applications.
			 */
			function getApplicationList() {
				var app = getXmlHttp();
				app.open("GET", urlRoot + "packages/", false);
				app.onreadystatechange = function() {
					if (app.readyState == 4 && app.status == 200) {
					    var txt = "<select id='appList' onchange='changeApp()'>";
						var apps = JSON.parse(app.responseText);
						apps.sort(function(a,b) { return a["name"].toLowerCase() > b["name"].toLowerCase() });
						for (var i = 0; i < apps.length; i++) {
						    var appObject = apps[i];
						    txt += "<option value=\"" + appObject["url"] + "\"";
						    if (i == 0) {
						    	txt += " selected";
						    } 
						    txt += ">" + appObject["name"] + "</option>";
						}
						txt += "</select>";
						document.getElementById('apps').innerHTML = txt;
					}
				}
				app.send();
			}
			
			/**
			 * retrieve the list of reports in the application
			 */
		    function getReportList() {
		        var reportList = getXmlHttp();
		        document.getElementById('reportList').innerHTML = '';
		        reportList.open("GET", appURL + "/definitions", true);
		        reportList.onreadystatechange = function() {
		            if (reportList.readyState == 4 && reportList.status == 200) {
		                var txt = "<table style='width:100%'><tr><th>Available Reports<hr/></th><th/></tr>";
		                var reports = JSON.parse(reportList.responseText);
		                reports.sort(function(a,b) { return a["name"].toLowerCase() > b["name"].toLowerCase() });
		                for ( var i = 0; i < reports.length; i++) {
		                    var report = reports[i];
		                    txt += "<tr><td><a style='color: white' href='#' onclick='getReport(\"" + report['id'] + "\", \"" + report['name'] + "\", \"" + report['url'] + "\", \"" + report['type'] + "\")'>";
		                    txt += report['name'];
		                    txt += "</a></td></tr>";
		                }
		                document.getElementById('reportList').innerHTML = txt;
		            }
		        }
		        reportList.send();
	    		document.getElementById('reportOutput').innerHTML = '';
		    }
		
			/**
			 * Run the selected report. use the "phtml" format so that the report is returned as an html fragment that
			 * can be inserted directly into this page.
			 */
		    function getReport(reportID, reportName, url, type) {
                var myDiv = document.getElementById('reportOutput');
                myDiv.innerHTML = "<div style='width:100%; text-align:center'><img src='../images/busy.gif'/></div>"
		    	if (xRunLocation != '') {
		    		deleteRunInstance();
	                xRunLocation = '';
		    	}
                if (type.indexOf("activereport") == -1) {
    		        var report = getXmlHttp();
    		        report.open("GET", url + "/reports/phtml", true); // phtml generates an HTML fragment
    		        report.onreadystatechange = function() {
    		            if (report.readyState == 4) {
    		                myDiv.innerHTML = "<div style='background-color: #3B4B54; color: white'><div style='width:100%; text-align:center'><strong>" + reportName + "</strong></div><hr/></div>" + 
    		                	report.responseText.replace(/\.\.\/\.\.\//g, urlRoot); // TODO: temporary workaround to fix img links
    		                if (report.status == 200) {
    			                xRunLocation = report.getResponseHeader("X-RunLocation");
    		                }
    		            }
    		        }
    		        report.send();
                } else {
    				var ifrm = document.createElement("iframe");
    				ifrm.src = url + "/reports/phtml";
    				ifrm.width = "100%";
    				ifrm.height = "540px";
	                myDiv.innerHTML = "<div style='background-color: #3B4B54; color: white'><div style='width:100%; text-align:center'><strong>" + reportName + "</strong></div><hr/></div>";
	                myDiv.appendChild(ifrm); 
                }
		    }
		    
			/**
			 * Always delete the run instance when you're "finished" with a report run.
			 */
		    function deleteRunInstance() {
		    	var del = getXmlHttp();
		    	del.open("DELETE", xRunLocation, true);
		    	del.send();
		    }
		    
		    function init() {
	    		getApplicationList();
	    		changeApp();
		    }
		</script>
	</head>
	<body style="background-color: #3B4B54;color: white;font-family: HelveticaNeue,Helvetica Neue,HelveticaNeueRoman,HelveticaNeue-Roman,Helvetica Neue Roman,TeXGyreHerosRegular,Helvetica,Tahoma,Geneva,Arial,sans-serif;" onload="init()">
		<table style="width:100%">
			<tr>
				<td><div id="apps" style="text-align:left;font-size:small"></div></td>
				<td><div style="text-align:right;font-size:small"><!-- <a target="_blank" href="/ba/rest-docs/">Rest API Documentation</a> --></div></td>
			</tr>
		</table>
		<table style="width:100%">
			<tr><th id="banner" style="text-align: center"><div style='width:100%'><img src='../images/busy.gif'/></div></th></tr>
		</table>
		<hr/>
		<table style="width:100%">
			<tr>
				<td style="width:25%; vertical-align:text-top">
					<div id="reportList" />
				</td>
				<td style="width:5%">&nbsp;</td>
				<td style="width:100%; vertical-align:top">
					<div id="reportOutput" style="width:100%; background-color: white; color: black"/>
				</td>
			</tr>
		</table>
	</body>
</html>
