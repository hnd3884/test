package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFStreamMetadata extends GIFMetadata
{
    static final String nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";
    static final String[] versionStrings;
    public String version;
    public int logicalScreenWidth;
    public int logicalScreenHeight;
    public int colorResolution;
    public int pixelAspectRatio;
    public int backgroundColorIndex;
    public boolean sortFlag;
    static final String[] colorTableSizes;
    public byte[] globalColorTable;
    
    protected GIFStreamMetadata(final boolean b, final String s, final String s2, final String[] array, final String[] array2) {
        super(b, s, s2, array, array2);
        this.globalColorTable = null;
    }
    
    public GIFStreamMetadata() {
        this(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null);
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s.equals("javax_imageio_gif_stream_1.0")) {
            return this.getNativeTree();
        }
        if (s.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        throw new IllegalArgumentException("Not a recognized format!");
    }
    
    private Node getNativeTree() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("javax_imageio_gif_stream_1.0");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("Version");
        iioMetadataNode2.setAttribute("value", this.version);
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("LogicalScreenDescriptor");
        iioMetadataNode3.setAttribute("logicalScreenWidth", (this.logicalScreenWidth == -1) ? "" : Integer.toString(this.logicalScreenWidth));
        iioMetadataNode3.setAttribute("logicalScreenHeight", (this.logicalScreenHeight == -1) ? "" : Integer.toString(this.logicalScreenHeight));
        iioMetadataNode3.setAttribute("colorResolution", (this.colorResolution == -1) ? "" : Integer.toString(this.colorResolution));
        iioMetadataNode3.setAttribute("pixelAspectRatio", Integer.toString(this.pixelAspectRatio));
        iioMetadataNode.appendChild(iioMetadataNode3);
        if (this.globalColorTable != null) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("GlobalColorTable");
            final int n = this.globalColorTable.length / 3;
            iioMetadataNode4.setAttribute("sizeOfGlobalColorTable", Integer.toString(n));
            iioMetadataNode4.setAttribute("backgroundColorIndex", Integer.toString(this.backgroundColorIndex));
            iioMetadataNode4.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
            for (int i = 0; i < n; ++i) {
                final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("ColorTableEntry");
                iioMetadataNode5.setAttribute("index", Integer.toString(i));
                final int n2 = this.globalColorTable[3 * i] & 0xFF;
                final int n3 = this.globalColorTable[3 * i + 1] & 0xFF;
                final int n4 = this.globalColorTable[3 * i + 2] & 0xFF;
                iioMetadataNode5.setAttribute("red", Integer.toString(n2));
                iioMetadataNode5.setAttribute("green", Integer.toString(n3));
                iioMetadataNode5.setAttribute("blue", Integer.toString(n4));
                iioMetadataNode4.appendChild(iioMetadataNode5);
            }
            iioMetadataNode.appendChild(iioMetadataNode4);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardChromaNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iioMetadataNode2.setAttribute("name", "RGB");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("BlackIsZero");
        iioMetadataNode3.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode3);
        if (this.globalColorTable != null) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("Palette");
            for (int n = this.globalColorTable.length / 3, i = 0; i < n; ++i) {
                final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("PaletteEntry");
                iioMetadataNode5.setAttribute("index", Integer.toString(i));
                iioMetadataNode5.setAttribute("red", Integer.toString(this.globalColorTable[3 * i] & 0xFF));
                iioMetadataNode5.setAttribute("green", Integer.toString(this.globalColorTable[3 * i + 1] & 0xFF));
                iioMetadataNode5.setAttribute("blue", Integer.toString(this.globalColorTable[3 * i + 2] & 0xFF));
                iioMetadataNode4.appendChild(iioMetadataNode5);
            }
            iioMetadataNode.appendChild(iioMetadataNode4);
            final IIOMetadataNode iioMetadataNode6 = new IIOMetadataNode("BackgroundIndex");
            iioMetadataNode6.setAttribute("value", Integer.toString(this.backgroundColorIndex));
            iioMetadataNode.appendChild(iioMetadataNode6);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardCompressionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Compression");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iioMetadataNode2.setAttribute("value", "lzw");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("Lossless");
        iioMetadataNode3.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode3);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDataNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Data");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("SampleFormat");
        iioMetadataNode2.setAttribute("value", "Index");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("BitsPerSample");
        iioMetadataNode3.setAttribute("value", (this.colorResolution == -1) ? "" : Integer.toString(this.colorResolution));
        iioMetadataNode.appendChild(iioMetadataNode3);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDimensionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
        float n = 1.0f;
        if (this.pixelAspectRatio != 0) {
            n = (this.pixelAspectRatio + 15) / 64.0f;
        }
        iioMetadataNode2.setAttribute("value", Float.toString(n));
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("ImageOrientation");
        iioMetadataNode3.setAttribute("value", "Normal");
        iioMetadataNode.appendChild(iioMetadataNode3);
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("HorizontalScreenSize");
        iioMetadataNode4.setAttribute("value", (this.logicalScreenWidth == -1) ? "" : Integer.toString(this.logicalScreenWidth));
        iioMetadataNode.appendChild(iioMetadataNode4);
        final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("VerticalScreenSize");
        iioMetadataNode5.setAttribute("value", (this.logicalScreenHeight == -1) ? "" : Integer.toString(this.logicalScreenHeight));
        iioMetadataNode.appendChild(iioMetadataNode5);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDocumentNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Document");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("FormatVersion");
        iioMetadataNode2.setAttribute("value", this.version);
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardTextNode() {
        return null;
    }
    
    public IIOMetadataNode getStandardTransparencyNode() {
        return null;
    }
    
    @Override
    public void setFromTree(final String s, final Node node) throws IIOInvalidTreeException {
        throw new IllegalStateException("Metadata is read-only!");
    }
    
    @Override
    protected void mergeNativeTree(final Node node) throws IIOInvalidTreeException {
        throw new IllegalStateException("Metadata is read-only!");
    }
    
    @Override
    protected void mergeStandardTree(final Node node) throws IIOInvalidTreeException {
        throw new IllegalStateException("Metadata is read-only!");
    }
    
    @Override
    public void reset() {
        throw new IllegalStateException("Metadata is read-only!");
    }
    
    static {
        versionStrings = new String[] { "87a", "89a" };
        colorTableSizes = new String[] { "2", "4", "8", "16", "32", "64", "128", "256" };
    }
}
