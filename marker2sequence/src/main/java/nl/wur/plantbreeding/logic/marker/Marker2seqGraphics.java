/*
 *  Copyright 2011, 2012 Plant Breeding, Wageningen UR.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package nl.wur.plantbreeding.logic.marker;

import nl.wur.plantbreeding.datatypes.MarkerSequence;
import nl.wur.plantbreeding.datatypes.Sequence;
import nl.wur.plantbreeding.datatypes.Markerws;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import org.biomoby.shared.MobyException;
import org.biomoby.shared.datatypes.GeneticMarker;

/**
 * This class handles the generation of the graphic for the marker2sequence
 * tool.
 * It generates a picture of the genetic map aligned with the physical map using
 * the markers coordinates on both.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class Marker2seqGraphics {

    /**
     * Width of the whole picture.
     * Length of the picture is calculated on the fly
     */
    public final static Integer PICTUREWIDTH = 700;
    /**
     * Left and right margin of the picture (needed for the text for example).
     */
    private Integer margin = 20;
    /**
     * Top margin (added to the top).
     */
    private final static Integer TOPMARGIN = 5;
    /**
     * Right margin (added to the right).
     */
    private final static Integer RIGHTMARGIN = 5;
    /**
     * How many pixel is one unit (here likely cM)
     */
