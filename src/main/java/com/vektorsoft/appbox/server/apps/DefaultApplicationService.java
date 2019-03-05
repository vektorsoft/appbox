/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author Vladimir Djurovic
 */
@Service
public class DefaultApplicationService implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Override
	public Application createApplication(Application app) {
		return applicationRepository.save(app);
	}

	@Override
	public Application getApplication(String id) {
		return applicationRepository.findById(id).get();
	}

	@Override
	public void setApplicationImage(InputStream in, String appId) {

	}

	@Override
	public Page<Application> getApplications() {
		return null;
	}
}
