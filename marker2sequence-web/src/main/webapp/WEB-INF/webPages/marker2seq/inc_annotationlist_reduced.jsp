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

<logic:present name="resultannotation" scope="session">
   <bean:message key="m2s.annotation.table.header"/>
    <display:table name="sessionScope.resultannotation" class="displayTable" export="true"
                   pagesize="100" id="resultrow" >

        <display:setProperty name="export.csv.filename" value="annotation.csv"/>
        <display:setProperty name="export.pdf.filename" value="annotation.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <display:column title="Name" sortable="true" media="csv pdf" property="name" />
        
        <display:column title="Name" sortable="true" media="html">
            <logic:notEmpty name="urlgbrowse">
            <html:link href="${urlgbrowse}${resultrow.name}" target="_blank">
                ${resultrow.name}
            </html:link>
            </logic:notEmpty>
            <logic:empty name="urlgbrowse">
                ${resultrow.name}
            </logic:empty>
        </display:column>

        <display:column title="Start position" property="seq_position_start" sortable="true" />
        <display:column title="Stop position" property="seq_position_end" sortable="true"/>

        <%--Method column changes according to the value in the cell--%>
        <display:column title="Annotation" sortable="false" media="html">
            <c:choose>
                <c:when test="${resultrow.name!=''}" >
                    <c:out value="<a target='_blank' href='annotation.do?geneid=${resultrow.name}'> Details </a> " escapeXml="false" />
                </c:when>
                <c:otherwise>
                    <c:out value="" />
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column title="Description" property="description" sortable="true"/>
        
    </display:table>
</logic:present>
