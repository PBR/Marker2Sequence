<%--
    Document   : Layout of the site
    Created on : Apr 10, 2009, 6:36:51 PM
    Author     : Richard Finkers
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="tiles" uri="http://struts.apache.org/tags-tiles" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>

<c:set var="theme" ><%=application.getInitParameter("theme")%></c:set>

<html:xhtml/>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:foaf="http://xmlns.com/foaf/0.1/">
    <head>
        <!-- favicon -->
        <c:if test='${theme == "CBSGTom" || theme == "CBSGPot"}'>
            <link rel="shortcut icon" type="image/x-icon" href="<html:rewrite page="/images/favicon_cbsg.ico"/>" />
        </c:if>
        <c:if test='${theme == "EU-SOL"}'>
            <link rel="shortcut icon" type="image/x-icon" href="<html:rewrite page="/images/favicon_eu-sol.ico"/>" />
        </c:if>
        <!-- RDF description -->
        <link rel="meta" type="application/rdf+xml" title="FOAF" href="/BreeDB.rdf" />
        <!-- current page -->
        <html:base />
        <!-- meta information -->
        <meta name="keywords" content="EU-SOL, CBSG, Wageningen UR Plantbreeding, tomato, potato" />
        <meta name="description" content="Research &amp; education for the EU-SOL project of the expertise group Wageningen UR Plant Breeding." />
        <meta name="reply-to" content="webmaster.plantbreeding@wur.nl" />
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
        <meta http-equiv="content-language" content="en" />
        <!-- stylesheets -->
        <link href="<html:rewrite page="/css/theme${theme}.css"/>" rel="stylesheet" media="screen" type="text/css" title="Standaard" />
        <!-- page title -->
        <title><tiles:getAsString name="title" /></title>
        <!-- javascript-->
        <script type="text/javascript" src="<html:rewrite page="/js/jquery-1.6.4.min.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery-ui-1.8.16.custom.min.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/mbMenu.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery.metadata.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery.hoverIntent.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/menu.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery.tools.min.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery.blockUI.2.39.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jquery.breeDB.0.1.js"/>"></script>
        <script type="text/javascript" src="<html:rewrite page="/js/jqcloud-0.2.10.min.js"/>"></script>
        <script type="text/javascript">
            function getUserPref() {
                var userType=document.getElementById('username').value;
                return userType;
            }
        </script>
        <!--        FIXME: SHould be generalized. Account ID from context-->
        <c:if test='${theme == "EU-SOL"}'>
            <script type="text/javascript">

                var _gaq = _gaq || [];
                _gaq.push(['_setAccount', 'UA-18743767-3']);
                _gaq.push(['_trackPageview']);

                (function() {
                    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                })();
            </script>
        </c:if>
        <c:if test='${theme == "CBSGTom" || theme == "CBSGPot"}'>
            <script type="text/javascript">

                var _gaq = _gaq || [];
                _gaq.push(['_setAccount', 'UA-18743767-4']);
                _gaq.push(['_trackPageview']);

                (function() {
                    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                })();
            </script>
        </c:if>
    </head>
    <body>
        <div id="canvas">
            <!-- start of the header-->
            <div id="header">
                <c:set var="title" ><tiles:getAsString name="title" /></c:set>
                <jsp:include page="/WEB-INF/tiles/${theme}/header.jsp">
                    <jsp:param name="title" value="${title}" />
                </jsp:include>
            </div>
            <!-- end of the header-->
            <div id="contentcontainer" class="homepage">
                <div id="maincontent">
                    <div id="pagecontainer">
                        <!-- start of the navigation menu -->
                        <div id="context">
                            <jsp:include page="/WEB-INF/tiles/${theme}/navigation.jsp" />
                            <c:if test='${theme == "EU-SOL"}'>
                                <bean:message key="facebook.like"/><br/><br/>
                            </c:if>
                        </div>
                        <!-- end of the navigation menu -->
                        <!-- start of the page bdy -->
                        <div id="content">
                            <tiles:insert attribute="body" />
                        </div>
                        <!-- end of the body -->
                    </div>
                    <!--  end pagecontainer -->
                </div>
                <!--  end maincontent -->
            </div>
            <!--  end contentcontainer -->
            <!-- start of the footer -->
            <div id ="footer">
                <jsp:include page="/WEB-INF/tiles/footer.jsp" />
            </div>
            <!-- end of the footer -->
        </div>
        <!-- End canvas -->
    </body>
</html>
