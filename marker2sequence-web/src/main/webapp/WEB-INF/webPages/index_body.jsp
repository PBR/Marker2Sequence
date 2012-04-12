<%--
    Document   : index_body
    Created on : Apr 10, 2009, 7:09:46 PM
    Author     : finke002
--%>

<%@page session="true" %>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>

<c:set var="theme" ><%=application.getInitParameter("theme")%></c:set>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <h2><bean:message key="welcome.heading.${theme}"/></h2>
    <logic:notPresent name="user">
        <bean:message key="welcome.message.${theme}"/>
    </logic:notPresent>
    <logic:present name="user">
        <bean:message key="welcome.message.authenticated.${theme}"/>
    </logic:present>
    <bean:message key="welcome.last.update"/>
</logic:present>
