package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.metadata.IIOMetadataNode;
import java.io.UnsupportedEncodingException;
import com.sun.imageio.plugins.common.I18N;
import org.w3c.dom.Node;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class BMPMetadata extends IIOMetadata implements BMPConstants
{
    public static final String nativeMetadataFormatName = "javax_imageio_bmp_1.0";
    public String bmpVersion;
    public int width;
    public int height;
    public short bitsPerPixel;
    public int compression;
    public int imageSize;
    public int xPixelsPerMeter;
    public int yPixelsPerMeter;
    public int colorsUsed;
    public int colorsImportant;
    public int redMask;
    public int greenMask;
    public int blueMask;
    public int alphaMask;
    public int colorSpace;
    public double redX;
    public double redY;
    public double redZ;
    public double greenX;
    public double greenY;
    public double greenZ;
    public double blueX;
    public double blueY;
    public double blueZ;
    public int gammaRed;
    public int gammaGreen;
    public int gammaBlue;
    public int intent;
    public byte[] palette;
    public int paletteSize;
    public int red;
    public int green;
    public int blue;
    public List comments;
    
    public BMPMetadata() {
        super(true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
        this.palette = null;
        this.comments = null;
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s.equals("javax_imageio_bmp_1.0")) {
            return this.getNativeTree();
        }
        if (s.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
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
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("javax_imageio_bmp_1.0");
        this.addChildNode(iioMetadataNode, "BMPVersion", this.bmpVersion);
        this.addChildNode(iioMetadataNode, "Width", new Integer(this.width));
        this.addChildNode(iioMetadataNode, "Height", new Integer(this.height));
        this.addChildNode(iioMetadataNode, "BitsPerPixel", new Short(this.bitsPerPixel));
        this.addChildNode(iioMetadataNode, "Compression", new Integer(this.compression));
        this.addChildNode(iioMetadataNode, "ImageSize", new Integer(this.imageSize));
        final IIOMetadataNode addChildNode = this.addChildNode(iioMetadataNode, "PixelsPerMeter", null);
        this.addChildNode(addChildNode, "X", new Integer(this.xPixelsPerMeter));
        this.addChildNode(addChildNode, "Y", new Integer(this.yPixelsPerMeter));
        this.addChildNode(iioMetadataNode, "ColorsUsed", new Integer(this.colorsUsed));
        this.addChildNode(iioMetadataNode, "ColorsImportant", new Integer(this.colorsImportant));
        int n = 0;
        for (int i = 0; i < this.bmpVersion.length(); ++i) {
            if (Character.isDigit(this.bmpVersion.charAt(i))) {
                n = this.bmpVersion.charAt(i) - '0';
            }
        }
        if (n >= 4) {
            final IIOMetadataNode addChildNode2 = this.addChildNode(iioMetadataNode, "Mask", null);
            this.addChildNode(addChildNode2, "Red", new Integer(this.redMask));
            this.addChildNode(addChildNode2, "Green", new Integer(this.greenMask));
            this.addChildNode(addChildNode2, "Blue", new Integer(this.blueMask));
            this.addChildNode(addChildNode2, "Alpha", new Integer(this.alphaMask));
            this.addChildNode(iioMetadataNode, "ColorSpaceType", new Integer(this.colorSpace));
            final IIOMetadataNode addChildNode3 = this.addChildNode(iioMetadataNode, "CIEXYZEndPoints", null);
            this.addXYZPoints(addChildNode3, "Red", this.redX, this.redY, this.redZ);
            this.addXYZPoints(addChildNode3, "Green", this.greenX, this.greenY, this.greenZ);
            this.addXYZPoints(addChildNode3, "Blue", this.blueX, this.blueY, this.blueZ);
            this.addChildNode(iioMetadataNode, "Intent", new Integer(this.intent));
        }
        if (this.palette != null && this.paletteSize > 0) {
            final IIOMetadataNode addChildNode4 = this.addChildNode(iioMetadataNode, "Palette", null);
            final int n2 = this.palette.length / this.paletteSize;
            int j = 0;
            int n3 = 0;
            while (j < this.paletteSize) {
                final IIOMetadataNode addChildNode5 = this.addChildNode(addChildNode4, "PaletteEntry", null);
                this.red = (this.palette[n3++] & 0xFF);
                this.green = (this.palette[n3++] & 0xFF);
                this.blue = (this.palette[n3++] & 0xFF);
                this.addChildNode(addChildNode5, "Red", new Byte((byte)this.red));
                this.addChildNode(addChildNode5, "Green", new Byte((byte)this.green));
                this.addChildNode(addChildNode5, "Blue", new Byte((byte)this.blue));
                if (n2 == 4) {
                    this.addChildNode(addChildNode5, "Alpha", new Byte((byte)(this.palette[n3++] & 0xFF)));
                }
                ++j;
            }
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardChromaNode() {
        if (this.palette != null && this.paletteSize > 0) {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("Palette");
            final int n = this.palette.length / this.paletteSize;
            iioMetadataNode2.setAttribute("value", "" + n);
            int i = 0;
            int n2 = 0;
            while (i < this.paletteSize) {
                final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("PaletteEntry");
                iioMetadataNode3.setAttribute("index", "" + i);
                iioMetadataNode3.setAttribute("red", "" + this.palette[n2++]);
                iioMetadataNode3.setAttribute("green", "" + this.palette[n2++]);
                iioMetadataNode3.setAttribute("blue", "" + this.palette[n2++]);
                if (n == 4 && this.palette[n2] != 0) {
                    iioMetadataNode3.setAttribute("alpha", "" + this.palette[n2++]);
                }
                iioMetadataNode2.appendChild(iioMetadataNode3);
                ++i;
            }
            iioMetadataNode.appendChild(iioMetadataNode2);
            return iioMetadataNode;
        }
        return null;
    }
    
    @Override
    protected IIOMetadataNode getStandardCompressionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Compression");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iioMetadataNode2.setAttribute("value", BMPCompressionTypes.getName(this.compression));
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardDataNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Data");
        String string = "";
        if (this.bitsPerPixel == 24) {
            string = "8 8 8 ";
        }
        else if (this.bitsPerPixel == 16 || this.bitsPerPixel == 32) {
            string = "" + this.countBits(this.redMask) + " " + this.countBits(this.greenMask) + this.countBits(this.blueMask) + "" + this.countBits(this.alphaMask);
        }
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("BitsPerSample");
        iioMetadataNode2.setAttribute("value", string);
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardDimensionNode() {
        if (this.yPixelsPerMeter > 0.0f && this.xPixelsPerMeter > 0.0f) {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
            final float n = (float)(this.yPixelsPerMeter / this.xPixelsPerMeter);
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
            iioMetadataNode2.setAttribute("value", "" + n);
            iioMetadataNode.appendChild(iioMetadataNode2);
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
            iioMetadataNode3.setAttribute("value", "" + 1 / this.xPixelsPerMeter * 1000);
            iioMetadataNode.appendChild(iioMetadataNode3);
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
            iioMetadataNode4.setAttribute("value", "" + 1 / this.yPixelsPerMeter * 1000);
            iioMetadataNode.appendChild(iioMetadataNode4);
            return iioMetadataNode;
        }
        return null;
    }
    
    @Override
    public void setFromTree(final String s, final Node node) {
        throw new IllegalStateException(I18N.getString("BMPMetadata1"));
    }
    
    @Override
    public void mergeTree(final String s, final Node node) {
        throw new IllegalStateException(I18N.getString("BMPMetadata1"));
    }
    
    @Override
    public void reset() {
        throw new IllegalStateException(I18N.getString("BMPMetadata1"));
    }
    
    private String countBits(int i) {
        int n = 0;
        while (i > 0) {
            if ((i & 0x1) == 0x1) {
                ++n;
            }
            i >>>= 1;
        }
        return (n == 0) ? "" : ("" + n);
    }
    
    private void addXYZPoints(final IIOMetadataNode iioMetadataNode, final String s, final double n, final double n2, final double n3) {
        final IIOMetadataNode addChildNode = this.addChildNode(iioMetadataNode, s, null);
        this.addChildNode(addChildNode, "X", new Double(n));
        this.addChildNode(addChildNode, "Y", new Double(n2));
        this.addChildNode(addChildNode, "Z", new Double(n3));
    }
    
    private IIOMetadataNode addChildNode(final IIOMetadataNode iioMetadataNode, final String s, final Object userObject) {
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode(s);
        if (userObject != null) {
            iioMetadataNode2.setUserObject(userObject);
            iioMetadataNode2.setNodeValue(ImageUtil.convertObjectToString(userObject));
        }
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode2;
    }
}
