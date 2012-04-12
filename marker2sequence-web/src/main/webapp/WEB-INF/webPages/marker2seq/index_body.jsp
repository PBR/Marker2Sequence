<%--
    Document   : index_body
    Created on : Feb 18, 2010, 10:20:23 AM
    Author     : Pierre-Yves Chibon -- py@chibon.fr
--%>


<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page session="true" %>

<html:xhtml/>
<script>
    $(function() {
        $( "#accordion" ).accordion();
    });
</script>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>
    
    <h2><bean:message key="m2s.header"/></h2>
    <html:errors/>
    <p><bean:message key="ms.enter.description.${species}" /></p>
    <p><bean:message key="m2s.data.access.${species}" /></p>
    <div id="accordion" style="width:70%;">
	<h3><a href="#">Flanking markers</a></h3>
        <div>
            <p><bean:message key="m2s.enter.marker.${species}"/></p>
            <html:form action="/marker2seq/marker2seq.do">
                <html:text property="marker1" /><br/>
                <html:text property="marker2" /><br/>
                <html:submit styleId="submitProcessing"/><html:reset/>
            </html:form>
        </div>
        <h3><a href="#">Multiple markers</a></h3>
        <div>
            <p><bean:message key="m2s.enter.markers.${species}"/></p>
            <html:form action="/marker2seq/marker2seq.do">
                <html:text property="markers" /><br/>
                <html:submit styleId="submitProcessing"/><html:reset/>
            </html:form>
        </div>
        <h3><a href="#">Coordinates</a></h3>
        <div>
            <p><bean:message key="m2s.enter.coordinate.${species}"/></p>
            <html:form action="/marker2seq/marker2seq.do">
                <html:text property="loci" /><br/>
                <html:submit styleId="submitProcessing"/><html:reset/>
            </html:form>
        </div>
        <h3><a href="#">Map Coordinates</a></h3>
        <div>
            <p><bean:message key="m2s.enter.map.coordinate.${species}"/></p>
            <html:form action="/marker2seq/marker2seq.do">
                <html:text property="maploci" /><br/>
                <html:submit styleId="submitProcessing"/><html:reset/>
            </html:form>
        </div>
    </div>

</logic:present>