/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
