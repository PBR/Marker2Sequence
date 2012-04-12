<%--
    Document   : Marker2seq_body
    Created on : Jan 25, 2010, 11:30:07 AM
    Author     : pierrey


<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<html:xhtml/>

<style type="text/css">
    .ui-widget-content{background:none;}
    .ui-widget-header{background:white; border:0px;}
    .ui-widget-content .ui-state-hover, .ui-widget-header .ui-state-hover a,
    .ui-widget-header .ui-state-hover a:hover{
        color:#007EC6; background:none; text-decoration:underline;
    }
    .ui-widget-content a{ color:#007EC6; }
</style>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>

<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <%@page session="true" %>
    <h2><bean:message key="m2s.header"/></h2>

    <html:errors/>

    <logic:present name="graphmap" scope="session">
        <map name="graphmap">
            <logic:iterate id="map" name="graphmap" indexId="counter">
                <bean:write name="map"  filter="false" />
            </logic:iterate>
        </map>
    </logic:present>

    <logic:present name="graphfilename" scope="session">
        <%
                    String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + session.getAttribute("graphfilename");
        %>
        <embed type="image/svg+xml" src="<%= graphURL%>" style="width:1000px; height:1000px">
            Your browser does not support SVG files.
        </embed>
        


    </logic:present>

    <p>
        <bean:message key="m2s.annotation.picture.description"/>
    </p>

</logic:present>
--%>