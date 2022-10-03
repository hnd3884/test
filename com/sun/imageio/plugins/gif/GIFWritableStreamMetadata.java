package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

class GIFWritableStreamMetadata extends GIFStreamMetadata
{
    static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";
    
    public GIFWritableStreamMetadata() {
        super(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null);
        this.reset();
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public void mergeTree(final String s, final Node node) throws IIOInvalidTreeException {
        if (s.equals("javax_imageio_gif_stream_1.0")) {
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
    
    @Override
    public void reset() {
        this.version = null;
        this.logicalScreenWidth = -1;
        this.logicalScreenHeight = -1;
        this.colorResolution = -1;
        this.pixelAspectRatio = 0;
        this.backgroundColorIndex = 0;
        this.sortFlag = false;
        this.globalColorTable = null;
    }
    
    @Override
    protected void mergeNativeTree(final Node node) throws IIOInvalidTreeException {
        if (!node.getNodeName().equals("javax_imageio_gif_stream_1.0")) {
            GIFMetadata.fatal(node, "Root must be javax_imageio_gif_stream_1.0");
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final String nodeName = node2.getNodeName();
            if (nodeName.equals("Version")) {
                this.version = GIFMetadata.getStringAttribute(node2, "value", null, true, GIFWritableStreamMetadata.versionStrings);
            }
            else if (nodeName.equals("LogicalScreenDescriptor")) {
                this.logicalScreenWidth = GIFMetadata.getIntAttribute(node2, "logicalScreenWidth", -1, true, true, 1, 65535);
                this.logicalScreenHeight = GIFMetadata.getIntAttribute(node2, "logicalScreenHeight", -1, true, true, 1, 65535);
                this.colorResolution = GIFMetadata.getIntAttribute(node2, "colorResolution", -1, true, true, 1, 8);
                this.pixelAspectRatio = GIFMetadata.getIntAttribute(node2, "pixelAspectRatio", 0, true, true, 0, 255);
            }
            else if (nodeName.equals("GlobalColorTable")) {
                final int intAttribute = GIFMetadata.getIntAttribute(node2, "sizeOfGlobalColorTable", true, 2, 256);
                if (intAttribute != 2 && intAttribute != 4 && intAttribute != 8 && intAttribute != 16 && intAttribute != 32 && intAttribute != 64 && intAttribute != 128 && intAttribute != 256) {
                    GIFMetadata.fatal(node2, "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!");
                }
                this.backgroundColorIndex = GIFMetadata.getIntAttribute(node2, "backgroundColorIndex", 0, true, true, 0, 255);
                this.sortFlag = GIFMetadata.getBooleanAttribute(node2, "sortFlag", false, true);
                this.globalColorTable = this.getColorTable(node2, "ColorTableEntry", true, intAttribute);
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
                    final String nodeName2 = node3.getNodeName();
                    if (nodeName2.equals("Palette")) {
                        this.globalColorTable = this.getColorTable(node3, "PaletteEntry", false, -1);
                    }
                    else if (nodeName2.equals("BackgroundIndex")) {
                        this.backgroundColorIndex = GIFMetadata.getIntAttribute(node3, "value", -1, true, true, 0, 255);
                    }
                }
            }
            else if (nodeName.equals("Data")) {
                for (Node node4 = node2.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                    if (node4.getNodeName().equals("BitsPerSample")) {
                        this.colorResolution = GIFMetadata.getIntAttribute(node4, "value", -1, true, true, 1, 8);
                        break;
                    }
                }
            }
            else if (nodeName.equals("Dimension")) {
                for (Node node5 = node2.getFirstChild(); node5 != null; node5 = node5.getNextSibling()) {
                    final String nodeName3 = node5.getNodeName();
                    if (nodeName3.equals("PixelAspectRatio")) {
                        final float floatAttribute = GIFMetadata.getFloatAttribute(node5, "value");
                        if (floatAttribute == 1.0f) {
                            this.pixelAspectRatio = 0;
                        }
                        else {
                            this.pixelAspectRatio = Math.max(Math.min((int)(floatAttribute * 64.0f - 15.0f), 255), 0);
                        }
                    }
                    else if (nodeName3.equals("HorizontalScreenSize")) {
                        this.logicalScreenWidth = GIFMetadata.getIntAttribute(node5, "value", -1, true, true, 1, 65535);
                    }
                    else if (nodeName3.equals("VerticalScreenSize")) {
                        this.logicalScreenHeight = GIFMetadata.getIntAttribute(node5, "value", -1, true, true, 1, 65535);
                    }
                }
            }
            else if (nodeName.equals("Document")) {
                for (Node node6 = node2.getFirstChild(); node6 != null; node6 = node6.getNextSibling()) {
                    if (node6.getNodeName().equals("FormatVersion")) {
                        final String stringAttribute = GIFMetadata.getStringAttribute(node6, "value", null, true, null);
                        for (int i = 0; i < GIFWritableStreamMetadata.versionStrings.length; ++i) {
                            if (stringAttribute.equals(GIFWritableStreamMetadata.versionStrings[i])) {
                                this.version = stringAttribute;
                                break;
                            }
                        }
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
