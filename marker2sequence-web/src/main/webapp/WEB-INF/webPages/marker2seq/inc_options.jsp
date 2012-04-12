<%-- 
    Document   : options_includes
    Created on : Feb 2, 2011, 1:50:56 PM
    Author     : Pierre-Yves Chibon <py@chibon.fr>
--%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:xhtml/>

<form action="searchAnnotation.do" method="get" >
    <table>
        <tr>
            <td style="vertical-align:bottom;">
                Search the annotation:
            </td>
            <td>
                <logic:notEqual name="kw" scope="session" value="notok">
                    <input type="text" name="keyword" value="${sessionScope.kw}"
                           style="width:245px" />
                </logic:notEqual>
                <logic:equal name="kw" scope="session" value="notok">
                    <input type="text" name="keyword" style="width:245px" />
                </logic:equal>
                <% String graphURL = request.getContextPath() + "/images/help-contents.png";%>
                <a href="#" id="toggleButton">
                    <img src="<%= graphURL%>" alt="Help" />
                </a>
            </td>
        </tr>
        <tr>
            <td>
                Graph:
            </td>
            <td>
                <select name="graph" style="width:250px">
                    <logic:equal name="type" scope="session" value="go">
                        <option value="go" >GO Distribution</option>
                        <option value="alignment" >Genetic/Physical alignment</option>
                    </logic:equal>
                    <logic:notEqual name="type" scope="session" value="go">
                        <option value="alignment" >Genetic/Physical alignment</option>
                        <option value="go" >GO Distribution</option>
                    </logic:notEqual>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                Restrict the results to the sub-list:
            </td>
            <td>
                <logic:equal name="restricted" scope="session" value="ok">
                    <input type="checkbox" value="1" name="restricted" checked />
                </logic:equal>
                <logic:notEqual name="restricted" scope="session" value="ok">
                    <input type="checkbox" value="1" name="restricted" />
                </logic:notEqual>
            </td>
        </tr>
    </table>
        <div id="ToggleHelpM2S" class="ui-widget-content ui-corner-all">
            <p style="color:green">
		<bean:message key="m2s.help.search.annotation"/>
            </p>
        </div>
    <input type="submit" value="Refresh" />
</form>