<%-- 
    Document   : index
    Created on : Dec 8, 2009, 2:50:26 PM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Marker to sequence" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/index_body.jsp" />
</tiles:insert>
