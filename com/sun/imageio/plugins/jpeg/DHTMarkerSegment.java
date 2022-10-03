package com.sun.imageio.plugins.jpeg;

import org.w3c.dom.NamedNodeMap;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadataNode;
import java.util.Iterator;
import org.w3c.dom.NodeList;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import java.util.ArrayList;
import java.util.List;

class DHTMarkerSegment extends MarkerSegment
{
    List tables;
    
    DHTMarkerSegment(final boolean b) {
        super(196);
        (this.tables = new ArrayList()).add(new Htable(JPEGHuffmanTable.StdDCLuminance, true, 0));
        if (b) {
            this.tables.add(new Htable(JPEGHuffmanTable.StdDCChrominance, true, 1));
        }
        this.tables.add(new Htable(JPEGHuffmanTable.StdACLuminance, false, 0));
        if (b) {
            this.tables.add(new Htable(JPEGHuffmanTable.StdACChrominance, false, 1));
        }
    }
    
    DHTMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.tables = new ArrayList();
        Htable htable;
        for (int i = this.length; i > 0; i -= 17 + htable.values.length) {
            htable = new Htable(jpegBuffer);
            this.tables.add(htable);
        }
        jpegBuffer.bufAvail -= this.length;
    }
    
    DHTMarkerSegment(final JPEGHuffmanTable[] array, final JPEGHuffmanTable[] array2) {
        super(196);
        this.tables = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            this.tables.add(new Htable(array[i], true, i));
        }
        for (int j = 0; j < array2.length; ++j) {
            this.tables.add(new Htable(array2[j], false, j));
        }
    }
    
    DHTMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(196);
        this.tables = new ArrayList();
        final NodeList childNodes = node.getChildNodes();
        final int length = childNodes.getLength();
        if (length < 1 || length > 4) {
            throw new IIOInvalidTreeException("Invalid DHT node", node);
        }
        for (int i = 0; i < length; ++i) {
            this.tables.add(new Htable(childNodes.item(i)));
        }
    }
    
    @Override
    protected Object clone() {
        final DHTMarkerSegment dhtMarkerSegment = (DHTMarkerSegment)super.clone();
        dhtMarkerSegment.tables = new ArrayList(this.tables.size());
        final Iterator iterator = this.tables.iterator();
        while (iterator.hasNext()) {
            dhtMarkerSegment.tables.add(((Htable)iterator.next()).clone());
        }
        return dhtMarkerSegment;
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("dht");
        for (int i = 0; i < this.tables.size(); ++i) {
            iioMetadataNode.appendChild(((Htable)this.tables.get(i)).getNativeNode());
        }
        return iioMetadataNode;
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
    }
    
    @Override
    void print() {
        this.printTag("DHT");
        System.out.println("Num tables: " + Integer.toString(this.tables.size()));
        for (int i = 0; i < this.tables.size(); ++i) {
            ((Htable)this.tables.get(i)).print();
        }
        System.out.println();
    }
    
    Htable getHtableFromNode(final Node node) throws IIOInvalidTreeException {
        return new Htable(node);
    }
    
    void addHtable(final JPEGHuffmanTable jpegHuffmanTable, final boolean b, final int n) {
        this.tables.add(new Htable(jpegHuffmanTable, b, n));
    }
    
    class Htable implements Cloneable
    {
        int tableClass;
        int tableID;
        private static final int NUM_LENGTHS = 16;
        short[] numCodes;
        short[] values;
        
        Htable(final JPEGBuffer jpegBuffer) {
            this.numCodes = new short[16];
            this.tableClass = jpegBuffer.buf[jpegBuffer.bufPtr] >>> 4;
            this.tableID = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xF);
            for (int i = 0; i < 16; ++i) {
                this.numCodes[i] = (short)(jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
            }
            int n = 0;
            for (int j = 0; j < 16; ++j) {
                n += this.numCodes[j];
            }
            this.values = new short[n];
            for (short n2 = 0; n2 < n; ++n2) {
                this.values[n2] = (short)(jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
            }
        }
        
        Htable(final JPEGHuffmanTable jpegHuffmanTable, final boolean b, final int tableID) {
            this.numCodes = new short[16];
            this.tableClass = (b ? 0 : 1);
            this.tableID = tableID;
            this.numCodes = jpegHuffmanTable.getLengths();
            this.values = jpegHuffmanTable.getValues();
        }
        
        Htable(final Node node) throws IIOInvalidTreeException {
            this.numCodes = new short[16];
            if (!node.getNodeName().equals("dhtable")) {
                throw new IIOInvalidTreeException("Invalid node, expected dqtable", node);
            }
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes.getLength() != 2) {
                throw new IIOInvalidTreeException("dhtable node must have 2 attributes", node);
            }
            this.tableClass = MarkerSegment.getAttributeValue(node, attributes, "class", 0, 1, true);
            this.tableID = MarkerSegment.getAttributeValue(node, attributes, "htableId", 0, 3, true);
            if (!(node instanceof IIOMetadataNode)) {
                throw new IIOInvalidTreeException("dhtable node must have user object", node);
            }
            final JPEGHuffmanTable jpegHuffmanTable = (JPEGHuffmanTable)((IIOMetadataNode)node).getUserObject();
            if (jpegHuffmanTable == null) {
                throw new IIOInvalidTreeException("dhtable node must have user object", node);
            }
            this.numCodes = jpegHuffmanTable.getLengths();
            this.values = jpegHuffmanTable.getValues();
        }
        
        @Override
        protected Object clone() {
            Htable htable = null;
            try {
                htable = (Htable)super.clone();
            }
            catch (final CloneNotSupportedException ex) {}
            if (this.numCodes != null) {
                htable.numCodes = this.numCodes.clone();
            }
            if (this.values != null) {
                htable.values = this.values.clone();
            }
            return htable;
        }
        
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("dhtable");
            iioMetadataNode.setAttribute("class", Integer.toString(this.tableClass));
            iioMetadataNode.setAttribute("htableId", Integer.toString(this.tableID));
            iioMetadataNode.setUserObject(new JPEGHuffmanTable(this.numCodes, this.values));
            return iioMetadataNode;
        }
        
        void print() {
            System.out.println("Huffman Table");
            System.out.println("table class: " + ((this.tableClass == 0) ? "DC" : "AC"));
            System.out.println("table id: " + Integer.toString(this.tableID));
            new JPEGHuffmanTable(this.numCodes, this.values).toString();
        }
    }
}
