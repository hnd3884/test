package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;

class SOFMarkerSegment extends MarkerSegment
{
    int samplePrecision;
    int numLines;
    int samplesPerLine;
    ComponentSpec[] componentSpecs;
    
    SOFMarkerSegment(final boolean b, final boolean b2, final boolean b3, final byte[] array, final int n) {
        super(b ? 194 : (b2 ? 193 : 192));
        this.samplePrecision = 8;
        this.numLines = 0;
        this.samplesPerLine = 0;
        this.componentSpecs = new ComponentSpec[n];
        for (int i = 0; i < n; ++i) {
            int n2 = 1;
            boolean b4 = false;
            if (b3) {
                n2 = 2;
                if (i == 1 || i == 2) {
                    n2 = 1;
                    b4 = true;
                }
            }
            this.componentSpecs[i] = new ComponentSpec(array[i], n2, (int)(b4 ? 1 : 0));
        }
    }
    
    SOFMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.samplePrecision = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.numLines = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.numLines |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.samplesPerLine = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.samplesPerLine |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        final int n = jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF;
        this.componentSpecs = new ComponentSpec[n];
        for (int i = 0; i < n; ++i) {
            this.componentSpecs[i] = new ComponentSpec(jpegBuffer);
        }
        jpegBuffer.bufAvail -= this.length;
    }
    
    SOFMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(192);
        this.samplePrecision = 8;
        this.numLines = 0;
        this.samplesPerLine = 0;
        this.updateFromNativeNode(node, true);
    }
    
    @Override
    protected Object clone() {
        final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)super.clone();
        if (this.componentSpecs != null) {
            sofMarkerSegment.componentSpecs = this.componentSpecs.clone();
            for (int i = 0; i < this.componentSpecs.length; ++i) {
                sofMarkerSegment.componentSpecs[i] = (ComponentSpec)this.componentSpecs[i].clone();
            }
        }
        return sofMarkerSegment;
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("sof");
        iioMetadataNode.setAttribute("process", Integer.toString(this.tag - 192));
        iioMetadataNode.setAttribute("samplePrecision", Integer.toString(this.samplePrecision));
        iioMetadataNode.setAttribute("numLines", Integer.toString(this.numLines));
        iioMetadataNode.setAttribute("samplesPerLine", Integer.toString(this.samplesPerLine));
        iioMetadataNode.setAttribute("numFrameComponents", Integer.toString(this.componentSpecs.length));
        for (int i = 0; i < this.componentSpecs.length; ++i) {
            iioMetadataNode.appendChild(this.componentSpecs[i].getNativeNode());
        }
        return iioMetadataNode;
    }
    
    void updateFromNativeNode(final Node node, final boolean b) throws IIOInvalidTreeException {
        final NamedNodeMap attributes = node.getAttributes();
        final int attributeValue = MarkerSegment.getAttributeValue(node, attributes, "process", 0, 2, false);
        this.tag = ((attributeValue != -1) ? (attributeValue + 192) : this.tag);
        MarkerSegment.getAttributeValue(node, attributes, "samplePrecision", 8, 8, false);
        final int attributeValue2 = MarkerSegment.getAttributeValue(node, attributes, "numLines", 0, 65535, false);
        this.numLines = ((attributeValue2 != -1) ? attributeValue2 : this.numLines);
        final int attributeValue3 = MarkerSegment.getAttributeValue(node, attributes, "samplesPerLine", 0, 65535, false);
        this.samplesPerLine = ((attributeValue3 != -1) ? attributeValue3 : this.samplesPerLine);
        final int attributeValue4 = MarkerSegment.getAttributeValue(node, attributes, "numFrameComponents", 1, 4, false);
        final NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() != attributeValue4) {
            throw new IIOInvalidTreeException("numFrameComponents must match number of children", node);
        }
        this.componentSpecs = new ComponentSpec[attributeValue4];
        for (int i = 0; i < attributeValue4; ++i) {
            this.componentSpecs[i] = new ComponentSpec(childNodes.item(i));
        }
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
    }
    
    @Override
    void print() {
        this.printTag("SOF");
        System.out.print("Sample precision: ");
        System.out.println(this.samplePrecision);
        System.out.print("Number of lines: ");
        System.out.println(this.numLines);
        System.out.print("Samples per line: ");
        System.out.println(this.samplesPerLine);
        System.out.print("Number of components: ");
        System.out.println(this.componentSpecs.length);
        for (int i = 0; i < this.componentSpecs.length; ++i) {
            this.componentSpecs[i].print();
        }
    }
    
    int getIDencodedCSType() {
        for (int i = 0; i < this.componentSpecs.length; ++i) {
            if (this.componentSpecs[i].componentId < 65) {
                return 0;
            }
        }
        switch (this.componentSpecs.length) {
            case 3: {
                if (this.componentSpecs[0].componentId == 82 && this.componentSpecs[1].componentId == 71 && this.componentSpecs[2].componentId == 66) {
                    return 2;
                }
                break;
            }
        }
        return 0;
    }
    
    ComponentSpec getComponentSpec(final byte b, final int n, final int n2) {
        return new ComponentSpec(b, n, n2);
    }
    
    class ComponentSpec implements Cloneable
    {
        int componentId;
        int HsamplingFactor;
        int VsamplingFactor;
        int QtableSelector;
        
        ComponentSpec(final byte componentId, final int n, final int qtableSelector) {
            this.componentId = componentId;
            this.HsamplingFactor = n;
            this.VsamplingFactor = n;
            this.QtableSelector = qtableSelector;
        }
        
        ComponentSpec(final JPEGBuffer jpegBuffer) {
            this.componentId = jpegBuffer.buf[jpegBuffer.bufPtr++];
            this.HsamplingFactor = jpegBuffer.buf[jpegBuffer.bufPtr] >>> 4;
            this.VsamplingFactor = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xF);
            this.QtableSelector = jpegBuffer.buf[jpegBuffer.bufPtr++];
        }
        
        ComponentSpec(final Node node) throws IIOInvalidTreeException {
            final NamedNodeMap attributes = node.getAttributes();
            this.componentId = MarkerSegment.getAttributeValue(node, attributes, "componentId", 0, 255, true);
            this.HsamplingFactor = MarkerSegment.getAttributeValue(node, attributes, "HsamplingFactor", 1, 255, true);
            this.VsamplingFactor = MarkerSegment.getAttributeValue(node, attributes, "VsamplingFactor", 1, 255, true);
            this.QtableSelector = MarkerSegment.getAttributeValue(node, attributes, "QtableSelector", 0, 3, true);
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
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("componentSpec");
            iioMetadataNode.setAttribute("componentId", Integer.toString(this.componentId));
            iioMetadataNode.setAttribute("HsamplingFactor", Integer.toString(this.HsamplingFactor));
            iioMetadataNode.setAttribute("VsamplingFactor", Integer.toString(this.VsamplingFactor));
            iioMetadataNode.setAttribute("QtableSelector", Integer.toString(this.QtableSelector));
            return iioMetadataNode;
        }
        
        void print() {
            System.out.print("Component ID: ");
            System.out.println(this.componentId);
            System.out.print("H sampling factor: ");
            System.out.println(this.HsamplingFactor);
            System.out.print("V sampling factor: ");
            System.out.println(this.VsamplingFactor);
            System.out.print("Q table selector: ");
            System.out.println(this.QtableSelector);
        }
    }
}
