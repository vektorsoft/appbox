/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content.converters;

import com.vektorsoft.appbox.server.content.entity.JvmDistribution;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts string to matching value of {@code JvmDistribution}
 *
 * @author Vladimir Djurovic
 */
@Component
public class JvmDistributionConverter implements Converter<String, JvmDistribution> {

	@Override
	public JvmDistribution convert(String source) {
		return JvmDistribution.valueOf(source.toUpperCase());
	}
}
