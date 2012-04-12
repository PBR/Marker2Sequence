<%-- 
    Document   : GoDistribution
    Created on : Jan 26, 2011, 10:40:37 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<tiles:insert page="/WEB-INF/tiles/layoutPage.jsp">
    <tiles:put name="title" value="Go distribution" />
    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/GoDistribution_body.jsp" />
</tiles:insert>