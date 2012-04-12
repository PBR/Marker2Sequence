<%-- 
    Document   : index
    Created on : Jan 27, 2009, 2:56:31 PM
    Author     : finke002
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Welcome to the BreeDB" />
    <tiles:put name="body" value="/WEB-INF/webPages/index_body.jsp" />
</tiles:insert>
<% //TODO: read the title value from the configuration file as this is db specific%>