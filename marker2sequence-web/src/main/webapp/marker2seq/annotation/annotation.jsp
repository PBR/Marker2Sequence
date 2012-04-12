<%-- 
    Document   : annotation
    Created on : Sep 1, 2010, 11:25:02 AM
    Author     : pierrey
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Annotation details" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/annotation_body.jsp" />
</tiles:insert>