/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.content.entity.JvmLauncher;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.AppBoxConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

/**
 * This class will perform periodic checks for modifications to launcher binaries.
 *
 * @author Vladimir Djurovic
 */
@Component
public class FileSystemLauncherMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemLauncherMonitor.class);

	@Autowired
	private ContentStorageMapping contentStorageMapping;
	@Autowired
	private JvmLauncherRepository launcherRepository;
	@Autowired
	private ContentStorage contentStorage;

	@Scheduled(initialDelayString = "${launcher.monitor.initdelay.ms}", fixedRateString = "${launcher.monitor.rate.ms}")
	public void runLauncherModificationCheck() {
		LOGGER.info("Running launchers modification check");
		var launchersLocationUri = contentStorageMapping.getLauncherStorageLocation();
		LOGGER.debug("Launcher storage URI: {}", launchersLocationUri);
		var launchersDir = new File((launchersLocationUri));
		LOGGER.debug("Launcher storage directory: {}", launchersDir.getAbsolutePath());
		for(var os : OS.values()) {
			for(var arch : CpuArch.values()) {
				String fileName = String.format(AppBoxConstants.LAUNCHER_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), arch.toString().toLowerCase());
				var launcherFile = new File(launchersDir, fileName);
				LOGGER.debug("Processing launcher file {}", launcherFile.getAbsolutePath());
				var digestUtil = new DigestUtils(MessageDigestAlgorithms.SHA_1);
				try {
					var launcherHash = digestUtil.digestAsHex(launcherFile);
					var curLauncher = launcherRepository.findFirstByOsAndCpuArchOrderByVersionDesc(os, arch);
					if(curLauncher != null) {
						if(!curLauncher.getHash().equals(launcherHash)) {
							LOGGER.info("Found updated launcher for OS {} and CPU {}", os, arch);
							saveLauncher(os, arch, launcherHash, curLauncher.getVersion() + 1, launcherFile.length());
							contentStorage.createContent(new FileInputStream(launcherFile), launcherHash);
							LOGGER.debug("Launcher file uploaded successfully");
						}
					} else {
						// launcher does not exist, create one with version 1
						saveLauncher(os, arch, launcherHash, 1, launcherFile.length());
						contentStorage.createContent(new FileInputStream(launcherFile), launcherHash);
						LOGGER.debug("Launcher file uploaded successfully");
					}
				} catch(Exception ex) {
					LOGGER.error("Failed to  process launcher modification check", ex);
				}

			}
		}
		LOGGER.info("Finished checking launcher modifications");
	}

	private void saveLauncher(OS os, CpuArch arch, String hash, int version, long size) {
		JvmLauncher launcher = new JvmLauncher();
		launcher.setOs(os);
		launcher.setCpuArch(arch);
		launcher.setVersion(version);
		launcher.setHash(hash);
		launcher.setSize(size);
		launcherRepository.save(launcher);
		LOGGER.debug("Saved launcher info for OS {} and CPU {} to DB", os, arch);
	}
}