//    private Integer step = 5;
    /**
     * Space between 2 scafolds on the picture.
     */
    private Integer verticalspace = 20;
    /**
     * Distance between the end of the bars and the text.
     */
    private Integer spacebartext = 25;
    /**
     * Heigth of the map bar.
     */
    private final static Integer MAPHEIGTH = 10;
    /**
     * Heigth of the marker bar on the map.
     */
    private final static Integer MARKERHEIGTH = MAPHEIGTH * 2 + 5;
    /**
     * Top position for the sequences.
     */
    private final static Integer SEQUENCETOP = MARKERHEIGTH * 3;
    /**
     * Heigth of the sequence bar.
     */
    private Integer sequenceheigth = 5;
    /**
     * Contains a list of string representing the map of the picture.
     */
    private final ArrayList<String> map = new ArrayList<String>();
    /**
     * String used in the map for the marker.
     */
    private final String markermap = "<area title=\"%s\" shape =\"rect\" "
            + "coords=\"%s,"
            + "%s,%s,%s\" href=\"http://solgenomics.wur.nl/search/markers/"
            + "markersearch.pl?w822_submit=Search&amp;w822_nametype=starts+"
            + "with&w822_marker_name=%s\" target=\"_blank\" />";
    /**
     * String used in the map for the sequence.
     */
    private final String sequencemap = "<area title=\"%s\" shape =\"rect\" "
            + "coords=\""
            + "%s,%s,%s,%s\" href=\"http://solgenomics.wur.nl/gbrowse/bin/"
            + "gbrowse/ITAG1_genomic/?name=%s\" target=\"_blank\" />";
    /**
     * String used in the map for the marker.
     */
    private final String mapmap = "<area title=\"%s\" shape =\"rect\" "
            + "coords=\"%s,%s,%s,%s\" href=\"%s\" target=\"_blank\" />";
    /**
     * Size of the squares in the legend.
     */
    private final Integer legendsquare = 7;
    /**
     * Heigth between the element of the legend.
     */
    private final Integer legendheigth = 13;
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            Marker2seqGraphics.class.getName());

    /**
     * This function generates the full image.
     * @param markers a list of GeneticMarkers
     * @param sequences a list of MarkerSequence
     * @param gr a Graphics2D object to draw the picture
     * @param legend a HashMap of color for the legen
     * @param entrymarkers a list of Markers
     * @throws MobyException when something happens with the web-service
     */
    public final void drawImage(final List<GeneticMarkers> markers,
            final List<MarkerSequence> sequences,
            final Graphics2D gr,
            final HashMap<String, Color> legend,
            final List<String> entrymarkers) throws MobyException {

        final float startpoint =
                new Float(markers.get(0).getMoby_position().getFloatValue());
        final float endpoint =
                new Float(markers.get(markers.size() - 1).getMoby_position().
                getFloatValue());

        // Set Font
        gr.setFont(new Font("Serif", Font.PLAIN, 12));
        // Calculate the y Top position
        Integer top = gr.getFontMetrics().stringWidth(
                this.getLonguestMarker(markers)) + TOPMARGIN;
        // Calculate the width on the map
        final Integer width = Marker2seqGraphics.PICTUREWIDTH
                - gr.getFontMetrics().stringWidth(
                this.getLonguestSequenceName(sequences))
                - spacebartext - RIGHTMARGIN;
        final Integer mapstep = (int) (width / (endpoint - startpoint));

        // Draw the baseline for the map
        gr.setColor(Color.gray);
        gr.drawRect(margin, //left
                top, //top
                (int) (mapstep * (endpoint - startpoint)), // width
                MAPHEIGTH); // heigth

        // Print the Chr # beside the map
        if (markers.get(0).getMoby_Chromosome() != null) {
            gr.drawString("LG: "
                    // String
                    + markers.get(0).getMoby_Chromosome().getIntValue(),
                    width + margin + spacebartext, //x
                    top + MAPHEIGTH);// y
        }

        // Draw the marker on the map
        ArrayList<Double> position = new ArrayList<Double>();
        for (GeneticMarkers marker : markers) {
            if (entrymarkers.contains(marker.getName())) {
                if (position.contains(
                        marker.getMoby_position().getFloatValue())) {
                    this.drawMarker(gr, marker, startpoint, top, mapstep,
                            true, Color.red, true);
                    position.add(marker.getMoby_position().
                            getFloatValue() + 10);
                } else {
                    this.drawMarker(gr, marker, startpoint, top, mapstep,
                            true, Color.red, false);
                }

            } else if (markers.indexOf(marker) == 0
                    || markers.indexOf(marker) == markers.size() - 1) {
                this.drawMarker(gr, marker, startpoint, top, mapstep,
                        true, Color.black, false);
            } else if (!new Float(marker.getMoby_position().
                    getFloatValue()).equals(startpoint)
                    && !new Float(marker.getMoby_position().
                    getFloatValue()).equals(endpoint)
                    && !position.contains(marker.getMoby_position().
                    getFloatValue())) {
                this.drawMarker(gr, marker, startpoint, top, mapstep,
                        true, Color.black, false);
            }
            position.add(marker.getMoby_position().getFloatValue());
        }

        top += SEQUENCETOP;
        // Draw the sequences on the map
        for (MarkerSequence scafold : sequences) {
            if (scafold.getMarkers().size() == 1) {
                //RF: do not draw when size==1
//       this.drawMSequence(gr, scafold, markers, sequences.indexOf(scafold),
//                        width, top, mapstep, startpoint, Color.pink);
            } else {
                this.drawMSequence(gr, scafold, markers, sequences.indexOf(
                        scafold),
                        width, top, mapstep, startpoint, Color.blue);
            }
        }

        // Print the Legend:
        final Integer toplegend = top + verticalspace * (sequences.size() + 1)
                + 10;
        this.drawLegend(gr, toplegend, legend);
    }

    /**
     * This functions prints the marker name and the bar localizing them on the
     * map on the picture.
     * It also handles the entry of the string used in the map
     * @param gr a Graphics2D object for drawing
     * @param marker a GeneticMarker
     * @param startpoint a starting point
     * @param top a top position
     * @param step a step interval
     * @param showtext a boolean to set the display of the text
     * @param color a Color
     * @param shift a boolean to shift the text
     * @throws MobyException when something happens
     */
    public final void drawMarker(Graphics2D gr,
            final GeneticMarkers marker,
            final float startpoint,
            final Integer top,
            final Integer step,
            final boolean showtext,
            final Color color,
            final boolean shift)
            throws MobyException {

        final Integer offset = -4;
        gr.setColor(color);
        final Float position =
                new Float(marker.getMoby_position().getFloatValue());
        // Draw the marker position
        gr.fillRect((int) ((position - startpoint) * step + margin), //left
                top - 2, //top
                1, // width
                MARKERHEIGTH); // heigth

        final AffineTransform oldAt = gr.getTransform();
        gr.rotate(-Math.PI / 2.0);
        // Print the marker name
        if (showtext) {
            // x and y are inverted since the orientation if changed
            if (shift) {
                gr.drawString(marker.getName(), // String
                        5 - top, //x
                        (position - startpoint) * step + margin + 13);// y
                gr.drawString(position.toString(), // String
                        - 8 - top - margin - MARKERHEIGTH, //x
                        (position - startpoint) * step + margin);// y
            } else {
                gr.drawString(marker.getName(), // String
                        5 - top, //x
                        (position - startpoint) * step + margin);// y
                gr.drawString(position.toString(), // String
                        - 8 - top - margin - MARKERHEIGTH, //x
                        (position - startpoint) * step + margin);// y
            }

            // Add the string to the list to link the marker name
            String mapstring;
            Integer xtop = ((int) (position - startpoint) * step
                    + gr.getFontMetrics().getHeight());
            if (shift) {
                xtop += 13;
            }
            xtop += offset;
            Integer ytop = top - gr.getFontMetrics().stringWidth(
                    marker.getName()) - 4;
            Integer xbottom = xtop + 10;
            Integer ybottom = ytop + gr.getFontMetrics().stringWidth(
                    marker.getName());
            mapstring = String.format(markermap, marker.getName(),
                    xtop, ytop, xbottom, ybottom, marker.getName());
            map.add(mapstring);
            // Print the marker position
        } else {
            // x and y are inverted since the orientation if changed
            gr.drawString(position.toString(), // String
                    5 - top, //x
                    (position - startpoint) * step + margin);// y

            // Add the string to the list to link the marker name
//            String mapstring;
//            Integer xtop = (int) ((marker.getMap_position() - startpoint)
//            * step + gr.getFontMetrics().getHeight());
//            Integer ytop = top - gr.getFontMetrics().stringWidth(
//                        marker.getName()) -4;
//            Integer xbottom = xtop + 10;
//            Integer ybottom = ytop + gr.getFontMetrics().stringWidth(
//                        marker.getName());
//             FIXME: link me to something !
//            mapstring = String.format(mapmap, marker.getMap_position(
//                            ).toString(), xtop, ytop, xbottom, ybottom,
//                                marker.getName());
//            map.add(mapstring);
        }
        gr.setTransform(oldAt);

    }

    /**
     * This function draw the sequence bar, add their name, add the gradient
     * at their end representing the uncertainty of the end of the sequence.
     *
     * @param gr a Graphics2D object
     * @param sequence a MarkerSequence object
     * @param markers a list of GeneticMarker
     * @param index an integer
     * @param width a width size
     * @param top a top position
     * @param step a step interval
     * @param startpoint the starting point
     * @param color the Color
     * @throws MobyException when something goes bad
     */
    public final void drawMSequence(final Graphics2D gr,
            final MarkerSequence sequence,
            final List<GeneticMarkers> markers,
            final Integer index,
            final Integer width,
            final Integer top,
            final Integer step,
            final float startpoint,
            final Color color) throws MobyException {

        gr.setColor(color);

        // Calculate the number of cM/nucleotide
        final float mapdis = sequence.getEndmarker().getMap_position()
                - sequence.getStartmarker().getMap_position();
        final float nucdis = Math.abs(this.getSequenceLength(sequence));
        final float range = mapdis / nucdis;

        // Get the start and stop position of the sequence
        // (based on the markers)
        final Integer start = (int) ((sequence.getStartmarker().
                getMap_position()
                - new Float(markers.get(0).getMoby_position().getFloatValue()))
                * step);
        final Integer end = (int) ((sequence.getEndmarker().getMap_position()
                - sequence.getStartmarker().getMap_position()) * step);

        // Draw left end of the sequence
        if (this.getPreviousMarker(markers,
                sequence.getStartmarker()) != null) {
            // get the previous marker
            final GeneticMarker previousmarker = this.getPreviousMarker(markers,
                    sequence.getStartmarker());
            // calcul the interval between the first marker and the previous
            // one and divide it by 2
            final Integer interval = (int) Math.ceil(
                    (sequence.getStartmarker().getMap_position()
                    - new Float(previousmarker.getMoby_position().
                    getFloatValue())) * step / 2);
            // calculate the starting x position
            final Integer startx = start + margin;
            // calculate the y position
            final Integer y = top + verticalspace * (index + 1);
            // Generate the gradient. Color depending onscafold vs bac
            // and on one or more markers in the sequence
            GradientPaint gradient;
            if (sequence.getMarkers() != null
                    && sequence.getMarkers().size() == 1) {
                gradient = new GradientPaint(startx - interval, y, Color.white,
                        startx, y, Color.pink, false);
            } else {
                gradient = new GradientPaint(startx - interval, y, Color.white,
                        startx, y, color, false);
            }
            // set the gradient
            gr.setPaint(gradient);
            // paint the rectangle where the gradient should appear
            gr.fillRect(startx - interval, //left
                    y, //top
                    interval, // width
                    sequenceheigth); // heigth
        }

        // Draw right end of the sequence
        if (this.getNextMarker(markers, sequence.getEndmarker()) != null) {
            // get the next marker
            final GeneticMarker nextmarker =
                    this.getNextMarker(markers, sequence.getEndmarker());
            // calcul the interval between the last marker and
            // the next one and divide it by 2
            final Integer interval = (int) Math.ceil((new Float(
                    nextmarker.getMoby_position().getFloatValue())
                    - sequence.getEndmarker().getMap_position()) * step / 2);
            // calculate the starting x position
            final Integer startx = start + margin + end;
            // calculate the y position
            final Integer y = top + verticalspace * (index + 1);
            GradientPaint gradient;
            // Generate the gradient. Color depending onscafold vs bac and
            // on one or more markers in the sequence
            if (sequence.getMarkers() != null
                    && sequence.getMarkers().size() == 1) {
                gradient = new GradientPaint(startx, y, Color.pink,
                        startx + interval, y, Color.white, false);
            } else {
                gradient = new GradientPaint(startx, y, color,
                        startx + interval, y, Color.white, false);
            }
            // set the gradient
            gr.setPaint(gradient);
            // paint the rectangle where the gradient should appear
            gr.fillRect(startx, //left
                    y, //top
                    interval, // width
                    sequenceheigth); // heigth
        }

        gr.setColor(color);

        // Draw the sequence bar ( /!\ The bar should always be
        // drawn after the gradient )
        gr.fillRect(start + margin, //left
                top + verticalspace * (index + 1), //top
                end, // width
                sequenceheigth); // heigth

        // Draw sequence name
        gr.drawString(sequence.getName(),//String
                width + spacebartext,//x
                top + verticalspace * (index + 1) + 5);// y


        // Link the markers on the sequence to the map
        gr.setColor(Color.black);

        for (Markerws marker : sequence.getMarkers()) {
            final Float position =
                    new Float(markers.get(0).getMoby_position().
                    getFloatValue());
            if (marker.getSeq_position_start() != null) {
                final Integer xmap = (int) ((marker.getMap_position()
                        - startpoint)
                        * step + margin);
                final Integer ymap = top - verticalspace;
                Integer xseq = margin;
                if (marker.getName().equals(sequence.getStartmarker().
                        getName())) {
                    xseq += (int) ((marker.getMap_position() - position)
                            * step);
                } else {
                    final float distancemarker = Math.abs(
                            marker.getSeq_position_end()
                            - sequence.getStartmarker().
                            getSeq_position_start());
                    xseq += (int) (((distancemarker * range)
                            + sequence.getStartmarker().getMap_position()
                            - position) * step);
                }
                final Integer yseq = top + verticalspace * (index + 1);
                gr.drawLine(xmap, ymap, xseq, yseq);
            }
        }


        // Add the string to the list/map to link the sequence name
        String mapstring;
        final Integer xtop = width + spacebartext;
        final Integer ytop = top + verticalspace * (index + 1) + 5
                - gr.getFontMetrics().getHeight();
        final Integer xbottom = xtop + gr.getFontMetrics().stringWidth(
                sequence.getName());
        final Integer ybottom = ytop + gr.getFontMetrics().getHeight();
        mapstring = String.format(sequencemap, sequence.getName(),
                xtop, ytop, xbottom, ybottom, sequence.getName());
        map.add(mapstring);

    }

    /**
     * Draw the legend at the bottom of the picture.
     * @param gr a Graphics2D object for drawing
     * @param top a top position
     * @param legend a HashMap for the legend
     */
    public final void drawLegend(
            final Graphics2D gr,
            final Integer top,
            final HashMap<String, Color> legend) {
        final Set<String> keys = legend.keySet();
        final Iterator<String> k = keys.iterator();
        int cnt = 0;
        while (k.hasNext()) {
            final String text = k.next();

            gr.setColor(legend.get(text));
            gr.fillRect(margin, //left
                    top + cnt * legendheigth, //top
                    legendsquare, // width
                    legendsquare); // heigth

            gr.setColor(Color.black);
            gr.drawString(text, // String
                    margin + spacebartext,//x
                    top + cnt * legendheigth + legendsquare);// y

            cnt = cnt + 1;
        }
    }

    /**
     * Returns the longuest sequence name.
     * Useful to estimate the size need on the left of the picture
     * @param sequences a list of MarkerSequence
     * @return a string of the longuest sequence name
     */
    public final String getLonguestSequenceName(
            final List<MarkerSequence> sequences) {
        String out = "";
        char[] max = new char[0];
        for (Sequence sequence : sequences) {
            if (sequence.getName().toCharArray().length > max.length) {
                max = sequence.getName().toCharArray();
                out = sequence.getName();
            }
        }
        return out;
    }

    /**
     * Returns the longuest marker name.
     * Useful to estimate the size need on the top of the picture
     * @param markersinfo
     * @return
     */
