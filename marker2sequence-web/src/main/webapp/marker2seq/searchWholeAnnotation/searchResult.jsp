<%--
    Document   : searchResult
    Created on : May 30, 2011, 12:00:42 AM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Annotation search" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/searchWholeAnnotation/searchResult_body.jsp" />
</tiles:insert>

