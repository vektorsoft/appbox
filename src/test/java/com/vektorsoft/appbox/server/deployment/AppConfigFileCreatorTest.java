/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment;

import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.content.entity.JvmLauncher;
import com.vektorsoft.appbox.server.content.JvmLauncherRepository;
import com.vektorsoft.appbox.server.deployment.impl.AppConfigFileCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import static org.mockito.Mockito.*;

/**
 * Contains test cases to verify correct generation of application config file from deployment config.
 *
 * @author Vladimir Djurovic
 */
public class AppConfigFileCreatorTest {

	private AppConfigFileCreator creator;
	private ContentStorage contentStorage;
	private JvmLauncherRepository launcherRepository;

	@Before
	public void setup() throws Exception {
		InputStream in = getClass().getResourceAsStream("/deployer-config.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(in);

		contentStorage = Mockito.mock(ContentStorage.class);
		launcherRepository = Mockito.mock(JvmLauncherRepository.class);


		creator = new AppConfigFileCreator(doc, contentStorage, launcherRepository);
	}

	@Test
	public void createConfigFile() throws Exception {
		JvmLauncher launcher = new JvmLauncher();
		launcher.setHash("122323abcd");
		when(launcherRepository.findFirstByOsAndCpuArchOrderByVersionDesc(any(), any())).thenReturn(launcher);
		creator.createAppConfigFile();
	}
}
