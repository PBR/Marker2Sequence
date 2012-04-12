<%-- 
    Document   : graph_godistrib_include
    Created on : Feb 3, 2011, 9:46:20 AM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://struts.apache.org/tags-bean"  prefix="bean" %>
<html:xhtml/>

<div id="tabsimg">
    <ul><!--TODO: resource bundle-->
        <li><a href="#tabsimg-1">GO distribution</a></li>
        <li><a href="#tabsimg-2">GO name space distribution</a></li>
        <li><a href="#tabsimg-3">GO synonym distribution</a></li>
    </ul>

    <div id="tabsimg-1">
        <logic:present name="m2s_go_map" scope="session">
            <map name="godistribmapname">
                <bean:write name="m2s_go_map"  filter="false" />
            </map>
        </logic:present>
        <logic:present name="m2s_go_fn" scope="session">
            <%
                        String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename="
                                + session.getAttribute("m2s_go_fn");
            %>
            <img src="<%= graphURL%>" usemap="#godistribmapname" border=0 alt="Custom generated graph" />
        </logic:present>
        <p> <bean:message key="m2s.gostrib.picture.description" arg0="${sessionScope.m2s_go_gonum}"/> </p>
    </div>

    <div id="tabsimg-2">
        <logic:present name="m2s_go_ns_map" scope="session">
            <map name="gonsdistribmapname">
                <bean:write name="m2s_go_ns_map"  filter="false" />
            </map>
        </logic:present>
        <logic:present name="m2s_go_ns_fn" scope="session">
            <%
                        String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename="
                                + session.getAttribute("m2s_go_ns_fn");
            %>
            <img src="<%= graphURL%>" usemap="#gonsdistribmapname" border=0 alt="Custom generated graph" />
        </logic:present>
        <p> <bean:message key="m2s.gostrib.picture.description.namespace" arg0="${sessionScope.m2s_go_ns_gonum}"/> </p>
    </div>

    <div id="tabsimg-3">
        <logic:present name="m2s_go_syn_map" scope="session">
            <map name="gosyndistribmapname">
                <bean:write name="m2s_go_syn_map"  filter="false" />
            </map>
        </logic:present>
        <logic:present name="m2s_go_syn_fn" scope="session">
            <%
                        String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename="
                                + session.getAttribute("m2s_go_syn_fn");
            %>
            <img src="<%= graphURL%>" usemap="#gosyndistribmapname" border=0 alt="Custom generated graph" />
        </logic:present>
        <p> <bean:message key="m2s.gostrib.picture.description.synonym" arg0="${sessionScope.m2s_go_syn_gonum}"/> </p>
    </div>
</div>