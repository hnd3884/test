package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;

class DRIMarkerSegment extends MarkerSegment
{
    int restartInterval;
    
    DRIMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.restartInterval = 0;
        this.restartInterval = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.restartInterval |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        jpegBuffer.bufAvail -= this.length;
    }
    
    DRIMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(221);
        this.restartInterval = 0;
        this.updateFromNativeNode(node, true);
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("dri");
        iioMetadataNode.setAttribute("interval", Integer.toString(this.restartInterval));
        return iioMetadataNode;
    }
    
    void updateFromNativeNode(final Node node, final boolean b) throws IIOInvalidTreeException {
        this.restartInterval = MarkerSegment.getAttributeValue(node, null, "interval", 0, 65535, true);
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
    }
    
    @Override
    void print() {
        this.printTag("DRI");
        System.out.println("Interval: " + Integer.toString(this.restartInterval));
    }
}
