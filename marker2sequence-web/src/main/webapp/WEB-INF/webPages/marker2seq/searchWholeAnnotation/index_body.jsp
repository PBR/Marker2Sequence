<%--
    Document   : index_body
    Created on : Mar 28, 2012, 4:57:30 PM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page session="true" %>

<html:xhtml/>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>

    <h2><bean:message key="m2s.header"/></h2>
    <html:errors/>
    <p><bean:message key="m2s.wgs.enter.description" /></p>
    <p><bean:message key="m2s.data.access.${species}" /></p>

    <div>
        <html:form action="/marker2seq/searchWholeAnnotation.do">
            <html:text property="keyword" /><br/>
            <html:submit styleId="submitProcessing"/><html:reset/>
        </html:form>
    </div>

</logic:present>