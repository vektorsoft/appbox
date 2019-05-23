/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content;

import lombok.Data;

/**
 * Data transfer object for JVM basic info.
 *
 * @author Vladimir Djurovic
 */
@Data
public class JvmInfoDTO {

	private String fileName;
	private String hash;
	private long size;
}
