package com.visionetsystems.framework.utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppConfigReader {

	public static TreeMap<String, String> xmlDataReader() throws Exception {
		Path configFilePath = Path.of(System.getProperty("user.dir"), "test-automation-config.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setValidating(false);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		UIConstantsUtil.APP_CONFIG_MAP = new TreeMap<>(Comparator.nullsFirst(String::compareTo));

		try (InputStream is = Files.newInputStream(configFilePath)) {
			Document doc = builder.parse(is);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//properties/entry[@key]");
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

			for (int i = 0; i < nl.getLength(); i++) {
				Node currentItem = nl.item(i);
				if (currentItem.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentItem;
					String key = element.getAttribute("key");
					String value = element.getTextContent(); // Assuming value is the text content of the <entry>
																// element

					if (key != null && !key.isEmpty()) { // Check if 'key' is not null or empty
						UIConstantsUtil.APP_CONFIG_MAP.put(key, value);
					}
				}
			}
		}
		return UIConstantsUtil.APP_CONFIG_MAP;
	}

}
