package org.strangeforest.xml.util;

import java.io.*;
import java.net.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.strangeforest.xml.helpers.*;

public abstract class ParserUtil {

	public static ElementHelper parse(String fileName) throws ParserException {
		return parse(fileName, false, false, false);
	}

	public static ElementHelper parse(String fileName, boolean namespaces, boolean validate, boolean schema) throws ParserException {
		try {
			DocumentBuilder docBuilder = createDocBuilder(namespaces, validate, schema);
			Document doc = docBuilder.parse(new File(fileName));
			return new ElementHelper(doc.getDocumentElement());
		}
		catch (Exception ex) {
			throw new ParserException("Error parsing file: " + fileName, ex);
		}
	}

	public static ElementHelper parse(URI uri) throws ParserException {
		try {
			Document doc = createDocBuilder().parse(uri.toString());
			return new ElementHelper(doc.getDocumentElement());
		}
		catch (Exception ex) {
			throw new ParserException("Error parsing XML: " + uri.toString(), ex);
		}
	}

	public static ElementHelper parse(InputStream in) throws ParserException {
		try {
			Document doc = createDocBuilder().parse(in);
			return new ElementHelper(doc.getDocumentElement());
		}
		catch (Exception ex) {
			throw new ParserException("Error parsing XML from input stream.", ex);
		}
	}

	public static ElementHelper parseString(String xml) throws ParserException {
		try {
			Document doc = createDocBuilder().parse(new InputSource(new StringReader(xml)));
			return new ElementHelper(doc.getDocumentElement());
		}
		catch (Exception ex) {
			throw new ParserException("Error parsing XML: " + xml, ex);
		}
	}

	private static DocumentBuilder createDocBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		return docBuilderFactory.newDocumentBuilder();
	}

	private static DocumentBuilder createDocBuilder(boolean namespaces, boolean validate, boolean schema) throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		if (namespaces)
			docBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", true);
		if (validate)
			docBuilderFactory.setFeature("http://xml.org/sax/features/validation", true);
		if (schema)
			docBuilderFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
		return docBuilderFactory.newDocumentBuilder();
	}
}
