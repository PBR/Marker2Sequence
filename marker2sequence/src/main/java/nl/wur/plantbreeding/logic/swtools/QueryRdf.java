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

package nl.wur.plantbreeding.logic.swtools;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.plantbreeding.datatypes.Annotation;
import nl.wur.plantbreeding.datatypes.GeneticMarkers;
import nl.wur.plantbreeding.datatypes.MarkerSequence;
import nl.wur.plantbreeding.datatypes.Markerws;
import nl.wur.plantbreeding.exceptions.SeveralMappedPositionException;

/**
 * This class handles the query of the endpoints (either locally (in a given
 * model) or remotly (on a given endpoint)).
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class QueryRdf extends QueryRdfEngine {

    /** Graph containing the ITAG information. */
    private String basegraph = "FROM <http://itag2.pbr.wur.nl/> \n"
            + "FROM <http://cbsg.pbr.wur.nl/> \n";
    /** Graph containing the GO information. */
    private final String go = "FROM <http://go.pbr.wur.nl/> \n";
    /** Graph containing the UNIPROT information. */
    private final String uniprot = "FROM <http://uniprot.pbr.wur.nl/> \n";
    /** Graph containing the Protein-Protein Interaction from EBI. */
    private final String ppi = "FROM <http://intact.pbr.wur.nl/>";
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(
            QueryRdf.class.getName());

    /**
     * Default constructor.
     */
    public QueryRdf() {
    }

    /**
     * Constructor setting the URI used.
     * @param uri to set
     */
    public QueryRdf(final String uri) {
        this.URI = uri;
    }

    /**
     * Retrieve the base graph(s) used in the queries.
     * @return the basegraph variable.
     */
    public String getBasegraph() {
        return basegraph;
    }

    /**
     * Set the base graph(s) which will be used in the queries.
     * @param basegraph the graph(s)
     */
    public void setBasegraph(String basegraph) {
        this.basegraph = basegraph;
    }

    /**
     * Returns name, scaffold, start and stop position on the scaffold and type
     * of all the genes present in the scaffold (and having these information).
     * @param model a Jena Model in which the gene will be retrieved
     * @return a list of Annotation
     */
    public final ArrayList<Annotation> getGeneList(final Model model) {
        final ArrayList<Annotation> geneslist = new ArrayList<Annotation>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "SELECT DISTINCT ?name ?sca ?start ?stop ?type \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?s a 'http://pbr.wur.nl/GENE#' . \n"
                + "    ?s gene:FeatureName ?name . \n"
                + "    ?s gene:FeatureType ?type . \n"
                + "    ?s gene:Position ?pos . \n"
                + "    ?pos pos:Scaffold ?sca . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "} \n"
                + "ORDER BY ?sca ?start ?stop ?name ?type ";
