/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.controller;

import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.content.ContentStorageService;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RestController("contentController")
public class ContentController {

    @Autowired
    private ContentStorageService storageService;

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

    @RequestMapping(method = RequestMethod.GET, value = "/launcher/{os}/{arch}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[] getApplicationLauncher(@PathVariable("appId") String applicationId, @PathVariable("os") String os, @PathVariable("arch") String arch) throws IOException, ContentException {
	InputStream in = storageService.getAppLauncher(applicationId, OS.valueOf(os.toUpperCase()), CpuArch.valueOf(arch.toUpperCase()));
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
    
    @RequestMapping(method = RequestMethod.GET, value = "/apps/content/{hash}")
    public @ResponseBody byte[] getBinaryData(@PathVariable("hash") String hash) throws ContentException, IOException {
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
}
