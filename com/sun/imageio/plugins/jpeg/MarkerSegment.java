package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.IOException;
import javax.imageio.IIOException;

class MarkerSegment implements Cloneable
{
    protected static final int LENGTH_SIZE = 2;
    int tag;
    int length;
    byte[] data;
    boolean unknown;
    
    MarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        this.data = null;
        this.unknown = false;
        jpegBuffer.loadBuf(3);
        this.tag = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.length = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.length |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.length -= 2;
        if (this.length < 0) {
            throw new IIOException("Invalid segment length: " + this.length);
        }
        jpegBuffer.bufAvail -= 3;
        jpegBuffer.loadBuf(this.length);
    }
    
    MarkerSegment(final int tag) {
        this.data = null;
        this.unknown = false;
        this.tag = tag;
        this.length = 0;
    }
    
    MarkerSegment(final Node node) throws IIOInvalidTreeException {
        this.data = null;
        this.unknown = false;
        this.tag = getAttributeValue(node, null, "MarkerTag", 0, 255, true);
        this.length = 0;
        if (node instanceof IIOMetadataNode) {
            final IIOMetadataNode iioMetadataNode = (IIOMetadataNode)node;
            try {
                this.data = (byte[])iioMetadataNode.getUserObject();
            }
            catch (final Exception ex) {
                final IIOInvalidTreeException ex2 = new IIOInvalidTreeException("Can't get User Object", node);
                ex2.initCause(ex);
                throw ex2;
            }
            return;
        }
        throw new IIOInvalidTreeException("Node must have User Object", node);
    }
    
    @Override
    protected Object clone() {
        MarkerSegment markerSegment = null;
        try {
            markerSegment = (MarkerSegment)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        if (this.data != null) {
            markerSegment.data = this.data.clone();
        }
        return markerSegment;
    }
    
    void loadData(final JPEGBuffer jpegBuffer) throws IOException {
        jpegBuffer.readData(this.data = new byte[this.length]);
    }
    
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("unknown");
        iioMetadataNode.setAttribute("MarkerTag", Integer.toString(this.tag));
        iioMetadataNode.setUserObject(this.data);
        return iioMetadataNode;
    }
    
    static int getAttributeValue(final Node node, NamedNodeMap attributes, final String s, final int n, final int n2, final boolean b) throws IIOInvalidTreeException {
        if (attributes == null) {
            attributes = node.getAttributes();
        }
        final String nodeValue = attributes.getNamedItem(s).getNodeValue();
        int int1 = -1;
        if (nodeValue == null) {
            if (b) {
                throw new IIOInvalidTreeException(s + " attribute not found", node);
            }
        }
        else {
            int1 = Integer.parseInt(nodeValue);
            if (int1 < n || int1 > n2) {
                throw new IIOInvalidTreeException(s + " attribute out of range", node);
            }
        }
        return int1;
    }
    
    void writeTag(final ImageOutputStream imageOutputStream) throws IOException {
        imageOutputStream.write(255);
        imageOutputStream.write(this.tag);
        write2bytes(imageOutputStream, this.length);
    }
    
    void write(final ImageOutputStream imageOutputStream) throws IOException {
        this.length = 2 + ((this.data != null) ? this.data.length : 0);
        this.writeTag(imageOutputStream);
        if (this.data != null) {
            imageOutputStream.write(this.data);
        }
    }
    
    static void write2bytes(final ImageOutputStream imageOutputStream, final int n) throws IOException {
        imageOutputStream.write(n >> 8 & 0xFF);
        imageOutputStream.write(n & 0xFF);
    }
    
    void printTag(final String s) {
        System.out.println(s + " marker segment - marker = 0x" + Integer.toHexString(this.tag));
        System.out.println("length: " + this.length);
    }
    
    void print() {
        this.printTag("Unknown");
        if (this.length > 10) {
            System.out.print("First 5 bytes:");
            for (int i = 0; i < 5; ++i) {
                System.out.print(" Ox" + Integer.toHexString(this.data[i]));
            }
            System.out.print("\nLast 5 bytes:");
            for (int j = this.data.length - 5; j < this.data.length; ++j) {
                System.out.print(" Ox" + Integer.toHexString(this.data[j]));
            }
        }
        else {
            System.out.print("Data:");
            for (int k = 0; k < this.data.length; ++k) {
                System.out.print(" Ox" + Integer.toHexString(this.data[k]));
            }
        }
        System.out.println();
    }
}
