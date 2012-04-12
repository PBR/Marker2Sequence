<%--
    Document   : annotation_body
    Created on : Sep 1, 2010, 11:26:27 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>

<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>

<style type="text/css">
    .ui-widget-content{background:none;}
    .ui-widget-header{background:white; border:0px;}
    .ui-widget-content .ui-state-hover, .ui-widget-header .ui-state-hover a,
    .ui-widget-header .ui-state-hover a:hover{
        color:#007EC6; background:none; text-decoration:underline;
    }
    .ui-widget-content a{ color:#007EC6; }
</style>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>

<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <%@page session="true" %>
    <c:set var="species" ><%=application.getInitParameter("m2sSpecies").toLowerCase()%></c:set>

    <h2><bean:message key="m2s.annotation.details.header"/></h2>

    <html:errors/>

    <!-- Annotation from MIPS -->
    <tiles:insert page="/WEB-INF/tiles/empty.jsp">
        <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/inc_annotation_genedesc.jsp" />
    </tiles:insert>

    <div id="tabs">
        <ul>
            <li><a href="#tabs-0"><bean:message key="m2s.annotation.tab1.title"/></a></li>
            <logic:present name="proteins" scope="request">
                <li><a href="#tabs-1"><bean:message key="m2s.annotation.tab2.title"/></a></li>
            </logic:present>
            <logic:present name="pathways" scope="request">
                <li><a href="#tabs-2"><bean:message key="m2s.annotation.tab3.title"/></a></li>
            </logic:present>
            <logic:present name="literature" scope="request">
                <li><a href="#tabs-3"><bean:message key="m2s.annotation.tab4.title"/></a></li>
            </logic:present>
            <logic:equal name="species" value="tomato">
                <li><a href="#tabs-4"><bean:message key="m2s.annotation.tab5.title"/></a></li>
            </logic:equal>

        </ul>

        <div id="tabs-0">

            <!-- Pie chart -->
            <logic:present name="annotationpie" scope="request">
                <%= request.getAttribute("chartmap")%>
                <%
                            String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + request.getAttribute("annotationpie");
                %>
                <img src="<%= graphURL%>" usemap="#<%= request.getAttribute("annotationpie")%>" border="0"  alt="Custom generated graph" />
            </logic:present>

            <!-- Annotation from MPIZ -->
            <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/inc_annotation_genego.jsp" />
            </tiles:insert>

        </div>

        <logic:present name="proteins" scope="request">
            <div id="tabs-1">
                <!-- Proteins information -->
                <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/inc_annotation_uniprot.jsp" />
                </tiles:insert>
            </div>
        </logic:present>

        <logic:present name="pathways" scope="request">
            <div id="tabs-2">
                <!-- pathway information -->
                <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/inc_annotation_pathway.jsp" />
                </tiles:insert>
            </div>
        </logic:present>

        <logic:present name="literature" scope="request">
            <div id="tabs-3">
                <!-- literature information -->
                <tiles:insert page="/WEB-INF/tiles/empty.jsp">
                    <tiles:put name="body" value="/WEB-INF/webPages/marker2seq/annotation/inc_annotation_literature.jsp" />
                </tiles:insert>
            </div>
        </logic:present>
        <logic:equal name="species"  value="tomato">
            <div id="tabs-4">
                <!-- external links -->
                <ul>
                    <li>
                        <a href="http://solgenomics.wur.nl/gbrowse/bin/gbrowse_details/ITAG2_genomic?name=${geneid}" target="_blank">SGN Annotation </a>
                    </li>
                    <li>
                        <a href="http://mips.helmholtz-muenchen.de/proj/plant/jsf/tomato/reportsjsp/geneticElement.jsp?gene=${geneid}" target="_blank">PlantDB Annotation </a>
                    </li>
                    <li>
                        <a href="http://bioinfo.mpiz-koeln.mpg.de/afawe_new/?keyword=${geneid}" target="_blank">AFAWE Annotation </a>
                    </li>
                    <li>
                        <a href="http://bioinformatics.psb.ugent.be/webtools/bogas/annotation/Solly/current/${geneid}" target="_blank">BOGAS Annotation </a>
                    </li>
                </ul>
            </div>
        </logic:equal>
    </div>

</logic:present>