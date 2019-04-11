/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.vektorsoft.appbox.server.util;

import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;

/**
 * Utility class containing constants used in application.
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public final class AppBoxConstants {
    
    /**
     * Template for configuration file name. Parameters are OS and CPU architecture. Valid configuration 
     * file name would be in form {@code config_win_x86}.
     * 
     * @see CpuArch
     * @see OS
     */
    public static final String CONFIG_FILE_NAME_TEMPLATE = "application_%s_%s.xml";
    
    /**
     * Template for launcher file name. Parameters are OS and CPU architecture. Valid launcher 
     * file name could be {@code launcher_win_x64}
     * 
     * @see CpuArch
     * @see OS
     */
    public static final String LAUNCHER_FILE_NAME_TEMPLATE = "launcher_%s_%s";
    
    /**
     * HTTP header containing URL of deployment status.
     */
    public static final String DEPLOYMENT_STATUS_HEADER = "X-Deployment-Status";
    
    /**
     * Content type fo deployment request.
     */
    public static final String DEPLOYMENT_CONTENT_TYPE = "application/zip";
    
    /**
     * File name of deployment configuration file.
     */
    public static final String DEPLOYMENT_CONFIG_FILE_NAME = "deployer-config.xml";

    /**
     * File name of deployment diff file.
     */
    public static final String DEPLOYMENT_DIFF_FILE_NAME = "diff.xml";

    private AppBoxConstants() {
	
    }
}
