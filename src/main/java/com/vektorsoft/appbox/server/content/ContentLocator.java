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

import javax.swing.text.AbstractDocument;
import java.net.URI;

/**
 * Defines methods for obtaining URIs for content location.
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public interface ContentLocator {

    /**
     * Check if content with specified hash exists.
     *
     * @param hash content hash
     * @return {@code true} if content already exists, {@code false otherwise}
     * @throws ContentException if an error occurs
     */
    boolean contentExists(String hash) throws ContentException;

    /**
     * Returns URI of content based on it's hash.
     *
     * @param hash content hash
     * @return content URI
     * @throws ContentException if an error occurs
     */
    URI getContentLocation(String hash) throws ContentException;
    
    URI getApplicationConfigLocation(String applicationId, OS os, CpuArch arch) throws ContentException;

    URI getJvmLocation(String version, OS os, CpuArch arch) throws ContentException;

}
