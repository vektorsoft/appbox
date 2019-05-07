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

import java.net.URI;

/**
 * Defines methods for mapping content storage logical operations to actual URIs. Implementation can used local
 * file system, networks or what ever is mappable by URI to provide actual storage locations.
 */
public interface ContentStorageMapping {

	/**
	 * Retrieves URI of storage root.
	 *
	 * @return storage root URI
	 */
	URI getStorageRootLocation();

	URI getContentStorageLocation();

	URI getLauncherStorageLocation();

	URI getAppStorageLocation(String applicationId);

	URI getJvmStorageLocation(String version, OS os, CpuArch cpuArch);
}
