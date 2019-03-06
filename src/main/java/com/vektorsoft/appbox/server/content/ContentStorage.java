/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import java.io.InputStream;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */

public interface ContentStorage {

    InputStream getAppRunConfigFile(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    InputStream getAppLauncher(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    InputStream getBinaryData(String hash) throws ContentException;
    
    void createContent(InputStream in, String expectedHash) throws ContentException;

    /**
     * Creates content from input stream and returns it's hash.
     *
     * @param in input stream data
     * @return content hash (SHA-1)
     * @throws ContentException if an error occurs
     */
    String createContent(InputStream in) throws ContentException;
}