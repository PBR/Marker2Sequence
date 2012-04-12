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

package nl.wur.plantbreeding.exceptions;

/**
 * This class is thrown when the several positions are found for a marker.
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class SeveralMappedPositionException extends Exception {

    private static final long serialVersionUID = 654121233L;

    /**
     * Default constructor
     */
    public SeveralMappedPositionException() {}

    /**
     * Constructor for a given message.
     * @param message String of the exception message
     */
    public SeveralMappedPositionException(String message) {
        super(message);
    }

}
