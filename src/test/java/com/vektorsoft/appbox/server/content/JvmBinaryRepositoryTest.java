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
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.test.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

/**
 * Test cases for DB operations related to JVM binary distributions.
 *
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class JvmBinaryRepositoryTest {

	@Autowired
	private JvmBinaryRepository repository;

	private JvmBinary jvmBinary;

	@Before
	public void setup() {
		jvmBinary = new JvmBinary();
		jvmBinary.setProvider(JvmProvider.OPENJDK);
		jvmBinary.setJdkVersion("11");
		jvmBinary.setDistribution(JvmDistribution.JDK);
		jvmBinary.setImplementation(JvmImplementation.HOTSPOT);
		jvmBinary.setCpuArch(CpuArch.X64);
		jvmBinary.setOs(OS.LINUX);
		jvmBinary.setSemVer("11.0.1");
		jvmBinary.setFileName("openjdk-11-jdk-hotspot-linux-x64-11.0.1.tar.gz");
		jvmBinary.setHash("122344556677889900");
		jvmBinary.setSize(123456L);
	}

	@Test
	public void saveJvmBinaryTest() {
		var out = repository.save(jvmBinary);
		assertNotNull(out.getId());
	}

	@Test
	public void findLatestJvmForSpecifiedVersion() {
		var jvm1 = repository.save(jvmBinary);

		var binary2 = new JvmBinary(jvm1);
		binary2.setSemVer("11.0.1+b2");
		jvm1.setFileName("openjdk-11-jdk-hotspot-linux-x64-11.0.1+b2.tar.gz");
		binary2.setHash("abcd123445");
		binary2.setSize(20000000L);
		var jvm2 = repository.save(binary2);

		var out = repository.findFirstByProviderAndJdkVersionAndDistributionAndImplementationAndOsAndCpuArchOrderBySemVerDesc(
				JvmProvider.OPENJDK, "11", JvmDistribution.JDK, JvmImplementation.HOTSPOT, OS.LINUX, CpuArch.X64);
		assertEquals(jvm2.getId(), out.getId());
		assertEquals(jvm2.getSemVer(), out.getSemVer());
		assertEquals(jvm2.getFileName(), out.getFileName());
		assertEquals(jvm2.getHash(), out.getHash());
	}

	@Test
	public void findExactJvmVersion() {
		var jvm1 = repository.save(jvmBinary);

		var binary2 = new JvmBinary(jvm1);
		binary2.setSemVer("11.0.1+b2");
		jvm1.setFileName("openjdk-11-jdk-hotspot-linux-x64-11.0.1+b2.tar.gz");
		binary2.setHash("abcd123445");
		binary2.setSize(20000000L);
		var jvm2 = repository.save(binary2);

		var out = repository.findByProviderAndJdkVersionAndDistributionAndImplementationAndOsAndCpuArchAndSemVer(
				JvmProvider.OPENJDK, "11", JvmDistribution.JDK, JvmImplementation.HOTSPOT, OS.LINUX, CpuArch.X64, binary2.getSemVer());
		assertEquals(jvm2.getId(), out.getId());
		assertEquals(jvm2.getSemVer(), out.getSemVer());
		assertEquals(jvm2.getFileName(), out.getFileName());
		assertEquals(jvm2.getHash(), out.getHash());
	}
}
