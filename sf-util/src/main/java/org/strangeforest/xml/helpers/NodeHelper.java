package org.strangeforest.xml.helpers;

import java.util.*;
import java.text.*;
import javax.xml.namespace.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

/**
 * <p>This class is a <i>Decorator</i> over the standard W3C DOM 3 <tt>Node</tt> interface.</p>
 */
public class NodeHelper<N extends Node> implements Node {

	// Factory methods

	/**
	 * Decorated DOM <tt>Node</tt> instance with new <tt>NodeHelper</tt> instance.
	 * @param node DOM <tt>Node</tt> to be decorated. If node is instance of <tt>NodeHelper</tt>
	 * then original node is returned (no decoration is performed).
	 * @return decorated DOM <tt>Node</tt> instance.
	 */
	public static NodeHelper decorate(Node node) {
		if (node instanceof NodeHelper)
			return (NodeHelper)node;
		else
			switch (node.getNodeType()) {
				case Node.ELEMENT_NODE:
					return new ElementHelper((Element)node);
				case Node.ATTRIBUTE_NODE:
					return new AttrHelper((Attr)node);
				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE:
					return new TextHelper((Text)node);
				default:
					return new NodeHelper<>(node);
			}
	}

	public static <N extends Node> N undecorate(N node) {
		if (node instanceof NodeHelper)
			return ((NodeHelper<N>)node).getNode();
		else
			return node;
	}


	// Instance members

	protected N node;
	private boolean readOnly;

	/**
	 * Creates a new helper that decorates specified node.
	 * @param node node to be decorated.
	 */
	public NodeHelper(N node) {
		super();
		checkNode(node);
		this.node = node;
	}

	/**
	 * Returns DOM <tt>Node</tt> decorated by this instance of <tt>NodeHelper</tt>.
	 * @return DOM <tt>Node</tt> decorated by this instance of <tt>NodeHelper</tt>.
	 */
	public N getNode() {
		return node;
	}

	protected void setNode(N node) {
		checkNode(node);
		this.node = node;
	}

	/**
	 * Checks if node is read only.
	 * @return <tt>true</tt> if this <tt>Node</tt> instance is read only.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Sets read only attribute of this node.
	 * @param readOnly new value for read only attribute.
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	protected final void checkNode(N node) {
		if (node == null)
			throw new IllegalArgumentException("Node must not be null.");
	}

	protected final void checkReadOnly() throws DOMException {
		if (readOnly)
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
										  MessageFormat.format("Can not modify read only node ''{0}''.", node.getNodeName()));
	}


	// Helper methods

	/**
	 * Returns child node by name.
	 * @param name the name of the child.
	 * @return child node with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public NodeHelper getChild(String name) throws DOMException {
		Node child = getChildNode(name);
		if (child != null)
			return decorate(child);
		String message = MessageFormat.format("Can''t find child node ''{0}'' in node ''{1}''.", name, node.getNodeName());
		throw new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	public NodeHelper tryGetChild(String name) {
		Node child = getChildNode(name);
		return child != null ? decorate(child) : null;
	}

	/**
	 * Checks if node has a child with specified name.
	 * @param name the name of the child.
	 * @return <tt>true</tt> if node has a child with specified name.
	 */
	public boolean hasChild(String name) {
		return getChildNode(name) != null;
	}

