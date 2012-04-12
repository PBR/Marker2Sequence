<%-- 
    Document   : maplist_include
    Created on : May 3, 2010, 5:58:39 PM
    Author     : pierrey
--%>

<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>

<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>
<c:set var="urlgbrowse" ><bean:message key='m2s.url.gbrowse.${species}'/></c:set>
<c:set var="urlmarkers" ><bean:message key='m2s.url.markers.sgnname.${species}'/></c:set>

<logic:present name="map" scope="session">
    <bean:message key="m2s.map.header"/>
    <display:table name="sessionScope.map" class="displayTable" export="true" pagesize="50" id="maprow">
        
        <display:setProperty name="export.rtf.filename" value="map.rtf"/>
        <display:setProperty name="export.csv.filename" value="map.csv"/>
        <display:setProperty name="export.pdf.filename" value="map.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <display:column title="Marker name" sortable="true" media="csv pdf" property="name" />

        <display:column title="Marker name" sortable="true" media="html">
            <logic:notEmpty name="urlmarkers">
                <html:link href="${urlmarkers}${maprow.name}" target="_blank">
                    ${maprow.name}
                </html:link>
            </logic:notEmpty>
            <logic:empty name="urlmarkers">
                ${maprow.name}
            </logic:empty>
        </display:column>

        <display:column title="Position (cM)" property="map_position" sortable="true" />
        <display:column title="Chromosome" property="chromosome" sortable="false"/>
    </display:table>
</logic:present>