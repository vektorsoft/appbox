/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment.impl;

import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
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

	public AppConfigFileCreator(Document deploymentConfigFile) {
		this.deploymentConfigFile = deploymentConfigFile;
	}

	public void createAppConfigFile() throws DeploymentException {
		DocumentBuilder builder = createDocumentBuilder();

		try{
			Document doc = builder.newDocument();
			XPath xpath = XPathFactory.newInstance().newXPath();
			createBasicElements(doc, xpath);
			LOGGER.debug("Creatied common application runtime configuration file");
			// add platform specific parts
			var linuxConfigDoc = createLinuxConfig(doc, xpath);
			linuxConfigDoc.normalizeDocument();
			XMLUtils.removeEmptyNodes(linuxConfigDoc);
			StringWriter writer = new StringWriter();
			XMLUtils.outputResultXml(linuxConfigDoc, new StreamResult(writer));
			System.out.println(writer.toString());
		} catch(XPathException | TransformerException ex) {
			LOGGER.error("Failed to parse XML document");
			throw new DeploymentException(ex);
		}

	}

	/**
	 * Create basic element hierarchy: application,  info, icons, dependencies
	 * @param doc
	 */
	private void createBasicElements(Document doc, XPath xPath) throws XPathException {
		var appElement = doc.createElement("application");
		doc.appendChild(appElement);
		var  versionAttr = (Attr)xPath.compile("//application/@version").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedVersionAttr = (Attr)doc.importNode(versionAttr, false); // need to import nodes into new document before using them
		appElement.setAttributeNode(importedVersionAttr);
		// create info section
		var infoElement = doc.createElement("info");
		appElement.appendChild(infoElement);
		// import name element
		var nameElement = (Element)xPath.compile("//application/info/name").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedName = doc.importNode(nameElement, true);
		infoElement.appendChild(importedName);

		// import JVM element
		var jvmElement = (Element)xPath.compile("//application/jvm").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedJvm = doc.importNode(jvmElement, false);
		appElement.appendChild(importedJvm);
		// import dependencies
		var dependenciesElement = (Element)xPath.compile("//application/jvm/dependencies").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedDepsElement = doc.importNode(dependenciesElement, true);
		importedJvm.appendChild(importedDepsElement);
		// import server element
		var serverElement = (Element)xPath.compile("//application/server").evaluate(deploymentConfigFile, XPathConstants.NODE);
		var importedServer = doc.importNode(serverElement, true);
		appElement.appendChild(importedServer);

	}

	/**
	 * Creates Linux-specific app configuration document.
	 *
	 * @param doc document containing common data
	 * @param xpath XPath processor
	 * @return document with Linux specific config
	 * @throws XPathException
	 */
	private Document createLinuxConfig(Document doc, XPath xpath) throws XPathException, TransformerException {
		Document linuxDoc = XMLUtils.cloneDocument(doc);
		// get dependencies node
		var dependenciesElement = (Element)xpath.compile("//application/jvm/dependencies").evaluate(linuxDoc, XPathConstants.NODE);
		// add linux dependencies
		var deps = (NodeList)xpath.compile("//application/jvm/platform-specific-dependencies/linux/dependency").evaluate(deploymentConfigFile, XPathConstants.NODESET);
		for(int i = 0;i < deps.getLength();i++) {
			var item = deps.item(i);
			var imported = linuxDoc.importNode(item, false);
			dependenciesElement.appendChild(imported);
		}
		// create icons node
		var infoElement = (Element)xpath.compile("//application/info").evaluate(linuxDoc, XPathConstants.NODE);
		var iconsElement = linuxDoc.createElement("icons");
		infoElement.appendChild(iconsElement);
		// add icons
		var iconsElements = (NodeList)xpath.compile("//application/info/icons/icon[substring(@path,string-length(@path) -string-length('.png') +1) = '.png']").evaluate(deploymentConfigFile, XPathConstants.NODESET);
		for(int i = 0;i < iconsElements.getLength();i++) {
			var item = iconsElements.item(i);
			var imported = linuxDoc.importNode(item, false);
			iconsElement.appendChild(imported);
		}
		return linuxDoc;
	}

	private DocumentBuilder createDocumentBuilder() throws DeploymentException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			return docBuilder;
		} catch(ParserConfigurationException ex) {
			LOGGER.error("Failed to create document builder", ex);
			throw new DeploymentException(ex);
		}

	}
}
