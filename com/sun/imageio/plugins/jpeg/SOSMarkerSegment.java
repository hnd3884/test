package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;

class SOSMarkerSegment extends MarkerSegment
{
    int startSpectralSelection;
    int endSpectralSelection;
    int approxHigh;
    int approxLow;
    ScanComponentSpec[] componentSpecs;
    
    SOSMarkerSegment(final boolean b, final byte[] array, final int n) {
        super(218);
        this.startSpectralSelection = 0;
        this.endSpectralSelection = 63;
        this.approxHigh = 0;
        this.approxLow = 0;
        this.componentSpecs = new ScanComponentSpec[n];
        for (int i = 0; i < n; ++i) {
            boolean b2 = false;
            if (b && (i == 1 || i == 2)) {
                b2 = true;
            }
            this.componentSpecs[i] = new ScanComponentSpec(array[i], (int)(b2 ? 1 : 0));
        }
    }
    
    SOSMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        final byte b = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.componentSpecs = new ScanComponentSpec[b];
        for (byte b2 = 0; b2 < b; ++b2) {
            this.componentSpecs[b2] = new ScanComponentSpec(jpegBuffer);
        }
        this.startSpectralSelection = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.endSpectralSelection = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.approxHigh = jpegBuffer.buf[jpegBuffer.bufPtr] >> 4;
        this.approxLow = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xF);
        jpegBuffer.bufAvail -= this.length;
    }
    
    SOSMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(218);
        this.startSpectralSelection = 0;
        this.endSpectralSelection = 63;
        this.approxHigh = 0;
        this.approxLow = 0;
        this.updateFromNativeNode(node, true);
    }
    
    @Override
    protected Object clone() {
        final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)super.clone();
        if (this.componentSpecs != null) {
            sosMarkerSegment.componentSpecs = this.componentSpecs.clone();
            for (int i = 0; i < this.componentSpecs.length; ++i) {
                sosMarkerSegment.componentSpecs[i] = (ScanComponentSpec)this.componentSpecs[i].clone();
            }
        }
        return sosMarkerSegment;
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("sos");
        iioMetadataNode.setAttribute("numScanComponents", Integer.toString(this.componentSpecs.length));
        iioMetadataNode.setAttribute("startSpectralSelection", Integer.toString(this.startSpectralSelection));
        iioMetadataNode.setAttribute("endSpectralSelection", Integer.toString(this.endSpectralSelection));
        iioMetadataNode.setAttribute("approxHigh", Integer.toString(this.approxHigh));
        iioMetadataNode.setAttribute("approxLow", Integer.toString(this.approxLow));
        for (int i = 0; i < this.componentSpecs.length; ++i) {
            iioMetadataNode.appendChild(this.componentSpecs[i].getNativeNode());
        }
        return iioMetadataNode;
    }
    
    void updateFromNativeNode(final Node node, final boolean b) throws IIOInvalidTreeException {
        final NamedNodeMap attributes = node.getAttributes();
        final int attributeValue = MarkerSegment.getAttributeValue(node, attributes, "numScanComponents", 1, 4, true);
        final int attributeValue2 = MarkerSegment.getAttributeValue(node, attributes, "startSpectralSelection", 0, 63, false);
        this.startSpectralSelection = ((attributeValue2 != -1) ? attributeValue2 : this.startSpectralSelection);
        final int attributeValue3 = MarkerSegment.getAttributeValue(node, attributes, "endSpectralSelection", 0, 63, false);
        this.endSpectralSelection = ((attributeValue3 != -1) ? attributeValue3 : this.endSpectralSelection);
        final int attributeValue4 = MarkerSegment.getAttributeValue(node, attributes, "approxHigh", 0, 15, false);
        this.approxHigh = ((attributeValue4 != -1) ? attributeValue4 : this.approxHigh);
        final int attributeValue5 = MarkerSegment.getAttributeValue(node, attributes, "approxLow", 0, 15, false);
        this.approxLow = ((attributeValue5 != -1) ? attributeValue5 : this.approxLow);
        final NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() != attributeValue) {
            throw new IIOInvalidTreeException("numScanComponents must match the number of children", node);
        }
        this.componentSpecs = new ScanComponentSpec[attributeValue];
        for (int i = 0; i < attributeValue; ++i) {
            this.componentSpecs[i] = new ScanComponentSpec(childNodes.item(i));
        }
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
    }
    
    @Override
    void print() {
        this.printTag("SOS");
        System.out.print("Start spectral selection: ");
        System.out.println(this.startSpectralSelection);
        System.out.print("End spectral selection: ");
        System.out.println(this.endSpectralSelection);
        System.out.print("Approx high: ");
        System.out.println(this.approxHigh);
        System.out.print("Approx low: ");
        System.out.println(this.approxLow);
        System.out.print("Num scan components: ");
        System.out.println(this.componentSpecs.length);
        for (int i = 0; i < this.componentSpecs.length; ++i) {
            this.componentSpecs[i].print();
        }
    }
    
    ScanComponentSpec getScanComponentSpec(final byte b, final int n) {
        return new ScanComponentSpec(b, n);
    }
    
    class ScanComponentSpec implements Cloneable
    {
        int componentSelector;
        int dcHuffTable;
        int acHuffTable;
        
        ScanComponentSpec(final byte componentSelector, final int n) {
            this.componentSelector = componentSelector;
            this.dcHuffTable = n;
            this.acHuffTable = n;
        }
        
        ScanComponentSpec(final JPEGBuffer jpegBuffer) {
            this.componentSelector = jpegBuffer.buf[jpegBuffer.bufPtr++];
            this.dcHuffTable = jpegBuffer.buf[jpegBuffer.bufPtr] >> 4;
            this.acHuffTable = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xF);
        }
        
        ScanComponentSpec(final Node node) throws IIOInvalidTreeException {
            final NamedNodeMap attributes = node.getAttributes();
            this.componentSelector = MarkerSegment.getAttributeValue(node, attributes, "componentSelector", 0, 255, true);
            this.dcHuffTable = MarkerSegment.getAttributeValue(node, attributes, "dcHuffTable", 0, 3, true);
            this.acHuffTable = MarkerSegment.getAttributeValue(node, attributes, "acHuffTable", 0, 3, true);
        }
        
        @Override
        protected Object clone() {
            try {
                return super.clone();
            }
            catch (final CloneNotSupportedException ex) {
                return null;
            }
        }
        
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("scanComponentSpec");
            iioMetadataNode.setAttribute("componentSelector", Integer.toString(this.componentSelector));
            iioMetadataNode.setAttribute("dcHuffTable", Integer.toString(this.dcHuffTable));
            iioMetadataNode.setAttribute("acHuffTable", Integer.toString(this.acHuffTable));
            return iioMetadataNode;
        }
        
        void print() {
            System.out.print("Component Selector: ");
            System.out.println(this.componentSelector);
            System.out.print("DC huffman table: ");
            System.out.println(this.dcHuffTable);
            System.out.print("AC huffman table: ");
            System.out.println(this.acHuffTable);
        }
    }
}
