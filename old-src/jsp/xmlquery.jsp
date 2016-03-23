<!--
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
-->
<HTML>
  <HEAD>
	 <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=ISO-8859-1">
	 <TITLE>XMLQuery Console</TITLE> 
	<script  language="javascript"  src="sarissa/sarissa.js"></script>
	<script  language="javascript"  src="sarissa/sarissa_ieemu_load.js"></script>
	<script  language="javascript"  src="sarissa/sarissa_ieemu_dhtml.js"></script>
	<script  language="javascript"  src="sarissa/sarissa_ieemu_xpath.js"></script>
	<script  language="javascript"  src="js/prototype.js"></script>
	 
	<SCRIPT LANGUAGE="javascript" TYPE="text/javascript">
	var startTime;
	var endTime;
	var url = '';
	var processComplete = false;
	

		function loadComplete()
		{

			alert('State:' + event.srcElement.readyState);

		}  // loadComplete
		function init()
		{
			url = "/aa4h/xmlqueryrequest";
			XMLResult.document.location=url;
		}  // init
		
		
		function generateUrl(xmlStr) {
			var oDomDoc = Sarissa.getDomDocument();
			var generatedURL = "?";
			oDomDoc = (new DOMParser()).parseFromString(xmlStr, "text/xml");
			if(Sarissa.getParseErrorText(oDomDoc) != Sarissa.PARSED_OK){  
				alert('XML Parsing Failed:' + Sarissa.getParseErrorText(oDomDoc));
			}
			oDomDoc.setProperty("SelectionLanguage", "XPath");	
			var nodeList = oDomDoc.selectNodes("//QueryDoc/Query");
			var serializer = new XMLSerializer();
			for(var i = 0; i < nodeList.length; i++) {
				generatedURL+="query=" + URLEncode(serializer.serializeToString(nodeList[i])) + "&";
			}
			nodeList = oDomDoc.selectNodes("//QueryDoc/NamedQuery");			
			for(var i = 0; i < nodeList.length; i++) {
				generatedURL+="query=" + URLEncode(serializer.serializeToString(nodeList[i])) + "&";
			}			
			return generatedURL;
		}

		function xmlQuery() {
          try {
						var xml = "<QueryDoc>" + $('XMLSource').value + "</QueryDoc>";
						var xurl = url + generateUrl(xml);
						startTime = new Date();
						processComplete = true;
						$('XMLResult').contentWindow.location=xurl;
						//new Ajax.Updater('RESULT', xurl, { method: 'get' });
          } catch (e) {
						 					alert('Error On xmlQuery:' + e);
          }
		}
		function complete() {
			if(processComplete) {
				endTime = new Date();
				var elapsed = endTime - startTime;
				var serverTime = extractInternalElapsed();
				var delivery = elapsed - serverTime;
				
				$('ELAPSED_ID').innerHTML = "<ul><li>Elapsed:&nbsp;" + elapsed + " ms.</li><li>Transport Time:&nbsp;" + delivery + " ms.</li><li>Server Time:&nbsp;" + serverTime + " ms.</li></ul>";
			}
			processComplete=false;	
			
		}
		function extractInternalElapsed() {
			return window.document.getElementById('XMLResult').contentDocument.firstChild.getAttribute('elapsedTime');
		}
		function URLEncode (clearString) {
		  var output = '';
		  var x = 0;
		  clearString = clearString.toString();
		  var regex = /(^[a-zA-Z0-9_.]*)/;
		  while (x < clearString.length) {
		    var match = regex.exec(clearString.substr(x));
		    if (match != null && match.length > 1 && match[1] != '') {
		    	output += match[1];
		      x += match[1].length;
		    } else {
		      if (clearString[x] == ' ')
		        output += '+';
		      else {
		        var charCode = clearString.charCodeAt(x);
		        var hexVal = charCode.toString(16);
		        output += '%' + ( hexVal.length < 2 ? '0' : '' ) + hexVal.toUpperCase();
		      }
		      x++;
		    }
		  }
		  return output;
		}
		
	</SCRIPT>
  </HEAD>
  <BODY onload="init()">
	 <H3>Enter XML Query</H3>
	 <FORM>
		<P><TEXTAREA NAME="XMLSource" ROWS="15" COLS="80" ID="XMLSource" ></TEXTAREA></P>
		<P>
			<INPUT TYPE="BUTTON" NAME="RunXMLQuery" VALUE="Run XMLQuery" ONCLICK="xmlQuery()">&nbsp;
		</P>
		<DIV ID="ELAPSED_ID"></DIV>
		<DIV ID="RESULT"></DIV>
		  <HR>
		  <H3>XML Results</H3>
	     <P><IFRAME NAME="XMLResult" HEIGHT="400" WIDTH="800" ID="XMLResult" onload="complete()"></IFRAME></P>
		  </FORM>
</BODY>
</HTML>
