<%-- 
    Document   : annotation_mpiz_include
    Created on : Mar 24, 2011, 11:16:05 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>

<html:xhtml/>

<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>
<c:set var="urlgbrowse" ><bean:message key='m2s.url.gbrowse.${species}'/></c:set>

<logic:present name="annotation" scope="request">

    <display:table name="requestScope.annotation" class="displayTable" export="true"
                   pagesize="100" id="row" >

        <display:setProperty name="export.csv.filename" value="annotation.csv"/>
        <display:setProperty name="export.pdf.filename" value="annotation.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <display:column title="Name" sortable="true" >
            <html:link href="http://amigo.geneontology.org/cgi-bin/amigo/term-details.cgi?term=${row.goid}" target="_blank">
                ${row.name}
            </html:link>
        </display:column>
        <display:column title="Process" property="process" sortable="true" />
        <display:column title="Definition" property="definition" sortable="true"/>
        <logic:notEmpty name="urlgbrowse">
            <display:column title="Origin" sortable="true">
                <c:choose>
                    <c:when test="${row.source=='afawe'}" >
                        <c:out value="<a target='_blank' href='http://bioinfo.mpiz-koeln.mpg.de/afawe_new/?keyword=${geneid}'> AFAWE </a> " escapeXml="false" />
                    </c:when>
                    <c:when test="${row.source=='itag'}" >
                        <c:out value="<a target='_blank' href='${urlgbrowse}${geneid}'> ITAG </a> " escapeXml="false" />
                    </c:when>
                    <c:otherwise>
                        ${row.source}
                    </c:otherwise>
                </c:choose>
            </display:column>
        </logic:notEmpty>
    </display:table>
</logic:present>
<logic:notPresent name="annotation" scope="request">
    <p>
        <bean:message key="m2s.annotation-mpiz.no-annotation"/>
    </p>
</logic:notPresent>