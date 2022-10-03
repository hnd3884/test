package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import javax.imageio.metadata.IIOMetadata;

abstract class GIFMetadata extends IIOMetadata
{
    static final int UNDEFINED_INTEGER_VALUE = -1;
    
    protected static void fatal(final Node node, final String s) throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(s, node);
    }
    
    protected static String getStringAttribute(final Node node, final String s, final String s2, final boolean b, final String[] array) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return s2;
            }
            fatal(node, "Required attribute " + s + " not present!");
        }
        final String nodeValue = namedItem.getNodeValue();
        if (array != null) {
            if (nodeValue == null) {
                fatal(node, "Null value for " + node.getNodeName() + " attribute " + s + "!");
            }
            boolean b2 = false;
            for (int length = array.length, i = 0; i < length; ++i) {
                if (nodeValue.equals(array[i])) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                fatal(node, "Bad value for " + node.getNodeName() + " attribute " + s + "!");
            }
        }
        return nodeValue;
    }
    
    protected static int getIntAttribute(final Node node, final String s, final int n, final boolean b, final boolean b2, final int n2, final int n3) throws IIOInvalidTreeException {
        final String stringAttribute = getStringAttribute(node, s, null, b, null);
        if (stringAttribute == null || "".equals(stringAttribute)) {
            return n;
        }
        int int1 = n;
        try {
            int1 = Integer.parseInt(stringAttribute);
        }
        catch (final NumberFormatException ex) {
            fatal(node, "Bad value for " + node.getNodeName() + " attribute " + s + "!");
        }
        if (b2 && (int1 < n2 || int1 > n3)) {
            fatal(node, "Bad value for " + node.getNodeName() + " attribute " + s + "!");
        }
        return int1;
    }
    
    protected static float getFloatAttribute(final Node node, final String s, final float n, final boolean b) throws IIOInvalidTreeException {
        final String stringAttribute = getStringAttribute(node, s, null, b, null);
        if (stringAttribute == null) {
            return n;
        }
        return Float.parseFloat(stringAttribute);
    }
    
    protected static int getIntAttribute(final Node node, final String s, final boolean b, final int n, final int n2) throws IIOInvalidTreeException {
        return getIntAttribute(node, s, -1, true, b, n, n2);
    }
    
    protected static float getFloatAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return getFloatAttribute(node, s, -1.0f, true);
    }
    
    protected static boolean getBooleanAttribute(final Node node, final String s, final boolean b, final boolean b2) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b2) {
                return b;
            }
            fatal(node, "Required attribute " + s + " not present!");
        }
        final String nodeValue = namedItem.getNodeValue();
        if (nodeValue.equals("TRUE") || nodeValue.equals("true")) {
            return true;
        }
        if (nodeValue.equals("FALSE") || nodeValue.equals("false")) {
            return false;
        }
        fatal(node, "Attribute " + s + " must be 'TRUE' or 'FALSE'!");
        return false;
    }
    
    protected static boolean getBooleanAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return getBooleanAttribute(node, s, false, true);
    }
    
    protected static int getEnumeratedAttribute(final Node node, final String s, final String[] array, final int n, final boolean b) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return n;
            }
            fatal(node, "Required attribute " + s + " not present!");
        }
        final String nodeValue = namedItem.getNodeValue();
        for (int i = 0; i < array.length; ++i) {
            if (nodeValue.equals(array[i])) {
                return i;
            }
        }
        fatal(node, "Illegal value for attribute " + s + "!");
        return -1;
    }
    
    protected static int getEnumeratedAttribute(final Node node, final String s, final String[] array) throws IIOInvalidTreeException {
        return getEnumeratedAttribute(node, s, array, -1, true);
    }
    
    protected static String getAttribute(final Node node, final String s, final String s2, final boolean b) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return s2;
            }
            fatal(node, "Required attribute " + s + " not present!");
        }
        return namedItem.getNodeValue();
    }
    
    protected static String getAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return getAttribute(node, s, null, true);
    }
    
    protected GIFMetadata(final boolean b, final String s, final String s2, final String[] array, final String[] array2) {
        super(b, s, s2, array, array2);
    }
    
    @Override
    public void mergeTree(final String s, final Node node) throws IIOInvalidTreeException {
        if (s.equals(this.nativeMetadataFormatName)) {
            if (node == null) {
                throw new IllegalArgumentException("root == null!");
            }
            this.mergeNativeTree(node);
        }
        else {
            if (!s.equals("javax_imageio_1.0")) {
                throw new IllegalArgumentException("Not a recognized format!");
            }
            if (node == null) {
                throw new IllegalArgumentException("root == null!");
            }
            this.mergeStandardTree(node);
        }
    }
    
    protected byte[] getColorTable(final Node node, final String s, final boolean b, final int n) throws IIOInvalidTreeException {
        final byte[] array = new byte[256];
        final byte[] array2 = new byte[256];
        final byte[] array3 = new byte[256];
        int n2 = -1;
        Node node2 = node.getFirstChild();
        if (node2 == null) {
            fatal(node, "Palette has no entries!");
        }
        while (node2 != null) {
            if (!node2.getNodeName().equals(s)) {
                fatal(node, "Only a " + s + " may be a child of a " + node2.getNodeName() + "!");
            }
            final int intAttribute = getIntAttribute(node2, "index", true, 0, 255);
            if (intAttribute > n2) {
                n2 = intAttribute;
            }
            array[intAttribute] = (byte)getIntAttribute(node2, "red", true, 0, 255);
            array2[intAttribute] = (byte)getIntAttribute(node2, "green", true, 0, 255);
            array3[intAttribute] = (byte)getIntAttribute(node2, "blue", true, 0, 255);
            node2 = node2.getNextSibling();
        }
        final int n3 = n2 + 1;
        if (b && n3 != n) {
            fatal(node, "Unexpected length for palette!");
        }
        final byte[] array4 = new byte[3 * n3];
        int i = 0;
        int n4 = 0;
        while (i < n3) {
            array4[n4++] = array[i];
            array4[n4++] = array2[i];
            array4[n4++] = array3[i];
            ++i;
        }
        return array4;
    }
    
    protected abstract void mergeNativeTree(final Node p0) throws IIOInvalidTreeException;
    
    protected abstract void mergeStandardTree(final Node p0) throws IIOInvalidTreeException;
}
