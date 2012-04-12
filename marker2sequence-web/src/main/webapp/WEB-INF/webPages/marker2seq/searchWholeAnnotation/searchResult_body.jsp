<%--
    Document   : searchResult_body
    Created on : May 30, 2011, 11:54:31 AM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>

<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"  %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@page session="true" %>

<html:xhtml/>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <h2><bean:message key="m2s.search.header"/></h2>
    <html:errors/>

    <div>
        <p><bean:message key="m2s.search.keyword"/></p>
        <html:form action="/marker2seq/searchWholeAnnotation.do">
            <html:text property="keyword" /><br/>
            <html:submit styleId="submitProcessing"/><html:reset/>
        </html:form>
    </div>

    <div>
        <tiles:insert page="/WEB-INF/tiles/empty.jsp">
            <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_annotationlist_reduced.jsp" />
        </tiles:insert>
    </div>
</logic:present>