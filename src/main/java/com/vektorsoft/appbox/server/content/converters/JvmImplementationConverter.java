/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content.converters;

import com.vektorsoft.appbox.server.content.entity.JvmImplementation;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convert target string to matching {@code JvmImplementation}.
 *
 * @author Vladimir Djurovic
 */
@Component
public class JvmImplementationConverter implements Converter<String, JvmImplementation> {

	@Override
	public JvmImplementation convert(String source) {
		return JvmImplementation.valueOf(source.toUpperCase());
	}
}