//    public String getLongestMarker(List<Markerws> markersinfo) {
//        String out = "";
//        char[] max = new char[0];
//        for (Markerws marker : markersinfo) {
//            if (marker.getName().toCharArray().length > max.length) {
//                max = marker.getName().toCharArray();
//                out = marker.getName();
//            }
//        }
//        return out;
//    }
    /**
     * Returns the longuest marker name.
     * Useful to estimate the size need on the top of the picture
     * @param markersinfo a list og GeneticMarkers
     * @return a string of the longuest marker name
     */
    public final String getLonguestMarker(
            final List<GeneticMarkers> markersinfo) {
        String out = "";
        char[] max = new char[0];
        for (GeneticMarker marker : markersinfo) {
            if (marker.getName().toCharArray().length > max.length) {
                max = marker.getName().toCharArray();
                out = marker.getName();
            }
        }
        return out;
    }

    /**
     * Returns the height of the map bar (space between the two bar of the map)
     * @return Interger mapheigth
     */
    public final Integer getMapheigth() {
        return MAPHEIGTH;
    }

    /**
     * Return the margin parameters.
     * @return the Integer of the margin
     */
    public final Integer getMargin() {
        return margin;
    }

    /**
     * Set the margin parameters.
     * @param marg an integer that will be used as margin
     */
    public final void setMargin(final Integer marg) {
        this.margin = marg;
    }

    /**
     * Returns the height of the marker bar on the map (the vertical bar)
     * @return Integer markerheigth
     */
    public final Integer getMarkerheigth() {
        return MARKERHEIGTH;
    }

    /**
     * Get the sequence height.
     * @return an Integer of the sequence heigth
     */
    public final Integer getSequenceheigth() {
        return sequenceheigth;
    }

    /**
     * Set the sequence heigth.
     * @param sequenceh a integer of the sequence heigth
     */
    public final void setSequenceheigth(final Integer sequenceh) {
        this.sequenceheigth = sequenceh;
    }

    /**
     * Get the distance between the end of the bars and the text.
     * @return the integer of the distance between the end of the bars and the
     * text.
     */
    public final Integer getSpace_bar_text() {
        return spacebartext;
    }

    /**
     * Set the distance between the end of the bars and the text.
     * @param space_bar_text an integer of the distance between the end of the
     * bars and the text.
     */
    public final void setSpace_bar_text(final Integer space_bar_text) {
        this.spacebartext = space_bar_text;
    }

