/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.content.entity;

import com.vektorsoft.appbox.server.content.JvmInfoDTO;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.modelmapper.ModelMapper;

import javax.persistence.*;

/**
 * Entity for JDK/JRE binary distribution.
 *
 * @author Vladimir Djurovic
 */
@Entity
@Table(name = "jvm_binary")
@Data
public class JvmBinary {

	public static JvmProvider DEFAULT_PROVIDER = JvmProvider.OPENJDK;
	public static JvmDistribution DEFAULT_DISTRIBUTION = JvmDistribution.JRE;
	public static JvmImplementation DEFAULT_IMPLEMENTATION = JvmImplementation.OPENJ9;

	@Transient
	private final ModelMapper mapper;

	public JvmBinary() {
		mapper = new ModelMapper();
	}

	public JvmBinary(JvmBinary source) {
		this();
		provider = source.provider;
		jdkVersion = source.jdkVersion;
		distribution = source.distribution;
		implementation = source.implementation;
		os = source.os;
		cpuArch = source.cpuArch;
		semVer = source.semVer;
		fileName = source.fileName;
		hash = source.hash;
		size = source.size;
	}

	@Id
	@Column(name = "id")
	@GenericGenerator(name = "id_generator", strategy = "com.vektorsoft.appbox.server.util.IdGenerator")
	@GeneratedValue(generator = "id_generator")
	private String id;

	@Enumerated(EnumType.STRING)
	private JvmProvider provider;

	private String jdkVersion;

	@Enumerated(EnumType.STRING)
	private JvmDistribution distribution;

	@Enumerated(EnumType.STRING)
	private JvmImplementation implementation;

	@Enumerated(EnumType.STRING)
	private OS os;

	@Enumerated(EnumType.STRING)
	private CpuArch cpuArch;

	private String semVer;
	private String fileName;
	private String hash;
	private Long size;

	public JvmInfoDTO convertToDTO() {
		return mapper.map(this, JvmInfoDTO.class);
	}

}
