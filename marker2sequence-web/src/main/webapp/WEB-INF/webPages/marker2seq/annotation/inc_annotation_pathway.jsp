<%-- 
    Document   : inc_annotation_pathway
    Created on : Mar 31, 2011, 11:10:04 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<html:xhtml/>

<logic:present name="pathways" scope="request">

    <display:table name="requestScope.pathways" class="displayTable" export="true"
                   pagesize="100" id="row" >

        <display:setProperty name="export.csv.filename" value="literature.csv"/>
        <display:setProperty name="export.pdf.filename" value="literature.pdf"/>
        <display:setProperty name="export.decorated" value="true"/>

        <%--TODO: Manage to not have to declare the columns to have them in the right order--%>
        <display:column title="Id" sortable="true" >
            <html:link href="${row.url}" target="_blank">
                ${row.id}
            </html:link>
        </display:column>
        <display:column title="Title" property="desc" />
    </display:table>
</logic:present>
<logic:notPresent name="pathways" scope="request">
    <p>
    <bean:message key="m2s.annotation-pathways.no-annotation"/>
</p>
</logic:notPresent>