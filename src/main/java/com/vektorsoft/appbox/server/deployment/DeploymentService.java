/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.deployment;

import com.vektorsoft.appbox.server.exception.DeploymentException;
import java.nio.file.Path;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public interface DeploymentService {

    String processConfigData(String configData) throws DeploymentException;
    
    void processContent(Path deploymentPath) throws DeploymentException;
    
    boolean validateDeploymentContent(Path deploymentPath) throws DeploymentException;
}
