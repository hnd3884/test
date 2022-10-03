package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadataNode;
import java.io.UnsupportedEncodingException;
import org.w3c.dom.Node;
import java.util.List;

public class GIFImageMetadata extends GIFMetadata
{
    static final String nativeMetadataFormatName = "javax_imageio_gif_image_1.0";
    static final String[] disposalMethodNames;
    public int imageLeftPosition;
    public int imageTopPosition;
    public int imageWidth;
    public int imageHeight;
    public boolean interlaceFlag;
    public boolean sortFlag;
    public byte[] localColorTable;
    public int disposalMethod;
    public boolean userInputFlag;
    public boolean transparentColorFlag;
    public int delayTime;
    public int transparentColorIndex;
    public boolean hasPlainTextExtension;
    public int textGridLeft;
    public int textGridTop;
    public int textGridWidth;
    public int textGridHeight;
    public int characterCellWidth;
    public int characterCellHeight;
    public int textForegroundColor;
    public int textBackgroundColor;
    public byte[] text;
    public List applicationIDs;
    public List authenticationCodes;
    public List applicationData;
    public List comments;
    
    protected GIFImageMetadata(final boolean b, final String s, final String s2, final String[] array, final String[] array2) {
        super(b, s, s2, array, array2);
        this.interlaceFlag = false;
        this.sortFlag = false;
        this.localColorTable = null;
        this.disposalMethod = 0;
        this.userInputFlag = false;
        this.transparentColorFlag = false;
        this.delayTime = 0;
        this.transparentColorIndex = 0;
        this.hasPlainTextExtension = false;
        this.applicationIDs = null;
        this.authenticationCodes = null;
        this.applicationData = null;
        this.comments = null;
    }
    
