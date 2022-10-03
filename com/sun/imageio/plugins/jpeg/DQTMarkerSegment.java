package com.sun.imageio.plugins.jpeg;

import org.w3c.dom.NamedNodeMap;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadataNode;
import java.util.Iterator;
import org.w3c.dom.NodeList;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import javax.imageio.plugins.jpeg.JPEGQTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DQTMarkerSegment extends MarkerSegment
{
    List tables;
    
    DQTMarkerSegment(final float n, final boolean b) {
        super(219);
        (this.tables = new ArrayList()).add(new Qtable(true, n));
        if (b) {
            this.tables.add(new Qtable(false, n));
        }
    }
    
    DQTMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.tables = new ArrayList();
        Qtable qtable;
        for (int i = this.length; i > 0; i -= qtable.data.length + 1) {
            qtable = new Qtable(jpegBuffer);
            this.tables.add(qtable);
        }
        jpegBuffer.bufAvail -= this.length;
    }
    
    DQTMarkerSegment(final JPEGQTable[] array) {
        super(219);
        this.tables = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            this.tables.add(new Qtable(array[i], i));
        }
    }
    
    DQTMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(219);
        this.tables = new ArrayList();
        final NodeList childNodes = node.getChildNodes();
        final int length = childNodes.getLength();
        if (length < 1 || length > 4) {
            throw new IIOInvalidTreeException("Invalid DQT node", node);
        }
        for (int i = 0; i < length; ++i) {
            this.tables.add(new Qtable(childNodes.item(i)));
        }
    }
    
    @Override
    protected Object clone() {
        final DQTMarkerSegment dqtMarkerSegment = (DQTMarkerSegment)super.clone();
        dqtMarkerSegment.tables = new ArrayList(this.tables.size());
        final Iterator iterator = this.tables.iterator();
        while (iterator.hasNext()) {
            dqtMarkerSegment.tables.add(((Qtable)iterator.next()).clone());
        }
        return dqtMarkerSegment;
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("dqt");
        for (int i = 0; i < this.tables.size(); ++i) {
            iioMetadataNode.appendChild(((Qtable)this.tables.get(i)).getNativeNode());
        }
        return iioMetadataNode;
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
    }
    
    @Override
    void print() {
        this.printTag("DQT");
        System.out.println("Num tables: " + Integer.toString(this.tables.size()));
        for (int i = 0; i < this.tables.size(); ++i) {
            ((Qtable)this.tables.get(i)).print();
        }
        System.out.println();
    }
    
    Qtable getChromaForLuma(final Qtable qtable) {
        boolean b = true;
        int n = 1;
        while (true) {
            final int n2 = n;
            qtable.getClass();
            if (n2 >= 64) {
                break;
            }
            if (qtable.data[n] != qtable.data[n - 1]) {
                b = false;
                break;
            }
            ++n;
        }
        Object o;
        if (b) {
            o = qtable.clone();
            ((Qtable)o).tableID = 1;
        }
        else {
            int n3 = 0;
            int n4 = 1;
            while (true) {
                final int n5 = n4;
                qtable.getClass();
                if (n5 >= 64) {
                    break;
                }
                if (qtable.data[n4] > qtable.data[n3]) {
                    n3 = n4;
                }
                ++n4;
            }
            o = new Qtable(JPEGQTable.K2Div2Chrominance.getScaledInstance(qtable.data[n3] / (float)JPEGQTable.K1Div2Luminance.getTable()[n3], true), 1);
        }
        return (Qtable)o;
    }
    
    Qtable getQtableFromNode(final Node node) throws IIOInvalidTreeException {
        return new Qtable(node);
    }
    
    class Qtable implements Cloneable
    {
        int elementPrecision;
        int tableID;
        final int QTABLE_SIZE = 64;
        int[] data;
        private final int[] zigzag;
        
        Qtable(final boolean b, float convertToLinearQuality) {
            this.zigzag = new int[] { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
            this.elementPrecision = 0;
            JPEGQTable jpegqTable;
            if (b) {
                this.tableID = 0;
                jpegqTable = JPEGQTable.K1Div2Luminance;
            }
            else {
                this.tableID = 1;
                jpegqTable = JPEGQTable.K2Div2Chrominance;
            }
            if (convertToLinearQuality != 0.75f) {
                convertToLinearQuality = JPEG.convertToLinearQuality(convertToLinearQuality);
                if (b) {
                    jpegqTable = JPEGQTable.K1Luminance.getScaledInstance(convertToLinearQuality, true);
                }
                else {
                    jpegqTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(convertToLinearQuality, true);
                }
            }
            this.data = jpegqTable.getTable();
        }
        
        Qtable(final JPEGBuffer jpegBuffer) throws IIOException {
            this.zigzag = new int[] { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
            this.elementPrecision = jpegBuffer.buf[jpegBuffer.bufPtr] >>> 4;
            this.tableID = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xF);
            if (this.elementPrecision != 0) {
                throw new IIOException("Unsupported element precision");
            }
            this.data = new int[64];
            for (int i = 0; i < 64; ++i) {
                this.data[i] = (jpegBuffer.buf[jpegBuffer.bufPtr + this.zigzag[i]] & 0xFF);
            }
            jpegBuffer.bufPtr += 64;
        }
        
        Qtable(final JPEGQTable jpegqTable, final int tableID) {
            this.zigzag = new int[] { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
            this.elementPrecision = 0;
            this.tableID = tableID;
            this.data = jpegqTable.getTable();
        }
        
        Qtable(final Node node) throws IIOInvalidTreeException {
            this.zigzag = new int[] { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
            if (!node.getNodeName().equals("dqtable")) {
                throw new IIOInvalidTreeException("Invalid node, expected dqtable", node);
            }
            final NamedNodeMap attributes = node.getAttributes();
            final int length = attributes.getLength();
            if (length < 1 || length > 2) {
                throw new IIOInvalidTreeException("dqtable node must have 1 or 2 attributes", node);
            }
            this.elementPrecision = 0;
            this.tableID = MarkerSegment.getAttributeValue(node, attributes, "qtableId", 0, 3, true);
            if (!(node instanceof IIOMetadataNode)) {
                throw new IIOInvalidTreeException("dqtable node must have user object", node);
            }
            final JPEGQTable jpegqTable = (JPEGQTable)((IIOMetadataNode)node).getUserObject();
            if (jpegqTable == null) {
                throw new IIOInvalidTreeException("dqtable node must have user object", node);
            }
            this.data = jpegqTable.getTable();
        }
        
        @Override
        protected Object clone() {
            Qtable qtable = null;
            try {
                qtable = (Qtable)super.clone();
            }
            catch (final CloneNotSupportedException ex) {}
            if (this.data != null) {
                qtable.data = this.data.clone();
            }
            return qtable;
        }
        
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("dqtable");
            iioMetadataNode.setAttribute("elementPrecision", Integer.toString(this.elementPrecision));
            iioMetadataNode.setAttribute("qtableId", Integer.toString(this.tableID));
            iioMetadataNode.setUserObject(new JPEGQTable(this.data));
            return iioMetadataNode;
        }
        
        void print() {
            System.out.println("Table id: " + Integer.toString(this.tableID));
            System.out.println("Element precision: " + Integer.toString(this.elementPrecision));
            new JPEGQTable(this.data).toString();
        }
    }
}
