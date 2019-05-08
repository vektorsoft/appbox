/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.content.entity.JvmBinary;
import com.vektorsoft.appbox.server.content.entity.JvmDistribution;
import com.vektorsoft.appbox.server.content.entity.JvmImplementation;
import com.vektorsoft.appbox.server.content.entity.JvmProvider;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Monitors file system for new JVM distributions.
 *
 * @author Vladimir Djurovic
 */
@Component
public class FileSystemJVMBinariesMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemJVMBinariesMonitor.class);

	@Autowired
	private ContentStorageMapping contentStorageMapping;
	@Autowired
	private JvmBinaryRepository jvmBinaryRepository;
	@Autowired
	private ContentStorage contentStorage;

	@Scheduled(initialDelayString = "${jvm.monitor.initdelay.ms}", fixedRateString = "${jvm.monitor.rate.ms}")
	public void runJvmBinaryCheck() {
		LOGGER.info("Running JVM update check");
		var jvmStorageLocationUri = contentStorageMapping.getJvmStorageLocation();
		LOGGER.debug("JVM storage URI: {}", jvmStorageLocationUri);
		var jvmDir = new File(jvmStorageLocationUri);
		LOGGER.debug("JVM storage directory: {}", jvmDir.getAbsolutePath());
		try {
			Files.list(jvmDir.toPath()).forEach(f -> processJvmFile(f));
			// cleanup - delete all files
			Files.list(jvmDir.toPath()).forEach(f -> f.toFile().delete());

		} catch(IOException ex) {
			LOGGER.error("Error reading JVM storage directory", ex);
		}
	}

	private void processJvmFile(Path filePath) {
		var jvmFile = filePath.toFile();
		LOGGER.debug("Processing JVM file {}", jvmFile.getName());
		if(!isValidFileName(jvmFile.getName())) {
			LOGGER.info("Invalid file name {}", jvmFile.getName());
			return;
		}
		// create JVM entity
		var parts = jvmFile.getName().split("-");
		JvmBinary jvm = new JvmBinary();
		jvm.setProvider(JvmProvider.valueOf(parts[0].toUpperCase()));
		jvm.setJdkVersion(parts[1]);
		jvm.setDistribution(JvmDistribution.valueOf(parts[2].toUpperCase()));
		jvm.setImplementation(JvmImplementation.valueOf(parts[3].toUpperCase()));
		jvm.setOs(OS.valueOf(parts[4].toUpperCase()));
		jvm.setCpuArch(CpuArch.valueOf(parts[5].toUpperCase()));
		jvm.setFileName(jvmFile.getName());
		jvm.setSize(jvmFile.length());
		jvm.setSemVer(inferSemVer(parts[6]));

		// check if file is already processed
		if(jvmBinaryRepository.countByFileName(jvmFile.getName()) > 0) {
			LOGGER.warn("JVM file {} is already present, skipping", jvmFile.getName());
			return;
		}

		// calculate hash
		var digestUtil = new DigestUtils(MessageDigestAlgorithms.SHA_1);
		try {
			var jvmHash = digestUtil.digestAsHex(jvmFile);
			jvm.setHash(jvmHash);
			contentStorage.createContent(new FileInputStream(jvmFile), jvmHash);
			LOGGER.info("Uploaded JVM file {} with hash {}", jvmFile.getName(), jvmHash);
			jvmBinaryRepository.save(jvm);
			LOGGER.info("Saving JVM file {} info to DB", jvmFile.getName());
		} catch(FileNotFoundException | ContentException ex) {
			LOGGER.error("Failed to upload JVM content");
			return;
		} catch(IOException ex) {
			LOGGER.error("Failed to calculate file hash", ex);
			return;
		}
	}

	private String inferSemVer(String source) {
		String semver = null;
		if(source.endsWith(".tar.gz")) {
			semver = source.substring(0, source.indexOf(".tar.gz"));
		}
		return semver;
	}

	/**
	 * Checks whether specified file name is valid per convention. File name should be in format:
	 *
	 *  {JDK provider}-{JDK version}-{distribution type}-{JVM implementation}-{OS}-{CPU architecture}-{semantic version}.tar.gz
	 *
	 *  An example of valid faile name:
	 *
	 *   openjdk-11-jre-hotspot-linux-x64-11.0.1.tar.gz
	 *
	 * @param fileName
	 * @return
	 */
	private  boolean isValidFileName(String fileName) {
		var parts = fileName.split("-");
		if(parts.length != 7) {
			return false;
		}
		// verify enum parts
		try {
			JvmProvider.valueOf(parts[0].toUpperCase());
			JvmDistribution.valueOf(parts[2].toUpperCase());
			JvmImplementation.valueOf(parts[3].toUpperCase());
			OS.valueOf(parts[4].toUpperCase());
			CpuArch.valueOf(parts[5].toUpperCase());
		} catch(IllegalArgumentException ex) {
			LOGGER.error("Invalid JVM file name", ex);
			return false;
		}
		return true;
	}
}
