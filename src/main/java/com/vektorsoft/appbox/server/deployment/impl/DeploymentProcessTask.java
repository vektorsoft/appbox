/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment.impl;

import com.vektorsoft.appbox.server.apps.Application;
import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.deployment.AppDeploymentStatusRepository;
import com.vektorsoft.appbox.server.deployment.entity.AppDeploymentStatus;
import com.vektorsoft.appbox.server.deployment.entity.DeploymentStatus;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.util.AppBoxConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.vektorsoft.appbox.server.util.HashUtil;
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
	private AppDeploymentStatus status;

	@Autowired
	private ContentStorage storageService;
	@Autowired
	private AppDeploymentStatusRepository deploymentStatusRepo;

	public DeploymentProcessTask(File file) {
		this.deploymentArchive = file;
	}

	@Override
	public void run() {
		LOGGER.info("Start processing deployment archive {}", deploymentArchive.getName());
		status.setCurrentStatus(DeploymentStatus.IN_PROGRESS);
		deploymentStatusRepo.save(status);
		LOGGER.debug("Setting deployment task status to {}", DeploymentStatus.IN_PROGRESS);
		ZipUtil.explode(deploymentArchive);
		File configFile = new File(deploymentArchive, AppBoxConstants.DEPLOYMENT_CONFIG_FILE_NAME);
		File diffFile = new File(deploymentArchive, AppBoxConstants.DEPLOYMENT_DIFF_FILE_NAME);
		if (!checkRequiredFile(configFile) || !checkRequiredFile(diffFile)) {
			return;
		}
		try {
			Document configDoc = createXmlDocument(configFile);
			Document diffDoc = createXmlDocument(diffFile);
			processDeploymentDiff(diffDoc);

			// create app launch configuration files
			AppConfigFileCreator creator = new AppConfigFileCreator(configDoc, storageService);
			creator.createAppConfigFile();
			status.setCurrentStatus(DeploymentStatus.SUCCESS);
			deploymentStatusRepo.save(status);
			LOGGER.info("Archive {} deployed successfully", deploymentArchive.getName());
		} catch (DeploymentException | ContentException ex) {
			LOGGER.error("Failed to deploy archive", ex);
			status.setCurrentStatus(DeploymentStatus.FAILED);
			status.setDetails(ex.getMessage());
			deploymentStatusRepo.save(status);
		}

	}

	private boolean checkRequiredFile(File file) {
		boolean exists = true;
		if(!file.exists()) {
			exists = false;
			LOGGER.error("File {} does not exist", file.getName());
			status.setCurrentStatus(DeploymentStatus.FAILED);
			status.setDetails("Required file " + file.getName() +  "not found");
			deploymentStatusRepo.save(status);
		}
		return exists;
	}

	public void init(Application app) {
		LOGGER.debug("Initializing deployment task with app id {}", app.getId());
		status = deploymentStatusRepo.save(new AppDeploymentStatus(deploymentArchive.getName(), app));
		LOGGER.info("Initialized deployment task {}", deploymentArchive.getName());
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
			Path path = HashUtil.createLocalHashPath(startPath, hash);
			LOGGER.debug("Copying content from {}", path.toString());
			InputStream in = new BufferedInputStream(new FileInputStream(path.toFile()));
			storageService.createContent(in, hash);
			in.close();
		} catch (IOException ex) {
			LOGGER.error("Failed to create content", ex);
			throw new ContentException(ex);
		}

	}

	private void processDeploymentDiff(Document diffDoc) throws DeploymentException, ContentException {
		iterateXmlNodes(diffDoc, "icon");
		iterateXmlNodes(diffDoc, "dependency");
		iterateXmlNodes(diffDoc, "splash-screen");
	}

}
