package org.strangeforest.xml.util;

import java.io.*;

import org.xml.sax.*;

/**
 * <p>This class is a <tt>ContentHandler</tt> that serializes all character data
 * to the specified <tt>Writer</tt>.</p>
 */
public class CharactersSerializer implements ContentHandler {

	private Writer out;

	/**
	 * Creates new <tt>CharactersSerializer</tt>.
	 * @param out <tt>Writer</tt> to serialize character data to.
	 */
	public CharactersSerializer(Writer out) {
		super();
		this.out = out;
	}

	public Writer getWriter() {
		return out;
	}

	public void setWriter(Writer out) {
		this.out = out;
	}

	@Override public void setDocumentLocator(Locator locator) {
	}

	@Override public void startDocument() throws SAXException {
	}

	@Override public void endDocument() throws SAXException {
	}

	@Override public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	@Override public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
	}

	@Override public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
	}

	@Override public void characters(char[] ch, int start, int length) throws SAXException {
		if (out != null)
			try {
				out.write(ch, start, length);
			}
			catch (IOException ex) {
				throw new SAXException(ex);
			}
	}

	@Override public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	@Override public void processingInstruction(String target, String data) throws SAXException {
	}

	@Override public void skippedEntity(String name) throws SAXException {
	}
}
