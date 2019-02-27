/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.controller;

import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.deployment.DeploymentService;
import com.vektorsoft.appbox.server.util.AppBoxConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RestController("deploymentController")
@RequestMapping(value = "/deployment", consumes = {MediaType.APPLICATION_XML_VALUE})
public class DeploymentController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);
    
    @Autowired
    private DeploymentService deploymentService;

    @RequestMapping(method = RequestMethod.PUT, value = "/{appId}/config", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody String handleConfigUpload(@RequestBody String configData) throws DeploymentException {
	 return deploymentService.processConfigData(configData);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/{appId}/content", consumes = "application/zip")
    public ResponseEntity handleContenUpload(HttpServletRequest request, @PathVariable("appId") String appId) throws DeploymentException {
	String contentType = request.getContentType();
	if(!AppBoxConstants.DEPLOYMENT_CONTENT_TYPE.equals(contentType)) {
	    throw new DeploymentException("Invalid content type");
	}
	try {
	    Path tmpPath = Files.createTempFile(appId, "");
	    Files.copy(request.getInputStream(), tmpPath, StandardCopyOption.REPLACE_EXISTING);
	    boolean valid = deploymentService.validateDeploymentContent(tmpPath);
	    if(!valid) {
		throw new DeploymentException("Invalid or corrupt deployment archive");
	    }
	    deploymentService.processContent(tmpPath);
	    String statusUrl = generateDeploymentStatusUrl(request, "/status/" + tmpPath.getFileName().toString());
	    return ResponseEntity.accepted().header(AppBoxConstants.DEPLOYMENT_STATUS_HEADER, statusUrl).build();
	} catch(IOException ex) {
	    throw new DeploymentException(ex);
	}
	
    }
    
    private String generateDeploymentStatusUrl(HttpServletRequest request, String path) {
	StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://");
        sb.append(request.getServerName());
        if(request.getServerPort() != 0){
            sb.append(":").append(request.getServerPort());
        }
        sb.append(request.getContextPath());
        sb.append(path);
	LOGGER.debug("Deployment status URL: {}", sb.toString());
        return sb.toString();
    }
}