<%--
    Document   : index
    Created on : Mar 28, 2012, 4:56:30 PM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Whole genome annotation search" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/searchWholeAnnotation/index_body.jsp" />
</tiles:insert>
