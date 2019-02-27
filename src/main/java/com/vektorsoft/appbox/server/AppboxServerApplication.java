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
