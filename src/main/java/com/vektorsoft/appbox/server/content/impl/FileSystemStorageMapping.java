/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content.impl;

import com.vektorsoft.appbox.server.content.ContentStorageMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

/**
 * @author Vladimir Djurovic
 */
@Component
public class FileSystemStorageMapping implements ContentStorageMapping {

	private final File storageRootDir;
	private final File contentStorageDir;
	private final File launchersDir;
	private final File applicationsDir;

	public FileSystemStorageMapping(@Value("${content.storage.root}") String storageRootPath) {
		storageRootDir = new File(storageRootPath);
		contentStorageDir = new File(storageRootDir, "content");
		launchersDir = new File(storageRootDir, "launchers");
		applicationsDir = new File(storageRootDir, "apps");
	}


	@Override
	public URI getStorageRootLocation() {
		return storageRootDir.toURI();
	}

	@Override
	public URI getContentStorageLocation() {
		return contentStorageDir.toURI();
	}

	@Override
	public URI getLauncherStorageLocation() {
		return launchersDir.toURI();
	}

	@Override
	public URI getAppStorageLocation(String applicationId) {
		return Path.of(applicationsDir.getAbsolutePath(), applicationId).toUri();
	}
}
