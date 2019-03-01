/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.util;

import org.junit.Test;

import javax.persistence.SecondaryTable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Contains test cases for {@link IdGenerator} class.
 *
 * @author Vladimir Djurovic
 */
public class IdGeneratorTest {

	private IdGenerator generator = new IdGenerator();

	/**
	 * Generate a number of IDs and make sure they are unique.
	 */
	@Test
	public void testUniqueness() {
		int count = 50;
		Set<Serializable> ids = new HashSet<>();
		for(int i = 0;i < count;i++) {
			ids.add(generator.generate(null, null));
		}
		assertEquals(count, ids.size());
	}
}
