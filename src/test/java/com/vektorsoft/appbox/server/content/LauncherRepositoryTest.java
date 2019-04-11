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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

/**
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class LauncherRepositoryTest {

	@Autowired
	private JvmLauncherRepository repository;

	@Test
	public void testSaveLancher() {
		JvmLauncher launcher = new JvmLauncher();
		launcher.setOs(OS.LINUX);
		launcher.setCpuArch(CpuArch.X86);
		launcher.setHash("123456abc");
		launcher.setVersion(1);

		JvmLauncher out = repository.save(launcher);
		assertNotNull(out);
	}

	@Test
	public void findCurrentLauncherTest() {
		JvmLauncher launcher1 = new JvmLauncher();
		launcher1.setOs(OS.LINUX);
		launcher1.setCpuArch(CpuArch.X86);
		launcher1.setHash("123456abc");
		launcher1.setVersion(1);
		repository.save(launcher1);

		JvmLauncher launcher2 = new JvmLauncher();
		launcher2.setOs(OS.LINUX);
		launcher2.setCpuArch(CpuArch.X86);
		launcher2.setHash("123456abcqwe");
		launcher2.setVersion(4);
		repository.save(launcher2);

		JvmLauncher out = repository.findFirstByOsAndCpuArchOrderByVersionDesc(OS.LINUX, CpuArch.X86);
		assertEquals(launcher2.getHash(), out.getHash());

	}

	@Test
	public void findLauncherEmpty() {
		JvmLauncher out = repository.findFirstByOsAndCpuArchOrderByVersionDesc(OS.LINUX, CpuArch.X86);
		assertNull(out);
	}
}
