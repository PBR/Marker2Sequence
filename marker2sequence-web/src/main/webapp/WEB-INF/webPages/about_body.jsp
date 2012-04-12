<%--
    Document   : about_body
    Created on : Apr 10, 2009, 7:09:46 PM
    Author     : Richard Finkers
--%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>

<c:set var="theme" ><%=application.getInitParameter("theme")%></c:set>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <h2><bean:message key="about.${theme}.heading"/></h2>
    <bean:message key="about.${theme}.message"/>
    <h3><bean:message key="about.funding"/></h3>
    <html:link bundle="images" href="http://www.eu-sol.net" target="_bank"><html:img bundle="images" pageKey="eusol.logo" altKey="eusol.alt" height="35"/></html:link><br/>
    <html:link bundle="images" href="http://www.cbsg.nl" target="_bank"><html:img bundle="images" pageKey="cbsg.logo" altKey="cbsg.alt" width="400px" /></html:link><br/>
    <html:link bundle="images" href="http://www.plantbreeding.wur.nl" target="_bank"><html:img bundle="images" pageKey="plantbreeding.logo" altKey="plantbreeding.alt" /></html:link><br/>
    <h3><bean:message key="about.dataProviders"/></h3>
    <ul>
        <c:if test='${theme == "EU-SOL"}'>
            <li><html:link href="http://www.cnr.it/sitocnr/Englishversion/Englishversion.html" target="_blank" titleKey="university.cnr"><bean:message key="university.cnr"/></html:link></li>
            <li><html:link href="http://www.deruiterseeds.nl/" target="_blank" titleKey="company.drs"><bean:message key="company.drs"/></html:link></li>
            <li><html:link href="http://www.enzazaden.nl/" target="_blank" titleKey="company.enza"><bean:message key="company.enza"/></html:link></li>
            <li><html:link href="http://www.hazera.com/english/" target="_blank" titleKey="company.hazera"><bean:message key="company.hazera"/></html:link></li>
            <li><html:link href="http://www.avignon.inra.fr/avignon_eng/" target="_blank" titleKey="institute.intra.avignon"><bean:message key="institute.intra.avignon"/></html:link></li>
            <li><html:link href="http://www.keygene.com" target="_blank" titleKey="company.keygene"><bean:message key="company.keygene"/></html:link></li>
            <li><html:link href="http://www.balkanvegetables.eu/en" target="_blank" titleKey="institute.mvcri"><bean:message key="institute.mvcri"/></html:link></li>
            <li><html:link href="http://www.mpiz-koeln.mpg.de/english/index.html" target="_blank" titleKey="institute.max.planck.cologne"><bean:message key="institute.max.planck.cologne"/></html:link></li>
            <li><html:link href="http://www-en.mpimp-golm.mpg.de/00-news/index.html" target="_blank" titleKey="institute.max.planck.golm"><bean:message key="institute.max.planck.golm"/></html:link></li>
            <li><html:link href="http://www.nunhems.com/" target="_blank" titleKey="company.nunhems"><bean:message key="company.nunhems"/></html:link></li>
        </c:if>
        <li><html:link href="http://www.pri.wur.nl/UK/research/bioscience/" target="_blank" titleKey="institute.pri"><bean:message key="institute.pri"/></html:link></li>
        <c:if test='${theme == "EU-SOL"}'>
            <li><html:link href="http://www.rijkzwaan.nl" target="_blank" titleKey="company.rijkzwaan"><bean:message key="company.rijkzwaan"/></html:link></li>
            <li><html:link href="http://www.rhul.ac.uk/" target="_blank" titleKey="university.rhul"><bean:message key="university.rhul"/></html:link></li>
            <li><html:link href="http://www.semillasfito.com/" target="_blank" titleKey="company.semillas"><bean:message key="company.semillas"/></html:link></li>
            <li><html:link href="http://www.syngenta.com/" target="_blank" titleKey="company.syngenta"><bean:message key="company.syngenta"/></html:link></li>
            <li><html:link href="http://www.agri.huji.ac.il/index-eng.html" target="_blank" titleKey="university.huj"><bean:message key="university.huj"/></html:link></li>
            <li><html:link href="http://www.fabinet.up.ac.za/" target="_blank" titleKey="university.pretoria"><bean:message key="university.pretoria"/></html:link></li>
        </c:if>
        <c:if test='${theme == "CBSGTom"}'>
            <li><html:link href="http://www.fbr.wur.nl/UK/" target="_blank" titleKey="institute.wur.fbr"><bean:message key="institute.wur.fbr"/></html:link></li>
        </c:if>
        <li><html:link href="http://www.plantbreeding.wur.nl/UK/" target="_blank" titleKey="university.wur.pbr"><bean:message key="university.wur.pbr"/></html:link></li>
        <c:if test='${theme == "EU-SOL"}'>
            <li><html:link href="http://www.hpc.wur.nl/UK/" target="_blank" titleKey="university.wur.hpc"><bean:message key="university.wur.hpc"/></html:link></li>
        </c:if>
        <li><html:link href="http://www.biometris.wur.nl/UK/" target="_blank" titleKey="university.wur.biometris"><bean:message key="university.wur.biometris"/></html:link></li>
        <c:if test='${theme == "CBSGPot"}'>
            <li>Dutch Potato Breeding Industry</li>
            <!-- TODO: specify companies  -->
        </c:if>
    </ul>
</logic:present>
