<%--
Document   : Plantbreeding navigation
Created on : Apr 10, 2009, 7:01:41 PM
Author     : Richard Finkers
--%>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<!-- start vertical menu -->
<div class="vertMenu">
    <table class="rootVoices vertical" cellspacing='0' cellpadding='0' border='0'>
        <tr><td class="rootVoice {menu: 'empty'}" onclick="window.open('<html:rewrite page='/'/>','_self')"><bean:message key="menu.home"/></td></tr>
        <tr><td class="rootVoice {menu: 'empty'}" onclick="window.open('<html:rewrite page='/marker2seq'/>','_self')"><bean:message key="menu.m2s"/></td></tr>
        <tr><td class="rootVoice {menu:'menu_8'}" onclick="window.open('<html:rewrite page='/links.jsp'/>','_self')"><bean:message key="menu.links"/></td></tr>
        <tr><td class="rootVoice {menu: 'empty'}" onclick="window.open('<html:rewrite page='/about.jsp'/>','_self')"><bean:message key="menu.about"/></td></tr>
    </table>
</div>
<!-- end vertical menu -->

<%--Search menu--%>
<div id="menu_1" class="mbmenu">
    <a rel="title" ><bean:message key="menu.search.heading"/></a>
    <html:link page="/passport/SelectAccessionByAlias.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.search.accession.name"/></html:link>
    <html:link page="/passport/SelectAccessionByAccessionID.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.search.accession.number"/></html:link>
    <html:link page="/passport/SelectAccessionByPhenotype.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.search.phenotype"/></html:link>
        <a rel="separator"> </a>
    <html:link page="/passport/SelectAccessionByPassportData.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.search.passport"/></html:link>
    <html:link page="/passport/ShowAccessionOnGoogleMap.jsp" styleClass="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.search.google.map"/></html:link>
        <a rel="separator"> </a>
    <logic:present name="user">
        <html:link page="/passport/SelectAccessionByFieldNumber.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.search.field.number"/></html:link>
        <html:link page="/passport/seedOrders.jsp"><bean:message key="menu.seed.orders"/></html:link>
    </logic:present>
</div>

<%--Options menu--%>
<div id="menu_2" class="mbmenu">
    <a rel="title" class="{action: 'document.title=(\'menu_2.1\')', img: 'icon_13.png'}"><bean:message key="menu.selections.heading"/></a>
         <html:link page="/experiment/selectPopulation.do" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.select.wizard"/></html:link>
         <logic:notPresent name="userConfig">
             <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.select.change.experiment"/></a>
     <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.select.change.map"/></a>
     <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.experiment.overview"/></a>
    </logic:notPresent>
    <logic:present name="userConfig">
        <html:link page="/experiment/changeExperiment.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.select.change.experiment"/></html:link>
        <logic:present name="mapAvailableFlag">
            <html:link page="/experiment/changeMap.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.select.change.map"/></html:link>
        </logic:present>
        <html:link page="/experiment/initializeExperimentOverview.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.experiment.overview"/></html:link>
    </logic:present>
    <html:link page="/experiment/initializeDetailedOverview.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.database.summary"/></html:link>
    </div>

