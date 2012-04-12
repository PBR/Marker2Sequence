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
import com.hp.hpl.jena.util.FileManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This gives IO support to semantic Model.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class ModelIO {

    /** The logger. */
    private final Logger log = Logger.getLogger(
            ModelIO.class.getName());

    /**
     *
     * RDF Reading
     *
     */
    /**
     * Reads the RDF from a given file and returns the associated model.
     * @param filename string of the file to read
     * @return the Jena Model contained in the given file
     */
    public final Model readRdf(final String filename) {
        log.log(Level.INFO, "Read model from: {0}", filename);
        // create an empty model
        final Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        final InputStream instream = FileManager.get().open(filename);
        if (instream == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }

        // read the RDF/XML file
        model.read(instream, null);

        return model;

    }

    /**
     *
     * Write Model (to stdout or file)
     *
     */
    /**
     * Print in standard output the RDF representation of the model.
     * @param model a Jena Model to write out
     */
    public final void displayModel(final Model model) {
        // now write the model in XML form to a file
        model.write(System.out);
    }

    /**
     * Print in standard output the N3 representation of the model.
     * @param model a Jena Model.
     */
    public final void displayModelN3(final Model model) {
        model.write(System.out, "TURTLE");
    }

    /**
     * Write to a file with the given filename the RDF representation of the
     * model.
     * @param model a Jena Model to write to a file
     * @param filename the name of the file to write into
     * @throws IOException when something goes wrong while writting
     */
    public final void printModelToFile(final Model model, final String filename)
            throws IOException {
        final FileOutputStream out = new FileOutputStream(filename);
        // now write the model in XML form to a file
        model.write(out);
        out.close();
        log.log(Level.INFO, "Write model in RDF in: {0}", filename);
    }

    /**
     * Write to a file with the given filename the RDF representation of the
     * model.
     * @param model a Jena Model to write to a file
     * @param filename the name of the file to write into
     * @param format the format in which to output the model. Accepted values
     * are: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE" and "N3".
     * @throws IOException when something goes wrong while writting
     */
    public final void printModelToFile(final Model model, final String filename,
            final String format)
            throws IOException {
        final FileOutputStream out = new FileOutputStream(filename);
        // now write the model in XML form to a file
        model.write(out, format);
        out.close();
        log.log(Level.INFO, "Write model in RDF in: {0}", filename);
    }

    /**
     * Print the given Model in N3 format.
     * @param out the stream in which outputing the model
     * @param model the Jena Model to output
     * @throws IOException if something goes wrong while outputing the model
     */
    public final void printModelN3(final PrintStream out, final Model model)
            throws IOException {
        // list the statements in the Model
        model.write(out, "turtle");
    }

    /**
     * Print in standard output the N3 representation of the model.
     * @param model a Jena Model.
     * @throws IOException if something goes wrong while outputing the model
     */
    public final void printModelN3(final Model model) throws IOException {
        this.printModelN3(System.out, model);
    }
}
