<%-- 
    Document   : annotationlist_include
    Created on : May 3, 2010, 5:42:17 PM
    Author     : pierrey
--%>

<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<html:xhtml/>

<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>
<c:set var="urlgbrowse" ><bean:message key='m2s.url.gbrowse.${species}'/></c:set>
<c:set var="urlmarkers" ><bean:message key='m2s.url.markers.name.${species}'/></c:set>

<logic:present name="markers" scope="session">
    <bean:message key="m2s.marker.header"/>
    <display:table name="sessionScope.markers" class="displayTable" export="true"
                   pagesize="100" id="markerrow" >

        <display:setProperty name="export.csv.filename" value="markersfromsgn.csv"/>
        <display:setProperty name="export.pdf.filename" value="markersfromsgn.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <display:column title="Name" sortable="true" media="csv pdf" property="name" />

        <display:column title="Name" sortable="true" media="html">
            <logic:notEmpty name="urlmarkers">
                <logic:notEmpty name="markerrow" property="sgnID" >
                    <html:link href="${urlmarkers}${markerrow.sgnID}" target="_blank">
                        ${markerrow.name}
                    </html:link>
                </logic:notEmpty>
                <logic:empty name="markerrow" property="sgnID" >
                    ${markerrow.name}
                </logic:empty>
            </logic:notEmpty>
            <logic:empty name="urlmarkers">
                ${markerrow.name}
            </logic:empty>
        </display:column>

        <display:column title="Scafold" sortable="true" media="csv pdf" property="scafoldname" />

        <display:column title="Scafold" sortable="true" media="html">
            <logic:notEmpty name="urlgbrowse">
                <html:link href="${urlgbrowse}${markerrow.scafoldname}" target="_blank">
                    ${markerrow.scafoldname}
                </html:link>
            </logic:notEmpty>
            <logic:empty name="urlgbrowse">
                ${markerrow.scafoldname}
            </logic:empty>
        </display:column>

        <display:column title="Start position" property="seq_position_start" sortable="true" />
        <display:column title="Stop position" property="seq_position_end" sortable="true"/>

    </display:table>
</logic:present>