<%-- 
    Document   : GoDistribution_body
    Created on : Jan 26, 2011, 10:44:29 AM
    Author     : Pierre-Yves Chibon
--%>

<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>
<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>

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
    <h2><bean:message key="m2s.godistrib.header"/></h2>

    <html:errors/>

   

    <form name="Filter" action="searchAnnotation.do" >
        Search the annotation <input type="text" name="keyword" />
        <input type="submit" value="Submit" />
    </form>
    <p>
        <bean:message key="m2s.annotation.description.${specie}"/>
    </p>

    <div id="tabs">
       	<ul><!--TODO: resource bundle-->
            <li><a href="#tabs-1">Gene list</a></li>
            <li><a href="#tabs-2">Physical map</a></li>
            <li><a href="#tabs-3">Genetic map</a></li>
        </ul>

        <div id="tabs-1">
            <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_annotationlist.jsp" />
            </tiles:insert>
        </div>

        <div id="tabs-2">
            <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_markerlist.jsp" />
            </tiles:insert>
        </div>

        <div id="tabs-3">
            <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_maplist.jsp" />
            </tiles:insert>
        </div>

    </div>
    <p><bean:message key="m2s.data.access.${species}" /></p>
</logic:present>