//    public Integer getStep() {
//        return step;
//    }
//
//    public void setStep(Integer step) {
//        this.step = step;
//    }
    /**
     * Get the space between 2 scafolds on the picture.
     * @return the integer of the space between 2 scafolds on the picture
     */
    public final Integer getVerticalspace() {
        return verticalspace;
    }

    /**
     * Set the space between 2 scafolds on the picture.
     * @param verticalsp an integer defining the space between 2 scafolds on
     * the picture.
     */
    public final void setVerticalspace(Integer verticalsp) {
        this.verticalspace = verticalsp;
    }

    /**
     * Get a list of string representing the map of the picture.
     * @return a ArrayList of string representing the map used on the picture
     */
    public final ArrayList<String> getMap() {
        return map;
    }

    /**
     * Get the top position for the sequences.
     * @return the integer of the top position for the sequences.
     */
    public final Integer getSequencetop() {
        return SEQUENCETOP;
    }

    /**
     * For a given marker returns the marker at the next position on the map.
     * @param markers a list of GeneticMarkers
     * @param from a Markerws object
     * @return the GeneticMarker which follows the given markers (from) in the
     * given list (markers)
     */
    private GeneticMarker getNextMarker(
            final List<GeneticMarkers> markers,
            final Markerws from)
            throws MobyException {
        boolean flag = false;
        for (GeneticMarker marker : markers) {
            if (flag && Math.ceil(marker.getMoby_position().getFloatValue())
                    != Math.ceil(from.getMap_position())) {
                return marker;
            }
            if (marker.getName().equals(from.getName())) {
                flag = true;
            }
        }
        return null;
    }

    /**
     * For a given marker returns the marker at the previous position on the
     * map.
     * @param markers a list og GeneticMarkers
     * @param from a Markerws
     * @return a GeneticMarker which is just before the given Markerws (from) in
     * the given list (markers)
     */
    private GeneticMarker getPreviousMarker(
            final List<GeneticMarkers> markers,
            final Markerws from)
            throws MobyException {
        boolean flag = false;
        int cnt = 0;
        GeneticMarker previous = null;
        for (GeneticMarker marker : markers) {
            if (flag) {
                return previous;
            }
            if (marker.getName().equals(from.getName())) {
                flag = true;
            }
            if (Math.ceil(marker.getMoby_position().getFloatValue())
                    != Math.ceil(from.getMap_position())) {
                previous = marker;
            }
            cnt = cnt + 1;
        }
        if (flag) {
            return previous;
        } else {
            return null;
        }
    }

    /**
     * For a given MarkerSequence this method returns the length of the sequence
     * based on the start and strop coordinate associated.
     * @param sequence a MarkerSequence
     * @return a float corresponding to the length of the given sequence
     */
    private float getSequenceLength(final MarkerSequence sequence) {
        float start = -1;
        float end = -1;
        for (Markerws marker : sequence.getMarkers()) {
            if (start == -1) {
                start = marker.getSeq_position_start();
            }
            if (end == -1) {
                end = marker.getSeq_position_end();
            }

            if (marker.getSeq_position_start() < start) {
                start = marker.getSeq_position_start();
            }
            if (marker.getSeq_position_end() > end) {
                end = marker.getSeq_position_end();
            }
        }
        return end - start;
    }
}
