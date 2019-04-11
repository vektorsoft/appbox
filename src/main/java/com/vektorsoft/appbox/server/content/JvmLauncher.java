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
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Entity for JVM launcher binary.
 * @author Vladimir Djurovic
 */
@Entity
@Table(name = "jvm_launcher")
@Data
public class JvmLauncher {

	@Id
	@Column(name = "launcher_id")
	@GenericGenerator(name = "id_generator", strategy = "com.vektorsoft.appbox.server.util.IdGenerator")
	@GeneratedValue(generator = "id_generator")
	private String id;

	@Enumerated(EnumType.STRING)
	private OS os;
	@Enumerated(EnumType.STRING)
	private CpuArch cpuArch;
	private String hash;
	private long size;
	private int version;
}
