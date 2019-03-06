/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.vektorsoft.appbox.server.test.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class ApplicationServiceTest {

	private static final String APP_IMAGE_PATH = "/content/image.png";

	@Autowired
	private ApplicationService appService;

	private Application app;

	@Before
	public void setup() {
		app = new Application();
		app.setName("app name");
		app.setHeadline("app headline");
		app.setDescription("description");
	}

	@Test
	public void testSaveApplication() {
		Application out = appService.createApplication(app);

		assertNotNull(out.getId());
		assertEquals(app.getName(), out.getName());
		assertEquals(app.getHeadline(), out.getHeadline());
		assertEquals(app.getDescription(), out.getDescription());
	}

	@Test
	public void testGetApplication() {
		Application out = appService.createApplication(app);
		Application result = appService.getApplication(out.getId());

		assertEquals(app.getName(), result.getName());
		assertEquals(app.getHeadline(), result.getHeadline());
		assertEquals(app.getDescription(), result.getDescription());
	}

	@Test
	public void listApplicationsTest() {
		var out = appService.createApplication(app);
		var apps = appService.getApplications(PageRequest.of(0, 20));
		assertNotNull(apps);
		assertEquals(1, apps.getNumberOfElements());
	}

	@Test
	public void addImageTest() throws Exception {
		Application out = appService.createApplication(app);
		InputStream imgIn = getClass().getResourceAsStream(APP_IMAGE_PATH);
		appService.setApplicationImage(imgIn, out.getId());
		// verify image hash is present
		var result = appService.getApplication(out.getId());
		assertNotNull(result.getAppImageHash());
	}
}
