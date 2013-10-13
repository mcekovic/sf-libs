package org.strangeforest.xml.helpers;

import org.w3c.dom.*;

/**
 * <p>This class is a <i>Decorator</i> over the standard W3C DOM 3 <tt>Text</tt> interface.</p>
 */
public class TextHelper extends NodeHelper<Text> implements Text {

	// Factory methods

	static TextHelper newTextHelper(Text text) {
		if (text instanceof TextHelper)
			return (TextHelper)text;
		else
			return new TextHelper(text);
	}


	// Instance methods

	/**
	 * Creates a new helper that decorates specified text node.
	 * @param text text node to be decorated.
	 */
	public TextHelper(Text text) {
		super(text);
	}

	@Override protected void setNode(Text text) {
		checkNode(text);
		node = text;
	}


	// Helper methods

	/**
	 * Returns decorated <tt>Text</tt>.
	 * @return decorated <tt>Text</tt>.
	 */
	public Text getTextElement() {
		return node;
	}

	/**
	 * Returns <tt>short</tt> value of this text node.
	 * @return <tt>short</tt> value of this text node.
	 * @throws DOMException
	 */
	public short getShort() throws DOMException {
		return Short.parseShort(getData());
	}

	/**
	 * Returns <tt>int</tt> value of this text node.
	 * @return <tt>int</tt> value of this text node.
	 * @throws DOMException
	 */
	public int getInt() throws DOMException {
		return Integer.parseInt(getData());
	}

	/**
	 * Returns <tt>long</tt> value of this text node.
	 * @return <tt>long</tt> value of this text node.
	 * @throws DOMException
	 */
	public long getLong() throws DOMException {
		return Long.parseLong(getData());
	}

	/**
	 * Returns <tt>boolean</tt> value of this text node.
	 * @return <tt>boolean</tt> value of this text node.
	 * @throws DOMException
	 */
	public boolean getBoolean() throws DOMException {
		return Boolean.valueOf(getData());
	}

	/**
	 * Returns <tt>float</tt> value of this text node.
	 * @return <tt>float</tt> value of this text node.
	 * @throws DOMException
	 */
	public float getFloat() throws DOMException {
		return Float.parseFloat(getData());
	}

	/**
	 * Returns <tt>double</tt> value of this text node.
	 * @return <tt>double</tt> value of this text node.
	 * @throws DOMException
	 */
	public double getDouble() throws DOMException {
		return Double.parseDouble(getData());
	}


	// Delegated methods of Text interface

	@Override public Text splitText(int offset) throws DOMException {
		checkReadOnly();
		return newTextHelper(node.splitText(offset));
	}

	@Override public String getData() throws DOMException {
		return node.getData();
	}

	@Override public void setData(String data) throws DOMException {
		checkReadOnly();
		node.setData(data);
	}

	@Override public int getLength() {
		return node.getLength();
	}

	@Override public String substringData(int offset, int count) throws DOMException {
		return node.substringData(offset, count);
	}

	@Override public void appendData(String arg) throws DOMException {
		checkReadOnly();
		node.appendData(arg);
	}

	@Override public void insertData(int offset, String arg) throws DOMException {
		checkReadOnly();
		node.insertData(offset, arg);
	}

	@Override public void deleteData(int offset, int count) throws DOMException {
		checkReadOnly();
		node.deleteData(offset, count);
	}

	@Override public void replaceData(int offset, int count, String arg) throws DOMException {
		checkReadOnly();
		node.replaceData(offset, count, arg);
	}


	// Methods introduced in DOM 3

	@Override public boolean isElementContentWhitespace() {
		return node.isElementContentWhitespace();
	}

	@Override public String getWholeText() {
		return node.getWholeText();
	}

	@Override public Text replaceWholeText(String content) throws DOMException {
		checkReadOnly();
		return node.replaceWholeText(content);
	}
}
