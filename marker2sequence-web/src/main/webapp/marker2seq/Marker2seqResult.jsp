<%--
    Document   : marker2seq.jsp
    Created on : Jan 25, 2010, 11:28:34 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Marker to sequence" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/Marker2seqResult_body.jsp" />
</tiles:insert>