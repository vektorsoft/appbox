/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content.entity;

/**
 * Denotes type of JVM distribution:
 * <ul>
 *     <li>{@code JDK} - complete JDK</li>
 *     <li>{@code JRE} - only runtime environment</li>
 * </ul>
 */
public enum JvmDistribution {
	JDK,JRE;
}