	private Node getChildNode(String name) {
		NodeList children = node.getChildNodes();
		for (int i = 0, length = children.getLength(); i < length; i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals(name))
				return child;
		}
		return null;
	}

	/**
	 * Returns text content of the child node with specified name.
	 * @param name the name of the child.
	 * @return text content of the child node with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public String getChildString(String name) throws DOMException {
		return getChild(name).getTextContent();
	}

	/**
	 * Returns concatenated string of all grandchild nodes of the child node with specified name.
	 * @param name the name of the child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return concatenated string of all grandchild nodes of the child node with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public String getChildString(String name, String def) {
		return hasChild(name) ? getChildString(name) : def;
	}

	/**
	 * Returns <tt>short</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>short</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public short getChildShort(String name) throws DOMException {
		return Short.parseShort(getChildString(name));
	}

	/**
	 * Returns <tt>short</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>short</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public short getChildShort(String name, short def) {
		return hasChild(name) ? getChildShort(name) : def;
	}

	/**
	 * Returns <tt>int</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>int</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public int getChildInt(String name) throws DOMException {
		return Integer.parseInt(getChildString(name));
	}

	/**
	 * Returns <tt>int</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>int</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public int getChildInt(String name, int def) {
		return hasChild(name) ? getChildInt(name) : def;
	}

	/**
	 * Returns <tt>long</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>long</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public long getChildLong(String name) throws DOMException {
		return Long.parseLong(getChildString(name));
	}

	/**
	 * Returns <tt>long</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>long</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public long getChildLong(String name, long def) {
		return hasChild(name) ? getChildLong(name) : def;
	}

	/**
	 * Returns <tt>boolean</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>boolean</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public boolean getChildBoolean(String name) throws DOMException {
		return Boolean.valueOf(getChildString(name));
	}

	/**
	 * Returns <tt>boolean</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>boolean</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public boolean getChildBoolean(String name, boolean def) {
		return hasChild(name) ? getChildBoolean(name) : def;
	}

	/**
	 * Returns <tt>float</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>float</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public float getChildFloat(String name) throws DOMException {
		return Float.parseFloat(getChildString(name));
	}

	/**
	 * Returns <tt>float</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>float</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public float getChildFloat(String name, float def) {
		return hasChild(name) ? getChildFloat(name) : def;
	}

	/**
	 * Returns <tt>double</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @return <tt>double</tt> value of the text enclosed by the child with specified name.
	 * @throws DOMException if child with specified name does not exist.
	 */
	public double getChildDouble(String name) throws DOMException {
		return Double.parseDouble(getChildString(name));
	}

	/**
	 * Returns <tt>double</tt> value of the text enclosed by the child with specified name.
	 * @param name the name of child.
	 * @param def default value to be returned if child with specified name does not exist.
	 * @return <tt>double</tt> value of the text enclosed by the child with specified name,
	 * or default value if child with specified name does not exist.
	 */
	public double getChildDouble(String name, double def) {
		return hasChild(name) ? getChildDouble(name) : def;
	}

	/**
	 * Returns <tt>NodeHelperList</tt> with this node's children.
	 * @return <tt>NodeHelperList</tt> with this node's children.
	 */
	public NodeHelperList getChildNodesHelpers() {
		return NodeHelperList.newNodeHelperList(node.getChildNodes());
	}

	/**
	 * Returns <tt>NodeHelperList</tt> with this node's children that have specified name.
	 * @param name String
	 * @return <tt>NodeHelperList</tt> with this node's children that have specified name.
	 */
	public NodeHelperList getChildNodesHelpers(String name) {
		List<NodeHelper> list = new ArrayList<>();
		NodeList children = node.getChildNodes();
		for (int i = 0, length = children.getLength(); i < length; i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals(name))
				list.add(decorate(child));
		}
		return new NodeHelperList(list.toArray(new NodeHelper[list.size()]));
	}


	/**
	 * Evaluates XPath expression and returns single node
	 * @param expression String
	 * @return NodeHelper
	 * @throws DOMException if node can not be found with speicfied XPath expression.
	 */
	public NodeHelper find(String expression) {
		NodeHelper result = tryFind(expression);
		if (result != null)
			return result;
		else
			throw newFindError(expression);
	}

	public NodeHelper tryFind(String expression) {
		Node result = (Node)doFind(expression, XPathConstants.NODE);
		return result != null ? decorate(result) : null;
	}

	/**
	 * Evaluates XPath expression and returns string
	 * @param expression String
	 * @return String
	 * @throws DOMException if node can not be found with speicfied XPath expression.
	 */
	public String findString(String expression) {
		String result = findString(expression, null);
		if (result != null)
			return result;
		else
			throw newFindError(expression);
	}

	public String tryFindString(String expression) {
		return findString(expression, null);
	}

	/**
	 * Evaluates XPath expression and returns string
	 * @param expression String
	 * @param def String
	 * @return String
	 */
	public String findString(String expression, String def) {
		String result = (String)doFind(expression, XPathConstants.STRING);
		return result != null ? result : def;
	}

	/**
	 * Evaluates XPath expression and returns integer
	 * @param expression String
	 * @return int
	 * @throws DOMException if node can not be found with speicfied XPath expression.
	 */
	public int findInt(String expression) {
		Integer result = tryFindInteger(expression);
		if (result != null)
			return result;
		else
			throw newFindError(expression);
	}

	public Integer tryFindInteger(String expression) {
		Number result = (Number)doFind(expression, XPathConstants.NUMBER);
		return result != null && !isNaN(result) ? result.intValue() : null;
	}

	/**
	 * Evaluates XPath expression and returns integer
	 * @param expression String
	 * @param def int
	 * @return int
	 */
	public int findInt(String expression, int def) {
		Number result = (Number)doFind(expression, XPathConstants.NUMBER);
		return result != null && !isNaN(result) ? result.intValue() : def;
	}

	/**
	 * Evaluates XPath expression and returns double
	 * @param expression String
	 * @return double
	 * @throws DOMException if node can not be found with speicfied XPath expression.
	 */
	public double findDouble(String expression) {
		Double result = tryFindDouble(expression);
		if (result != null)
			return result;
		else
			throw newFindError(expression);
	}

	public Double tryFindDouble(String expression) {
		Number result = (Number)doFind(expression, XPathConstants.NUMBER);
		return result != null && !isNaN(result) ? result.doubleValue() : null;
	}

	/**
	 * Evaluates XPath expression and returns double
	 * @param expression String
	 * @param def double
	 * @return double
	 */
	public double findDouble(String expression, double def) {
		Number result = (Number)doFind(expression, XPathConstants.NUMBER);
		return result != null && !isNaN(result) ? result.doubleValue() : def;
	}

	/**
	 * Evaluates XPath expression and returns boolean
	 * @param expression String
	 * @return boolean
	 * @throws DOMException if node can not be found with speicfied XPath expression.
	 */
	public boolean findBoolean(String expression) {
		Boolean result = tryFindBoolean(expression);
		if (result != null)
			return result;
		else
			throw newFindError(expression);
	}

	public Boolean tryFindBoolean(String expression) {
		Object found = doFind(expression, XPathConstants.NODE);
		return found != null ? (Boolean)doFind(expression, XPathConstants.BOOLEAN) : null;
	}

	/**
	 * Evaluates XPath expression and returns boolean
	 * @param expression String
	 * @param def boolean
	 * @return boolean
	 */
	public boolean findBoolean(String expression, boolean def) {
		Object found = doFind(expression, XPathConstants.NODE);
		return found != null ? (Boolean)doFind(expression, XPathConstants.BOOLEAN) : def;
	}

	/**
	 * Evaluates XPath expression and returns node list
	 * @param expression String
	 * @return NodeHelperList
	 */
	public NodeHelperList findAll(String expression) {
		return NodeHelperList.newNodeHelperList((NodeList)doFind(expression, XPathConstants.NODESET));
	}

	private Object doFind(String expression, QName type) throws DOMException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			return xPath.evaluate(expression, node, type);
		}
		catch (XPathExpressionException ex) {
			DOMException domEx = new DOMException(DOMException.SYNTAX_ERR, ex.getMessage());
			domEx.initCause(ex);
			throw domEx;
		}
	}

	private static boolean isNaN(Number n) {
		return n instanceof Double && ((Double)n).isNaN();
	}

	private static DOMException newFindError(String expression) throws DOMException {
		String message = MessageFormat.format("Can''t find node with expression: ''{0}''." , expression);
		return new DOMException(DOMException.NOT_FOUND_ERR, message);
	}

	/**
	 * Removes all children of this node.
	 * @throws DOMException if this node is read only.
	 */
	public void removeAllChildren() throws DOMException {
		checkReadOnly();
		NodeList children = node.getChildNodes();
		for (int i = children.getLength()-1; i >= 0; i--)
			node.removeChild(children.item(i));
	}


	/**
	 * Removes this node from parent.
	 * @throws DOMException if this node is read only or has no parent.
	 */
	public void remove() throws DOMException {
		checkReadOnly();
		Node parent = node.getParentNode();
		if (parent != null)
			parent.removeChild(node);
		else
			throw new DOMException(DOMException.INVALID_ACCESS_ERR, "Can not remove top node.");
	}

	/**
	 * Replaces this element with the new one.
	 * @param newNode new node.
	 * @throws DOMException if descendant element with specified tag name does not exist,
	 * or this node is read only.
	 */
	public void replace(Node newNode) throws DOMException {
		checkReadOnly();
		Node parent = node.getParentNode();
		if (parent != null)
			parent.replaceChild(undecorate(newNode), node);
		else
			throw new DOMException(DOMException.INVALID_ACCESS_ERR, "Can not replace top node.");
	}

	/**
	 * Replaces this element with the new text node that contains specified text.
	 * @param text new node.
	 * @throws DOMException if descendant element with specified tag name does not exist,
	 * or this node is read only.
	 */
	public void replace(String text) throws DOMException {
		replace(node.getOwnerDocument().createTextNode(text));
	}


	// DOM 1 methods of Node interface delegated to node

	@Override public String getNodeName() {
		return node.getNodeName();
	}

	@Override public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	@Override public void setNodeValue(String nodeValue) throws DOMException {
		checkReadOnly();
		node.setNodeValue(nodeValue);
	}

	@Override public short getNodeType() {
		return node.getNodeType();
	}

	@Override public Node getParentNode() {
		return decorate(node.getParentNode());
	}

	@Override public NodeList getChildNodes() {
		return node.getChildNodes();
	}

	@Override public Node getFirstChild() {
		return decorate(node.getFirstChild());
	}

	@Override public Node getLastChild() {
		return decorate(node.getLastChild());
	}

	@Override public Node getPreviousSibling() {
		return decorate(node.getPreviousSibling());
	}

	@Override public Node getNextSibling() {
		return decorate(node.getNextSibling());
	}

	@Override public NamedNodeMap getAttributes() {
		return node.getAttributes();
	}

	@Override public Document getOwnerDocument() {
		return node.getOwnerDocument();
	}

	@Override public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		checkReadOnly();
		return decorate(node.insertBefore(undecorate(newChild), undecorate(refChild)));
	}

	@Override public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		checkReadOnly();
		return decorate(node.replaceChild(undecorate(newChild), undecorate(oldChild)));
	}

	@Override public Node removeChild(Node oldChild) throws DOMException {
		checkReadOnly();
		return decorate(node.removeChild(undecorate(oldChild)));
	}

	@Override public Node appendChild(Node newChild) throws DOMException {
		checkReadOnly();
		return decorate(node.appendChild(undecorate(newChild)));
	}

	@Override public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	@Override public NodeHelper cloneNode(boolean deep) {
		return decorate(node.cloneNode(deep));
	}


	// Methods introduced in DOM 2

	@Override public boolean hasAttributes() {
		return node.hasAttributes();
	}

	@Override public String getLocalName() {
		return node.getLocalName();
	}

	@Override public String getPrefix() {
		return node.getPrefix();
	}

	@Override public void setPrefix(String prefix) throws DOMException {
		checkReadOnly();
		node.setPrefix(prefix);
	}

	@Override public String getNamespaceURI() {
		return node.getNamespaceURI();
	}

	@Override public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}

	@Override public void normalize() {
		checkReadOnly();
		node.normalize();
	}


	// Methods introduced in DOM 3

	@Override public short compareDocumentPosition(Node other) throws DOMException {
		return node.compareDocumentPosition(undecorate(other));
	}

	@Override public String getTextContent() throws DOMException {
		return node.getTextContent();
	}

	@Override public void setTextContent(String textContent) throws DOMException {
		checkReadOnly();
		node.setTextContent(textContent);
	}

	@Override public boolean isSameNode(Node other) {
		return node.isSameNode(undecorate(other));
	}

	@Override public boolean isEqualNode(Node arg) {
		return node.isEqualNode(undecorate(arg));
	}

	@Override public String lookupPrefix(String namespaceURI) {
		return node.lookupPrefix(namespaceURI);
	}

	@Override public String getBaseURI() {
		return node.getBaseURI();
	}

	@Override public String lookupNamespaceURI(String prefix) {
		return node.lookupNamespaceURI(prefix);
	}

	@Override public boolean isDefaultNamespace(String namespaceURI) {
		return node.isDefaultNamespace(namespaceURI);
	}

	@Override public Object getFeature(String feature, String version) {
		return node.getFeature(feature, version);
	}

	@Override public Object getUserData(String key) {
		return node.getUserData(key);
	}

	@Override public Object setUserData(String key, Object data, UserDataHandler handler) {
		checkReadOnly();
		return node.setUserData(key, data, handler);
	}
}
