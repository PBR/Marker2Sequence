<%-- 
    Document   : annotation_uniprot_include
    Created on : Mar 24, 2011, 11:20:04 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<html:xhtml/>

<logic:present name="proteins" scope="request">

    <display:table name="requestScope.proteins" class="displayTable" export="true"
                   pagesize="100" id="row" >

        <display:setProperty name="export.csv.filename" value="proteins.csv"/>
        <display:setProperty name="export.pdf.filename" value="proteins.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <%--TODO: Manage to not have to declare the columns to have them in the right order--%>
        <display:column title="Name" sortable="true" >
            <html:link href="${row.uri}" target="_blank">
                ${row.id}
            </html:link>
        </display:column>
        <display:column title="Description" property="description" sortable="true" />
        <display:column title="Alternative description" property="Alternative description" sortable="true"/>
        <display:column title="Reviewed" property="reviewed" sortable="true"/>
    </display:table>
</logic:present>
<logic:notPresent name="proteins" scope="request">
    <p>
    <bean:message key="m2s.annotation-uniprot.no-annotation"/>
</p>
</logic:notPresent>