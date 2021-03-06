/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.test;

import com.vektorsoft.appbox.server.content.FileSystemContentLocatorTest;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.AppBoxConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public final class TestUtil {

	public static final String MOCK_HASH = "49b97dc6dfffb11f90b3ea378dd3bc19f5d44b7d";
	private static final DigestUtils DIGEST = new DigestUtils(MessageDigestAlgorithms.SHA_1);

	public static void createTestContent() throws Exception {
		File contentDir = new File("target", "content");
		File dataDir = new File(contentDir, "content");
		dataDir.mkdirs();
		// create applications directory
		File appsDir = new File(contentDir, "apps");
		if(!appsDir.exists()) {
			appsDir.mkdirs();
		}
		// create launchers directory
		File launchersDir = new File(contentDir, "launchers");
		if(!launchersDir.exists()) {
			launchersDir.mkdirs();
		}
		// create mock launcher files
		for(var os : OS.values()) {
			for(var cpu : CpuArch.values()) {
				var fileName = String.format(AppBoxConstants.LAUNCHER_FILE_NAME_TEMPLATE, os.toString().toLowerCase(), cpu.toString().toLowerCase());
				File mockLauncher = new File(launchersDir, fileName);
				FileOutputStream fout = new FileOutputStream(mockLauncher);
				fout.write(1);
				fout.close();
			}
		}


		String hash = DIGEST.digestAsHex(new File(FileSystemContentLocatorTest.class.getResource("/content/mock_content").toURI()));
		String[] parts = new String[]{
				hash.substring(0, 2),
				hash.substring(2, 4),
				hash.substring(4, 6)
		};
		Path fileDir = Path.of("target", "content", "content", parts[0], parts[1], parts[2]);
		fileDir.toFile().mkdirs();
		Path contentPath = Path.of("target", "content", "content", parts[0], parts[1], parts[2], hash);
		if(!contentPath.toFile().exists()) {
			Files.copy(FileSystemContentLocatorTest.class.getResourceAsStream("/content/mock_content"), contentPath, StandardCopyOption.REPLACE_EXISTING);
		}


	}


}
