/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment.impl;

import com.vektorsoft.appbox.server.content.ContentStorageService;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.util.AppBoxConstants;
import com.vektorsoft.xapps.deployer.client.HashCalculator;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Task that handles potentially long running deployment process. It will
 * process deployment archive and perform any necessary operations.
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public class DeploymentProcessTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentProcessTask.class);

    private final File deploymentArchive;

    @Autowired
    private ContentStorageService storageService;

    public DeploymentProcessTask(File file) {
	this.deploymentArchive = file;
    }

    @Override
    public void run() {
	ZipUtil.explode(deploymentArchive);
	File configFile = new File(deploymentArchive, AppBoxConstants.DEPLOYMENT_CONFIG_FILE_NAME);
	if (!configFile.exists()) {
	    LOGGER.error("Configuration files does not exist");
	    return;
	}
	try {
	    Document configDoc = createXmlDocument(configFile);
	    iterateXmlNodes(configDoc, "icon");
	    iterateXmlNodes(configDoc, "dependency");
	} catch (DeploymentException | ContentException ex) {
	    LOGGER.error("Failed to deploy archive", ex);
	} 

    }

    private Document createXmlDocument(File configFile) throws DeploymentException {
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = dbf.newDocumentBuilder();
	    Document doc = builder.parse(configFile);
	    return doc;
	} catch (SAXException | ParserConfigurationException | IOException ex) {
	    throw new DeploymentException(ex);
	}
    }

    private void iterateXmlNodes(Document doc, String tagName) throws DeploymentException, ContentException {
	NodeList tagNodes = doc.getElementsByTagName(tagName);
	for (int i = 0; i < tagNodes.getLength(); i++) {
	    Node node = tagNodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element element = (Element) node;
		String hash = element.getAttribute("hash");
		if (hash == null || hash.isEmpty() || hash.isBlank()) {
		    LOGGER.error("Invalid hash found");
		    throw new DeploymentException("Hash can not be empty");
		}
		uploadFile(hash);
	    }
	}
    }

    private void uploadFile(String hash) throws ContentException {
	try {
	    LOGGER.debug("Creating content for hash {}", hash);
	    Path startPath = Path.of(deploymentArchive.getAbsolutePath(), "content");
	    Path path = HashCalculator.createHashPath(startPath.toString(), hash);
	    LOGGER.debug("Copying content from {}", path.toString());
	    InputStream in = new BufferedInputStream(new FileInputStream(path.toFile()));
	    storageService.createContent(in, hash);
	    in.close();
	} catch (IOException ex) {
	    LOGGER.error("Failed to create content", ex);
	    throw new ContentException(ex);
	}

    }

}
