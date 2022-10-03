package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;

class AdobeMarkerSegment extends MarkerSegment
{
    int version;
    int flags0;
    int flags1;
    int transform;
    private static final int ID_SIZE = 5;
    
    AdobeMarkerSegment(final int transform) {
        super(238);
        this.version = 101;
        this.flags0 = 0;
        this.flags1 = 0;
        this.transform = transform;
    }
    
    AdobeMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        jpegBuffer.bufPtr += 5;
        this.version = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.version |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.flags0 = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.flags0 |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.flags1 = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.flags1 |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.transform = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        jpegBuffer.bufAvail -= this.length;
    }
    
    AdobeMarkerSegment(final Node node) throws IIOInvalidTreeException {
        this(0);
        this.updateFromNativeNode(node, true);
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("app14Adobe");
        iioMetadataNode.setAttribute("version", Integer.toString(this.version));
        iioMetadataNode.setAttribute("flags0", Integer.toString(this.flags0));
        iioMetadataNode.setAttribute("flags1", Integer.toString(this.flags1));
        iioMetadataNode.setAttribute("transform", Integer.toString(this.transform));
        return iioMetadataNode;
    }
    
    void updateFromNativeNode(final Node node, final boolean b) throws IIOInvalidTreeException {
        final NamedNodeMap attributes = node.getAttributes();
        this.transform = MarkerSegment.getAttributeValue(node, attributes, "transform", 0, 2, true);
        final int length = attributes.getLength();
        if (length > 4) {
            throw new IIOInvalidTreeException("Adobe APP14 node cannot have > 4 attributes", node);
        }
        if (length > 1) {
            final int attributeValue = MarkerSegment.getAttributeValue(node, attributes, "version", 100, 255, false);
            this.version = ((attributeValue != -1) ? attributeValue : this.version);
            final int attributeValue2 = MarkerSegment.getAttributeValue(node, attributes, "flags0", 0, 65535, false);
            this.flags0 = ((attributeValue2 != -1) ? attributeValue2 : this.flags0);
            final int attributeValue3 = MarkerSegment.getAttributeValue(node, attributes, "flags1", 0, 65535, false);
            this.flags1 = ((attributeValue3 != -1) ? attributeValue3 : this.flags1);
        }
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
        this.length = 14;
        this.writeTag(imageOutputStream);
        imageOutputStream.write(new byte[] { 65, 100, 111, 98, 101 });
        MarkerSegment.write2bytes(imageOutputStream, this.version);
        MarkerSegment.write2bytes(imageOutputStream, this.flags0);
        MarkerSegment.write2bytes(imageOutputStream, this.flags1);
        imageOutputStream.write(this.transform);
    }
    
    static void writeAdobeSegment(final ImageOutputStream imageOutputStream, final int n) throws IOException {
        new AdobeMarkerSegment(n).write(imageOutputStream);
    }
    
    @Override
    void print() {
        this.printTag("Adobe APP14");
        System.out.print("Version: ");
        System.out.println(this.version);
        System.out.print("Flags0: 0x");
        System.out.println(Integer.toHexString(this.flags0));
        System.out.print("Flags1: 0x");
        System.out.println(Integer.toHexString(this.flags1));
        System.out.print("Transform: ");
        System.out.println(this.transform);
    }
}
