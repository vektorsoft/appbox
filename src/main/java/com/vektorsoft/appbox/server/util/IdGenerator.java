/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.util;

import org.hashids.Hashids;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

/**
 * Generates unique alphanumeric IDs based on current timestamp in UTC timezone, and on random generated number.
 * It should guarantee that all IDs are unique, regardless of installation (with low collision probability).
 *
 * @author Vladimir Djurovic
 */
public class IdGenerator implements IdentifierGenerator {

	/**
	 * Salt for generated IDs. Not intended to be secure, just a common base for all installations.
	 */
	private static final String HASH_SALT = "AppBox";

	/**
	 * Mimimum lenghts for hashes.
	 */
	private static final int MIN_HASH_LENGTH = 10;

	private static final int RANDOM_BOUND = 100000;

	private static final Hashids GENERATOR = new Hashids(HASH_SALT, MIN_HASH_LENGTH);
	private static final Random RANDOM = new Random();

	/**
	 * UTC timezone ID.
	 */
	private static ZoneId UTC_ZONE_ID = ZoneId.of("UTC");


	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		long now = ZonedDateTime.now(UTC_ZONE_ID).toEpochSecond();
		return GENERATOR.encode(now, RANDOM.nextInt(RANDOM_BOUND));
	}

}
