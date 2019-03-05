/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.util;

import java.io.File;
import java.nio.file.Path;

/**
 * Utility class holding methods related to hash operations.
 *
 *
 * @author Vladimir Djurovic
 */
public final class HashUtil {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private HashUtil() {

	}

	/**
	 * Creates path to file based on it's hash and parent directory.
	 *
	 * @param origin parent directory
	 * @param hash file hash
	 * @return file path
	 */
	public static Path createLocalHashPath(File origin, String hash) {
		String[] parts = new String[]{
				hash.substring(0, 2),
				hash.substring(2, 4),
				hash.substring(4, 6),
				hash
		};
		return Path.of(origin.getAbsolutePath(), parts);
	}

	public static Path createLocalHashPath(Path origin, String hash) {
		return createLocalHashPath(origin.toFile(), hash);
	}
}
