/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.deployment;

import com.vektorsoft.appbox.server.test.TestConfig;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(properties = "content.storage.root=target/content")
public class DeploymentServiceTest {
    
    @Autowired
    private DeploymentService deplyomentService;

    @Test
    public void testDeploymentConfig() throws Exception {
	URL configUrl = getClass().getResource("/deployer-config.xml");
	Path configPath = Path.of(configUrl.toURI());
	String configData = Files.readString(configPath);
	String out = deplyomentService.processConfigData(configData);
	
    }
}
