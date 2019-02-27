/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server;

import com.vektorsoft.appbox.server.deployment.impl.DeploymentProcessTask;
import java.io.File;
import java.util.function.Function;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class AppboxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppboxServerApplication.class, args);
	}
	
	@Bean
	public Function<File, DeploymentProcessTask> deploymentTaskFactory() {
	    return source ->  processTask(source);
	}
	
	@Bean
	@Scope("prototype")
	public DeploymentProcessTask processTask(File source) {
	    return new DeploymentProcessTask(source);
	}
}
