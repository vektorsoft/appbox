/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.content.impl;

import com.vektorsoft.appbox.server.content.ContentLocator;
import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.content.ContentStorageMapping;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.HashUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Component("fileSystemStorage")
public class FileSystemStorage implements ContentStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorage.class);
	private static final DigestUtils DIGEST = new DigestUtils(MessageDigestAlgorithms.SHA_1);

	@Autowired
	private ContentLocator contentLocator;

	@Autowired
	private ContentStorageMapping contentMapping;


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
		if (contentLocator.contentExists(expectedHash)) {
			LOGGER.error("Content with hash {} already exists", expectedHash);
			return;
		}
		Path contentDir = Path.of(contentMapping.getContentStorageLocation());
		Path contentPath = HashUtil.createLocalHashPath(contentDir, expectedHash);
		boolean result = contentPath.toFile().getParentFile().mkdirs();
		if (!result) {
			LOGGER.error("Failed to create directory ");
		}
		try {
			Files.copy(in, contentPath);
			// verify that hash matches expectedÒÒ
			String actual = DIGEST.digestAsHex(contentPath.toFile());
			if (!actual.equals(expectedHash)) {
				throw new ContentException("Hash mismatch: expected " + expectedHash + ", but was " + actual);
			}
		} catch (IOException ex) {
			throw new ContentException(ex);
		}

	}

	@Override
	public String createContent(InputStream in) throws ContentException {
		Path contentDir = Path.of(contentMapping.getContentStorageLocation());
		try {
			File tmpFile = File.createTempFile("content_", "");
			Files.copy(in, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			String hash = DIGEST.digestAsHex(tmpFile);
			if (contentLocator.contentExists(hash)) {
				LOGGER.error("Content with hash {} already exists", hash);
				return hash;
			}
			Path contentPath = HashUtil.createLocalHashPath(contentDir, hash);
			boolean result = contentPath.toFile().getParentFile().mkdirs();
			if (!result) {
				LOGGER.error("Failed to create directory ");
			}
			Files.copy(tmpFile.toPath(), contentPath);
			tmpFile.delete();
			return hash;

		} catch (IOException ex) {
			throw new ContentException(ex);
		}
	}

	@Override
	public void createApplicationConfigFile(InputStream in, String applicationId, OS os, CpuArch arch) throws ContentException {
		URI appStorageUri = contentMapping.getAppStorageLocation(applicationId);
		StringBuilder sb = new StringBuilder("application_");
		sb.append(os.toString().toLowerCase());
		if(os == OS.LINUX || os == OS.WINDOWS) {
			sb.append("_").append(arch.toString().toLowerCase());
		}
		sb.append(".xml");
		File appDir = new File(appStorageUri);
		if(!appDir.exists()) {
			var success = appDir.mkdirs();
			if(!success) {
				throw new ContentException("Failed to create application directory");
			}
		}
		File appConfigFile = new File(appDir, sb.toString());
		try {
			Files.copy(in, appConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException ex) {
			throw  new ContentException(ex);
		}

	}

	private InputStream uriToInputStream(URI uri) throws ContentException {
		try {
			File file = new File(uri);
			System.out.println("File absolute path: " + file.getAbsolutePath());
			return new FileInputStream(file.getAbsolutePath());
		} catch (FileNotFoundException ex) {
			throw new ContentException(ex);
		}
	}


}