<%--Documents menu--%>
<div id="menu_3" class="mbmenu">
    <a rel="title" class="{action: 'document.title=(\'menu_2.1\')', img: 'icon_13.png'}"><bean:message key="menu.documents.heading"/></a>
         <logic:present name="user">
             <html:link page="/documents/General MTA CC EUSOL.pdf" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.documents.mta.eusol"/></html:link>
             <html:link page="/documents/General MTA CC Non EUSOL.pdf" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.documents.mta.nonEusol"/></html:link>
             <html:link page="/documents/Descriptors.pdf" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.documents.descriptors"/></html:link>
             <html:link page="/documents/Core-collection phenotyping.ppt" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.documents.ccDescriptors"/></html:link>
         </logic:present>
     </div>

     <%--Markers menu--%>
     <div id="menu_4" class="mbmenu">
         <a rel="title" class="{action: 'document.title=(\'menu_2.1\')', img: 'icon_13.png'}"><bean:message key="menu.markers.heading"/></a>
         <logic:present name="userConfig">
             <html:link page="/marker/MarkerSet.do?type=overview" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.markers.markers"/></html:link>"/>
             <html:link page="/ggtQtl/GraphicalGenotypes.do" styleClass="{img: 'ico_view.gif'}"><bean:message key="ggtQtl.showgraphgenotypes"/></html:link>
             <html:link page="/marker/" styleClass="{menu: 'sub_menu_6', img: 'icon_14.png'}"><bean:message key="menu.markers.export"/></html:link>
             <html:link page="/marker/" styleClass="{menu: 'sub_menu_5', img: 'icon_14.png'}"><bean:message key="menu.markers.diversity"/></html:link>
             <html:link page="/marker/ParentSelection.do" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.markers.selection"/></html:link>"/>
         </logic:present>
     </div>

     <%--Maps menu--%>
     <div id="menu_5" class="mbmenu">
         <a rel="title" ><bean:message key="menu.maps.heading"/></a>
         <logic:notPresent name="userConfig">
             <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.maps.maps"/></a>
     <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.maps.compare"/></a>
     <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.maps.export"/></a>
    </logic:notPresent>
    <logic:present name="userConfig">
        <html:link page="/ggtQtl/ShowMap.do" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.maps.maps"/></html:link>
        <html:link page="/ggtQtl/GraphicalGenotypes.do" styleClass="{img: 'ico_view.gif'}"><bean:message key="ggtQtl.showgraphgenotypes"/></html:link>
        <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.maps.compare"/></a>
        <a class="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.maps.export"/></a>
    </logic:present>
</div>

<%--Accesiations menu--%>
<div id="menu_6" class="mbmenu">
    <a rel="title" ><bean:message key="menu.associations.heading"/></a>
    <html:link page="/trait/visualisationMethods.jsp" styleClass="{menu: 'sub_menu_1', img: 'icon_14.png'}"><bean:message key="menu.data.visualisation"/></html:link>
    <html:link page="/trait/visualisationMethods.jsp" styleClass="{menu: 'sub_menu_2', img: 'icon_14.png'}"><bean:message key="menu.associations.trait.trait"/></html:link>
    <logic:present name="mapAvailableFlag" scope="session">
        <html:link page="/trait/visualisationMethods.jsp" styleClass="{menu: 'sub_menu_4', img: 'icon_14.png'}"><bean:message key="menu.associations.trait.marker"/></html:link>
    </logic:present>
    <logic:present name="metaboliteData">
        <html:link page="/metabolomics/ShowAllDataForMetabolite.jsp" styleClass="{menu: 'sub_menu_4', img: 'icon_14.png',disabled:true}"><bean:message key="menu.associations.trait.metabolite"/></html:link>
        <html:link page="/trait/visualisationMethods.jsp" styleClass="{menu: 'sub_menu_3', img: 'icon_14.png'}"><bean:message key="menu.associations.marker.metabolite"/></html:link>
    </logic:present>
</div>

<%--Tools menu--%>
<div id="menu_7" class="mbmenu">
    <a rel="title" ><bean:message key="menu.tools.heading"/></a>
    <logic:present name="user">
        <html:link page="/upload/upload.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.upload.dataset"/></html:link>
            <a rel="separator"> </a>
            <a class="{menu: 'sub_menu_2', img: '24-tag-add.png',disabled:true}"><bean:message key="menu.download.datasets"/></a>
        <a rel="separator"></a>
        <html:link page="/upload/upload.jsp" styleClass="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.personal.data"/></html:link>
    </logic:present>
</div>
<%--Links menu--%>
<div id="menu_8" class="mbmenu">
    <a rel="title" ><bean:message key="menu.links"/></a>
    <%--TODO: href should not be hardcoded--%>
    <html:link href="http://www.eu-sol.net" target="_blank" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.links.eusol.publiek"/></html:link>
    <html:link page="/links.jsp" styleClass="{menu: 'sub_menu_7', img: 'icon_14.png'}"><bean:message key="menu.links.partners"/></html:link>
    </div>

    <!--Data Visualisation options sub-menu -->
    <div id="sub_menu_1" class="mbmenu">
    <html:link page="/trait/barplotAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.barPlot"/></html:link>
    <html:link page="/trait/boxplotAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.boxPlot"/></html:link>
    <html:link page="/trait/histogramAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.histogram"/></html:link>
    </div>