    public GIFImageMetadata() {
        this(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s.equals("javax_imageio_gif_image_1.0")) {
            return this.getNativeTree();
        }
        if (s.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        throw new IllegalArgumentException("Not a recognized format!");
    }
    
    private String toISO8859(final byte[] array) {
        try {
            return new String(array, "ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    private Node getNativeTree() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("javax_imageio_gif_image_1.0");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ImageDescriptor");
        iioMetadataNode2.setAttribute("imageLeftPosition", Integer.toString(this.imageLeftPosition));
        iioMetadataNode2.setAttribute("imageTopPosition", Integer.toString(this.imageTopPosition));
        iioMetadataNode2.setAttribute("imageWidth", Integer.toString(this.imageWidth));
        iioMetadataNode2.setAttribute("imageHeight", Integer.toString(this.imageHeight));
        iioMetadataNode2.setAttribute("interlaceFlag", this.interlaceFlag ? "TRUE" : "FALSE");
        iioMetadataNode.appendChild(iioMetadataNode2);
        if (this.localColorTable != null) {
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("LocalColorTable");
            final int n = this.localColorTable.length / 3;
            iioMetadataNode3.setAttribute("sizeOfLocalColorTable", Integer.toString(n));
            iioMetadataNode3.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
            for (int i = 0; i < n; ++i) {
                final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("ColorTableEntry");
                iioMetadataNode4.setAttribute("index", Integer.toString(i));
                final int n2 = this.localColorTable[3 * i] & 0xFF;
                final int n3 = this.localColorTable[3 * i + 1] & 0xFF;
                final int n4 = this.localColorTable[3 * i + 2] & 0xFF;
                iioMetadataNode4.setAttribute("red", Integer.toString(n2));
                iioMetadataNode4.setAttribute("green", Integer.toString(n3));
                iioMetadataNode4.setAttribute("blue", Integer.toString(n4));
                iioMetadataNode3.appendChild(iioMetadataNode4);
            }
            iioMetadataNode.appendChild(iioMetadataNode3);
        }
        final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("GraphicControlExtension");
        iioMetadataNode5.setAttribute("disposalMethod", GIFImageMetadata.disposalMethodNames[this.disposalMethod]);
        iioMetadataNode5.setAttribute("userInputFlag", this.userInputFlag ? "TRUE" : "FALSE");
        iioMetadataNode5.setAttribute("transparentColorFlag", this.transparentColorFlag ? "TRUE" : "FALSE");
        iioMetadataNode5.setAttribute("delayTime", Integer.toString(this.delayTime));
        iioMetadataNode5.setAttribute("transparentColorIndex", Integer.toString(this.transparentColorIndex));
        iioMetadataNode.appendChild(iioMetadataNode5);
        if (this.hasPlainTextExtension) {
            final IIOMetadataNode iioMetadataNode6 = new IIOMetadataNode("PlainTextExtension");
            iioMetadataNode6.setAttribute("textGridLeft", Integer.toString(this.textGridLeft));
            iioMetadataNode6.setAttribute("textGridTop", Integer.toString(this.textGridTop));
            iioMetadataNode6.setAttribute("textGridWidth", Integer.toString(this.textGridWidth));
            iioMetadataNode6.setAttribute("textGridHeight", Integer.toString(this.textGridHeight));
            iioMetadataNode6.setAttribute("characterCellWidth", Integer.toString(this.characterCellWidth));
            iioMetadataNode6.setAttribute("characterCellHeight", Integer.toString(this.characterCellHeight));
            iioMetadataNode6.setAttribute("textForegroundColor", Integer.toString(this.textForegroundColor));
            iioMetadataNode6.setAttribute("textBackgroundColor", Integer.toString(this.textBackgroundColor));
            iioMetadataNode6.setAttribute("text", this.toISO8859(this.text));
            iioMetadataNode.appendChild(iioMetadataNode6);
        }
        final int n5 = (this.applicationIDs == null) ? 0 : this.applicationIDs.size();
        if (n5 > 0) {
            final IIOMetadataNode iioMetadataNode7 = new IIOMetadataNode("ApplicationExtensions");
            for (int j = 0; j < n5; ++j) {
                final IIOMetadataNode iioMetadataNode8 = new IIOMetadataNode("ApplicationExtension");
                iioMetadataNode8.setAttribute("applicationID", this.toISO8859(this.applicationIDs.get(j)));
                iioMetadataNode8.setAttribute("authenticationCode", this.toISO8859(this.authenticationCodes.get(j)));
                iioMetadataNode8.setUserObject(this.applicationData.get(j).clone());
                iioMetadataNode7.appendChild(iioMetadataNode8);
            }
            iioMetadataNode.appendChild(iioMetadataNode7);
        }
        final int n6 = (this.comments == null) ? 0 : this.comments.size();
        if (n6 > 0) {
            final IIOMetadataNode iioMetadataNode9 = new IIOMetadataNode("CommentExtensions");
            for (int k = 0; k < n6; ++k) {
                final IIOMetadataNode iioMetadataNode10 = new IIOMetadataNode("CommentExtension");
                iioMetadataNode10.setAttribute("value", this.toISO8859(this.comments.get(k)));
                iioMetadataNode9.appendChild(iioMetadataNode10);
            }
            iioMetadataNode.appendChild(iioMetadataNode9);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardChromaNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iioMetadataNode2.setAttribute("name", "RGB");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("NumChannels");
        iioMetadataNode3.setAttribute("value", this.transparentColorFlag ? "4" : "3");
        iioMetadataNode.appendChild(iioMetadataNode3);
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("BlackIsZero");
        iioMetadataNode4.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode4);
        if (this.localColorTable != null) {
            final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("Palette");
            for (int n = this.localColorTable.length / 3, i = 0; i < n; ++i) {
                final IIOMetadataNode iioMetadataNode6 = new IIOMetadataNode("PaletteEntry");
                iioMetadataNode6.setAttribute("index", Integer.toString(i));
                iioMetadataNode6.setAttribute("red", Integer.toString(this.localColorTable[3 * i] & 0xFF));
                iioMetadataNode6.setAttribute("green", Integer.toString(this.localColorTable[3 * i + 1] & 0xFF));
                iioMetadataNode6.setAttribute("blue", Integer.toString(this.localColorTable[3 * i + 2] & 0xFF));
                iioMetadataNode5.appendChild(iioMetadataNode6);
            }
            iioMetadataNode.appendChild(iioMetadataNode5);
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
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("NumProgressiveScans");
        iioMetadataNode4.setAttribute("value", this.interlaceFlag ? "4" : "1");
        iioMetadataNode.appendChild(iioMetadataNode4);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDataNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Data");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("SampleFormat");
        iioMetadataNode2.setAttribute("value", "Index");
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDimensionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ImageOrientation");
        iioMetadataNode2.setAttribute("value", "Normal");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("HorizontalPixelOffset");
        iioMetadataNode3.setAttribute("value", Integer.toString(this.imageLeftPosition));
        iioMetadataNode.appendChild(iioMetadataNode3);
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("VerticalPixelOffset");
        iioMetadataNode4.setAttribute("value", Integer.toString(this.imageTopPosition));
        iioMetadataNode.appendChild(iioMetadataNode4);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardTextNode() {
        if (this.comments == null) {
            return null;
        }
        final Iterator iterator = this.comments.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Text");
        while (iterator.hasNext()) {
            final byte[] array = (byte[])iterator.next();
            String s;
            try {
                s = new String(array, "ISO-8859-1");
            }
            catch (final UnsupportedEncodingException ex) {
                throw new RuntimeException("Encoding ISO-8859-1 unknown!");
            }
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("TextEntry");
            iioMetadataNode2.setAttribute("value", s);
            iioMetadataNode2.setAttribute("encoding", "ISO-8859-1");
            iioMetadataNode2.setAttribute("compression", "none");
            iioMetadataNode.appendChild(iioMetadataNode2);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardTransparencyNode() {
        if (!this.transparentColorFlag) {
            return null;
        }
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Transparency");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("TransparentIndex");
        iioMetadataNode2.setAttribute("value", Integer.toString(this.transparentColorIndex));
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
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
        disposalMethodNames = new String[] { "none", "doNotDispose", "restoreToBackgroundColor", "restoreToPrevious", "undefinedDisposalMethod4", "undefinedDisposalMethod5", "undefinedDisposalMethod6", "undefinedDisposalMethod7" };
    }
}
