// GeneticMarker.java
//
// Generated: Wed Apr 11 16:53:31 CEST 2012
//            (by finke002 on Linux)
//

package org.biomoby.shared.datatypes;

/**
 * <b>Data type name:</b> GeneticMarker. <br>
 * <b>Authority:</b> www.plantbreeding.wur.nl <br>
 * <b>Email contact:</b> Richard.Finkers@wur.nl <p>
 *
 * A base class for a Marker <p>
 *
 *  Here is a picture of this data type in the context of its children and parents: <p> 
 *  <img src="doc-files/GeneticMarker.png" border=0 alt="Data types graph" usemap="#MobyDataTypes"/> <p> 
 *  <map id="MobyDataTypes" name="MobyDataTypes">
<area shape="poly" id="node2" href="Marker.html" title="Marker" alt="" coords="291,72 288,65 279,58 266,53 249,49 231,48 212,49 195,53 182,58 173,65 170,72 173,79 182,86 195,91 212,95 231,96 249,95 266,91 279,86 288,79"/>
</map>
 <p> 
 *
 * Source code for this object was generated: Wed Apr 11 16:53:31 CEST 2012 (by finke002 on Linux). <p>
 *
 * @see <a href="http://biomoby.org/" target="_top">Biomoby project</a>
 * @see <a href="http://biomoby.org/moby-live/Java/docs/" target="_top">jMoby: Supporting Biomoby in Java</a>
 * @see <a href="http://biomoby.org/moby-live/Java/docs/Moses.html" target="_top">MoSeS generators</a>
 */

public class GeneticMarker
    extends Marker {

    /** A name of this data type. */
    private static final String DATA_TYPE_NAME = "GeneticMarker";

    /** An article name for a member of this object. */
    public static final String ARTICLE_NAME_CHROMOSOME = "Chromosome";
    protected MobyInteger Chromosome;

    /** An article name for a member of this object. */
    public static final String ARTICLE_NAME_POSITION = "position";
    protected MobyFloat position;

    /** An article name for a member of this object. */
    public static final String ARTICLE_NAME_SGN_MARKERID = "SGN-MarkerID";
    protected MobyString SGN_MarkerID;



    /**************************************************************************
     * Default constructor.
     *************************************************************************/
    public GeneticMarker() {
	super();
    }

    /**************************************************************************
     *
     *************************************************************************/
    public String toString() {
	StringBuilder buf_I_am_sorry_that_this_produces_a_warning = new StringBuilder();
	buf_I_am_sorry_that_this_produces_a_warning.append (super.toString());
 	if (Chromosome != null)
 	    buf_I_am_sorry_that_this_produces_a_warning.append (Chromosome.format (1));
 	if (position != null)
 	    buf_I_am_sorry_that_this_produces_a_warning.append (position.format (1));
 	if (SGN_MarkerID != null)
 	    buf_I_am_sorry_that_this_produces_a_warning.append (SGN_MarkerID.format (1));

	return new String (buf_I_am_sorry_that_this_produces_a_warning.toString());
    }

    /**************************************************************************
     *
     *************************************************************************/
    public org.jdom.Element toXML() {
	org.jdom.Element elem_I_am_sorry_that_this_produces_a_warning = super.toXML();
	elem_I_am_sorry_that_this_produces_a_warning.setName (DATA_TYPE_NAME);
 	if (Chromosome != null)
 	    elem_I_am_sorry_that_this_produces_a_warning.addContent (Chromosome.toXML());
 	if (position != null)
 	    elem_I_am_sorry_that_this_produces_a_warning.addContent (position.toXML());
 	if (SGN_MarkerID != null)
 	    elem_I_am_sorry_that_this_produces_a_warning.addContent (SGN_MarkerID.toXML());

	return elem_I_am_sorry_that_this_produces_a_warning;
    }

    /**************************************************************************
     * Set a new value to the member (child) 'Chromosome'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     *************************************************************************/
    public void set_Chromosome (MobyInteger value) {
	// add a correct article name
	if (value != null)
	    value.setName (ARTICLE_NAME_CHROMOSOME);
	Chromosome = value;
    }

    /**************************************************************************
     *
     *************************************************************************/
    public MobyInteger getMoby_Chromosome() {
	return Chromosome;
    }

    /**************************************************************************
     * Set a new value to the member (child) 'Chromosome'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     * @see #set_Chromosome(MobyInteger)
     *************************************************************************/
    public void set_Chromosome (long value) {
	    set_Chromosome (new MobyInteger (value));
    }

    /**************************************************************************
     * Return value of 'Chromosome', as a primitive Java type. If
     * 'Chromosome' is empty, return an empty string (not
     * null). <p>
     *
     * If you need access to BioMoby attributes (such as <tt>id</tt>
     * and <tt>namespace</tt>) of 'Chromosome' use rather {@link
     * #getMoby_Chromosome}.
     *************************************************************************/
    public long get_Chromosome() throws org.biomoby.shared.MobyException {
	if (Chromosome == null) return Integer.MIN_VALUE;
	return Chromosome.getIntValue();
    }

    /**************************************************************************
     * Set a new value to the member (child) 'position'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     *************************************************************************/
    public void set_position (MobyFloat value) {
	// add a correct article name
	if (value != null)
	    value.setName (ARTICLE_NAME_POSITION);
	position = value;
    }

    /**************************************************************************
     *
     *************************************************************************/
    public MobyFloat getMoby_position() {
	return position;
    }

    /**************************************************************************
     * Set a new value to the member (child) 'position'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     * @see #set_position(MobyFloat)
     *************************************************************************/
    public void set_position (double value) {
	    set_position (new MobyFloat (value));
    }

    /**************************************************************************
     * Return value of 'position', as a primitive Java type. If
     * 'position' is empty, return an empty string (not
     * null). <p>
     *
     * If you need access to BioMoby attributes (such as <tt>id</tt>
     * and <tt>namespace</tt>) of 'position' use rather {@link
     * #getMoby_position}.
     *************************************************************************/
    public double get_position() throws org.biomoby.shared.MobyException {
	if (position == null) return Float.NaN;
	return position.getFloatValue();
    }

    /**************************************************************************
     * Set a new value to the member (child) 'SGN-MarkerID'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     *************************************************************************/
    public void set_SGN_MarkerID (MobyString value) {
	// add a correct article name
	if (value != null)
	    value.setName (ARTICLE_NAME_SGN_MARKERID);
	SGN_MarkerID = value;
    }

    /**************************************************************************
     *
     *************************************************************************/
    public MobyString getMoby_SGN_MarkerID() {
	return SGN_MarkerID;
    }

    /**************************************************************************
     * Set a new value to the member (child) 'SGN-MarkerID'. It also
     * adds there its registered "article name". <p>
     *
     * @param value to be stored
     * @see #set_SGN_MarkerID(MobyString)
     *************************************************************************/
    public void set_SGN_MarkerID (String value) {
	if (value == null) {
	    SGN_MarkerID = null;
	} else {
	    set_SGN_MarkerID (new MobyString (value));
	}
    }

    /**************************************************************************
     * Return value of 'SGN-MarkerID', as a primitive Java type. If
     * 'SGN-MarkerID' is empty, return an empty string (not
     * null). <p>
     *
     * If you need access to BioMoby attributes (such as <tt>id</tt>
     * and <tt>namespace</tt>) of 'SGN-MarkerID' use rather {@link
     * #getMoby_SGN_MarkerID}.
     *************************************************************************/
    public String get_SGN_MarkerID() {
	if (SGN_MarkerID == null) return "";
	return SGN_MarkerID.getValue();
    }



}
