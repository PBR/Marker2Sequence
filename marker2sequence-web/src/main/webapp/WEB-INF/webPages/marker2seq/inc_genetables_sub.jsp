<%-- 
    Document   : genetables_sub_include
    Created on : Feb 2, 2011, 3:05:41 PM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"  %>
<%@taglib uri="http://struts.apache.org/tags-bean"  prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:xhtml/>

<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>

<div id="tabs">
    <p>
        <bean:message key="m2s.annotation.description.${species}"/>
    </p>

    <ul>
        <li><a href="#tabs-0"><bean:message key="m2s.result.tab4.title"/></a></li>
        <li><a href="#tabs-1"><bean:message key="m2s.result.tab1.title"/></a></li>
        <li><a href="#tabs-2"><bean:message key="m2s.result.tab2.title"/></a></li>
        <logic:present name="map" scope="session">
            <li><a href="#tabs-3"><bean:message key="m2s.result.tab3.title"/></a></li>
        </logic:present>
    </ul>

    <div id="tabs-0">
        <tiles:insert page="/WEB-INF/tiles/empty.jsp">
            <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_annotationlist_reduced.jsp" />
        </tiles:insert>
    </div>

    <div id="tabs-1">
        <tiles:insert page="/WEB-INF/tiles/empty.jsp">
            <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_annotationlist_full.jsp" />
        </tiles:insert>
    </div>

    <div id="tabs-2">
        <tiles:insert page="/WEB-INF/tiles/empty.jsp">
            <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_markerlist.jsp" />
        </tiles:insert>
    </div>

    <logic:present name="map" scope="session">
        <div id="tabs-3">
            <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/inc_maplist.jsp" />
            </tiles:insert>
        </div>
    </logic:present>

</div>