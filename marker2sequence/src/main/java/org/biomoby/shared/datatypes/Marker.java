// Marker.java
//
// Generated: Wed Apr 11 16:53:31 CEST 2012
//            (by finke002 on Linux)
//

package org.biomoby.shared.datatypes;

/**
 * <b>Data type name:</b> Marker. <br>
 * <b>Authority:</b> www.plantbreeding.wur.nl <br>
 * <b>Email contact:</b> Richard.Finkers@wur.nl <p>
 *
 * A base class for a Marker <p>
 *
 *  Here is a picture of this data type in the context of its children and parents: <p> 
 *  <img src="doc-files/Marker.png" border=0 alt="Data types graph" usemap="#MobyDataTypes"/> <p> 
 *  <map id="MobyDataTypes" name="MobyDataTypes">
</map>
 <p> 
 *
 * Source code for this object was generated: Wed Apr 11 16:53:31 CEST 2012 (by finke002 on Linux). <p>
 *
 * @see <a href="http://biomoby.org/" target="_top">Biomoby project</a>
 * @see <a href="http://biomoby.org/moby-live/Java/docs/" target="_top">jMoby: Supporting Biomoby in Java</a>
 * @see <a href="http://biomoby.org/moby-live/Java/docs/Moses.html" target="_top">MoSeS generators</a>
 */

public class Marker
    extends MobyObject {

    /** A name of this data type. */
    private static final String DATA_TYPE_NAME = "Marker";



    /**************************************************************************
     * Default constructor.
     *************************************************************************/
    public Marker() {
	super();
    }

    /**************************************************************************
     *
     *************************************************************************/
    public String toString() {
	StringBuilder buf_I_am_sorry_that_this_produces_a_warning = new StringBuilder();
	buf_I_am_sorry_that_this_produces_a_warning.append (super.toString());

	return new String (buf_I_am_sorry_that_this_produces_a_warning.toString());
    }

    /**************************************************************************
     *
     *************************************************************************/
    public org.jdom.Element toXML() {
	org.jdom.Element elem_I_am_sorry_that_this_produces_a_warning = super.toXML();
	elem_I_am_sorry_that_this_produces_a_warning.setName (DATA_TYPE_NAME);

	return elem_I_am_sorry_that_this_produces_a_warning;
    }



}
