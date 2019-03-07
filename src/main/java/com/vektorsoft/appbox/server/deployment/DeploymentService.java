/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.deployment;

import com.vektorsoft.appbox.server.deployment.entity.AppDeploymentStatus;
import com.vektorsoft.appbox.server.exception.DeploymentException;
import java.nio.file.Path;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public interface DeploymentService {

    String processConfigData(String configData) throws DeploymentException;
    
    void processContent(Path deploymentPath, String appId) throws DeploymentException;
    
    boolean validateDeploymentContent(Path deploymentPath) throws DeploymentException;

    AppDeploymentStatus getDeploymentStatus(String id);
}
