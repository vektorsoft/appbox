/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public static final String CONFIG_FILE_NAME_TEMPLATE = "config_%s_%s.xml";
    
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

    private AppBoxConstants() {
	
    }
}
