<%-- 
    Document   : annotation_mips_include
    Created on : Mar 24, 2011, 11:16:53 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>

<c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>
<c:set var="urlgbrowse" ><bean:message key='m2s.url.gbrowse.${species}'/></c:set>

<logic:present name="annotation-mips" scope="request">
    <table style="border:0px">
        <tr>
            <td> Name </td>
            <td><jsp:getProperty name="annotation-mips" property="name" /> </td>
        </tr>
        <tr>
            <td> Description </td>
            <td><jsp:getProperty name="annotation-mips" property="description" /> </td>
        </tr>
        <tr>
            <td> Chromosome </td>
            <td><jsp:getProperty name="annotation-mips" property="scafoldname" /> </td>
        </tr>
        <tr>
            <td> Start </td>
            <td><jsp:getProperty name="annotation-mips" property="seq_position_start" /> </td>
        </tr>
        <tr>
            <td> Stop </td>
            <td><jsp:getProperty name="annotation-mips" property="seq_position_end" /> </td>
        </tr>

        <logic:notEmpty name="urlgbrowse">
            <tr>
                <td> Position</td>
                <td>
                    <a href="${urlgbrowse}${geneid}" target="_blank">gBrowse </a>
                </td>
            </tr>
        </logic:notEmpty>

    </table>
</logic:present>

<logic:notPresent name="annotation-mips" scope="request">
    <p>
        <bean:message key="m2s.annotation-mips.no-annotation"/>
    </p>
</logic:notPresent>