//        System.out.println(querystring);
        String[] keys = {"name", "sca", "start", "stop", "type"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
            String[] tmp = rows.get(1).trim().split("/", 4);
            String scaffold = tmp[tmp.length - 1].split("#")[1];
            Annotation gene = new Annotation();
            gene.setName(rows.get(0).trim());
            gene.setScafoldname(scaffold);
            gene.setSeq_position_start(new Integer(rows.get(2).trim()));
            gene.setSeq_position_end(new Integer(rows.get(3).trim()));
            gene.setType(rows.get(4).trim());
            geneslist.add(gene);
        }
        if (debug) {
            System.out.println("genes : " + geneslist.size());
        }
        return geneslist;
    }

    /**
     * Returns the name, scaffold, start and stop position on the scaffold, type
     * and description of all the gene present in the model (and having these
     * information, except for the description which is optionnal).
     * @param model a Jena model in which the annotation will be retrieved
     * @return a list of Annotation
     */
    public final ArrayList<Annotation> getAnnotationList(final Model model) {
        ArrayList<Annotation> annotationlist = new ArrayList<Annotation>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "SELECT DISTINCT ?name ?sca ?start ?stop ?type ?desc \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?s a 'http://pbr.wur.nl/GENE#' . \n"
                + "    ?s gene:Position ?pos . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    ?pos pos:Scaffold ?sca . \n"
                + "    ?s gene:FeatureName ?name . \n"
                + "    ?s gene:FeatureType ?type . \n"
                + "    OPTIONAL { ?s gene:Description ?desc } . \n"
                + "} \n"
                + "ORDER BY ?sca xsd:int(?start) xsd:int(?stop) "
                + "?name ?desc ?type \n";
//        System.out.println(querystring);
        String[] keys = {"name", "sca", "start", "stop", "type", "desc"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);
//        ModelIO mio = new ModelIO();
//        mio.displayModel(model);

        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            String scaffold;
            if (rows.get(1).contains("#")) {
                String[] tmp = rows.get(1).trim().split("/", 4);
                scaffold = tmp[tmp.length - 1].split("#")[1];
            } else {
                scaffold = rows.get(1);
            }
            Annotation annot = new Annotation();
            annot.setName(rows.get(0).trim());
            annot.setScafoldname(scaffold);
            annot.setSeq_position_start(new Integer(rows.get(2).trim()));
            annot.setSeq_position_end(new Integer(rows.get(3).trim()));
            annot.setType(rows.get(4).trim());
            if (rows.size() == 6) {
                annot.setDescription(rows.get(5).trim());
            }
            annotationlist.add(annot);
        }
        LOG.log(Level.INFO, "annotations : {0}",
                Integer.toString(annotationlist.size()));
        return annotationlist;
    }

    /**
     * Returns the name, start and stop position on the scaffold and the SGN-ID
     * of all the markers present in the model (and having these information).
     * @param model a Jena model in which the physical map will be retrieved
     * @return a list of Markerws
     */
    public final ArrayList<Markerws> getPhysicalMap(final Model model) {
        ArrayList<Markerws> map = new ArrayList<Markerws>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n "
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "SELECT DISTINCT ?name ?start ?stop ?sca ?sgn \n"
                + "WHERE { \n"
                + "    ?s a 'http://pbr.wur.nl/MARKER#' . \n"
                + "    OPTIONAL {?s mkr:Chromosome ?c } . \n"
                + "    ?s mkr:MarkerName ?name . \n"
                + "    OPTIONAL {?s mkr:SGN-ID ?sgn } . \n"
                + "    ?s mkr:Position ?mpos . \n"
                + "    ?mpos pos:Start ?start . \n"
                + "    ?mpos pos:Stop ?stop . \n"
                + "    ?mpos pos:Scaffold ?sca . \n"
                + "} \n"
                + "ORDER BY ?sca ?start ?name ?stop ";
//        System.out.println(querystring);
        String[] keys = {"name", "start", "stop", "sca", "sgn"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            Markerws marker = new Markerws();
            String scaffold;
            if (rows.get(3).contains("#")) {
                String[] tmp = rows.get(3).trim().split("/", 4);
                scaffold = tmp[tmp.length - 1].split("#")[1];
            } else {
                scaffold = rows.get(3);
            }
            marker.setName(rows.get(0).trim());
            marker.setSeq_position_start(new Integer(rows.get(1).trim()));
            marker.setSeq_position_end(new Integer(rows.get(2).trim()));
            marker.setScafoldname(scaffold);
            String chr = null;
            if (scaffold.contains("chr")) { // At
                chr = scaffold.split("chr")[1];
            } else if (scaffold.contains("ch")) { // Tomato
                chr = scaffold.split("ch")[1];
            } else if (scaffold.contains("PGSC")) { // Potato
                chr = scaffold.split("PGSC")[1].split("DMB")[0];
            }
            marker.setChromosome(Long.parseLong(chr));
            if (rows.size() == 5) {
                marker.setSgnID(rows.get(4).trim());
            }
//            marker.setMap_position(Float.parseFloat(rows.get(5).trim()));
            map.add(marker);
        }
        if (debug) {
            System.out.println("physical map : " + map.size());
        }
        return map;
    }

    /**
     * Return the name, chromosome and position on the genetic map of all the
     * marker present in the model (and having these information).
     * @param model a Jena Model in which the genectic map will be retrieved
     * @return a list of GeneticMarkers
     */
    public final List<GeneticMarkers> getGeneticMap(final Model model) {
        ArrayList<GeneticMarkers> map = new ArrayList<GeneticMarkers>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX pbr:<http://pbr.wur.nl/> "
                + "PREFIX mkr:<http://pbr.wur.nl/MARKER#> "
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "
                + "SELECT DISTINCT ?name ?pos ?chr "
                + "WHERE { "
                + "    ?s mkr:Chromosome ?chr . "
                + "    ?s mkr:mapPosition ?pos . "
                + "    ?s mkr:MarkerName ?name . "
                + "} "
                + "ORDER BY ASC(xsd:int(?chr)) ASC(xsd:float(?pos)) ?name";
//        System.out.println(querystring);
        String[] keys = {"name", "pos", "chr"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            GeneticMarkers marker = new GeneticMarkers();
            marker.setName(rows.get(0).trim());
            marker.set_Chromosome(Long.parseLong(rows.get(2).trim()));
            marker.set_position(Double.parseDouble(rows.get(1).trim()));
            map.add(marker);
        }
        if (debug) {
            System.out.println("genetic map : " + map.size());
        }
        return map;
    }

    /**
     * Returns the scaffold and the number of position linking to this scaffold.
     * @param model a Jena model in which the MarkerSequence will be retrieved
     * @return a list of MarkerSequence
     */
    public final ArrayList<MarkerSequence> getMarkerSequence(
            final Model model) {
        ArrayList<MarkerSequence> map = new ArrayList<MarkerSequence>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "SELECT ?sca (count(?sca) as ?cnt) \n"
                + "WHERE { \n"
                + "    ?pos pos:Scaffold ?sca . \n"
                + "} "
                + "GROUP BY ?sca "
                + "HAVING (count(?sca) > 3) "
                + "ORDER BY ?sca ";
        String[] keys = {"sca"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
            String scaffold;
            if (rows.get(0).contains("#")) {
                String[] tmp = rows.get(0).trim().split("/", 4);
                scaffold = tmp[tmp.length - 1].split("#")[1];
            } else {
                scaffold = rows.get(0);
            }
//            System.out.println(scaffold);
            MarkerSequence marker = new MarkerSequence();
            marker.setName(scaffold);
            ArrayList<Markerws> markers =
                    this.getMarkerScaffold(model, rows.get(0));
            if (!markers.isEmpty()) {
                marker.setMarkers(markers);
                map.add(marker);
            }
        }
        LOG.log(Level.INFO, "scaffolds : {0}",
                Integer.toString(map.size()));
        return map;
    }

    /**
     * Returns the name, chromosome, position on the genetic map, start and stop
     * position of all the markers on a given scaffold.
     * @param model a Jena Model in which the MarkersScaffold will be retrieved
     * @param scaffoldname the name of a scaffold
     * @return a list of Markerws
     */
    public final ArrayList<Markerws> getMarkerScaffold(final Model model,
            String scaffoldname) {
        ArrayList<Markerws> markers = new ArrayList<Markerws>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        if (scaffoldname.startsWith("http")) {
            scaffoldname = "<" + scaffoldname + ">";
        } else {
            scaffoldname = "\"" + scaffoldname + "\"";
        }
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "SELECT DISTINCT ?name ?chr ?pos ?start ?stop \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?marker a 'http://pbr.wur.nl/MARKER#' . \n"
                + "    ?marker mkr:Position ?mpos . \n"
                + "    ?mpos pos:Scaffold " + scaffoldname + " . \n"
                + "    ?mpos pos:Start ?start . \n"
                + "    ?mpos pos:Stop ?stop . \n"
                + "    ?marker mkr:Chromosome ?chr . \n"
                + "    ?marker mkr:MarkerName ?name . \n"
                + "    ?marker mkr:mapPosition ?pos . \n"
                + "} \n"
                + "ORDER BY ?name ?chr ?start ?stop \n";
//        System.out.println(querystring);
        String[] keys = {"name", "chr", "pos", "start", "stop"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

//        System.out.println(matrix.size());
        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            Markerws marker = new Markerws();
            marker.setName(rows.get(0));
            marker.setChromosome(Long.parseLong(rows.get(1).trim()));
            marker.setMap_position(Float.parseFloat(rows.get(2).trim()));
            marker.setSeq_position_start(new Integer(rows.get(3).trim()));
            marker.setSeq_position_end(new Integer(rows.get(4).trim()));
            markers.add(marker);
        }
//        System.out.println("sca: " + scaffoldname + " markers : "
//        + markers.size());
        return markers;
    }

    /**
     * For a given keyword, build a new model containing all the scaffold and
     * adding to it all the genes having the given keyword in their gene
     * description or in their go term name, description or process.
     * @param model a Jena mode in which the restricted mode will be done
     * @param kw the string searched in the GO terms and Gene description
     * @return a Jena Model filled with the description of the genes and GO
     */
    public final Model getRestrictedModel(final Model model, final String kw) {
        List<String> genelist = this.getGeneUriList(model);
        //System.out.println(genelist.size());
        // Add the genes and GO terms which have a
        // description/name/process containning the keyword
        Model m2 = ModelFactory.createDefaultModel();
        for (String list : genelist) {
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                    + "PREFIX go:"
                    + "<http://www.geneontology.org/formats/oboInOwl#>\n "
                    + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                    + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                    + "DESCRIBE ?gene ?pos \n"
                    + this.basegraph
                    + this.go
                    + this.uniprot
                    + "WHERE{ \n"
                    + "    ?gene gene:Description ?desc . \n"
                    + "    OPTIONAL { ?gene gene:Go ?go  . \n"
                    + "               ?go rdfs:label ?goname . \n"
                    + "               ?go go:hasDefinition ?godefs . \n"
                    + "               ?godefs rdfs:label ?godef . \n"
                    + "        OPTIONAL  { \n"
                    + "                 ?go go:hasExactSynonym ?gosyns . \n"
                    + "                 ?gosyns rdfs:label ?gosyn \n"
                    + "        } . \n"
                    + "    } . \n"
                    + "    OPTIONAL { ?gene gene:Protein ?prot . \n"
                    + "               ?prot uniprot:annotation ?annot . \n"
                    + "               ?annot rdfs:seeAlso ?url . \n"
                    + "       OPTIONAL { ?annot rdfs:comment ?pathdesc }. \n"
                    + "       OPTIONAL { \n"
                    + "            ?prot uniprot:recommendedName ?protrname .\n"
                    + "            ?protrname uniprot:fullName ?protfname }. \n"
                    + "       OPTIONAL { \n"
                    + "            ?prot uniprot:alternativeName ?protaname .\n"
                    + "            ?protaname uniprot:fullName ?protafname . \n"
                    + "             } . \n"
                    + "    }. \n"
                    + "    ?gene gene:FeatureName ?name . \n"
                    + "    ?gene gene:Position ?pos . \n"
                    + "  FILTER( \n"
                    + "     (regex(str(?desc), \"" + kw + "\", \"i\")) || \n"
                    + "     (regex(str(?goname), '" + kw + "', \"i\")) || \n"
                    + "     (regex(str(?godef), '" + kw + "', \"i\")) || \n"
                    + "     (regex(str(?gosyn), '" + kw + "', \"i\")) || \n"
                    + "     (regex(str(?name), '" + kw + "', \"i\"))  ||\n"
                    + "     (regex(str(?protfname), '" + kw + "', \"i\")) ||\n"
                    + "     (regex(str(?protafname), '" + kw + "', \"i\")) ||\n"
                    + "     (regex(str(?pathdesc), '" + kw + "', \"i\")) \n"
                    + "  ) . \n"
                    + "  FILTER ( \n"
                    + "    ?gene IN ("
                    + list
                    + "    ) \n"
                    + "  ) \n"
                    + "} \n";

//        System.out.println(endpoint);
//        System.out.println(querystring);
            Model mout = this.remoteDescribeQuery(querystring);
//        System.out.println(mout.size());
//        System.out.println(this.endpoint);
            m2 = m2.union(mout);
        }
        return m2;
    }

    /**
     * Search the literature for a given keyword and returns a model containing
     * the all genes related with these articles and present in the QTL
     * interval of interest.
     * @param model the model containing all the genes of the QTL interval.
     * @param kw the keyword to search in the literature.
     * @return a Jena Model containing the genes of the interval related with
     * the keyword by literature.
     */
    public Model getRestrictedLiteratureModel(Model model, String kw) {
        List<String> genelist = this.getGeneUriList(model);
        //System.out.println(genelist.size());
        // Add the genes and GO terms which have a
        // description/name/process containning the keyword
        Model m2 = ModelFactory.createDefaultModel();
        for (String list : genelist) {
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                    + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#>\n "
                    + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> \n"
                    + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                    + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                    + "DESCRIBE ?gene ?pos \n"
                    + this.basegraph
                    + this.uniprot
                    + "WHERE{ \n"
                    + "    ?gene gene:Description ?desc . \n"
                    + "    ?gene gene:Protein ?prot . \n"
                    + "    ?prot uniprot:citation ?cit . \n"
                    + "    ?cit skos:exactMatch ?url . \n"
                    + "    FILTER (regex(?url, \"pubmed\")) . \n"
                    + "    ?url uniprot:title ?title . \n"
                    + "    ?url rdfs:comment ?abstract . \n"
                    + "    ?gene gene:FeatureName ?name . \n"
                    + "    ?gene gene:Position ?pos . \n"
                    + "  FILTER( \n"
                    + "     (regex(str(?title), \"" + kw + "\", \"i\")) || \n"
                    + "     (regex(str(?abstract), '" + kw + "', \"i\")) \n"
                    + "  ) . \n"
                    + "  FILTER ( \n"
                    + "    ?gene IN ("
                    + list
                    + "    ) \n"
                    + "  ) \n"
                    + "} \n";

//        System.out.println(endpoint);
//        System.out.println(querystring);
            Model mout = this.remoteDescribeQuery(querystring);
            m2 = m2.union(mout);
        }
        return m2;
    }

    /**
     * Returns an ordered list of pathways for the gene present in the given
     * model.
     * @param model Jena Model to search into
     * @return a ArrayList of ArrayList of String containing the pathway
     * description followed by the number of time it appears.
     */
    public final ArrayList<ArrayList<String>> getOrderedPathways(final Model model) {

        String querystring = ""
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT DISTINCT ?desc (COUNT(?desc) AS ?cnt) ?url \n"
                + "WHERE { \n"
                + "    ?gene gene:Pathway ?url. "
                + "    ?url rdfs:comment ?desc . "
                + "} GROUP BY ?desc ?url ORDER BY DESC(?cnt) \n";
        String[] keys = {"desc", "cnt", "url"};
        ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
        out = this.localSelectQuery(model, querystring, out, keys);
        for (ArrayList<String> row : out) {
            String cnt = row.get(1);
            cnt = cnt.split("\\^\\^")[0];
            row.set(1, cnt);
        }

        return out;
    }

    /**
     * This function generates a small Jena model containing only the geneid
     * with their pathway URI and description.
     * @param model a Jena Model from which extract all the genes.
     * @return a Jena Model with highly reduced information
     */
    public final Model generateSmallPathwayModel(final Model model) {
        List<String> genelist = this.getGeneUriList(model);
        // Add the genes their pathways to the new model
        Model m2 = ModelFactory.createDefaultModel();
        for (String list : genelist) {
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                    + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                    + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                    + "CONSTRUCT { \n"
                    + "    ?gene gene:Pathway ?url . \n "
                    + "    ?url rdfs:comment ?desc . \n"
                    + " } \n"
                    + this.basegraph
                    + this.uniprot
                    + "WHERE{ \n"
                    + "    ?gene gene:Protein ?prot . \n"
                    + "    ?prot uniprot:annotation ?annot . \n"
                    + "    ?annot rdfs:seeAlso ?url . \n"
                    + "    ?annot rdfs:comment ?desc . \n"
                    + "  FILTER ( \n"
                    + "    ?gene IN ("
                    + list
                    + "    ) \n"
                    + "  ) \n"
                    + "} \n";

            Model mout = this.remoteConstructQuery(querystring);
//            System.out.println(querystring);
            m2 = m2.union(mout);
        }
        return m2;
    }

    /**
     * Ask if a gene is present as a pbr.wur.nl/GENE# in a given graph.
     * return true if the gene is present
     * return false if the gene is not present
     * @param geneid the geneid to search for
     * @param model the Jena model in which we check if the gene is in
     * @return a boolean giving wether the gene is in the model or not
     */
    public final boolean askGene(final String geneid, final Model model) {
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "ASK "
                + this.basegraph
                + "{<http://pbr.wur.nl/GENE#" + geneid + "> a "
                + "'http://pbr.wur.nl/GENE#' }";
        return this.localAskQuery(model, querystring);
    }

    /**
     * Returns the Go Uri for all Go term associated with a gene in the given
     * model.
     * @param model a Jena model in which the GO URI will be listed
     * @return a list of GO URI
     */
    public final ArrayList<String> getGoUri(final Model model) {
        final ArrayList<String> golist = new ArrayList<String>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "SELECT ?go "
                + this.basegraph
                + this.go
                + "WHERE {"
                + "    ?s a 'http://pbr.wur.nl/GENE#' . "
                + "    ?s gene:Go ?go . "
                + "} ";
        String[] keys = {"go"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);
        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            golist.add(rows.get(0).trim());
        }
        return golist;
    }

    /**
     * Returns the Describe query for each Go term being referenced by a gen
     * in the given model.
     * @param model a Jena model in which the GO will be retrieved and described
     * @return a sparql query of to describe all the GO term in the model
     */
    public final String getGoDescribeQuery(final Model model) {
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "SELECT ?go "
                + this.basegraph
                + this.go
                + "WHERE {"
                + "    ?s a 'http://pbr.wur.nl/GENE#' . "
                + "    ?s gene:HasGoTerm ?go . "
                + "} ";
        String[] keys = {"go"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        querystring = "DESCRIBE ";
        for (ArrayList<String> rows : matrix) {
            querystring += " <" + rows.get(0).trim() + "> ";
        }
        return querystring;
    }

    /**
     * Add to the model the description of all marker and their position from
     * the list of given marker.
     * It relies on the marker name to filter the markers.
     * @param model a Jena model in which the MarkerInfo will be retrieved
     * @param markerlist a list of GeneticMarkers
     * @return a Jena Model containing informations about all the markers
     */
    public final Model getMarkerInfo(Model model,
            final List<GeneticMarkers> markerlist) {
        String querystring =
                "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "DESCRIBE ?marker ?pos \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?marker mkr:MarkerName ?name . \n"
                + "    ?marker mkr:Position ?pos . \n"
                + "    FILTER ( \n";
        int cnt = 1;

        String querystring2 = querystring + "";
        for (GeneticMarkers marker : markerlist) {
            if (( cnt % 40 ) == 0) {
                querystring2 += "        (?name = \"" + marker.getName()
                        + "\") \n";
                querystring2 += "    ) . \n"
                        + "}\n";
//                System.out.println(querystring);
                model = model.union(this.remoteDescribeQuery(querystring2));

                querystring2 = querystring + "";

            } else {
                if (cnt == markerlist.size()) {
                    querystring2 += "        (?name = \"" + marker.getName()
                            + "\") \n";
                } else {
                    querystring2 += "        (?name = \"" + marker.getName()
                            + "\") || \n";
                }
            }
            cnt = cnt + 1;
        }
        querystring = querystring2 + "    ) . \n"
                + "}\n";
//        System.out.println(querystring);
        return model.union(this.remoteDescribeQuery(querystring));
    }

    /**
     * For all the scaffold present in the model, find the extreme element
     * and add to the model all the genes which are between these two elements.
     * @param model a Jena model in which the genes info will be retrieved
     * @return a Jena model with the information about the genes
     */
    public final Model getGenesInfo(final Model model) {
        return this.getGenesInfo(model, 3);
    }

    /**
     * For all the scaffold present in the model, find the extreme element
     * and add to the model all the genes which are between these two elements.
     * @param model a Jena model in which the genes info will be retrieved
     * @param min an integer representing the minimum number of markers that a
     * scaffold should have to be taken into account.
     * @return a Jena model with the information about the genes
     */
    public final Model getGenesInfo(Model model, final int min) {
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "SELECT ?sca (count(?sca) as ?cnt) \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?s mkr:MarkerName ?name . \n"
                + "    ?s mkr:Position ?pos . \n"
                + "    ?pos pos:Scaffold ?sca . \n"
                + "} "
                + "GROUP BY ?sca \n"
                + "HAVING (count(?sca) > " + min + ") \n"
                + "ORDER BY ?sca ";
        final String[] keys = {"sca"};
//        System.out.println(querystring);
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        LOG.log(Level.INFO, "Number of scaffold: {0}",
                Integer.toString(matrix.size()));
        for (ArrayList<String> rows : matrix) {
            String name = rows.get(0);
            int[] extremes = this.getStartAndStopPosition(model, name);
//            System.out.println(model.size() + " - " + name + " - "
//                    + extremes[0] + " - " + extremes[1]);
            LOG.log(Level.INFO, "Scaffold: {0}", name);
            model = this.getGeneInfo(model, name, extremes);

        }
        return model;

    }

    /**
     * Return the start and stop position of the extreme element present on the
     * model on a scaffold.
     * @param model a Jena model in which the start and stop position will be
     * retrieved
     * @param scaffoldname the name of a scaffold
     * @return an array of int containing the start and stop position of the
     * scaffold
     */
    public final int[] getStartAndStopPosition(final Model model,
            String scaffoldname) {
        if (scaffoldname.startsWith("http")) {
            scaffoldname = "<" + scaffoldname + ">";
        } else {
            scaffoldname = "\"" + scaffoldname + "\"";
        }
        String querystring =
                "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "SELECT (MIN(xsd:int(?start)) AS ?min) \n"
                + " (MAX(xsd:int(?stop)) AS ?max) \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?s mkr:Position ?pos . \n"
                + "    ?pos pos:Scaffold " + scaffoldname + " . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    ?pos pos:Start  ?start . \n"
                + "} ";
//        System.out.println(querystring);
        final String[] keys = {"min", "max"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.localSelectQuery(model, querystring, matrix, keys);
        String min;
        String max;
        if (matrix.get(0).get(0).contains("htt")) {
            min = matrix.get(0).get(0).toString().split("htt")[0];
            min = min.substring(0, min.length() - 2);
        } else {
            min = matrix.get(0).get(0);
        }
        if (matrix.get(0).get(1).contains("htt")) {
            max = matrix.get(0).get(1).toString().split("htt")[0];
            max = max.substring(0, max.length() - 2);
        } else {
            max = matrix.get(0).get(0);
        }
        int[] val = {Integer.parseInt(min), Integer.parseInt(max)};
        return val;
    }

    /**
     * Returns all the genes and their positions on a given scaffold between the
     * two given extremes position.
     * @param model a Jena Model to query
     * @param scaffoldname name of the scaffold
     * @param ext array of integer containing the two extreme position of the
     * given scaffold
     * @return a Jena Model containing the gene information
     */
    public final Model getGeneInfo(final Model model, String scaffoldname,
            final int[] ext) {
        if (scaffoldname.startsWith("http")) {
            scaffoldname = "<" + scaffoldname + ">";
        } else {
            scaffoldname = "\"" + scaffoldname + "\"";
        }
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> \n"
                + "DESCRIBE ?gene ?pos ?go ?godefs ?gosyns \n"
                + this.basegraph
                + this.go
                + "WHERE {    \n"
                + "    ?gene gene:Position ?pos . \n"
                + "    ?pos pos:Scaffold " + scaffoldname + " . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    FILTER ( xsd:int(?start) >= " + ext[0] + " && \n"
                + "             xsd:int(?stop) <= " + ext[1] + " ) . \n"
                + "    OPTIONAL { \n"
                + "         ?gene gene:Go ?go. \n"
                + "         ?go go:hasDefinition ?godefs . \n"
                + "         ?go go:hasExactSynonym ?gosyns . \n"
                + "    } . \n"
                + "} ORDER BY ?gene \n";

//        System.out.println(querystring);
        Model m2 = this.remoteDescribeQuery(querystring);

//         querystring =
//                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
//                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
//                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
//                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#>
//        \n"
//                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
//                + "CONSTRUCT { \n"
//                + "   ?gene gene:Protein ?prot . \n"
//                + "   ?prot ?p ?o . \n"
//                + "   ?o ?p2 ?o2 . \n"
//                + "} \n"
//                + this.basegraph
//                + this.uniprot
//                + "WHERE {    \n"
//                + "    ?gene gene:Position ?pos . \n"
//                + "    ?pos pos:Scaffold <" + scaffoldname + "> . \n"
//                + "    ?pos pos:Start ?start . \n"
//                + "    ?pos pos:Stop ?stop . \n"
//                + "    FILTER ( xsd:int(?start) >= " + ext[0] + " && "
//                + "             xsd:int(?stop) <= " + ext[1] + " ) . \n"
//                + "    ?gene gene:Protein ?prot . \n"
//                + "    ?prot ?p ?o . \n"
//                + "    ?o ?p2 ?o2 . \n"
//                + "} \n";
//        System.out.println(querystring);
//        Model m3 = this.remoteConstructQuery(querystring);

        return model.union(m2); //.union(m3);
    }

    /**
     * Returns all the markers and their positions on a given scaffold between the
     * two given extremes position.
     * @param model a Jena Model to query
     * @param scaffoldname name of the scaffold
     * @param ext array of integer containing the two extreme position of the
     * given scaffold
     * @return a Jena Model containing the marker information
     */
    public final Model getMarkerInfo(final Model model, String scaffoldname,
            final int[] ext) {
        if (scaffoldname.startsWith("http")) {
            scaffoldname = "<" + scaffoldname + ">";
        } else {
            scaffoldname = "\"" + scaffoldname + "\"";
        }
        String querystring =
                "PREFIX mkr:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "DESCRIBE ?marker ?pos \n"
                + this.basegraph
                + "WHERE {    \n"
                + "    ?marker mkr:Position ?pos . \n"
                + "    ?pos pos:Scaffold " + scaffoldname + " . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    FILTER ( xsd:int(?start) >= " + ext[0] + " && \n"
                + "             xsd:int(?stop) <= " + ext[1] + " ) . \n"
                + "} ORDER BY ?marker \n";

//        System.out.println(querystring);
        Model m2 = this.remoteDescribeQuery(querystring);

        return model.union(m2);
    }

    /**
     * Query the description of all GO term present in the given model.
     * Returns the union between the original model and the model generated
     * by the descriptions
     * @param model a Jena Model in which the list of GO is extracted
     * @return a Jena Model containing the GO information
     */
    public final Model getGoDescribeInfo(final Model model) {
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#>"
                + "SELECT DISTINCT ?go "
                + this.basegraph
                + this.go
                + "WHERE { "
                + "  ?gene gene:Go ?go ."
                + "}";
        ArrayList<String> matrix = new ArrayList<String>();
        matrix = this.localSelectQuery(model, querystring, matrix, "go");

        querystring = "DESCRIBE ";
        for (String goid : matrix) {
            querystring += " <" + goid + "> \n";
        }
        querystring += this.basegraph;
        querystring += this.go;

        Model m2 = this.remoteDescribeQuery(querystring);

        return model.union(m2);

    }

    /**
     * Query all GO term present in the given model.
     * Describe all these GO term
     * Returns the union between the original model and the model generated
     * by the descriptions
     * @param model a Jena model in which the G
     * @param limit a int used to limit the query
     * @return a Jena Model resulting from the Construct query
     */
    public final Model getGeneDescriptionInModel(final Model model,
            final int limit) {
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "CONSTRUCT { ?g gene:Go ?go } "
                + this.go
                + this.basegraph
                + "WHERE { "
                + "  ?g gene:Go ?go . "
                + "} "
                + "LIMIT " + limit;

        return this.localConstructQuery(model, querystring);
    }

    /**
     * Query all GO term present in the given model.
     * Describe all these GO term
     * Returns the union between the original model and the model generated
     * by the descriptions
     * @param model a Jena model in which the gene Description will be retrieved
     * @return a Jena Model resulting from the Construct Query
     */
    public final Model getGeneDescriptionInModel(final Model model) {
        return this.getGeneDescriptionInModel(model, 50);
    }

    /**
     * For a given start and stop position on a given chromosome, lists all
     * the marker present in this interval, order them by start position and
     * return the first and last of the list.
     * @param chr a Linkage Group name (ex SL2.30ch06)
     * @param start a Start position (ex 42268851)
     * @param stop a Stop position (ex 42317124)
     * @return an arrayList of all the markers in the interval
     */
    public final String[] getClosestMarkers(final String chr,
            final String start, final String stop) {

        String querystring = ""
                + " PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + " PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + " PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + " SELECT ?name \n"
                + this.basegraph
                + " WHERE { \n"
                + "  ?marker mk:Position ?pos . \n"
                + "  ?pos pos:Scaffold <http://pbr.wur.nl/SCAFFOLD#" + chr
                + "> . \n"
                + "  ?pos pos:Start ?start . \n"
                + "  ?pos pos:Stop ?stop . \n"
                + "  FILTER ( \n"
                + "      xsd:int(?start) >= " + start + " && \n"
                + "     xsd:int(?stop) <= " + stop + " \n"
                + "  ) . \n"
                + "  ?marker mk:MarkerName ?name . \n"
                + "} ORDER BY xsd:int(?start) \n";
//        System.out.println(querystring);
        ArrayList<String> matrix = new ArrayList<String>();
        final String key = "name";
        matrix = this.remoteSelectQuery(endpoint, querystring, matrix, key);

        String[] markernames = new String[matrix.size()];
        for (int cnt = 0; cnt < markernames.length; cnt++) {
            markernames[cnt] = matrix.get(cnt);
        }
        return markernames;
    }

    /**
     * For a given start and stop position on a given chromosome, lists all
     * the marker present in this interval, order them by start position and
     * return the first and last of the list.
     * @param chr a Linkage Group name (ex: 6)
     * @param start a Start position in cM (ex: 0)
     * @param stop a Stop position in cM (ex: 15)
     * @return an array of string containning the closest markers.
     */
    public final String[] getClosestMarkersFromMap(final String chr,
            final String start, final String stop) {

        String querystring = ""
                + " PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + " PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + " PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + " SELECT DISTINCT ?name \n"
                + this.basegraph
                + " WHERE { \n"
                + "   "
                + "  ?marker mk:Chromosome \"" + chr + "\" . \n"
                + "  ?marker mk:mapPosition ?start . \n"
                + "  ?marker mk:mapPosition ?stop . \n"
                + "  FILTER ( \n"
                + "      xsd:double(?start) >= " + start + " && \n"
                + "     xsd:double(?stop) <= " + stop + " \n"
                + "  ) . \n"
                + "  ?marker mk:MarkerName ?name . \n"
                + "  ?marker mk:Position ?pos . \n"
                + "  ?pos pos:Scaffold <http://pbr.wur.nl/SCAFFOLD#SL2.31ch0"
                + chr + "> .\n"
                + "} ORDER BY xsd:double(?start) \n";

//        System.out.println(endpoint);
//        System.out.println(querystring);
        ArrayList<String> matrix = new ArrayList<String>();
        final String key = "name";
        matrix = this.remoteSelectQuery(endpoint, querystring, matrix, key);

        String[] markernames = new String[matrix.size()];
        for (int cnt = 0; cnt < markernames.length; cnt++) {
            markernames[cnt] = matrix.get(cnt);
        }
        return markernames;
    }

    /**
     * Return the distribution of the namespace of the GO in a given model.
     * This distribution is given as a HashMap having the NameSpace as key
     * and their count as value
     * @param model a Jena model in which the GO distribution will be retrieved
     * @return HashMap "String", "Integer" a hash of "Go names space": "count"
     */
    public final HashMap<String, Integer> getGoNameSpaceDistribution(
            final Model model) {
        HashMap<String, Integer> distribution = new HashMap<String, Integer>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring = "\n"
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> \n"
                + "SELECT ?ns (count(?ns) AS ?cnt) \n"
                + this.basegraph
                + this.go
                + "WHERE{ \n"
                + "        ?gene gene:Go ?go . \n"
                + "        ?go go:hasOBONamespace ?ns . \n"
                + "} GROUP BY ?ns \n";
//        System.out.println(querystring);
        String[] keys = {"ns", "cnt"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                String name = row.get(0);
                String cnts = row.get(1).toString().split("\\^\\^")[0];
                Integer cnt = new Integer(cnts);
                distribution.put(name, cnt);
            }
        }
        return distribution;
    }

    /**
     * Return the distribution of the synonym of the GO in a given model.
     * This distribution is given as a HashMap having the synonym as key
     * and their count as value
     * @param model Jena model in which the GO distribution will be retrieved
     * @return HashMap "String", "Integer" a hash of "Go synonym", "count"
     */
    public final HashMap<String, Integer> getGoSynonymDistribution(
            final Model model) {
        HashMap<String, Integer> distribution = new HashMap<String, Integer>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> "
                + "SELECT DISTINCT ?syn (count(?syn) AS ?cnt) "
                + this.basegraph
                + this.go
                + "WHERE{ "
                + "        ?gene gene:Go ?go . "
                + "        ?go go:hasExactSynonym ?s . "
                + "        ?s rdfs:label ?syn . "
                + "} GROUP BY ?syn ";
        String[] keys = {"syn", "cnt"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                String name = row.get(0).toString().split("@")[0];
                String cnts = row.get(1).toString().split("\\^\\^")[0];
                Integer cnt = new Integer(cnts);
                distribution.put(name, cnt);
            }
        }
        return distribution;
    }

    /**
     * Return the distribution of the synonym of the GO in a given model.
     * This distribution is given as a HashMap having the synonym as key
     * and their count as value
     * @param model a Jena model in which the GO distribution will be retrieved
     * @return HashMap"String", "Integer" a hash of "Go synonym", "count"
     */
    public final HashMap<String, Integer> getGoDistribution(
            final Model model) {
        HashMap<String, Integer> distribution = new HashMap<String, Integer>();
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> "
                + "SELECT DISTINCT ?name (count(?name) AS ?cnt) "
                + this.basegraph
                + this.go
                + "WHERE{ "
                + "        ?gene gene:Go ?go . "
                + "        ?go rdfs:label ?name . "
                + "} GROUP BY ?name ";
        String[] keys = {"name", "cnt"};
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                String name = row.get(0).toString().split("@")[0];
                String cnts = row.get(1).toString().split("\\^\\^")[0];
                Integer cnt = new Integer(cnts);
                distribution.put(name, cnt);
            }
        }
        return distribution;
    }

    /**
     * Retrieve proteins information for a given gene ID using the uniprot
     * information present on the sparql endpoint.
     * These information are :
     *  - protein uri (containning the uniprot ID)
     *  - protein name
     *  - protein alternative name (if present)
     *  - reviewed boolean (giving information if the protein has been reviewed
     * or automatically annotated)
     * @param geneid the gene id from basegraph
     * @return a list of HashMap containing the protein informaiton
     */
    public final ArrayList<HashMap<String, String>> getProteinInfoForGene(
            final String geneid) {
        ArrayList<HashMap<String, String>> info =
                new ArrayList<HashMap<String, String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "SELECT ?prot ?fname ?afname ?review \n"
                + this.basegraph
                + this.uniprot
                + "WHERE { \n"
                + "    <http://pbr.wur.nl/GENE#" + geneid
                + "> gene:Protein ?prot . \n"
                + "    ?prot uniprot:recommendedName ?rname . \n"
                + "    ?rname uniprot:fullName ?fname . \n"
                + "    OPTIONAL { \n"
                + "        ?prot uniprot:alternativeName ?aname . \n"
                + "        ?aname uniprot:fullName ?afname . \n"
                + "    } \n"
                + "    ?prot uniprot:reviewed ?review . \n"
                + "} \n";
//        System.out.println(querystring);
        String[] keys = {"prot", "fname", "review", "afname"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, String> prot;
        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                prot = new HashMap<String, String>();
                prot.put("uri", row.get(0));
                String id = row.get(0).split("uniprot/")[1];
                prot.put("id", id);
                prot.put("description", row.get(1));
                String review = row.get(2).toString().split("\\^\\^")[0];
                prot.put("reviewed", review);
                if (row.size() == 4) {
                    prot.put("Alternative description", row.get(3));
                }
                info.add(prot);
            }
        }
        return info;
    }

    /**
     * Retrieve for a given gene all the associated GO term with their id, name,
     * definition and namespace.
     * @param geneid the gene for which the GO terms are retrieved
     * @return the list of HashMap containing all the GO terms.
     */
    public final ArrayList<HashMap<String, String>> getGoOfGene(
            final String geneid) {
        ArrayList<HashMap<String, String>> info =
                new ArrayList<HashMap<String, String>>();
        String querystring =
                "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX gouni:<http://purl.org/obo/owl/GO#> \n"
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> \n"
                + "SELECT DISTINCT ?goid ?goname ?lab ?ns \n"
                + this.basegraph
                + this.go
                + "WHERE{ \n"
                + "  <http://pbr.wur.nl/GENE#" + geneid + "> gene:Go ?go . \n"
                + "  ?go gouni:GoID ?goid . \n"
                + "  ?go go:hasDefinition ?def . \n"
                + "  ?go go:hasOBONamespace ?ns . \n"
                + "  ?def rdfs:label ?lab . \n"
                + "  ?go rdfs:label ?goname . \n"
                + "}";
        String[] keys = {"goid", "goname", "lab", "ns"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();

        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, String> prot;
        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                prot = new HashMap<String, String>();
                prot.put("goid", row.get(0));
                prot.put("name", row.get(1).split("@")[0]);
                prot.put("definition", row.get(2).split("@")[0]);
                prot.put("process", row.get(3).split("@")[0]);
//                prot.put("source", "basegraph");
                info.add(prot);
            }
        }
        return info;
    }

    /**
     * Retrieve for a given gene all the associated pubmed literature with their
     * url title and authors.
     * @param geneid the gene for which the literature references are retrieved
     * @return the list of HashMap containing all the literature references.
     */
    public final ArrayList<HashMap<String, String>> getLiteratureOfGene(
            final String geneid) {
        ArrayList<HashMap<String, String>> info =
                new ArrayList<HashMap<String, String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> \n"
                + "SELECT DISTINCT ?url ?title \n"
                + this.basegraph
                + this.uniprot
                + "WHERE { \n"
                + "    <http://pbr.wur.nl/GENE#" + geneid
                + "> gene:Protein ?prot . \n"
                + "    ?prot uniprot:citation ?cit . \n"
                + "    ?cit skos:exactMatch ?url . \n"
                + "    FILTER (regex(?url, \"pubmed\")) . \n"
                + "    ?cit uniprot:title ?title . \n"
                + "} ORDER BY ?url \n";
        String[] keys = {"url", "title"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, String> lit;
        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                lit = new HashMap<String, String>();
                lit.put("url", row.get(0));
                lit.put("id", row.get(0).split("pubmed/")[1]);
                lit.put("title", row.get(1));
                info.add(lit);
            }
        }
        return info;
    }

    /**
     * Retrieve for a given gene all the pathway of the associated proteins.
     * Information returned contains url, pathway id and pathway description.
     * @param geneid the gene for which the pathways are retrieved
     * @return the list of HashMap containing all the pathways information.
     */
    public final ArrayList<HashMap<String, String>> getPathwayOfGene(
            final String geneid) {
        ArrayList<HashMap<String, String>> info =
                new ArrayList<HashMap<String, String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT DISTINCT ?url ?desc \n"
                + this.basegraph
                + this.uniprot
                + "WHERE { \n"
                + "    <http://pbr.wur.nl/GENE#" + geneid
                + "> gene:Protein ?prot . \n"
                + "    ?prot uniprot:annotation ?annot . \n"
                + "    ?annot rdfs:seeAlso ?url . \n"
                + "    ?annot rdfs:comment ?desc . \n"
                + "    FILTER regex(?url, \"unipathway\") . \n"
                + "} ORDER BY ?url";
        String[] keys = {"url", "desc"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, String> path;
        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                path = new HashMap<String, String>();
                path.put("url", row.get(0));
                path.put("desc", row.get(1));
                path.put("id", row.get(0).split("unipathway/")[1]);
                info.add(path);
            }
        }
        return info;
    }

    /**
     * Retrieve for a given set of genes all the pathway of the associated
     * proteins.
     * Information returned contains url, pathway id and pathway description.
     * @param geneids the genes for which the pathways are retrieved
     * @return the list of HashMap containing all the pathways information.
     */
    public final ArrayList<HashMap<String, String>> getPathwayOfGenes(
            final ArrayList<String> geneids) {
        ArrayList<HashMap<String, String>> info =
                new ArrayList<HashMap<String, String>>();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT DISTINCT ?url ?desc \n"
                + this.basegraph
                + this.uniprot
                + "WHERE { \n"
                + "    ?gene gene:Protein ?prot . \n"
                + "    ?prot uniprot:annotation ?annot . \n"
                + "    ?annot rdfs:seeAlso ?url . \n"
                + "    ?annot rdfs:comment ?desc . \n"
                + "    FILTER regex(?url, \"unipathway\") . \n"
                + "      FILTER( ?gene IN ( \n";
        for (int cnt = 0; cnt < geneids.size(); cnt++) {
            if (cnt + 1 < geneids.size()) {
                querystring += "<http://pbr.wur.nl/GENE#" + geneids.get(cnt) + ">, \n";
            } else {
                querystring += "<http://pbr.wur.nl/GENE#" + geneids.get(cnt) + ">";
            }

        }
        querystring += "))} ORDER BY ?url";

//        System.out.println(querystring);
        String[] keys = {"url", "desc"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        HashMap<String, String> path;
        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                path = new HashMap<String, String>();
                path.put("url", row.get(0));
                path.put("desc", row.get(1));
                path.put("id", row.get(0).split("unipathway/")[1]);
                info.add(path);
            }
        }
        return info;
    }

    /**
     * Returns in a string all the URI of genes present in the model.
     * This can then be integrated in a big "IN" filter.
     * @param model the Jena model to query
     * @return the list of all the URI of gene present in the model
     */
    private ArrayList<String> getGeneUriList(final Model model) {
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "SELECT DISTINCT ?gene \n"
                + "WHERE { \n"
                + "    ?gene a \"http://pbr.wur.nl/GENE#\" \n"
                + "} ORDER BY ?gene";
        String[] keys = {"gene"};
//        System.out.println(querystring);
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.localSelectQuery(model, querystring, matrix, keys);

        ArrayList<String> urilist = new ArrayList<String>();
        String uristring = "";
        int cnt = 0;
        for (ArrayList<String> row : matrix) {
            if (row.size() >= 1) {
                uristring += "<" + row.get(0) + "> ";
                if (( cnt % 40 ) == 0) {
                    urilist.add(uristring);
                    uristring = "";
                } else if (cnt + 1 < matrix.size()) {
                    uristring += ", \n";
                }
            }
            cnt++;
        }
        if (!uristring.isEmpty()) {
            urilist.add(uristring);
        }
        return urilist;
    }

    /**
     * Return the gene information into an Annotation object.
     * This query is used to generate the small table at the top of the
     * annotation page
     * @param geneid the String of the geneid to query.
     * @return an Annotation object containning the desired information
     */
    public final Annotation getGeneInfoInAnnotation(final String geneid) {
        Annotation annot = new Annotation();
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + " PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "SELECT DISTINCT ?desc ?sca ?start ?stop \n"
                + this.basegraph
                + "WHERE { \n"
                + "    <http://pbr.wur.nl/GENE#" + geneid
                + "> gene:Description ?desc ; \n"
                + "     gene:Position ?pos . \n"
                + "  ?pos pos:Start ?start ; \n"
                + "       pos:Stop ?stop ; \n"
                + "       pos:Scaffold ?sca . \n"
                + "}";
        String[] keys = {"desc", "start", "stop", "sca"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        for (ArrayList<String> row : matrix) {
            if (row.size() > 1) {
                annot.setName(geneid);
                annot.setDescription(row.get(0));
                annot.setSeq_position_start(new Integer(row.get(1)));
                annot.setSeq_position_end(new Integer(row.get(2)));
                if (row.get(3).contains("#")) {
                    annot.setScafoldname(row.get(3).split("#")[1]);
                } else {
                    annot.setScafoldname(row.get(3));
                }
            }
        }
        return annot;
    }

    /**
     * Construct a model containing all the information known (at first leve)
     * about all the markers for a given interval (+5cM on each side).
     * The given interval is defined in the String array provided.
     * This array contains (in this order!), the chromosome number, the start
     * and stop position in cM on the map.
     * @param info a String array containing in this order: Chromosome number,
     * Start and Stop position in cM.
     * @return a Model containing the first level information about all the
     * genes present in the interval + 5cM on each side.
     */
    public final Model getModelFromInputMarkers(final String[] info) {
        final String querystring =
                "PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "Construct { "
                + " ?marker mk:mapPosition ?mp . \n"
                + " ?marker mk:MarkerName ?name . \n"
                + " ?marker mk:Chromosome ?chr . \n"
                + "} \n"
                + this.basegraph
                + "WHERE { \n"
                + "    ?marker mk:mapPosition ?mp . \n"
                + "    ?marker mk:MarkerName ?name . \n"
                + "    ?marker mk:Chromosome ?chr . \n"
                + "    FILTER ( xsd:double(?mp) >= " + info[1] + " -5 && \n"
                + "            xsd:double(?mp) <= " + info[2] + " +5 && \n"
                + "            ?chr = \"" + info[0] + "\" ) \n"
                + "    ?marker ?p ?o \n"
                + "} \n";
//        System.out.println(querystring);
        return this.remoteConstructQuery(querystring);
    }

    /**
     * For given markers returns the chromosome, minimum and maximum
     * position in cM of the interval delimited by these markers.
     * @param markers an array of String containing all the markers of interest
     * @return an array of String containing in this order: Chromosome, Start
     * and Stop position in cM.
     * @throws Exception When the markers map in more than one chromosomes.
     */
    public final String[] getChrAndPositionFromInputMarkers(
            final String[] markers)
            throws SeveralMappedPositionException {
        String querystring =
                "PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "SELECT ?chr (MIN(xsd:double(?m1p)) AS ?min)"
                + "       (MAX(xsd:double(?m1p)) AS ?max) \n"
                + this.basegraph
                + "WHERE { \n"
                + "      ?m1 mk:MarkerName ?m1name . \n"
                + "      ?m1 mk:mapPosition ?m1p . \n"
                + "      ?m1 mk:Chromosome ?chr . \n"
                + "      FILTER( ?m1name IN (";
        int cnt = 0;
        for (String marker : markers) {
            querystring += '"' + marker + '"';
            if (cnt < markers.length - 1) {
                querystring += ",";
            }
            cnt += 1;
        }
        querystring += ") ) \n"
                + "} GROUP BY ?chr ";
//        System.out.println(querystring);
        String[] keys = {"chr", "min", "max"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);
        if (matrix.size() > 1) {
            throw new SeveralMappedPositionException("These markers map in more than"
                    + " one Chromosome.");
        } else {
            ArrayList<String> outlist = matrix.get(0);
            String[] output = {outlist.get(0),
                outlist.get(1).split("\\^\\^")[0],
                outlist.get(2).split("\\^\\^")[0]};
            return output;
        }
    }

    /**
     * For given markers returns the chromosome, minimum and maximum
     * position on the genome of the interval delimited by these markers.
     * @param markers an array of String containing all the markers of interest
     * @return an array of String containing in this order: Chromosome, Start
     * and Stop position on the genom.
     * @throws SeveralMappedPositionException When the markers map in more than
     * one chromosomes.
     */
    public final String[] getPhysicalChrAndPositionFromInputMarkers(
            final String[] markers)
            throws SeveralMappedPositionException {
        String querystring =
                "PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "SELECT ?chr (MIN(xsd:double(?start)) AS ?min)"
                + "       (MAX(xsd:double(?stop)) AS ?max) \n"
                + this.basegraph
                + "WHERE { \n"
                + "      ?m1 mk:MarkerName ?m1name . \n"
                + "      ?m1 mk:Position ?pos . \n"
                + "      ?pos pos:Scaffold ?chr . \n"
                + "      ?pos pos:Start ?start. \n"
                + "      ?pos pos:Start ?stop. \n"
                + "      FILTER( ?m1name IN (";
        int cnt = 0;
        for (String marker : markers) {
            querystring += '"' + marker + '"';
            if (cnt < markers.length - 1) {
                querystring += ",";
            }
            cnt += 1;
        }
        querystring += ") ) \n"
                + "} GROUP BY ?chr ";
//        System.out.println(querystring);
        String[] keys = {"chr", "min", "max"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);
        if (matrix.size() > 1) {
            throw new SeveralMappedPositionException("These markers map in more than"
                    + " one Chromosome.");
        } else {
            ArrayList<String> outlist = matrix.get(0);
            String[] output = {outlist.get(0),
                outlist.get(1).split("\\^\\^")[0],
                outlist.get(2).split("\\^\\^")[0]};
            return output;
        }
    }

    /**
     * For given markers returns the chromosome, minimum and maximum
     * position in cM of the interval delimited by these markers.
     * @param markers an array of String containing all the markers of interest
     * @param chr the chromosome number on which these markers are searched.
     * @return an array of String containing in this order: Chromosome, Start
     * and Stop position in cM.
     * @throws SeveralMappedPositionException When the markers map in more than one chromosomes
     * (which should not happen).
     */
    public final String[] getPositionFromInputMarkers(final String[] markers,
            final String chr)
            throws SeveralMappedPositionException {
        String querystring =
                "PREFIX mk:<http://pbr.wur.nl/MARKER#> \n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
                + "SELECT ?chr (MIN(xsd:double(?m1p)) AS ?min)"
                + "       (MAX(xsd:double(?m1p)) AS ?max) \n"
                + this.basegraph
                + "WHERE { \n"
                + "      ?m1 mk:MarkerName ?m1name . \n"
                + "      ?m1 mk:mapPosition ?m1p . \n"
                + "      ?m1 mk:Chromosome ?chr . \n"
                + "      FILTER( ?chr = \"" + chr + "\" && \n"
                + "              ?m1name IN (";
        int cnt = 0;
        for (String marker : markers) {
            querystring += '"' + marker + '"';
            if (cnt < markers.length - 1) {
                querystring += ",";
            }
            cnt += 1;
        }
        querystring += ") ) \n"
                + "} GROUP BY ?chr ";
//        System.out.println(querystring);
        String[] keys = {"chr", "min", "max"};
        ArrayList<ArrayList<String>> matrix =
                new ArrayList<ArrayList<String>>();
        matrix = this.remoteSelectQuery(querystring, matrix, keys);
        if (matrix.size() > 1) {
            throw new SeveralMappedPositionException("These markers map in more than"
                    + " one Chromosome.");
        } else {
            ArrayList<String> outlist = matrix.get(0);
            String[] output = {outlist.get(0),
                outlist.get(1).split("\\^\\^")[0],
                outlist.get(2).split("\\^\\^")[0]};
            return output;
        }
    }

    /**
     * This function returns a model of associated Genes and their Position for
     * a given keyword by searching the whole genome annotation.
     * @param kw a string of the keyword to search in the annotation.
     * @return a Jena model containning Genes and their position for genes
     * associated with the given keyword either by the gene description, the GO
     * terms associated with the gene, the protein description or pathway
     * description.
     */
    public final Model getGeneAssociatedWithKwFromAnnotation(final String kw) {
        Model mout = ModelFactory.createDefaultModel();

        // Add genes:
        LOG.log(Level.INFO, "Adding genes");
        mout = mout.union(this.getGeneAssociatedWithKw(kw));
        LOG.log(Level.INFO, "size: {0}", mout.size());
        // Add proteins
        LOG.log(Level.INFO, "Adding proteins");
        mout = mout.union(this.getProteineAssociatedWithKw(kw));
        LOG.log(Level.INFO, "size: {0}", mout.size());
        // Add pathways
        LOG.log(Level.INFO, "Adding pathways");
        mout = mout.union(this.getPathwayAssociatedWithKw(kw));
        LOG.log(Level.INFO, "size: {0}", mout.size());

        return mout;
    }

    /**
     * For a given keyword, returns a new model of all the genes in the genome
     * having a gene description or a GO terms containning the given keyword.
     * @param kw the string searched in the GO terms and Gene description
     * @return a Jena Model filled with the description of the genes and its
     * position
     */
    public final Model getGeneAssociatedWithKw(final String kw) {

        // Add the genes and GO terms which have a
        // description/name/process containning the keyword
        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                + "PREFIX go:"
                + "<http://www.geneontology.org/formats/oboInOwl#>\n "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "DESCRIBE ?gene ?pos \n"
                + "WHERE{ \n"
                + "    ?gene gene:Description ?desc . "
                + "    ?gene gene:FeatureName ?name . "
                + "    ?gene gene:Position ?pos . \n"
                + "    OPTIONAL { ?gene gene:Go ?go } . \n"
                + "    OPTIONAL {?go rdfs:label ?goname }. \n"
                + "    OPTIONAL {?go go:hasDefinition ?godefs }. \n"
                + "    OPTIONAL {?godefs rdfs:label ?godef }. \n"
                + "    OPTIONAL {?go go:hasExactSynonym ?gosyns }. \n"
                + "    OPTIONAL {?gosyns rdfs:label ?gosyn }. \n"
                + "  FILTER( \n"
                + "     (regex(str(?desc), \"" + kw + "\", \"i\")) || \n"
                + "     (regex(str(?goname), '" + kw + "', \"i\")) || \n"
                + "     (regex(str(?godef), '" + kw + "', \"i\")) || \n"
                + "     (regex(str(?gosyn), '" + kw + "', \"i\")) || \n"
                + "     (regex(str(?name), '" + kw + "', \"i\"))  \n"
                + "  ) . \n"
                + "} \n";

//        System.out.println(querystring);
        Model mout = this.remoteDescribeQuery(querystring);

        return mout;
    }

    /**
     * For a given keyword, returns a new model of all the genes in the genome
     * being related with a protein which have a description containning the
     * given keyword.
     * @param kw the string searched in the GO terms and Gene description
     * @return a Jena Model filled with the description of the genes and its
     * position
     */
    public final Model getProteineAssociatedWithKw(final String kw) {

        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                + "PREFIX go:"
                + "<http://www.geneontology.org/formats/oboInOwl#>\n "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "DESCRIBE ?gene ?pos \n"
                + "WHERE{ \n"
                + "    ?gene gene:Description ?desc . "
                + "    ?gene gene:Position ?pos . \n"
                + "    ?gene gene:Protein ?prot . \n"
                + "    ?prot uniprot:recommendedName ?protrname . \n"
                + "    ?protrname uniprot:fullName ?protfname . \n"
                + "    ?prot uniprot:alternativeName ?protaname . \n"
                + "    ?protaname uniprot:fullName ?protafname . \n"
                + "  FILTER( \n"
                + "     (regex(str(?protrname), '" + kw + "', \"i\")) ||\n"
                + "     (regex(str(?protfname), '" + kw + "', \"i\")) ||\n"
                + "     (regex(str(?protaname), '" + kw + "', \"i\")) ||\n"
                + "     (regex(str(?protafname), '" + kw + "', \"i\")) \n"
                + "  ) . \n"
                + "} \n";

//        System.out.println(querystring);
        Model mout = this.remoteDescribeQuery(querystring);
        return mout;
    }

    /**
     * For a given keyword, returns a new model of all the genes in the genome
     * being related with a protein which have a pathway containning the given
     * keyword.
     * @param kw the string searched in the pathway description of the
     * associated proteins
     * @return a Jena Model filled with the description of the genes and its
     * position
     */
    public final Model getPathwayAssociatedWithKw(final String kw) {

        String querystring =
                "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                + "PREFIX go:"
                + "<http://www.geneontology.org/formats/oboInOwl#>\n "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "DESCRIBE ?gene ?pos \n"
                + "WHERE{ \n"
                + "    ?gene gene:Description ?desc . "
                + "    ?gene gene:Protein ?prot . \n"
                + "    ?prot uniprot:annotation ?annot . \n"
                + "    ?annot rdfs:seeAlso ?url . \n"
                + "    ?annot rdfs:comment ?pathdesc . \n"
                + "    ?gene gene:Position ?pos . \n"
                + "  FILTER( \n"
                + "     (regex(str(?pathdesc), '" + kw + "', \"i\")) \n"
                + "  ) . \n"
                + "} \n";

//        System.out.println(querystring);
        Model mout = this.remoteDescribeQuery(querystring);
        return mout;
    }

    /**
     * Returns for a given geneid and keyword a matrix of information with the
     * objects containing the keyword and information on where it is coming
     * from.
     * See the methods on Marker2SeqUtils to convert this list to a HasMap.
     *
     * @param geneid a String of the gene identifier to use in the URI.
     * @param kw a String of the keyword to search in the gene's annotation.
     * @return an ArrayList of ArrayList of String containing the matrix of
     * results.
     */
    public final ArrayList<ArrayList<String>> getGeneSelectedAnnotation(final String geneid,
            final String kw) {
        String querystring = ""
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "SELECT distinct ?gene ?type ?obj \n"
                + this.basegraph
                + this.go
                + this.uniprot
                + "WHERE{ \n"
                + "    FILTER(?gene = <http://pbr.wur.nl/GENE#" + geneid + ">) \n"
                + "    {?gene gene:Description ?obj . \n"
                + "     ?gene a ?type } \n"
                + "    UNION { ?gene gene:FeatureName ?obj . \n"
                + "            ?gene a ?type}  \n"
                + "    UNION { ?gene gene:Position ?pos .  \n"
                + "            ?gene gene:Go ?go  . \n"
                + "            ?go rdfs:label ?obj . \n"
                + "            ?go a ?type} \n"
                + "    UNION { ?gene gene:Position ?pos . \n"
                + "            ?gene gene:Go ?go  . \n"
                + "            ?go go:hasDefinition ?godefs . \n"
                + "            ?godefs rdfs:label ?obj . \n"
                + "            ?godefs a ?type} \n"
                + "    UNION { ?gene gene:Position ?pos . \n"
                + "            ?gene gene:Go ?go  . \n"
                + "            ?go go:hasExactSynonym ?gosyns . \n"
                + "            ?gosyns rdfs:label ?obj . \n"
                + "            ?gosyns a ?type} \n"
                + "    UNION { ?gene gene:Protein ?prot . \n"
                + "            ?prot uniprot:annotation ?annot . \n"
                + "            ?annot rdfs:comment ?obj . \n"
                + "            ?annot a ?type} \n"
                + "    UNION { ?gene gene:Protein ?prot . \n"
                + "            ?prot uniprot:recommendedName ?protrname . \n"
                + "            ?gene gene:Protein ?prot . \n"
                + "            ?protrname uniprot:fullName ?obj . \n"
                + "            ?protrname a ?type} \n"
                + "    UNION { ?gene gene:Protein ?prot . \n"
                + "            ?prot uniprot:alternativeName ?protaname . \n"
                + "            ?gene gene:Protein ?prot . \n"
                + "            ?protaname uniprot:fullName ?obj . \n"
                + "            ?protaname a ?type} \n"
                + "  FILTER( regex(str(?obj), \"" + kw + "\", \"i\") ) . \n"
                + "}";
//        System.out.println(querystring);
        ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
        String[] keys = {"gene", "type", "obj"};
        matrix = this.remoteSelectQuery(querystring, matrix, keys);
        return matrix;
    }

    /**
     * Returns the list of scaffold used by genes on a given model.
     * @param mod a Jena model containing the gene and their position.
     * @return an ArrayList of String of the different scaffold.
     */
    public ArrayList<String> getScaffold(Model mod) {
        String querystring = ""
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> "
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> "
                + "SELECT DISTINCT ?sca "
                + this.basegraph
                + "WHERE{ "
                + "    ?gene gene:Position ?pos . "
                + "    ?pos pos:Scaffold ?sca ."
                + "}";
        ArrayList<String> matrix = new ArrayList<String>();
        String key = "sca";
        matrix = this.localSelectQuery(mod, querystring, matrix, key);
        return matrix;
    }

    /**
     * Return the scaffold on which is located a given marker.
     * @param marker the name of the marker to locate.
     * @return the name of the scaffold on which is located the marker
     */
    public String getScaffoldMarker(String marker) {
        String querystring = ""
                + "PREFIX mk:<http://pbr.wur.nl/MARKER#> "
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> "
                + "SELECT DISTINCT ?sca "
                + this.basegraph
                + "WHERE{ "
                + "    <http://pbr.wur.nl/MARKER#" + marker + "> mk:Position ?pos . "
                + "    ?pos pos:Scaffold ?sca ."
                + "}";
        ArrayList<String> matrix = new ArrayList<String>();
        String key = "sca";
        matrix = this.remoteSelectQuery(querystring, matrix, key);
        if (matrix.isEmpty()) {
            return null;
        } else {
            return matrix.get(0);
        }
    }

    public ArrayList<Annotation> getGeneFromPathwayOnChromosome(String pathway,
            ArrayList<String> scaffolds) {
        String querystring = ""
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "SELECT DISTINCT ?name ?sca ?start ?stop ?type ?desc \n"
                + this.basegraph
                + this.uniprot
                + "WHERE{ \n"
                + "    ?gene gene:Position ?pos . \n"
                + "    ?pos pos:Scaffold ?sca . \n"
                + "    ?gene gene:Protein ?prot . \n"
                + "    ?prot uniprot:annotation ?annot . \n"
                + "    ?annot rdfs:seeAlso ?url . \n"
                + "    ?annot rdfs:comment ?pathdesc . \n"
                + "  FILTER( \n"
                + "     (regex(str(?pathdesc), '" + pathway + "', 'i')) && (\n";
        for (int cnt = 0; cnt < scaffolds.size(); cnt++) {
            if (cnt + 1 < scaffolds.size()) {
                querystring += "         (?sca = <" + scaffolds.get(cnt) + "> ) || \n";
            } else {
                querystring += "         (?sca = <" + scaffolds.get(cnt) + "> ) \n";
            }

        }
        querystring += "     ) \n"
                + "  ) . \n"
                + "    ?gene gene:Description ?desc . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    ?gene gene:FeatureName ?name . \n"
                + "    ?gene gene:FeatureType ?type . \n"
                + "} \n";
//        System.out.println(querystring);

        ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
        ArrayList<Annotation> annotationlist = new ArrayList<Annotation>();

        String[] keys = {"name", "sca", "start", "stop", "type", "desc"};
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            String scaffold;
            if (rows.get(1).contains("#")) {
                String[] tmp = rows.get(1).trim().split("/", 4);
                scaffold = tmp[tmp.length - 1].split("#")[1];
            } else {
                scaffold = rows.get(1);
            }
            Annotation annot = new Annotation();
            annot.setName(rows.get(0).trim());
            annot.setScafoldname(scaffold);
            annot.setSeq_position_start(new Integer(rows.get(2).trim()));
            annot.setSeq_position_end(new Integer(rows.get(3).trim()));
            annot.setType(rows.get(4).trim());
            if (rows.size() == 6) {
                annot.setDescription(rows.get(5).trim());
            }
            annotationlist.add(annot);
        }
        LOG.log(Level.INFO, "annotations : {0}",
                Integer.toString(annotationlist.size()));
        return annotationlist;
    }

    /**
     * Get all the genes linked to proteins interacting with proteins in a given
     * pathway.
     * @param pathway a String of the pathway name
     * @return a ArrayList of Annotation which represents all the genes related
     * to proteins interacting with the proteins in the given pathway.
     */
    public ArrayList<Annotation> getGeneLinkedToPathwayByPPI(String pathway) {
        String querystring = ""
                + "PREFIX gene:<http://pbr.wur.nl/GENE#> \n"
                + "PREFIX pos:<http://pbr.wur.nl/POSITION#> \n"
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX uniprot:<http://purl.uniprot.org/core/> \n"
                + "SELECT DISTINCT ?name ?sca ?start ?stop ?type ?desc \n"
                + this.basegraph
                + this.uniprot
                + this.ppi
                + "WHERE{  \n"
                + "    ?prot uniprot:annotation ?annot . \n"
                + "    ?annot rdfs:seeAlso ?url . \n"
                + "    ?annot rdfs:comment ?pathdesc . \n"
                + "    FILTER( \n"
                + "        regex(str(?pathdesc), '" + pathway + "', 'i') \n"
                + "    ) . \n"
                + "    ?prot uniprot:Interact ?prot2 . \n"
                + "    ?gene gene:Protein ?prot2 . \n"
                + "    ?gene gene:Description ?desc . \n"
                + "    ?gene gene:Position ?pos . \n"
                + "    ?pos pos:Start ?start . \n"
                + "    ?pos pos:Stop ?stop . \n"
                + "    ?gene gene:FeatureName ?name . \n"
                + "    ?gene gene:FeatureType ?type . \n"
                + "}";

        ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
        ArrayList<Annotation> annotationlist = new ArrayList<Annotation>();

        String[] keys = {"name", "sca", "start", "stop", "type", "desc"};
        matrix = this.remoteSelectQuery(querystring, matrix, keys);

        for (ArrayList<String> rows : matrix) {
//            System.out.println(rows);
            String scaffold;
            if (rows.get(1).contains("#")) {
                String[] tmp = rows.get(1).trim().split("/", 4);
                scaffold = tmp[tmp.length - 1].split("#")[1];
            } else {
                scaffold = rows.get(1);
            }
            Annotation annot = new Annotation();
            annot.setName(rows.get(0).trim());
            annot.setScafoldname(scaffold);
            annot.setSeq_position_start(new Integer(rows.get(2).trim()));
            annot.setSeq_position_end(new Integer(rows.get(3).trim()));
            annot.setType(rows.get(4).trim());
            if (rows.size() == 6) {
                annot.setDescription(rows.get(5).trim());
            }
            annotationlist.add(annot);
        }
        LOG.log(Level.INFO, "annotations : {0}",
                Integer.toString(annotationlist.size()));
        return annotationlist;
    }

    /**
     * This function returns all the genes which are present in the given model
     * and somehow related with the given GO term or its children.
     * @param model the model in which to look for the genes.
     * @param goid the GO identifier (just the number, no 'GO:') for which we
     * are looking for the related gene.
     * @return a Jena Model with the gene information and their position.
     */
    public Model getGenesRelatedWithGo(Model model, String goid) {
        List<String> golist = this.getGoChildrenList(goid);
        //System.out.println(genelist.size());
        // Add the genes and GO terms which have a
        // description/name/process containning the keyword
        Model m2 = ModelFactory.createDefaultModel();
        for (String list : golist) {
            String querystring =
                    "PREFIX gene:<http://pbr.wur.nl/GENE#> \n "
                    + "PREFIX go:<http://www.geneontology.org/formats/oboInOwl#>\n "
                    + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n "
                    + "DESCRIBE ?gene ?pos \n"
                    + "WHERE{ \n"
                    + "    ?gene gene:Go ?go . \n"
                    + "    ?gene gene:Position ?pos . \n"
                    + "  FILTER ( \n"
                    + "    ?go IN ("
                    + list
                    + "    ) \n"
                    + "  ) \n"
                    + "} \n";

//        System.out.println(endpoint);
//        System.out.println(querystring);
            Model mout = this.localDescribeQuery(model, querystring);
            m2 = m2.union(mout);
        }
        return m2;
    }

    /**
     * Return the list of all the children of a given GO term.
     * @param goid the GO identifier (just the number, no 'GO:') to which we
     * return all the children.
     * @return a list of URI representing all the children of the given GO term.
     */
    public List<String> getGoChildrenList(String goid) {
        String query = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + "SELECT ?s "
                + this.go
                + "WHERE { "
                + "  ?s rdfs:subClassOf  <http://purl.org/obo/owl/GO#GO_" + goid
                + "> option(transitive) . "
                + "}";

//        System.out.println(query);
        ArrayList<String> matrix = new ArrayList<String>();
        matrix = this.remoteSelectQuery(query, matrix, "s");

        LOG.log(Level.INFO, "go term found : {0}",
                Integer.toString(matrix.size()));

        ArrayList<String> urilist = new ArrayList<String>();
        String uristring = "";
        int cnt = 0;
        for (String row : matrix) {
            uristring += "<" + row + "> ";
            if (( cnt % 40 ) == 0) {
                urilist.add(uristring);
                uristring = "";
            } else if (cnt + 1 < matrix.size()) {
                uristring += ", \n";
            }
            cnt++;
        }
        if (!uristring.isEmpty()) {
            urilist.add(uristring);
        }
        return urilist;
    }
}
