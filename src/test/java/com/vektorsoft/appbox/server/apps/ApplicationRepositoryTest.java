/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.vektorsoft.appbox.server.test.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vladimir Djurovic
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ApplicationRepositoryTest {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Test
	public void testSaveApplication() {
		Application app = new Application();
		app.setName("app name");
		app.setHeadline("app headline");
		app.setDescription("description");

		Application out = applicationRepository.save(app);

		assertNotNull(out.getId());
		assertEquals(app.getName(), out.getName());
		assertEquals(app.getHeadline(), out.getHeadline());
		assertEquals(app.getDescription(), out.getDescription());
	}
}
