/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.apps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 * DTO for application entity.
 *
 * @author Vladimir Djurovic
 */
@Data
public class ApplicationDTO {

	@JsonIgnore
	private final ModelMapper mapper = new ModelMapper();

	private String id;
	private String name;
	private String headline;
	private String description;
	private String appImageUrl;

	public Application convertToEntity() {
		return mapper.map(this, Application.class);
	}
}
