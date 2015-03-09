package org.strangeforest.xml.helpers;

import org.w3c.dom.*;

/**
 * <p>This class is a <i>Decorator</i> over the standard W3C DOM 3 <tt>Attr</tt> interface.</p>
 */
public class AttrHelper extends NodeHelper<Attr> implements Attr {

	// Factory methods

	static AttrHelper newAttrHelper(Attr attr) {
		if (attr instanceof AttrHelper)
			return (AttrHelper)attr;
		else
			return new AttrHelper(attr);
	}


	// Instance methods

	/**
	 * Creates a new helper that decorates specified attribute.
	 * @param attr attribute to be decorated.
	 */
	public AttrHelper(Attr attr) {
		super(attr);
	}

	@Override protected void setNode(Attr attr) {
		checkNode(attr);
		node = attr;
	}


	// Helper methods

	/**
	 * Returns decorated <tt>Attr</tt>.
	 * @return decorated <tt>Attr</tt>.
	 */
	public Attr getAttr() {
		return node;
	}

	/**
	 * Returns <tt>short</tt> value of this attribute.
	 * @return <tt>short</tt> value of this attribute.
	 */
	public short getShort() {
		return Short.parseShort(getValue());
	}

	/**
	 * Returns <tt>int</tt> value of this attribute.
	 * @return <tt>int</tt> value of this attribute.
	 */
	public int getInt() {
		return Integer.parseInt(getValue());
	}

	/**
	 * Returns <tt>long</tt> value of this attribute.
	 * @return <tt>long</tt> value of this attribute.
	 */
	public long getLong() {
		return Long.parseLong(getValue());
	}

	/**
	 * Returns <tt>boolean</tt> value of this attribute.
	 * @return <tt>boolean</tt> value of this attribute.
	 */
	public boolean getBoolean() {
		return Boolean.valueOf(getValue());
	}

	/**
	 * Returns <tt>float</tt> value of this attribute.
	 * @return <tt>float</tt> value of this attribute.
	 */
	public float getFloat() {
		return Float.parseFloat(getValue());
	}

	/**
	 * Returns <tt>double</tt> value of this attribute.
	 * @return <tt>double</tt> value of this attribute.
	 */
	public double getDouble() {
		return Double.parseDouble(getValue());
	}

	/**
	 * Sets new <tt>short</tt> value for this attribute.
	 * @param value new <tt>short</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setShort(short value) throws DOMException {
		setValue(String.valueOf(value));
	}

	/**
	 * Sets new <tt>int</tt> value for this attribute.
	 * @param value new <tt>int</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setInt(int value) throws DOMException {
		setValue(String.valueOf(value));
	}

	/**
	 * Sets new <tt>long</tt> value for this attribute.
	 * @param value new <tt>long</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setLong(long value) throws DOMException {
		setValue(String.valueOf(value));
	}

	/**
	 * Sets new <tt>boolean</tt> value for this attribute.
	 * @param value new <tt>boolean</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setBoolean(boolean value) throws DOMException {
		setValue(String.valueOf(value));
	}

	/**
	 * Sets new <tt>float</tt> value for this attribute.
	 * @param value new <tt>float</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setFloat(float value) throws DOMException {
		setValue(String.valueOf(value));
	}

	/**
	 * Sets new <tt>double</tt> value for this attribute.
	 * @param value new <tt>double</tt> value.
	 * @throws DOMException if this attribute is read only.
	 */
	public void setDouble(double value) throws DOMException {
		setValue(String.valueOf(value));
	}


	// Delegated methods of Attr interface

	@Override public String getName() {
		return node.getName();
	}

	@Override public boolean getSpecified() {
		return node.getSpecified();
	}

	@Override public String getValue() {
		return node.getValue();
	}

	@Override public void setValue(String value) throws DOMException {
		checkReadOnly();
		node.setValue(value);
	}

	@Override public Element getOwnerElement() {
		return ElementHelper.newElementHelper(node.getOwnerElement());
	}


	// Methods introduced in DOM 3

	@Override public TypeInfo getSchemaTypeInfo() {
		return node.getSchemaTypeInfo();
	}

	@Override public boolean isId() {
		return node.isId();
	}
}
