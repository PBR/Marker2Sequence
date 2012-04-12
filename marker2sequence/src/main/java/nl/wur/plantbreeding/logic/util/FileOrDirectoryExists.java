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

import java.io.File;
import java.util.logging.Logger;

/**
 * Utility class to check if a file or directory exists (true / false).
 * @author Richard Finkers
 * @version 0.1
 * @since 0.1
 */
public class FileOrDirectoryExists {

    /** The logger */
    private static final Logger LOG = Logger.getLogger(FileOrDirectoryExists.class.getName());

    private FileOrDirectoryExists() {
    }

    /**
     * Check if a file or directory exists in the file system.
     * @param name Name of the file or directory to check
     * @return Boolean if file/directory exists
     */
    public static boolean FileOrDirectoryExists(String name) {
        File file = new File(name);
        return file.exists();
    }
}
