/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.deployment.impl;

import com.vektorsoft.appbox.server.content.ContentLocator;
import com.vektorsoft.appbox.server.exception.ContentException;
import com.vektorsoft.appbox.server.exception.DeploymentException;
import com.vektorsoft.appbox.server.deployment.DeploymentService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@Service
public class DefaultDeploymentService implements DeploymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeploymentService.class);
	private ExecutorService executorService;

	@Autowired
	private ContentLocator contentLocator;

	@Autowired
	private Function<File, DeploymentProcessTask> deploymentTaskFactory;

	@PostConstruct
	public void init() {
		executorService = Executors.newCachedThreadPool();
	}

	@Override
	public String processConfigData(String configData) throws DeploymentException {
		Document doc = createXmlDocument(configData);
		doProcessDocument(doc);
		return outputResultXml(doc);
	}

	@Override
	public void processContent(Path deploymentPath) throws DeploymentException {
		DeploymentProcessTask task = deploymentTaskFactory.apply(deploymentPath.toFile());
		executorService.submit(task);
		LOGGER.info("Deployment task submitted. Deployment archive: {}", deploymentPath.getFileName().toString());
	}

	@Override
	public boolean validateDeploymentContent(Path deploymentPath) throws DeploymentException {
		try {
			RandomAccessFile raf = new RandomAccessFile(deploymentPath.toFile(), "r");
			long n = raf.readInt();
			raf.close();
			if (n != 0x504B0304 && n != 0x504B0506 && n != 0x504B0708) {
				return false;
			}
		} catch (IOException ex) {
			throw new DeploymentException(ex.getMessage());
		}
		return true;

	}

	private Document createXmlDocument(String configData) throws DeploymentException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(configData)));
			return doc;
		} catch (SAXException | ParserConfigurationException | IOException ex) {
			throw new DeploymentException(ex);
		}
	}

	private void doProcessDocument(Document doc) throws DeploymentException {
		List<Node> nodesToRemove = new ArrayList<>();
		try {
			iterateXmlNodes(doc, "icon", nodesToRemove);
			iterateXmlNodes(doc, "dependency", nodesToRemove);
			// remove nodes which exist
			for (Node n : nodesToRemove) {
				n.getParentNode().removeChild(n);
			}
			doc.normalize();
			// remove empty nodes from document
			removeEmptyNodes(doc);
		} catch (ContentException ex) {
			throw new DeploymentException(ex);
		}

	}

	private void iterateXmlNodes(Document doc, String tagName, List<Node> removalList) throws DeploymentException, ContentException {
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
				if (contentLocator.contentExists(hash)) {
					String name = element.getAttribute("fileName");
					LOGGER.debug("File with hash {} and name {} already exists", hash, name);
					removalList.add(node);
				}
			}
		}
	}

	private String outputResultXml(Document doc) throws DeploymentException {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();

		} catch (TransformerException ex) {
			throw new DeploymentException(ex);
		}

	}

	private void removeEmptyNodes(Document doc) {
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
			NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

			// Remove each empty text node from document.
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
				Node emptyTextNode = emptyTextNodes.item(i);
				emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		} catch (XPathExpressionException ex) {
			LOGGER.error("Failed to remove empty nodes", ex);
		}

	}

}
