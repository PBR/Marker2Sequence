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

package nl.wur.plantbreeding.logic.marker2seq;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.logic.swtools.QueryRdf;

/**
 * This class handles the search of a model.
 * @author Pierre-Yves Chibon
 */
public class AnnotationSearch {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            AnnotationSearch.class.getName());
    /** default URL to virtuoso. */
    private String endpoint = "http://sparql.plantbreeding.nl:8080/sparql/";
    /** The QueryRdf object used to run the query. */
    private final QueryRdf query = new QueryRdf();
    /** Base graph used for the queries. */
    private String basegraph;

    /**
     * Constructor taking the graph to search as argument.
     * @param graph String of the graph to search.
     */
    public AnnotationSearch(String graph) {
        this.basegraph = graph;
        query.setBasegraph(graph);
    }

    /**
     * Sets the base graph used in the queries.
     * @param bg a string of the basic graph(s) to used for querying
     */
    public final void setBasegraph(final String bg) {
        this.basegraph = bg;
        query.setBasegraph(bg);
    }

    /**
     * Sets the endpoint used for querying when input is a Loci or a MapLoci.
     * @param endp a string of the url used for querying
     */
    public final void setEndpoint(final String endp) {
        this.endpoint = endp;
        query.setService(endp);
    }

    /**
     * Search the given model using the keywords given in the string.
     * @param model Jena Model to search into
     * @param kw string of keywords separated by AND or OR
     * @return the reduced Model containning only the genes of interest
     */
    public final Model searchModel(final Model model, final String kw) {
        LOG.log(Level.INFO, "Original model size: {0}", model.size());
        Model modelout = ModelFactory.createDefaultModel();
        String[] keyand = kw.split("AND");
        for (String keyw : keyand) {
            keyw = keyw.trim();
            String[] keywords = keyw.split("OR");
            if (keywords.length == 1) {
                // There is no OR
                String key = keywords[0].trim();
                LOG.log(Level.INFO, "keyword: \"{0}\"", key);
                if (modelout.isEmpty()) {
                    modelout = query.getRestrictedModel(model, key);
                } else {
                    modelout = modelout.intersection(
                            query.getRestrictedModel(model, key));
                }
                LOG.log(Level.INFO, "Model size: {0}", modelout.size());
            } else {
                // There is a OR
                for (String key : keywords) {
                    key = key.trim();
                    LOG.log(Level.INFO, "keyword: \"{0}\"", key);
                    modelout = modelout.union(
                            query.getRestrictedModel(model, key));
                    LOG.log(Level.INFO, "Model size: {0}", modelout.size());
                }
            }
        }

        return modelout;
    }

    public Model searchLiterature(Model model, String trait) {
        LOG.log(Level.INFO, "Original model size: {0}", model.size());
        Model modelout = ModelFactory.createDefaultModel();
        modelout = query.getRestrictedLiteratureModel(model, trait);
        return modelout;
    }

    public Model searchGenesRelatedWithGo(Model model, String goid) {
        LOG.log(Level.INFO, "Original model size: {0}", model.size());
        Model modelout = ModelFactory.createDefaultModel();
        modelout = query.getGenesRelatedWithGo(model, goid);
        return modelout;
    }
}
