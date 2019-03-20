/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Utility class containing useful methods to manipulate XML files.
 *
 * @author Vladimir Djurovic
 */
public class XMLUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);
	public static final String DEFAULT_XML_ENCODING = "UTF-8";
	public static final String DEFAULT_XML_INDENT = "yes";

	private XMLUtils() {
		// do nothing
	}

	/**
	 * Generates XML document to specified output.
	 *
	 * @param doc document to generate
	 * @param out target output
	 * @throws TransformerException
	 */
	public static void outputResultXml(Document doc, StreamResult out) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_XML_ENCODING);
		transformer.setOutputProperty(OutputKeys.INDENT, DEFAULT_XML_INDENT);

		transformer.transform(new DOMSource(doc), out);
	}

	/**
	 * Removes empty nodes, which removes blank lines from generated document.
	 *
	 * @param doc document to process
	 */
	public static void removeEmptyNodes(Document doc) {
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

	/**
	 * Creates a copy of source document.
	 *
	 * @param doc source document
	 * @return document copy
	 */
	public static Document cloneDocument(Document doc) throws TransformerException {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer tx   = tfactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		DOMResult result = new DOMResult();
		tx.transform(source,result);
		return (Document)result.getNode();
	}
}
