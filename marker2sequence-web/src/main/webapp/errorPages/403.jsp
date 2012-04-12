<%-- 
    Document   : 404
    Created on : Apr 10, 2009, 8:09:45 PM
    Author     : finke002
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="403 - Access to the requested resource has been denied" />
    <tiles:put name="body" value="/WEB-INF/webPages/errorPages/403_body.jsp" />
</tiles:insert>