<%--Trait <->Trait associations sub menu--%>
<div id="sub_menu_2" class="mbmenu">
    <html:link page="/trait/clusterAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.cluster"/></html:link>
    <html:link page="/trait/heatmapAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.heatmap"/></html:link>
    <html:link page="/trait/multipleScatterTraitOneExpAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.multipleScatter"/></html:link>
    <html:link page="/trait/multipleScatterOneTraitExpAnalysis.jsp" styleClass="{img: 'ico_view.gif', disabled:true}"><bean:message key="menu.visualisation.trait.experiment"/></html:link>
    <html:link page="/trait/pcaAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.pca"/></html:link>
    <html:link page="/trait/scatterplotAnalysis.jsp" styleClass="{img: 'ico_view.gif'}"><bean:message key="menu.visualisation.singleScatter"/></html:link>
    </div>


    <!--Marker <-> Metabolite Associations sub menu -->
    <div id="sub_menu_3" class="mbmenu">
    <html:link page="/metabolomics/ShowAllDataForMetabolite.jsp" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.download.metabolite"/></html:link>
    <html:link page="/metabolomics/MetaboliteMarkerRFAnalysis.jsp" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.visualisation.randomForest"/></html:link>
        <a rel="separator"> </a>
    </div>

<%--Trait <-> Marker Associations sub menu--%>
<div id="sub_menu_4" class="mbmenu">
    <html:link page="/ggtQtl/QTLvisualisation.do?method=qtl&qtlMethod=kw" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.kw.visualisation"/></html:link>
    <%--<html:link page="/association/markerTraitAssociation.jsp?associationMethod=AM" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.AM.visualisation"/></html:link>--%>
    <%--<html:link page="/association/markerTraitAssociation.jsp?associationMethod=LD" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.LD.visualisation"/></html:link>--%>
    <a rel="separator"> </a>
</div>

<!--Marker <-> Marker Associations sub menu -->
<div id="sub_menu_5" class="mbmenu">
    <html:link page="/marker/clusterAnalysis.jsp" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.markers.cluster"/></html:link>
    <html:link page="/marker/pcoAnalysis.jsp" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.markers.pco"/></html:link>
        <a rel="separator"> </a>
    </div>

    <!--Marker export sub menu -->
    <div id="sub_menu_6" class="mbmenu">
    <html:link page="/marker/MarkerSet.do?type=download" styleClass="{img: 'buttonfind.gif'}" ><bean:message key="menu.markers.export.joinmap"/></html:link>
    </div>

<%--Partner sites sub menu--%>
<div id="sub_menu_7" class="mbmenu">
    <html:link href="http://www.cpgp.ca/" target="_blank" titleKey="project.canadia.potato.genome"><bean:message key="project.canadia.potato.genome"/></html:link>
    <html:link href="http://www.cbsg.nl" target="_blank" titleKey="project.cbsg"><bean:message key="project.cbsg"/></html:link>
    <html:link href="http://solgenomics.wur.nl/about/SOLANDINO/" target="_blank" titleKey="project.ecosol"><bean:message key="project.ecosol"/></html:link>
    <html:link href="http://www.gabipd.org/" target="_blank" titleKey="dbase.gabi"><bean:message key="dbase.gabi"/></html:link>
    <html:link href="http://cnia.inta.gov.ar/lat-sol/" target="_blank" titleKey="project.latsol"><bean:message key="project.latsol"/></html:link>
    <html:link href="http://www.gabipd.org/projects/Pomamo/" target="_blank" titleKey="project.pomamo"><bean:message key="project.pomamo"/></html:link>
    <html:link href="http://solcap.msu.edu" target="_blank" titleKey="project.solcap"><bean:message key="project.solcap"/></html:link>
    <html:link href="http://solgenomics.wur.nl" target="_blank" titleKey="institute.sgn"><bean:message key="institute.sgn"/></html:link>
    <html:link href="http://solanaceae.plantbiology.msu.edu/" target="_blank" titleKey="resource.solanacea.genomics.resource"><bean:message key="resource.solanacea.genomics.resource"/></html:link>
    <html:link href="http://www.nhm.ac.uk/solanaceaesource" target="_blank" titleKey="dbase.solanaceae.source"><bean:message key="dbase.solanaceae.source"/></html:link>
    <html:link href="http://www.potatogenome.net " target="_blank" titleKey="project.pgsc"><bean:message key="project.pgsc"/></html:link>
    <html:link href="http://www.tomatoestoday.com" target="_blank" titleKey="resource.tomatoes.today"><bean:message key="resource.tomatoes.today"/></html:link>
    </div>
<!-- end menues -->