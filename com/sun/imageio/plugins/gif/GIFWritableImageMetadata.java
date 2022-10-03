package com.sun.imageio.plugins.gif;

import java.nio.charset.Charset;
import javax.imageio.metadata.IIOInvalidTreeException;
import java.util.ArrayList;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import java.io.UnsupportedEncodingException;

class GIFWritableImageMetadata extends GIFImageMetadata
{
    static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";
    
    GIFWritableImageMetadata() {
        super(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public void reset() {
        this.imageLeftPosition = 0;
        this.imageTopPosition = 0;
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.interlaceFlag = false;
        this.sortFlag = false;
        this.localColorTable = null;
        this.disposalMethod = 0;
        this.userInputFlag = false;
        this.transparentColorFlag = false;
        this.delayTime = 0;
        this.transparentColorIndex = 0;
        this.hasPlainTextExtension = false;
        this.textGridLeft = 0;
        this.textGridTop = 0;
        this.textGridWidth = 0;
        this.textGridHeight = 0;
        this.characterCellWidth = 0;
        this.characterCellHeight = 0;
        this.textForegroundColor = 0;
        this.textBackgroundColor = 0;
        this.text = null;
        this.applicationIDs = null;
        this.authenticationCodes = null;
        this.applicationData = null;
        this.comments = null;
    }
    
    private byte[] fromISO8859(final String s) {
        try {
            return s.getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            return "".getBytes();
        }
    }
    
    @Override
    protected void mergeNativeTree(final Node node) throws IIOInvalidTreeException {
        if (!node.getNodeName().equals("javax_imageio_gif_image_1.0")) {
            GIFMetadata.fatal(node, "Root must be javax_imageio_gif_image_1.0");
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final String nodeName = node2.getNodeName();
            if (nodeName.equals("ImageDescriptor")) {
                this.imageLeftPosition = GIFMetadata.getIntAttribute(node2, "imageLeftPosition", -1, true, true, 0, 65535);
                this.imageTopPosition = GIFMetadata.getIntAttribute(node2, "imageTopPosition", -1, true, true, 0, 65535);
                this.imageWidth = GIFMetadata.getIntAttribute(node2, "imageWidth", -1, true, true, 1, 65535);
                this.imageHeight = GIFMetadata.getIntAttribute(node2, "imageHeight", -1, true, true, 1, 65535);
                this.interlaceFlag = GIFMetadata.getBooleanAttribute(node2, "interlaceFlag", false, true);
            }
            else if (nodeName.equals("LocalColorTable")) {
                final int intAttribute = GIFMetadata.getIntAttribute(node2, "sizeOfLocalColorTable", true, 2, 256);
                if (intAttribute != 2 && intAttribute != 4 && intAttribute != 8 && intAttribute != 16 && intAttribute != 32 && intAttribute != 64 && intAttribute != 128 && intAttribute != 256) {
                    GIFMetadata.fatal(node2, "Bad value for LocalColorTable attribute sizeOfLocalColorTable!");
                }
                this.sortFlag = GIFMetadata.getBooleanAttribute(node2, "sortFlag", false, true);
                this.localColorTable = this.getColorTable(node2, "ColorTableEntry", true, intAttribute);
            }
            else if (nodeName.equals("GraphicControlExtension")) {
                final String stringAttribute = GIFMetadata.getStringAttribute(node2, "disposalMethod", null, true, GIFWritableImageMetadata.disposalMethodNames);
                this.disposalMethod = 0;
                while (!stringAttribute.equals(GIFWritableImageMetadata.disposalMethodNames[this.disposalMethod])) {
                    ++this.disposalMethod;
                }
                this.userInputFlag = GIFMetadata.getBooleanAttribute(node2, "userInputFlag", false, true);
                this.transparentColorFlag = GIFMetadata.getBooleanAttribute(node2, "transparentColorFlag", false, true);
                this.delayTime = GIFMetadata.getIntAttribute(node2, "delayTime", -1, true, true, 0, 65535);
                this.transparentColorIndex = GIFMetadata.getIntAttribute(node2, "transparentColorIndex", -1, true, true, 0, 65535);
            }
            else if (nodeName.equals("PlainTextExtension")) {
                this.hasPlainTextExtension = true;
                this.textGridLeft = GIFMetadata.getIntAttribute(node2, "textGridLeft", -1, true, true, 0, 65535);
                this.textGridTop = GIFMetadata.getIntAttribute(node2, "textGridTop", -1, true, true, 0, 65535);
                this.textGridWidth = GIFMetadata.getIntAttribute(node2, "textGridWidth", -1, true, true, 1, 65535);
                this.textGridHeight = GIFMetadata.getIntAttribute(node2, "textGridHeight", -1, true, true, 1, 65535);
                this.characterCellWidth = GIFMetadata.getIntAttribute(node2, "characterCellWidth", -1, true, true, 1, 65535);
                this.characterCellHeight = GIFMetadata.getIntAttribute(node2, "characterCellHeight", -1, true, true, 1, 65535);
                this.textForegroundColor = GIFMetadata.getIntAttribute(node2, "textForegroundColor", -1, true, true, 0, 255);
                this.textBackgroundColor = GIFMetadata.getIntAttribute(node2, "textBackgroundColor", -1, true, true, 0, 255);
                this.text = this.fromISO8859(GIFMetadata.getStringAttribute(node2, "text", "", false, null));
            }
            else if (nodeName.equals("ApplicationExtensions")) {
                final IIOMetadataNode iioMetadataNode = (IIOMetadataNode)node2.getFirstChild();
                if (!iioMetadataNode.getNodeName().equals("ApplicationExtension")) {
                    GIFMetadata.fatal(node2, "Only a ApplicationExtension may be a child of a ApplicationExtensions!");
                }
                final String stringAttribute2 = GIFMetadata.getStringAttribute(iioMetadataNode, "applicationID", null, true, null);
                final String stringAttribute3 = GIFMetadata.getStringAttribute(iioMetadataNode, "authenticationCode", null, true, null);
                final Object userObject = iioMetadataNode.getUserObject();
                if (userObject == null || !(userObject instanceof byte[])) {
                    GIFMetadata.fatal(iioMetadataNode, "Bad user object in ApplicationExtension!");
                }
                if (this.applicationIDs == null) {
                    this.applicationIDs = new ArrayList();
                    this.authenticationCodes = new ArrayList();
                    this.applicationData = new ArrayList();
                }
                this.applicationIDs.add(this.fromISO8859(stringAttribute2));
                this.authenticationCodes.add(this.fromISO8859(stringAttribute3));
                this.applicationData.add(userObject);
            }
            else if (nodeName.equals("CommentExtensions")) {
                Node node3 = node2.getFirstChild();
                if (node3 != null) {
                    while (node3 != null) {
                        if (!node3.getNodeName().equals("CommentExtension")) {
                            GIFMetadata.fatal(node2, "Only a CommentExtension may be a child of a CommentExtensions!");
                        }
                        if (this.comments == null) {
                            this.comments = new ArrayList();
                        }
                        this.comments.add(this.fromISO8859(GIFMetadata.getStringAttribute(node3, "value", null, true, null)));
                        node3 = node3.getNextSibling();
                    }
                }
            }
            else {
                GIFMetadata.fatal(node2, "Unknown child of root node!");
            }
        }
    }
    
    @Override
    protected void mergeStandardTree(final Node node) throws IIOInvalidTreeException {
        if (!node.getNodeName().equals("javax_imageio_1.0")) {
            GIFMetadata.fatal(node, "Root must be javax_imageio_1.0");
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final String nodeName = node2.getNodeName();
            if (nodeName.equals("Chroma")) {
                for (Node node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (node3.getNodeName().equals("Palette")) {
                        this.localColorTable = this.getColorTable(node3, "PaletteEntry", false, -1);
                        break;
                    }
                }
            }
            else if (nodeName.equals("Compression")) {
                Node node4 = node2.getFirstChild();
                while (node4 != null) {
                    if (node4.getNodeName().equals("NumProgressiveScans")) {
                        if (GIFMetadata.getIntAttribute(node4, "value", 4, false, true, 1, Integer.MAX_VALUE) > 1) {
                            this.interlaceFlag = true;
                            break;
                        }
                        break;
                    }
                    else {
                        node4 = node4.getNextSibling();
                    }
                }
            }
            else if (nodeName.equals("Dimension")) {
                for (Node node5 = node2.getFirstChild(); node5 != null; node5 = node5.getNextSibling()) {
                    final String nodeName2 = node5.getNodeName();
                    if (nodeName2.equals("HorizontalPixelOffset")) {
                        this.imageLeftPosition = GIFMetadata.getIntAttribute(node5, "value", -1, true, true, 0, 65535);
                    }
                    else if (nodeName2.equals("VerticalPixelOffset")) {
                        this.imageTopPosition = GIFMetadata.getIntAttribute(node5, "value", -1, true, true, 0, 65535);
                    }
                }
            }
            else if (nodeName.equals("Text")) {
                for (Node node6 = node2.getFirstChild(); node6 != null; node6 = node6.getNextSibling()) {
                    if (node6.getNodeName().equals("TextEntry") && GIFMetadata.getAttribute(node6, "compression", "none", false).equals("none") && Charset.isSupported(GIFMetadata.getAttribute(node6, "encoding", "ISO-8859-1", false))) {
                        final byte[] fromISO8859 = this.fromISO8859(GIFMetadata.getAttribute(node6, "value"));
                        if (this.comments == null) {
                            this.comments = new ArrayList();
                        }
                        this.comments.add(fromISO8859);
                    }
                }
            }
            else if (nodeName.equals("Transparency")) {
                for (Node node7 = node2.getFirstChild(); node7 != null; node7 = node7.getNextSibling()) {
                    if (node7.getNodeName().equals("TransparentIndex")) {
                        this.transparentColorIndex = GIFMetadata.getIntAttribute(node7, "value", -1, true, true, 0, 255);
                        this.transparentColorFlag = true;
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void setFromTree(final String s, final Node node) throws IIOInvalidTreeException {
        this.reset();
        this.mergeTree(s, node);
    }
}
