<%-- 
    Document   : AuthenticationException
    Created on : Jan 3, 2010, 11:52:58 AM
    Author     : finke002
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="403 - Access to the requested resource has been denied" />
    <tiles:put name="body" value="/WEB-INF/webPages/errorPages/AuthenticationException_body.jsp" />
</tiles:insert>
