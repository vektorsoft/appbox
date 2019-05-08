/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.content.entity.JvmDistribution;
import com.vektorsoft.appbox.server.content.entity.JvmImplementation;
import com.vektorsoft.appbox.server.content.entity.JvmProvider;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import java.io.InputStream;

/**
 * Defines methods concerned with getting data into and from storage.
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */

public interface ContentStorage {

    InputStream getAppRunConfigFile(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    InputStream getBinaryData(String hash) throws ContentException;

    /**
     * Returns input stream for JVM binary distribution data.
     *
     */
    InputStream getJvmData(JvmProvider provider,
                           String jdkVersion,
                           JvmDistribution distribution,
                           JvmImplementation implementation,
                           OS os,
                           CpuArch cpuArch,
                           String semVer) throws ContentException;

    /**
     * Store content from input file into backend storage. Content hash must match provided hash. If hashes do not match,
     * an exception will be thrown.
     *
     * @param in content input stream
     * @param expectedHash expected content hash
     * @throws ContentException if an error occurs
     */
    void createContent(InputStream in, String expectedHash) throws ContentException;

    /**
     * Creates content from input stream and returns it's hash.
     *
     * @param in input stream data
     * @return content hash (SHA-1)
     * @throws ContentException if an error occurs
     */
    String createContent(InputStream in) throws ContentException;

    /**
     * Create runtime configuration file for application with given ID. File name will be generated based on OS and CPU architecture.
     *
     * @param in content input stream
     * @param applicationId application ID
     * @param os target operating system
     * @param arch target CPU architecture
     * @throws ContentException
     */
    void createApplicationConfigFile(InputStream in, String applicationId, OS os, CpuArch arch) throws ContentException;
}
