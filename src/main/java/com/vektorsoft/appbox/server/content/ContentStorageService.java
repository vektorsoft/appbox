/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

public interface ContentStorageService {

    InputStream getAppRunConfigFile(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    InputStream getAppLauncher(String applicationId, OS os, CpuArch arch) throws ContentException;
    
    InputStream getBinaryData(String hash) throws ContentException;
    
    void createContent(InputStream in, String expectedHash) throws ContentException;
}
