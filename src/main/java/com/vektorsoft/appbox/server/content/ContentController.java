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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RestController("contentController")
public class ContentController {

	@Autowired
	private ContentStorage storageService;

	@RequestMapping(method = RequestMethod.GET, value = "/apps/{appId}/content/config/{os}/{arch}", produces = {MediaType.APPLICATION_XML_VALUE})
	public @ResponseBody
	String getApplicationRuntimeConfig(@PathVariable("appId") String applicationId, @PathVariable("os") String os, @PathVariable("arch") String arch) throws ContentException, IOException {

		InputStream in = storageService.getAppRunConfigFile(applicationId, OS.valueOf(os.toUpperCase()), CpuArch.valueOf(arch.toUpperCase()));
		StringBuilder sb;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		return sb.toString().trim();
	}


	@RequestMapping(method = RequestMethod.GET, value = "/apps/content/{hash}")
	public ResponseEntity<InputStreamResource> getBinaryData(@PathVariable("hash") String hash) throws ContentException, IOException {
		InputStream in = storageService.getBinaryData(hash);
		return ResponseEntity.ok(new InputStreamResource(in));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jvm")
	public ResponseEntity<InputStreamResource> downloadJvm(@RequestParam(name = "provider", required = false) JvmProvider provider,
					   @RequestParam(name = "jdkversion") String jdkVersion,
					   @RequestParam(name = "distribution", required = false)JvmDistribution distribution,
					   @RequestParam(name = "implementation", required = false)JvmImplementation implementation,
					   @RequestParam(name = "os") String os,
					   @RequestParam(name = "cpu") String arch,
					   @RequestParam(name = "semver", required = false) String semVer) throws ContentException, IOException {
		var osValue = OS.valueOf(os.toUpperCase());
		var cpuValue = CpuArch.valueOf(arch.toUpperCase());
		if(provider == null) {
			provider = JvmProvider.OPENJDK;
		}
		if(distribution == null) {
			distribution = JvmDistribution.JRE;
		}
		if(implementation == null) {
			implementation = JvmImplementation.OPENJ9;
		}
		InputStream in = storageService.getJvmData(provider, jdkVersion, distribution, implementation, osValue, cpuValue, semVer);
		return ResponseEntity.ok(new InputStreamResource(in));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jvminfo")
	public @ResponseBody JvmInfoDTO getJvmBinaryInfo(@RequestParam(name = "provider", required = false) JvmProvider provider,
													 @RequestParam(name = "jdkversion") String jdkVersion,
													 @RequestParam(name = "distribution", required = false)JvmDistribution distribution,
													 @RequestParam(name = "implementation", required = false)JvmImplementation implementation,
													 @RequestParam(name = "os") String os,
													 @RequestParam(name = "cpu") String arch,
													 @RequestParam(name = "semver", required = false) String semVer) {
		var osValue = OS.valueOf(os.toUpperCase());
		var cpuValue = CpuArch.valueOf(arch.toUpperCase());
		if(provider == null)  {
			provider = JvmProvider.OPENJDK;
		}
		if(distribution == null) {
			distribution = JvmDistribution.JRE;
		}
		if(implementation == null) {
			implementation = JvmImplementation.OPENJ9;
		}
		if(semVer != null && (semVer.isBlank() || semVer.isEmpty())) {
			semVer = null;
		}
		JvmBinary binary = storageService.getJvmInfo(provider, jdkVersion, distribution, implementation, osValue, cpuValue, semVer);
		return binary.convertToDTO();
	}
}
