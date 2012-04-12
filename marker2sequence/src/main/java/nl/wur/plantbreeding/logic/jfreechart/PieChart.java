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


package nl.wur.plantbreeding.logic.jfreechart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * This class can be used to generate a pie chart from:
 *  - a matrix with two columns (group, value)
 *  - a hashmap "String" "List" with group as key and the list of values as
 * value
 *  - a hashmap "String", "double" with group as key and number of values as
 * value.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class PieChart {

    /**
     * Create the Pie Chart using the given dataset.
     * It assigns to the chart the given title, prints the legend if asked,
     * adds a tooltips of the given url.
     * @param dataset a PieDataset
     * @param title the title of the graph
     * @param boollegend wether to add the legend or not
     * @param tooltips wether to add the tooltips or not
     * @param urls wether to link the different part of the plot or not
     * @return a JFreeChart object of the pie chart
     */
    public final JFreeChart createChart(
            final PieDataset dataset, final String title,
            final boolean boollegend, final boolean tooltips,
            final boolean urls) {

        JFreeChart chart = ChartFactory.createPieChart(
                title, // chart title
                dataset, // data
                boollegend, // include legend
                tooltips, // include tooltips
                urls // include urls
                );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundAlpha(new Float(0.0));
        plot.setSectionOutlinesVisible(false);
        plot.setNoDataMessage("No data available");
        plot.setOutlineVisible(false); // remove borders around the plot

        return chart;
    }

    /**
     * Create a default Pie Chart for a given dataset and using the default
     * values (add legend, add tooltips and add urls).
     * @param dataset the Dataset to plot
     * @return a JFreeChart object of the pie chart
     */
    public final JFreeChart createChart(final PieDataset dataset) {
        return createChart(dataset, "", true, true, true);
    }

    /**
     * Create a default Pie Chart for a given dataset using the given title and
     * the default values (add legend, add tooltips and add urls).
     * @param dataset the PieDataset to plot
     * @param title the title of the pie chart
     * @return a JFreeChart object of the pie chart
     */
    public final JFreeChart createChart(final PieDataset dataset,
            final String title) {
        return createChart(dataset, title, true, true, true);
    }

    /**
     * Return a PieDataset from a HashMap of String, List where the
     * key is the key used for the pie chart and the size of the list
     * is used for the section of the pie.
     * @param map a HashMap of String: List of String
     * @return a PieDataset
     */
    public final PieDataset createDatasetFromArray(
            final HashMap<String, List<String>> map) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            dataset.setValue(key, new Double((map.get(key)).size()));
        }
        return dataset;
    }

    /**
     * Returns a PieDataset from a List of String.
     * @param list List of String containg all the values
     * @return a PieDataset
     */
    public final PieDataset createDataset(final List<String> list) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        ArrayList<String> done = new ArrayList<String>();
        for (String key : list) {
            if (!done.contains(key)) {
                dataset.setValue(key, this.count(key, list));
            }

        }
        return dataset;
    }

    /**
     * Returns a PieDataset from a HashMap "String", "Integer".
     * @param distrib HashMap containing the key as key and the count as value
     * @return a PieDataset
     */
    public final PieDataset createDataset(
            final HashMap<String, Integer> distrib) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Set<String> keys = distrib.keySet();
        Iterator<String> kit = keys.iterator();
        while (kit.hasNext()) {
            String key = kit.next();
            dataset.setValue(key, distrib.get(key));
        }
        return dataset;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    public static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Housekeeping terms", new Double(46.2));
        dataset.setValue("Organ Growth", new Double(14.0));
        dataset.setValue("Cell Growth", new Double(20.5));
        dataset.setValue("Seed Growth", new Double(12));
        dataset.setValue("Yield", new Double(15.0));
        dataset.setValue("Regulation of Growth", new Double(25.3));
        return dataset;
    }

    /**
     * For a given String in a List of String return how many
     * time this String is present.
     * @param key String to search in the list
     * @param list List of String investigated
     * @return the number of time the string is in the list (a double)
     */
    private double count(final String key, final List<String> list) {
        double cnt = 0;
        for (String item : list) {
            if (item.equals(key)) {
                cnt += 1;
            }
        }
        return cnt;
    }
}
