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
package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.test.TestConfig;
import com.vektorsoft.appbox.server.test.TestUtil;

import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class FileSystemStorageTest {

	@Autowired
	@Qualifier("fileSystemStorage")
	private ContentStorage storageService;

	@Autowired
	private ContentLocator contentLocator;

	@BeforeClass
	public static void setupContent() throws Exception {
		TestUtil.createTestContent();

	}

	@Test
	public void createAppConfig() throws Exception {
		var in = getClass().getResourceAsStream("/deployer-config.xml");
		storageService.createApplicationConfigFile(in, "appId", OS.LINUX, CpuArch.X86);
		// verify that files exist
		var uri = contentLocator.getApplicationConfigLocation("appId", OS.LINUX, CpuArch.X86);
		assertNotNull(uri);
	}


	@Test
	public void testGetBinaryData() throws Exception {
		InputStream is = storageService.getBinaryData(TestUtil.MOCK_HASH);
		assertNotNull(is);
	}
}
