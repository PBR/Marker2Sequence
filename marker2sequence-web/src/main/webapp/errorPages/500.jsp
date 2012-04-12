<%-- 
    Document   : 500
    Created on : Dec 21, 2009, 1:43:25 PM
    Author     : Richard Finkers
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Internal server error" />
    <tiles:put name="body" value="/WEB-INF/webPages/errorPages/500_body.jsp" />
</tiles:insert>
