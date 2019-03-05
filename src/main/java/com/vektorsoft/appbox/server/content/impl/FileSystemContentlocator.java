/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.content.impl;

import com.vektorsoft.appbox.server.content.ContentLocator;
import com.vektorsoft.appbox.server.content.ContentStorageMapping;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.AppBoxConstants;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vektorsoft.appbox.server.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Component(value = "fileSystemLocator")
public class FileSystemContentlocator implements ContentLocator {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemContentlocator.class);

	@Autowired
	private ContentStorageMapping contentStorageMapping;

	@Override
	public boolean contentExists(String hash) throws ContentException {
		if (hash == null || hash.isBlank() || hash.isEmpty()) {
			throw new ContentException("Invalid hash ");
		}
		LOGGER.debug("Checking if file with hash {} exists", hash);

		Path filePath = HashUtil.createLocalHashPath(Path.of(contentStorageMapping.getContentStorageLocation()), hash);
		LOGGER.debug("Calculated file path {}", filePath);

		return Files.exists(filePath.toAbsolutePath());
	}

	@Override
	public URI getContentLocation(String hash) throws ContentException {

		Path contentPath = HashUtil.createLocalHashPath(Path.of(contentStorageMapping.getContentStorageLocation()), hash);
		LOGGER.debug("Binary file path: {}", contentPath);
		return contentPath.toUri();
	}

	@Override
	public URI getApplicationConfigLocation(String applicationId, OS os, CpuArch arch) throws ContentException {
		String fileName = String.format(AppBoxConstants.CONFIG_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), arch.toString().toLowerCase());
		LOGGER.debug("Config file name: {}", fileName);
		Path configFilePath = Path.of(Path.of(contentStorageMapping.getAppStorageLocation(applicationId)).toString(), fileName);
		LOGGER.debug("Config file path: {}", configFilePath.toString());
		return configFilePath.toUri();
	}

	@Override
	public URI getAppLauncherLocation(String applicationId, OS os, CpuArch arch) throws ContentException {
		String fileName = String.format(AppBoxConstants.LAUNCHER_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), arch.toString().toLowerCase());
		LOGGER.debug("Launcher file name: {}", fileName);
		Path launcherFilePath = Path.of(Path.of(contentStorageMapping.getLauncherStorageLocation()).toString(), fileName);

		return launcherFilePath.toUri();
	}

}
