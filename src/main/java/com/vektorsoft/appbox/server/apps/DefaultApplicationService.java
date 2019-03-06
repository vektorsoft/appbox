/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.exception.ContentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author Vladimir Djurovic
 */
@Service
public class DefaultApplicationService implements ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private ContentStorage contentStorage;

	@Override
	public Application createApplication(Application app) {
		return applicationRepository.save(app);
	}

	@Override
	public Application getApplication(String id) {
		return applicationRepository.findById(id).get();
	}

	@Override
	public void setApplicationImage(InputStream in, String appId) throws ContentException {
		String hash = contentStorage.createContent(in);
		Application app = applicationRepository.findById(appId).get();
		app.setAppImageHash(hash);
		applicationRepository.save(app);
	}

	@Override
	public Page<Application> getApplications(Pageable pageable) {
		 return applicationRepository.findAll(pageable);
	}
}
