package com.BSISJ7.TestCreator.testIO;

/**
 * Utility by Holger
 * https://stackoverflow.com/questions/19589231/can-i-iterate-through-a-nodelist-using-for-each-in-java
 */

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public final class XmlUtil {
    private XmlUtil() {
    }

    /**
     * Returns a NodeList as a List<Node>
     */
    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0 ?
                Collections.<Node>emptyList() : new NodeListWrapper(n);
    }

    static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        private final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        public Node get(int index) {
            return list.item(index);
        }

        public int size() {
            return list.getLength();
        }
    }
}
