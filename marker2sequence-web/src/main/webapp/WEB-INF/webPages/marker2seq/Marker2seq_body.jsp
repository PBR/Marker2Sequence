<%--
    Document   : Marker2seq_body
    Created on : Jan 25, 2010, 11:30:07 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>

<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
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
    <h2><bean:message key="m2s.header"/></h2>

    <html:errors/>

    <%-- Insert the graph containing the genetic to physical alignment --%>
    <tiles:insert page="/WEB-INF/tiles/empty.jsp">
        <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_graph_alignment.jsp" />
    </tiles:insert>

    <%-- Insert the options tables --%>
    <tiles:insert page="/WEB-INF/tiles/empty.jsp">
        <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_options.jsp" />
    </tiles:insert>

    <%-- Insert the gene tables without the sub-selection --%>
    <tiles:insert page="/WEB-INF/tiles/empty.jsp">
        <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_genetables_nosub.jsp" />
    </tiles:insert>
    <p><bean:message key="m2s.data.access.${species}" /></p>
</logic:present>