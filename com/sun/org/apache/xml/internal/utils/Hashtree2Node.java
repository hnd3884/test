package com.sun.org.apache.xml.internal.utils;

import java.util.Enumeration;
import org.w3c.dom.Element;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.Hashtable;

public abstract class Hashtree2Node
{
    public static void appendHashToNode(final Hashtable hash, final String name, final Node container, final Document factory) {
        if (null == container || null == factory || null == hash) {
            return;
        }
        String elemName = null;
        if (null == name || "".equals(name)) {
            elemName = "appendHashToNode";
        }
        else {
            elemName = name;
        }
        try {
            final Element hashNode = factory.createElement(elemName);
            container.appendChild(hashNode);
            Enumeration keys = hash.keys();
            final Vector v = new Vector();
            while (keys.hasMoreElements()) {
                final Object key = keys.nextElement();
                final String keyStr = key.toString();
                final Object item = hash.get(key);
                if (item instanceof Hashtable) {
                    v.addElement(keyStr);
                    v.addElement(item);
                }
                else {
                    try {
                        final Element node = factory.createElement("item");
                        node.setAttribute("key", keyStr);
                        node.appendChild(factory.createTextNode((String)item));
                        hashNode.appendChild(node);
                    }
                    catch (final Exception e) {
                        final Element node2 = factory.createElement("item");
                        node2.setAttribute("key", keyStr);
                        node2.appendChild(factory.createTextNode("ERROR: Reading " + key + " threw: " + e.toString()));
                        hashNode.appendChild(node2);
                    }
                }
            }
            keys = v.elements();
            while (keys.hasMoreElements()) {
                final String n = keys.nextElement();
                final Hashtable h = keys.nextElement();
                appendHashToNode(h, n, hashNode, factory);
            }
        }
        catch (final Exception e2) {
            e2.printStackTrace();
        }
    }
}
