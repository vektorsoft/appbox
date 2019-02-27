/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.content.impl;

import com.vektorsoft.appbox.server.content.ContentStorageService;
import com.vektorsoft.appbox.server.content.ContentLocator;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.xapps.deployer.client.DeployerException;
import com.vektorsoft.xapps.deployer.client.HashCalculator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Service("fileSystemStorageService")
public class FileSystemStorageService implements ContentStorageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);
    
    @Autowired
    private ContentLocator contentLocator;
    
     @Value("${content.storage.root}")
    private String storageRootPath;
    
    private final String binaryDataDir = "content";
    

    @Override
    public InputStream getAppRunConfigFile(String applicationId, OS os, CpuArch arch) throws ContentException {
	URI uri = contentLocator.getApplicationConfigLocation(applicationId, os, arch);
	return uriToInputStream(uri);
    }

    @Override
    public InputStream getAppLauncher(String applicationId, OS os, CpuArch arch) throws ContentException {
	URI uri = contentLocator.getAppLauncherLocation(applicationId, os, arch);
	return uriToInputStream(uri);
    }


    @Override
    public InputStream getBinaryData(String hash) throws ContentException {
	URI uri = contentLocator.getContentLocation(hash);
	return uriToInputStream(uri);
    }

    @Override
    public void createContent(InputStream in, String expectedHash) throws ContentException {
	if(contentLocator.contentExists(expectedHash)) {
	    LOGGER.error("Content with hash {} already exists", expectedHash);
	    throw new ContentException("Content with given has already exists. Hash: " + expectedHash);
	}
	Path contentDir = Path.of(storageRootPath, binaryDataDir);
	Path contentPath = HashCalculator.createHashPath(contentDir.toString(), expectedHash);
	boolean result = contentPath.toFile().getParentFile().mkdirs();
	if(!result) {
	    LOGGER.error("Failed to create directory ");
	}
	try {
	    Files.copy(in, contentPath);
	    // verify that hash matches expected
	    String actual = HashCalculator.fileHash(contentPath.toFile());
	    if(!actual.equals(expectedHash)) {
		throw new ContentException("Hash mismatch: expected " + expectedHash + ", but was " + actual);
	    }
	} catch(IOException | DeployerException ex) {
	    throw new ContentException(ex);
	}
	
    }
    
    
    
    private InputStream uriToInputStream(URI uri) throws ContentException {
	try {
	    return new FileInputStream(new File(uri));
	} catch(FileNotFoundException ex) {
	    throw new ContentException(ex);
	}
    }
    
    
    
}
