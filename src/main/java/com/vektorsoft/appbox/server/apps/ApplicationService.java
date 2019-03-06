/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.vektorsoft.appbox.server.exception.ContentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;

public interface ApplicationService {

	Application createApplication(Application app);

	Application getApplication(String id);

	void setApplicationImage(InputStream in, String appId) throws ContentException;

	Page<Application> getApplications(Pageable pageable);
}
