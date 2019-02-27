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
import java.net.URI;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public interface ContentLocator {

    boolean contentExists(String hash) throws ContentException;
    
    URI getContentLocation(String hash) throws ContentException;
    
    URI getApplicationConfigLocation(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    URI getAppLauncherLocation(String applicationId, OS os, CpuArch arch) throws ContentException;
}
