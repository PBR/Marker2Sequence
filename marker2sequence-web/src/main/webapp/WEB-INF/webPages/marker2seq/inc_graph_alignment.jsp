<%-- 
    Document   : alignment_graph_include
    Created on : Feb 2, 2011, 1:46:43 PM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib uri="http://struts.apache.org/tags-bean"  prefix="bean" %>
<html:xhtml/>


<logic:present name="picturemap" scope="session">
    <map name="marker2sequencemap">
        <logic:iterate id="map" name="picturemap" indexId="counter">
            <bean:write name="map"  filter="false" />
        </logic:iterate>
    </map>
</logic:present>

<logic:present name="m2s_alignment_fn" scope="session">
    <%
                String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename="
                        + session.getAttribute("m2s_alignment_fn");
    %>
    <img src="<%= graphURL%>" usemap="#marker2sequencemap" border=0 alt="Custom generated graph" />
    <p>
        <bean:message key="m2s.annotation.picture.description"/>
    </p>
</logic:present>
<logic:notPresent name="m2s_alignment_fn" scope="session">
    <bean:message key="m2s.annotation.alignment.picture.missing"/>
</logic:notPresent>