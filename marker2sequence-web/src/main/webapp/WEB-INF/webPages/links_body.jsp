<%--
    Document   : about_body
    Created on : Apr 10, 2009, 7:09:46 PM
    Author     : Richard Finkers
--%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml/>

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
    <div  style="color: red">ERROR:  Application resources not loaded -- check servlet container logs for error messages.</div>
</logic:notPresent>
<logic:present name="org.apache.struts.action.MESSAGE" scope="application">
    <h2><bean:message key="menu.links.heading"/></h2>
    <bean:message key="menu.links.eusol"/>
    <ul>
        <li><html:link href="http://www.eu-sol.net" target="_blank" titleKey="menu.links.eusol.publiek"><bean:message key="menu.links.eusol.publiek"/></html:link></li>
        <li><html:link href="https://portal.wur.nl/sites/EU-SOL/default.aspx" target="_blank" titleKey="menu.links.eusol.intranet"><bean:message key="menu.links.eusol.intranet"/></html:link></li>
        <%--<li><html:link href="http://" target="_blank" titleKey="change"><bean:message key="change"/></html:link></li>--%>
    </ul>
    <li><bean:message key="menu.links.solanaceae"/></li>
    <ul>
        <li><html:link href="http://www.cpgp.ca/" target="_blank" titleKey="project.canadia.potato.genome"><bean:message key="project.canadia.potato.genome"/></html:link></li>
        <li><html:link href="http://www.cbsg.nl" target="_blank" titleKey="project.cbsg"><bean:message key="project.cbsg"/></html:link></li>
        <li><html:link href="http://solgenomics.wur.nl/about/ecosol/" target="_blank" titleKey="project.ecosol"><bean:message key="project.ecosol"/></html:link></li>
        <li><html:link href="http://www.gabipd.org/" target="_blank" titleKey="dbase.gabi"><bean:message key="dbase.gabi"/></html:link></li>
        <li><html:link href="http://cnia.inta.gov.ar/lat-sol/" target="_blank" titleKey="project.latsol"><bean:message key="project.latsol"/></html:link></li>
        <li><html:link href="http://www.gabipd.org/projects/Pomamo/" target="_blank" titleKey="project.pomamo"><bean:message key="project.pomamo"/></html:link></li>
        <li><html:link href="http://solcap.msu.edu" target="_blank" titleKey="project.solcap"><bean:message key="project.solcap"/></html:link></li>
        <li><html:link href="http://solgenomics.wur.nl" target="_blank" titleKey="institute.sgn"><bean:message key="institute.sgn"/></html:link></li>
        <li><html:link href="http://solanaceae.plantbiology.msu.edu/" target="_blank" titleKey="resource.solanacea.genomics.resource"><bean:message key="resource.solanacea.genomics.resource"/></html:link></li>
        <li><html:link href="http://www.nhm.ac.uk/solanaceaesource" target="_blank" titleKey="dbase.solanaceae.source"><bean:message key="dbase.solanaceae.source"/></html:link></li>
        <li><html:link href="http://www.potatogenome.net " target="_blank" titleKey="project.pgsc"><bean:message key="project.pgsc"/></html:link></li>
        <li><html:link href="http://www.tomatoestoday.com" target="_blank" titleKey="resource.tomatoes.today"><bean:message key="resource.tomatoes.today"/></html:link></li>
    </ul>
</logic:present>
