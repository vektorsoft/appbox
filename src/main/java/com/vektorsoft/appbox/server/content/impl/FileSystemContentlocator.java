/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.content.impl;

import com.vektorsoft.appbox.server.content.ContentLocator;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.AppBoxConstants;
import java.net.URI;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Component(value = "fileSystemLocator")
public class FileSystemContentlocator implements ContentLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemContentlocator.class);
    
    private final String launchersDir = "launchers";
    
    @Value("${content.storage.root}")
    private String storageRootPath;
    
    private final String binaryDataDir = "content";

    @Override
    public boolean contentExists(String hash) throws ContentException {
	if(hash == null || hash.isBlank() || hash.isEmpty()) {
	    throw new ContentException("Invalid hash ");
	}
	LOGGER.debug("Checking if file with hash {} exists", hash);
	String[] parts = new String[]{
	    hash.substring(0, 2),
	    hash.substring(2,4),
	    hash.substring(4,6)
	};
	Path filePath = Path.of(storageRootPath, binaryDataDir, parts[0], parts[1], parts[2], hash);
	LOGGER.debug("Calculated file path {}", filePath);
	return filePath.toFile().exists();
    }

    @Override
    public URI getContentLocation(String hash) throws ContentException {
	String[] parts = new String[]{
	    hash.substring(0, 2),
	    hash.substring(2,4),
	    hash.substring(4,6)
	};
	Path contentPath = Path.of(storageRootPath, binaryDataDir, parts[0], parts[1], parts[2], hash);
	LOGGER.debug("Binary file path: {}", contentPath);
	return contentPath.toUri();
    }

    @Override
    public URI getApplicationConfigLocation(String applicationId, OS os, CpuArch arch) throws ContentException {
	String fileName = String.format(AppBoxConstants.CONFIG_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), arch.toString().toLowerCase());
	LOGGER.debug("Config file name: {}", fileName);
	Path configFilePath = Path.of(storageRootPath, applicationId, fileName);
	LOGGER.debug("Config file path: {}", configFilePath.toString());
	return configFilePath.toUri();
    }

    @Override
    public URI getAppLauncherLocation(String applicationId, OS os, CpuArch arch) throws ContentException {
	String fileName = String.format(AppBoxConstants.LAUNCHER_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), arch.toString().toLowerCase());
	LOGGER.debug("Launcher file name: {}", fileName);
	Path launcherFilePath = Path.of(storageRootPath, launchersDir, fileName);
	
	return launcherFilePath.toUri();
    }
    
}
