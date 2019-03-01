/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.test;

import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Configuration
@ComponentScan (basePackages = {"com.vektorsoft.appbox.server"})
@PropertySource(value = "classpath:test.properties")
//@PropertySource(value = "classpath:application.properties")
public class TestConfig {

	@Bean
	public PostgresConfig postgresConfig() throws IOException {
		final PostgresConfig postgresConfig = new PostgresConfig(Version.V9_6_11,
				new AbstractPostgresConfig.Net("localhost", 60000),
				new AbstractPostgresConfig.Storage("appbox"),
				new AbstractPostgresConfig.Timeout(),
				new AbstractPostgresConfig.Credentials("user", "pass")
		);
		return postgresConfig;
	}

	@Bean
	@DependsOn("postgresProcess")
	public DataSource dataSource(PostgresConfig config) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setUrl("jdbc:postgresql://localhost:60000/appbox");
		ds.setUsername(config.credentials().username());
		ds.setPassword(config.credentials().password());
		return ds;
	}

	@Bean(destroyMethod = "stop")
	public PostgresProcess postgresProcess(PostgresConfig config) throws IOException {
		PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
		PostgresExecutable exec = runtime.prepare(config);
		PostgresProcess process = exec.start();
		return process;
	}

}
