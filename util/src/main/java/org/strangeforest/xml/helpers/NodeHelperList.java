package org.strangeforest.xml.helpers;

import org.w3c.dom.*;
import java.util.Iterator;

/**
 * <p>This class is a <i>Decorator</i> over the standard W3C DOM 3 <tt>NodeList</tt> interface.</p>
 */
public class NodeHelperList implements NodeList, Iterable<NodeHelper> {

	// Factory methods

	static NodeHelperList newNodeHelperList(NodeList nodeList) {
		if (nodeList instanceof NodeHelperList)
			return (NodeHelperList)nodeList;
		else
			return new NodeHelperList(nodeList);
	}


	// Instance methods

	private NodeHelper[] nodeHelpers;

	/**
	 * Creates new <tt>NodeHelperList</tt> instance that decorates specified <tt>NodeList</tt>.
	 * @param nodeList <tt>NodeList</tt> to be decorated.
	 */
	public NodeHelperList(NodeList nodeList) {
		super();
		setItems(nodeList);
	}

	NodeHelperList(NodeHelper[] nodeHelpers) {
		super();
		this.nodeHelpers = nodeHelpers;
	}


	// Helper methods

	/**
	 * Returns <tt>NodeHelper</tt> at the specified position in this list.
	 * @param index position of the <tt>NodeHelper</tt> to be returns. First <tt>NodeHelper</tt> is with index 0.
	 * @return <tt>NodeHelper</tt> at the specified position in this list, or null if index is not valid.
	 */
	public NodeHelper itemHelper(int index) {
		try {
			return nodeHelpers[index];
		}
		catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}


	// NodeList methods

	@Override public Node item(int index) {
		return itemHelper(index);
	}

	@Override public int getLength() {
		return nodeHelpers.length;
	}


	// Iterable method

	@Override public Iterator<NodeHelper> iterator() {
		return new Iterator<NodeHelper>() {
			private int index = 0;
			@Override public boolean hasNext() {
				return index < nodeHelpers.length;
			}
			@Override public NodeHelper next() {
				return nodeHelpers[index++];
			}
			@Override public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}


	// Utility methods

	private void setItems(NodeList nodeList) {
		int nodeCount = nodeList.getLength();
		nodeHelpers = new NodeHelper[nodeCount];
		for (int index = 0; index < nodeCount; index++)
			nodeHelpers[index] = NodeHelper.decorate(nodeList.item(index));
	}
}
