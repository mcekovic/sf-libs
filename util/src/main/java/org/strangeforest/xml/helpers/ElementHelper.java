package org.strangeforest.xml.helpers;

import java.util.*;
import java.text.*;

import org.w3c.dom.*;

import org.strangeforest.util.*;

/**
 * <p>This class is a <i>Decorator</i> over the standard W3C DOM 3 <tt>Element</tt> interface.</p>
 */
public class ElementHelper extends NodeHelper<Element> implements Element {

	// Factory methods

	static ElementHelper newElementHelper(Element elem) {
		if (elem instanceof ElementHelper)
			return (ElementHelper)elem;
		else
			return new ElementHelper(elem);
	}


	// Instance methods

	/**
	 * Creates a new helper that decorates specified element.
	 * @param element element to be decorated.
	 */
	public ElementHelper(Element element) {
		super(element);
	}

	@Override protected void setNode(Element element) {
		checkNode(element);
		node = element;
	}


	// Helper methods

	/**
	 * Returns decorated <tt>Element</tt>.
	 * @return decorated <tt>Element</tt>.
	 */
	public Element getElement() {
		return node;
	}

	/**
	 * Returns child element by name.
	 * @param name the name of the child.
	 * @return child element with specified name.
	 * @throws DOMException if child element with specified name does not exist.
	 */
	public ElementHelper getChildElement(String name) throws DOMException {
		NodeHelper child = getChild(name);
		if (child instanceof ElementHelper)
			return (ElementHelper)child;
		else {
			String message = MessageFormat.format("Child node ''{0}'' in node ''{1}'' is not an element.", name, node.getNodeName());
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR, message);
		}
	}

	public ElementHelper tryGetChildElement(String name) {
		NodeHelper child = tryGetChild(name);
		return child instanceof ElementHelper ? (ElementHelper)child : null;
	}

	/**
	 * Finds first descendant element with specified tag name.
	 * @param tagName tag name of the descendant.
	 * @return first descendant element with specified tag name.
	 * @throws DOMException if descendant element with specified tag name does not exist.
	 */
	public ElementHelper findElement(String tagName) throws DOMException {
		ElementHelper elem = tryFindElement(tagName);
		if (elem != null)
		   return elem;
		String message = MessageFormat.format("Can''t find element ''{0}''." , tagName);
		throw new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	public ElementHelper tryFindElement(String tagName) {
		NodeList nodes = node.getElementsByTagName(tagName);
		return nodes.getLength() > 0 ? new ElementHelper((Element)nodes.item(0)) : null;
	}

	/**
	 * Returns <tt>NodeHelperList</tt> of all descendant elements with specified tag name.
	 * @param tagName tag name of the descendants.
	 * @return <tt>NodeHelperList</tt> of all descendant elements with specified tag name.
	 * The list is empty when there is no descendant element with specified tag name.
	 */
	public NodeHelperList findElements(String tagName) {
		NodeList nodes = node.getElementsByTagName(tagName);
		return NodeHelperList.newNodeHelperList(nodes);
	}

	/**
	 * Finds descendant element with specified tag name that has specified attribute with specified value.
	 * @param tagName tag name of the descendant.
	 * @param attrName attribute name.
	 * @param attrValue attribute value.
	 * @return first descendant element with specified tag name that has specified attribute with specified value.
	 * @throws DOMException if descendant element with specified tag name that has specified attribute with specified value does not exist.
	 */
	public ElementHelper findElementByAttrValue(String tagName, String attrName, String attrValue) throws DOMException {
		ElementHelper elem = tryFindElementByAttrValue(tagName, attrName, attrValue);
		if (elem != null)
		   return elem;
		String message = MessageFormat.format("Can''t find element ''{0}'' with attribute ''{1}'' equals to ''{2}''." , tagName, attrName, attrValue);
		throw new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	public ElementHelper tryFindElementByAttrValue(String tagName, String attrName, String attrValue) {
		NodeList nodes = node.getElementsByTagName(tagName);
		for (int i = 0, length = nodes.getLength(); i < length; i++) {
			Element elem = (Element)nodes.item(i);
			if (ObjectUtil.equal(attrValue, elem.getAttribute(attrName)))
				return new ElementHelper(elem);
		}
		return null;
	}

	/**
	 * Finds descendant element with specified tag name that has a child with specified name and specified value.
	 * @param tagName tag name of the descendant.
	 * @param childName child name.
	 * @param childValue child value.
	 * @return first descendant element with specified tag name that has a child with specified name and specified value.
	 * @throws DOMException if descendant element with specified tag name that has a child with specified name and specified value does not exist.
	 */
	public NodeHelper findNodeByChildValue(String tagName, String childName, String childValue) throws DOMException {
		NodeHelper node = tryFindNodeByChildValue(tagName, childName, childValue);
		if (node != null)
		   return node;
		String message = MessageFormat.format("Can''t find node ''{0}'' with child ''{1}'' equals to ''{2}''." , tagName, childName, childValue);
		throw new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	public NodeHelper tryFindNodeByChildValue(String tagName, String childName, String childValue) {
		NodeList nodes = node.getElementsByTagName(tagName);
		for (int i = 0, length = nodes.getLength(); i < length; i++) {
			Node child = nodes.item(i);
			NodeHelper childHelper = decorate(child);
			try {
				if (childValue.equals(childHelper.getChildString(childName)))
					return childHelper;
			}
			catch (DOMException ignored) {}
		}
		return null;
	}

	/**
	 * Finds descendant element with specified tag name that has a parent with specified name.
	 * @param parentName parent name.
	 * @param tagName descendant's tag name.
	 * @return first descendant element with specified tag name that has a parent with specified name.
	 * @throws DOMException if descendant element with specified tag name that has a parent with specified name does not exist.
	 */
	public NodeHelper findNodeByParentName(String parentName, String tagName) throws DOMException {
		NodeHelper node = tryFindNodeByParentName(parentName, tagName);
		if (node != null)
		   return node;
		String message = MessageFormat.format("Can''t find node ''{0}'' with parent ''{1}''." , tagName, parentName);
		throw new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	public NodeHelper tryFindNodeByParentName(String parentName, String tagName) {
		NodeList nodes = node.getElementsByTagName(tagName);
		for (int i = 0, length = nodes.getLength(); i < length; i++) {
			Node child = nodes.item(i);
			if (child.getParentNode().getNodeName().equals(parentName))
				return decorate(child);
		}
		return null;
	}

	/**
	 * Clones first element with specified tag name.
	 * @param tagName tag name.
	 * @return <tt>ElementHelper</tt> of the cloned element.
	 * @throws DOMException if element with specified tag name dows not exist.
	 */
	public ElementHelper cloneElement(String tagName) throws DOMException {
		return (ElementHelper)findElement(tagName).cloneNode(true);
	}

	/**
	 * Removes first descendant element with specified tag name.
	 * @param tagName descendant's tag name.
	 * @return descendant element that has been removed.
	 * @throws DOMException if descendant element with specified tag name does not exist
	 * or this node is read only.
	 */
	public ElementHelper removeElement(String tagName) throws DOMException {
		checkReadOnly();
		ElementHelper elem = findElement(tagName);
		elem.getParentNode().removeChild(elem);
		return elem;
	}

	/**
	 * Removes all descendant elements with specified tag name.
	 * @param tagName descendants' tag name.
	 * @return <tt>NodeHelperList</tt> of descendant elements that have been removed.
	 * @throws DOMException if this node is read only.
	 */
	public NodeHelperList removeAllElements(String tagName) throws DOMException {
		checkReadOnly();
		NodeHelperList nodes = findElements(tagName);
		for (Node aNode : nodes)
			aNode.getParentNode().removeChild(aNode);
		return nodes;
	}

	/**
	 * Replaces first descendant element with specified tag name with new node.
	 * @param tagName descendant's tag name.
	 * @param newNode new node.
	 * @return descendant element that has been replaced.
	 * @throws DOMException if descendant element with specified tag name does not exist,
	 * or this node is read only.
	 */
	public ElementHelper replaceElement(String tagName, Node newNode) throws DOMException {
		checkReadOnly();
		ElementHelper elem = findElement(tagName);
		elem.getParentNode().replaceChild(undecorate(newNode), elem);
		return elem;
	}

	/**
	 * Replaces all descendant elements with specified tag name with new node.
	 * @param tagName descendants' tag name.
	 * @param newNode new node.
	 * @return <tt>NodeHelperList</tt> of descendant elements that have been replaced.
	 * @throws DOMException if this node is read only.
	 */
	public NodeHelperList replaceAllElements(String tagName, Node newNode) throws DOMException {
		checkReadOnly();
		newNode = undecorate(newNode);
		NodeHelperList nodes = findElements(tagName);
		for (Node aNode : nodes)
			aNode.getParentNode().replaceChild(newNode, aNode);
		return nodes;
	}

	/**
	 * Replaces first descendant element with specified tag name with new text node that contains specified text.
	 * @param tagName descendant's tag name.
	 * @param text text for new text node.
	 * @return descendant element that has been replaced.
	 * @throws DOMException if descendant element with specified tag name does not exist,
	 * or this node is read only.
	 */
	public ElementHelper replaceElement(String tagName, String text) throws DOMException {
		return replaceElement(tagName, node.getOwnerDocument().createTextNode(text));
	}

	/**
	 * Replaces all descendant elements with specified tag name with new text node that contains specified text.
	 * @param tagName descendants' tag name.
	 * @param text text for new text node.
	 * @return <tt>NodeHelperList</tt> of descendant elements that have been replaced.
	 * @throws DOMException if this node is read only.
	 */
	public NodeHelperList replaceAllElements(String tagName, String text) throws DOMException {
		checkReadOnly();
		NodeHelperList nodes = findElements(tagName);
		for (Node aNode : nodes)
			aNode.getParentNode().replaceChild(aNode.getOwnerDocument().createTextNode(text), aNode);
		return nodes;
	}

	/**
	 * Removes all attributes from this element.
	 * @throws DOMException if this element is read only.
	 */
	public void removeAllAttributes() throws DOMException {
		checkReadOnly();
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0, count = attrs.getLength(); i < count; i++)
			node.removeAttributeNode((Attr)attrs.item(i));
	}

	/**
	 * Returns 'properties' associated with this element.
	 * Properties can be specified by attributes or child nodes.
	 * @param tagName tag name of the property element.
	 * @param isKeyAttribute specifies if property key (name) is represented by attribute or child element.
	 * @param keyName the name of the key.
	 * @param isValueAttribute specifies if property value is represented by attribute or child element.
	 * @param valueName the name of the value.
	 * @return properties encapsulated in <tt>java.util.Properties</tt> class.
	 */
	public Properties getProperties(String tagName,
											  boolean isKeyAttribute, String keyName,
											  boolean isValueAttribute, String valueName) {
		Properties props = new Properties();
		for (NodeHelper aNode : findElements(tagName)) {
			ElementHelper pair = (ElementHelper)aNode;
			String key = isKeyAttribute ? pair.getAttribute(keyName) : pair.getChildString(keyName);
			String value = isValueAttribute ? pair.getAttribute(valueName) : pair.getChildString(valueName);
			props.setProperty(key, value);
		}
		return props;
	}

	/**
	 * Returns <tt>AttrHelper</tt> for attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>AttrHelper</tt> for attribute with specified name.
	 * @throws DOMException if atribute with specified name does not exist.
	 */
	public AttrHelper getAttrHelper(String name) {
		Attr attr = node.getAttributeNode(name);
		if (attr != null)
			return AttrHelper.newAttrHelper(attr);
		else {
			String message = MessageFormat.format("Can''t find attribute ''{0}''." , name);
			throw new DOMException(DOMException.NOT_FOUND_ERR, message);
		}
	}

	/**
	 * Returns <tt>String</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>String</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public String getAttrString(String name) throws DOMException {
		if (hasAttribute(name))
			return getAttribute(name);
		else {
			String message = MessageFormat.format("Can''t find attribute ''{0}''." , name);
			throw new DOMException(DOMException.NOT_FOUND_ERR, message);
		}
	}

	/**
	 * Returns <tt>String</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>String</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public String getAttrString(String name, String def) {
		return hasAttribute(name) ? getAttribute(name) : def;
	}

	/**
	 * Returns <tt>short</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>short</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public short getAttrShort(String name) throws DOMException {
		return Short.parseShort(getAttrString(name));
	}

	/**
	 * Returns <tt>short</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>short</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public short getAttrShort(String name, short def) {
		return hasAttribute(name) ? getAttrShort(name) : def;
	}

	/**
	 * Returns <tt>int</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>int</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public int getAttrInt(String name) throws DOMException {
		return Integer.parseInt(getAttrString(name));
	}

	/**
	 * Returns <tt>int</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>int</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public int getAttrInt(String name, int def) {
		return hasAttribute(name) ? getAttrInt(name) : def;
	}

	/**
	 * Returns <tt>long</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>long</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public long getAttrLong(String name) throws DOMException {
		return Long.parseLong(getAttrString(name));
	}

	/**
	 * Returns <tt>long</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>long</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public long getAttrLong(String name, long def) {
		return hasAttribute(name) ? getAttrLong(name) : def;
	}

	/**
	 * Returns <tt>boolean</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>boolean</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public boolean getAttrBoolean(String name) throws DOMException {
		return Boolean.valueOf(getAttrString(name));
	}

	/**
	 * Returns <tt>boolean</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>boolean</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public boolean getAttrBoolean(String name, boolean def) {
		return hasAttribute(name) ? getAttrBoolean(name) : def;
	}

	/**
	 * Returns <tt>float</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>float</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public float getAttrFloat(String name) throws DOMException {
		return Float.parseFloat(getAttrString(name));
	}

	/**
	 * Returns <tt>float</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>float</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public float getAttrFloat(String name, float def) {
		return hasAttribute(name) ? getAttrFloat(name) : def;
	}

	/**
	 * Returns <tt>double</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @return <tt>double</tt> value of the attribute with specified name.
	 * @throws DOMException if attribute with specified name does not exist.
	 */
	public double getAttrDouble(String name) throws DOMException {
		return Double.parseDouble(getAttrString(name));
	}

	/**
	 * Returns <tt>double</tt> value of the attribute with specified name.
	 * @param name the name of the attribute.
	 * @param def default value to be returned if attribute with specified name does not exist.
	 * @return <tt>doubleString</tt> value of the attribute with specified name,
	 * or default value if attribute with specified name does not exist.
	 */
	public double getAttrDouble(String name, double def) {
		return hasAttribute(name) ? getAttrDouble(name) : def;
	}


	// Methods of Element interface delegated to node element

	@Override public String getTagName() {
		return node.getTagName();
	}

	@Override public String getAttribute(String name) {
		return node.getAttribute(name);
	}

	@Override public void setAttribute(String name, String value) throws DOMException {
		checkReadOnly();
		node.setAttribute(name, value);
	}

	@Override public void removeAttribute(String name) throws DOMException {
		node.removeAttribute(name);
	}

	@Override public Attr getAttributeNode(String name) {
		Attr attr = node.getAttributeNode(name);
		return attr != null ? AttrHelper.newAttrHelper(attr) : null;
	}

	@Override public Attr setAttributeNode(Attr newAttr) throws DOMException {
		checkReadOnly();
		return AttrHelper.newAttrHelper(node.setAttributeNode(newAttr));
	}

	@Override public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		checkReadOnly();
		return AttrHelper.newAttrHelper(node.removeAttributeNode(oldAttr));
	}

	@Override public NodeList getElementsByTagName(String name) {
		return NodeHelperList.newNodeHelperList(node.getElementsByTagName(name));
	}


	// Methods introduced in DOM 2

	@Override public String getAttributeNS(String namespaceURI, String localName) {
		return node.getAttributeNS(namespaceURI, localName);
	}

	@Override public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		checkReadOnly();
		node.setAttributeNS(namespaceURI, qualifiedName, value);
	}

	@Override public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		checkReadOnly();
		node.removeAttributeNS(namespaceURI, localName);
	}

	@Override public Attr getAttributeNodeNS(String namespaceURI, String localName) {
		return AttrHelper.newAttrHelper(node.getAttributeNodeNS(namespaceURI, localName));
	}

	@Override public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		checkReadOnly();
		return AttrHelper.newAttrHelper(node.setAttributeNodeNS(newAttr));
	}

	@Override public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return NodeHelperList.newNodeHelperList(node.getElementsByTagNameNS(namespaceURI, localName));
	}

	@Override public boolean hasAttribute(String name) {
		return node.hasAttribute(name);
	}

	@Override public boolean hasAttributeNS(String namespaceURI, String localName) {
		return node.hasAttributeNS(namespaceURI, localName);
	}


	// Methods introduced in DOM 3

	@Override public TypeInfo getSchemaTypeInfo() {
		return node.getSchemaTypeInfo();
	}

	@Override public void setIdAttribute(String name, boolean isId) throws DOMException {
		checkReadOnly();
		node.setIdAttribute(name, isId);
	}

	@Override public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		checkReadOnly();
		node.setIdAttributeNS(namespaceURI, localName, isId);
	}

	@Override public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		checkReadOnly();
		node.setIdAttributeNode(idAttr, isId);
	}
}
