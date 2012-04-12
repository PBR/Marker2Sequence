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

package nl.wur.plantbreeding.www.marker2seq;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;
import nl.wur.plantbreeding.logic.jfreechart.PieChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.general.PieDataset;

/**
 * This class handles the generation of the PieChart we use to present the GO
 * term distribution.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public final class GoDistribution {

    /**
     * Default private constructor.
     */
    private GoDistribution() { }

    /**
     * Generate the Pie chart for a set of given Go terms.
     * @param distrib a HashMap of GO name and GO frequency
     * @param title the title of the GO graph
     * @param url the url used for the tooltips
     * @param session the HttpSession used to save the file
     * @param legend Print the legend or not
     * @param tooltips Add tooltip or not
     * @param urls Add urls or not (link in the graph)
     * @param max Maximum number of item to display
     * @return a list containing the filename and the map for the figure
     * @throws IOException when something happens while writting the image
     */
    public static String[] generateDistribution(
            final HashMap<String, Integer> distrib,
            final String title, final String url, final HttpSession session,
            final boolean legend, final boolean tooltips, final boolean urls,
            final int max)
            throws IOException {

        final PieChart piec = new PieChart();
        final PieDataset piedata = piec.createDataset(distrib);

        final JFreeChart chart = piec.createChart(piedata, title, legend,
                tooltips, urls);
        final PiePlot plot = (PiePlot) chart.getPlot();
        if (distrib.size() >= max) {
            plot.setLabelGenerator(null);
        }
        plot.setURLGenerator(new StandardPieURLGenerator(url, "section"));

        final ChartRenderingInfo info = new ChartRenderingInfo(
                new StandardEntityCollection());

        final String filename = ServletUtilities.saveChartAsPNG(
                chart, 500, 400, info, session);

        final String map = ChartUtilities.getImageMap(filename, info);

        final String[] output = {filename, map};
        return output;
    }

    /**
     * Generate the Pie chart for a set of given Go terms.
     * @param gotermlist of String (here GO names)
     * @param title the title of the GO graph
     * @param url the url used for the tooltips
     * @param session the HttpSession used to save the file
     * @param legend Print the legend or not
     * @param tooltips Add tooltip or not
     * @param urls Add urls or not (link in the graph)
     * @param max Maximum number of item to display
     * @return a list containing the filename and the map for the figure
     * @throws IOException when something happens while writting the image
     */
    public static String[] generateDistribution(
            final List<String> gotermlist,
            final String title, final String url, final HttpSession session,
            final boolean legend, final boolean tooltips, final boolean urls,
            final int max)
            throws IOException {
        final PieChart piec = new PieChart();
        final PieDataset dataset = piec.createDataset(gotermlist);
        final JFreeChart chart = piec.createChart(dataset, title, legend,
                                                tooltips, urls);
        final PiePlot plot = (PiePlot) chart.getPlot();
        if (gotermlist.size() >= max) {
            plot.setLabelGenerator(null);
        }
        plot.setURLGenerator(new StandardPieURLGenerator(url, "section"));

        final ChartRenderingInfo info = new ChartRenderingInfo(
                new StandardEntityCollection());

        final String filename = ServletUtilities.saveChartAsPNG(
                chart, 500, 400, info, session);

        final String map = ChartUtilities.getImageMap(filename, info);

        final String[] output = {filename, map};
        return output;

    }
}
