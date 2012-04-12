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

package nl.wur.plantbreeding.logic.util;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Heleen de Weerd
 */
public class Validation {

    /** The logger */
    private static final Logger LOG = Logger.getLogger(Validation.class.getName());

    private Validation() {
    }

    /**
     * Validate if String contains special characters
     * @param validation
     * @return boolean
     */
    public static boolean containsSpecialCharactersCheck(String validation) {
        if (validation == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("[!@#$%&|\\+?\\*;]");
        Matcher letterMatch = pattern.matcher(validation);
        if (letterMatch.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validates int
     * @param validation
     * @return boolean
     */
    public static boolean containsSpecialCharactersCheck(int validation) {
        Pattern pattern = Pattern.compile("[!@#$%&|\\+?\\*;\"\']");
        Matcher letterMatch = pattern.matcher(Integer.toString(validation));
        if (letterMatch.find()) {
            LOG.info(Integer.toString(validation));
            return true;
        } else {
            return false;
        }
    }

    /**
     * See if string contains information
     * @param nullString
     * @return string (null or given string)
     */
    public static String nullString(String nullString) {
        Pattern characters = Pattern.compile("[\\S\\W\\D]+");
        Matcher letterMatch = characters.matcher(nullString);
        if (!(letterMatch.find())) {
            return null;
        } else {
            return nullString;
        }
    }

    /**
     * See if float contains letters
     * @param floatValidation
     * @return
     */
    public static boolean floatContainsLetters(String floatValidation) {
        Pattern digits = Pattern.compile("[A-Za-z]+");
        Matcher digitMatch = digits.matcher(floatValidation);
        if (digitMatch.find()) {
            return true;
        } else {
            return false;
        }
    }
}
