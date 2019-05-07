/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.test.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class FileSystemMappingTest {

	@Autowired
	private ContentStorageMapping storageMapping;
	private Path currentDir;

	@Before
	public void init(){
		currentDir = Path.of(System.getProperty("user.dir"));
	}

	@Test
	public void storageRootTest() {
		URI uri = storageMapping.getStorageRootLocation();
		assertNotNull(uri);
		Path path = Path.of(uri);
		assertTrue(path.startsWith(currentDir));
		assertTrue(path.endsWith(Path.of(currentDir.toString(), "target", "content")));
	}

	@Test
	public void contentStorageTest() {
		URI uri = storageMapping.getContentStorageLocation();
		Path path = Path.of(uri);
		assertTrue(path.startsWith(currentDir));
		assertTrue(path.endsWith(Path.of(currentDir.toString(), "target", "content", "content")));
	}

	@Test
	public void launchersStorageTest() {
		URI uri = storageMapping.getLauncherStorageLocation();
		Path path = Path.of(uri);
		assertTrue(path.startsWith(currentDir));
		assertTrue(path.endsWith(Path.of(currentDir.toString(), "target", "content", "launchers")));
	}

	@Test
	public void appStorageLocationTest() {
		URI uri = storageMapping.getAppStorageLocation("appid");
		Path path = Path.of(uri);
		assertTrue(path.startsWith(currentDir));
		assertTrue(path.endsWith(Path.of(currentDir.toString(), "target", "content", "apps", "appid")));
	}

	@Test
	public void jvmStorageLocationTest() {
		URI uri = storageMapping.getJvmStorageLocation("11", OS.LINUX, CpuArch.X86);
		Path path = Path.of(uri);
		assertTrue(path.startsWith(currentDir));
		assertTrue(path.endsWith(Path.of(currentDir.toString(), "target", "content", "jvm", "11", "linux", "x86")));
	}
}
