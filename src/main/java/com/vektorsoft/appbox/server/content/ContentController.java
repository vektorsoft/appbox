/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
	public @ResponseBody
	byte[] getBinaryData(@PathVariable("hash") String hash) throws ContentException, IOException {
		InputStream in = storageService.getBinaryData(hash);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		byte[] buffer = new byte[1024];
		while (read != -1) {
			read = in.read(buffer);
			if (read != -1) {
				out.write(buffer, 0, read);
			}
		}
		out.close();
		return out.toByteArray();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jvm/{version}/{os}/{arch}")
	public @ResponseBody
	byte[] downloadJvm(@PathVariable("version") String version,
					   @PathVariable("os") String os,
					   @PathVariable("arch") String arch) throws ContentException, IOException {
		InputStream in = storageService.getJvmData(version, OS.valueOf(os.toUpperCase()), CpuArch.valueOf(arch.toUpperCase()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		byte[] buffer = new byte[1024];
		while (read != -1) {
			read = in.read(buffer);
			if (read != -1) {
				out.write(buffer, 0, read);
			}
		}
		out.close();
		return out.toByteArray();

	}
}