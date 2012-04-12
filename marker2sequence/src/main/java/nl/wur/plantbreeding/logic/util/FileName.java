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

import java.util.Calendar;

/**
 * Generate file name
 * Code from Roeland Voorrips
 * @authos Roeland Voorrips
 * @author Pierre-Yves Chibon -- py@chibon.fr
 */
public class FileName {

    /**
     * Returns a filename composed of YearMonthDay-HourMinuteSec-Millisecond
     * @return
     */
    public static String generateFileNameByTime() {
        Calendar now = Calendar.getInstance();
        String filename = //ImageDir +
                iStr(now.get(Calendar.YEAR), 2)
                + iStr(now.get(Calendar.MONTH) + 1, 2) + //JANUARY is Month 0
                iStr(now.get(Calendar.DAY_OF_MONTH), 2) + "-"
                + iStr(now.get(Calendar.HOUR_OF_DAY), 2) + //HOUR gives the AM or PM hour
                iStr(now.get(Calendar.MINUTE), 2)
                + iStr(now.get(Calendar.SECOND), 2) + "-"
                + iStr(now.get(Calendar.MILLISECOND), 4);
        return filename;
    }

    /**
     * Returns a filename composed of YearMonth
     * @return
     */
    public static String generateFileNameByMonth() {
        Calendar now = Calendar.getInstance();
        String filename = //ImageDir +
                iStr(now.get(Calendar.YEAR), 2)
                + iStr(now.get(Calendar.MONTH) + 1, 2);
        return filename;

    }

    /**
     * Returns a decimal string rep of i, left-padded with zeroes, to length
     * @param i
     * @param length
     * @return String
     */
    public static String iStr(int i, int length) {
        //return a decimal string rep of i, left-padded with zeroes, to length
        String s = Integer.toString(i + 1000000);
        return (s.substring(s.length() - length));
    }
}
