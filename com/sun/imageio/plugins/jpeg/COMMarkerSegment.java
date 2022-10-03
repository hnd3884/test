package com.sun.imageio.plugins.jpeg;

import javax.imageio.stream.ImageOutputStream;
import java.io.UnsupportedEncodingException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import java.io.IOException;

class COMMarkerSegment extends MarkerSegment
{
    private static final String ENCODING = "ISO-8859-1";
    
    COMMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.loadData(jpegBuffer);
    }
    
    COMMarkerSegment(final String s) {
        super(254);
        this.data = s.getBytes();
    }
    
    COMMarkerSegment(final Node node) throws IIOInvalidTreeException {
        super(254);
        if (node instanceof IIOMetadataNode) {
            this.data = (byte[])((IIOMetadataNode)node).getUserObject();
        }
        if (this.data == null) {
            final String nodeValue = node.getAttributes().getNamedItem("comment").getNodeValue();
            if (nodeValue == null) {
                throw new IIOInvalidTreeException("Empty comment node!", node);
            }
            this.data = nodeValue.getBytes();
        }
    }
    
    String getComment() {
        try {
            return new String(this.data, "ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("com");
        iioMetadataNode.setAttribute("comment", this.getComment());
        if (this.data != null) {
            iioMetadataNode.setUserObject(this.data.clone());
        }
        return iioMetadataNode;
    }
    
    @Override
    void write(final ImageOutputStream imageOutputStream) throws IOException {
        this.length = 2 + this.data.length;
        this.writeTag(imageOutputStream);
        imageOutputStream.write(this.data);
    }
    
    @Override
    void print() {
        this.printTag("COM");
        System.out.println("<" + this.getComment() + ">");
    }
}
