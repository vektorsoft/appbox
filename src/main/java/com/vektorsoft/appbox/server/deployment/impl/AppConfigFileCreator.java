/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment.impl;

import com.vektorsoft.appbox.server.content.ContentStorage;
import com.vektorsoft.appbox.server.content.JvmLauncherRepository;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.model.CpuArch;
import com.vektorsoft.appbox.server.model.OS;
import com.vektorsoft.appbox.server.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

/**
 * Creates application configuration files for install or update. This class will produce configuration
 * files for each supported platform and mark them as current version.
 *
 * @author Vladimir Djurovic
 */
public class AppConfigFileCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppConfigFileCreator.class);

	private final Document deploymentConfigFile;
	private final ContentStorage contentStorage;
	private final JvmLauncherRepository launcherRepository;

	public AppConfigFileCreator(Document deploymentConfigFile, ContentStorage contentStorage, JvmLauncherRepository launcherRepository) {
		this.deploymentConfigFile = deploymentConfigFile;
		this.contentStorage = contentStorage;
		this.launcherRepository = launcherRepository;
	}

	public void createAppConfigFile() throws DeploymentException {
		DocumentBuilder builder = createDocumentBuilder();

		try {
			Document doc = builder.newDocument();
			XPath xpath = XPathFactory.newInstance().newXPath();
			String appId = (String) xpath.compile("//application/@application-id").evaluate(deploymentConfigFile, XPathConstants.STRING);
			createBasicElements(doc, xpath);
			LOGGER.debug("Created common application runtime configuration file");
			// add platform specific parts
			for (OS os : OS.values()) {
				createOsConfig(doc, xpath, appId, os);
			}

		} catch (XPathException | TransformerException ex) {
			LOGGER.error("Failed to parse XML document");
			throw new DeploymentException(ex);
		} catch (ContentException ex) {
			LOGGER.error("Failed to create app config file", ex);
			throw new DeploymentException(ex);
		}

	}

	/**
	 * Create basic element hierarchy: application,  info, icons, dependencies
	 *
	 * @param doc
	 */
	private void createBasicElements(Document doc, XPath xPath) throws XPathException {
		var appElement = doc.createElement("application");
		doc.appendChild(appElement);
		var versionAttr = (Attr) xPath.compile("//application/@version").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedVersionAttr = (Attr) doc.importNode(versionAttr, false); // need to import nodes into new document before using them
		appElement.setAttributeNode(importedVersionAttr);
		// create info section
		var infoElement = doc.createElement("info");
		appElement.appendChild(infoElement);
		// import name element
		var nameElement = (Element) xPath.compile("//application/info/name").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedName = doc.importNode(nameElement, true);
		infoElement.appendChild(importedName);

		// import JVM element
		var jvmElement = (Element) xPath.compile("//application/jvm").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedJvm = doc.importNode(jvmElement, false);
		appElement.appendChild(importedJvm);
		// import dependencies
		var dependenciesElement = (Element) xPath.compile("//application/jvm/dependencies").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedDepsElement = doc.importNode(dependenciesElement, true);
		importedJvm.appendChild(importedDepsElement);

		// import main class
		var importedMainClass = importElement(doc, xPath, "//application/jvm/main-class", true);
		if (importedMainClass != null) {
			importedJvm.appendChild(importedMainClass);
		}

		// import JVM options
		var importedJvmOptions = importElement(doc, xPath, "//application/jvm/jvm-options", true);
		if (importedJvmOptions != null) {
			importedJvm.appendChild(importedJvmOptions);
		}

		// import system properties
		var importedSysProps = importElement(doc, xPath, "//application/jvm/system-properties", true);
		if (importedSysProps != null) {
			importedJvm.appendChild(importedSysProps);
		}

		// import splash screen
		var importedSplashScreen = importElement(doc, xPath, "//application/jvm/splash-screen", false);
		if (importedSplashScreen != null) {
			importedJvm.appendChild(importedSplashScreen);
		}

		// import server element
		var importedServer = importElement(doc, xPath, "//application/server", false);
		if (importedServer != null) {
			appElement.appendChild(importedServer);
		}

	}

	private Node importElement(Document importDoc, XPath xPath, String xpathExpression, boolean deepImport) throws XPathException {
		var element = (Element) xPath.compile(xpathExpression).evaluate(deploymentConfigFile, XPathConstants.NODE);
		Node importedElement = null;
		if (element != null) {
			importedElement = importDoc.importNode(element, deepImport);
		}
		return importedElement;
	}

	private String platformDependenciesXpathExpression(OS os) {
		return String.format("//application/jvm/platform-specific-dependencies/%s/dependency", os.toString().toLowerCase());
	}

	private String iconExtensionXpathExpression(OS os) {
		String extension = ".png";
		if (os == OS.MAC) {
			extension = ".icns";
		} else if (os == OS.WINDOWS) {
			extension = ".ico";
		}
		return String.format("//application/info/icons/icon[substring(@path,string-length(@path) -string-length('%s') +1) = '%s']", extension, extension);
	}

	/**
	 * Creates Linux-specific app configuration document.
	 *
	 * @param doc           document containing common data
	 * @param xpath         XPath processor
	 * @param applicationId application ID
	 * @param os            operating system
	 * @return document with Linux specific config
	 * @throws XPathException
	 */
	private void createOsConfig(Document doc, XPath xpath, String applicationId, OS os) throws XPathException, TransformerException, ContentException {
		for (CpuArch arch : CpuArch.values()) {
			if (os == OS.MAC && arch == CpuArch.X86) {
				// for Mac, we only want 64-bit config
				continue;
			}
			Document currentDoc = XMLUtils.cloneDocument(doc);
			// get dependencies node
			var dependenciesElement = (Element) xpath.compile("//application/jvm/dependencies").evaluate(currentDoc, XPathConstants.NODE);
			// add linux dependencies
			var deps = (NodeList) xpath.compile(platformDependenciesXpathExpression(os)).evaluate(deploymentConfigFile, XPathConstants.NODESET);
			for (int i = 0; i < deps.getLength(); i++) {
				var item = deps.item(i);
				var imported = currentDoc.importNode(item, false);
				dependenciesElement.appendChild(imported);
			}
			// create icons node
			var infoElement = (Element) xpath.compile("//application/info").evaluate(currentDoc, XPathConstants.NODE);
			var iconsElement = currentDoc.createElement("icons");
			infoElement.appendChild(iconsElement);
			// add icons
			var iconsElements = (NodeList) xpath.compile(iconExtensionXpathExpression(os)).evaluate(deploymentConfigFile, XPathConstants.NODESET);
			for (int i = 0; i < iconsElements.getLength(); i++) {
				var item = iconsElements.item(i);
				var imported = currentDoc.importNode(item, false);
				iconsElement.appendChild(imported);
			}
			// add launcher to JVM node
			var jvmElement = (Element) xpath.compile("//application/jvm").evaluate(currentDoc, XPathConstants.NODE);
			String appName = (String) xpath.compile("//application/info/name").evaluate(currentDoc, XPathConstants.STRING);
			var launcherElement = createLauncherTag(currentDoc, applicationId, os, arch, appName);
			jvmElement.appendChild(launcherElement);
			currentDoc.normalizeDocument();
			XMLUtils.removeEmptyNodes(currentDoc);
			StringWriter writer = new StringWriter();
			XMLUtils.outputResultXml(currentDoc, new StreamResult(writer));
			contentStorage.createApplicationConfigFile(new BufferedInputStream(new ByteArrayInputStream(writer.toString().getBytes())), applicationId, os, arch);
		}
	}

	private DocumentBuilder createDocumentBuilder() throws DeploymentException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			return docBuilder;
		} catch (ParserConfigurationException ex) {
			LOGGER.error("Failed to create document builder", ex);
			throw new DeploymentException(ex);
		}

	}

	private Element createLauncherTag(Document document, String applicationId, OS os, CpuArch arch, String name) throws ContentException {
		var launcherElement = document.createElement("launcher");
		var launcher = launcherRepository.findFirstByOsAndCpuArchOrderByVersionDesc(os, arch);
		launcherElement.setAttribute("hash", launcher.getHash());
		launcherElement.setAttribute("size", Long.toString(launcher.getSize()));
		String fileName = name.replaceAll("\\s", "");
		if (os == OS.WINDOWS) {
			fileName = fileName + ".exe";
		}
		launcherElement.setAttribute("file-name", fileName);
		return launcherElement;

	}
}
