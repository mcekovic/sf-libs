package org.strangeforest.xml.util;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import org.xml.sax.helpers.*;

/**
 * <p>Utility class for serializing XML content.</p>
 */
public abstract class Serializer {

	/**
	 * Serializes DOM tree represented with <tt>Node</tt> to the <tt>OutputStream</tt>.
	 * @param node node to be serialized.
	 * @param out <tt>OutputStream</tt> to serialize to.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static void serialize(Node node, OutputStream out) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Source source = new DOMSource(node);
		Result result = new StreamResult(out);
		transformer.transform(source, result);
	}

	/**
	 * Serializes DOM tree represented with <tt>Node</tt> to the <tt>Writer</tt>.
	 * @param node node to be serialized.
	 * @param writer <tt>Writer</tt> to serialize to.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static void serialize(Node node, Writer writer) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Source source = new DOMSource(node);
		Result result = new StreamResult(writer);
		transformer.transform(source, result);
	}

	/**
	 * Serializes DOM tree represented with <tt>Node</tt> to string.
	 * @param node node to be serialized.
	 * @return serialized string of the specified DOM node.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static String serialize(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		serialize(node, writer);
		return writer.toString();
	}

	/**
	 * Serializes DOM tree represented with <tt>Node</tt> to standard output.
	 * @param node node to be serialized.
	 */
	public static void println(Node node) {
		try {
			serialize(node, System.out);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Serializes text content of the DOM tree represented with <tt>Node</tt> to the <tt>Writer</tt>.
	 * @param node node to be serialized.
	 * @param writer <tt>Writer</tt> to serialize to.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static void serializeText(Node node, Writer writer) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Source source = new DOMSource(node);
		Result result = new SAXResult(new CharactersSerializer(writer));
		transformer.transform(source, result);
	}

	/**
	 * Serializes text content of the SAX filter output of the DOM tree represented with <tt>Node</tt> to the <tt>Writer</tt>.
	 * @param node node to be serialized.
	 * @param writer <tt>Writer</tt> to serialize to.
	 * @param filter SAX filter.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static void serializeText(Node node, Writer writer, XMLFilterImpl filter) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Source source = new DOMSource(node);
		Result result = new SAXResult(filter);
		filter.setContentHandler(new CharactersSerializer(writer));
		transformer.transform(source, result);
	}

	/**
	 * Serializes text content of the DOM tree represented with <tt>Node</tt> to string.
	 * @param node node to be serialized.
	 * @return serialized string of the specified DOM node.
	 * @throws TransformerException if there is an error in serialization.
	 */
	public static String serializeText(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		serializeText(node, writer);
		return writer.toString();
	}
}
