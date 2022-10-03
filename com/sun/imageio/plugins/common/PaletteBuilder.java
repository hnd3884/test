package com.sun.imageio.plugins.common;

import java.awt.Color;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageTypeSpecifier;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;

public class PaletteBuilder
{
    protected static final int MAXLEVEL = 8;
    protected RenderedImage src;
    protected ColorModel srcColorModel;
    protected Raster srcRaster;
    protected int requiredSize;
    protected ColorNode root;
    protected int numNodes;
    protected int maxNodes;
    protected int currLevel;
    protected int currSize;
    protected ColorNode[] reduceList;
    protected ColorNode[] palette;
    protected int transparency;
    protected ColorNode transColor;
    
    public static RenderedImage createIndexedImage(final RenderedImage renderedImage) {
        final PaletteBuilder paletteBuilder = new PaletteBuilder(renderedImage);
        paletteBuilder.buildPalette();
        return paletteBuilder.getIndexedImage();
    }
    
    public static IndexColorModel createIndexColorModel(final RenderedImage renderedImage) {
        final PaletteBuilder paletteBuilder = new PaletteBuilder(renderedImage);
        paletteBuilder.buildPalette();
        return paletteBuilder.getIndexColorModel();
    }
    
    public static boolean canCreatePalette(final ImageTypeSpecifier imageTypeSpecifier) {
        if (imageTypeSpecifier == null) {
            throw new IllegalArgumentException("type == null");
        }
        return true;
    }
    
    public static boolean canCreatePalette(final RenderedImage renderedImage) {
        if (renderedImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        return canCreatePalette(new ImageTypeSpecifier(renderedImage));
    }
    
    protected RenderedImage getIndexedImage() {
        final BufferedImage bufferedImage = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, this.getIndexColorModel());
        final WritableRaster raster = bufferedImage.getRaster();
        for (int i = 0; i < bufferedImage.getHeight(); ++i) {
            for (int j = 0; j < bufferedImage.getWidth(); ++j) {
                raster.setSample(j, i, 0, this.findColorIndex(this.root, this.getSrcColor(j, i)));
            }
        }
        return bufferedImage;
    }
    
    protected PaletteBuilder(final RenderedImage renderedImage) {
        this(renderedImage, 256);
    }
    
    protected PaletteBuilder(final RenderedImage src, final int requiredSize) {
        this.src = src;
        this.srcColorModel = src.getColorModel();
        this.srcRaster = src.getData();
        this.transparency = this.srcColorModel.getTransparency();
        this.requiredSize = requiredSize;
    }
    
    private Color getSrcColor(final int n, final int n2) {
        return new Color(this.srcColorModel.getRGB(this.srcRaster.getDataElements(n, n2, null)), this.transparency != 1);
    }
    
    protected int findColorIndex(final ColorNode colorNode, final Color color) {
        if (this.transparency != 1 && color.getAlpha() != 255) {
            return 0;
        }
        if (colorNode.isLeaf) {
            return colorNode.paletteIndex;
        }
        return this.findColorIndex(colorNode.children[this.getBranchIndex(color, colorNode.level)], color);
    }
    
    protected void buildPalette() {
        this.reduceList = new ColorNode[9];
        for (int i = 0; i < this.reduceList.length; ++i) {
            this.reduceList[i] = null;
        }
        this.numNodes = 0;
        this.maxNodes = 0;
        this.root = null;
        this.currSize = 0;
        this.currLevel = 8;
        final int width = this.src.getWidth();
        for (int height = this.src.getHeight(), j = 0; j < height; ++j) {
            for (int k = 0; k < width; ++k) {
                final Color srcColor = this.getSrcColor(width - k - 1, height - j - 1);
                if (this.transparency != 1 && srcColor.getAlpha() != 255) {
                    if (this.transColor == null) {
                        --this.requiredSize;
                        this.transColor = new ColorNode();
                        this.transColor.isLeaf = true;
                    }
                    this.transColor = this.insertNode(this.transColor, srcColor, 0);
                }
                else {
                    this.root = this.insertNode(this.root, srcColor, 0);
                }
                if (this.currSize > this.requiredSize) {
                    this.reduceTree();
                }
            }
        }
    }
    
    protected ColorNode insertNode(ColorNode colorNode, final Color color, final int level) {
        if (colorNode == null) {
            colorNode = new ColorNode();
            ++this.numNodes;
            if (this.numNodes > this.maxNodes) {
                this.maxNodes = this.numNodes;
            }
            colorNode.level = level;
            colorNode.isLeaf = (level > 8);
            if (colorNode.isLeaf) {
                ++this.currSize;
            }
        }
        final ColorNode colorNode2 = colorNode;
        ++colorNode2.colorCount;
        final ColorNode colorNode3 = colorNode;
        colorNode3.red += color.getRed();
        final ColorNode colorNode4 = colorNode;
        colorNode4.green += color.getGreen();
        final ColorNode colorNode5 = colorNode;
        colorNode5.blue += color.getBlue();
        if (!colorNode.isLeaf) {
            final int branchIndex = this.getBranchIndex(color, level);
            if (colorNode.children[branchIndex] == null) {
                final ColorNode colorNode6 = colorNode;
                ++colorNode6.childCount;
                if (colorNode.childCount == 2) {
                    colorNode.nextReducible = this.reduceList[level];
                    this.reduceList[level] = colorNode;
                }
            }
            colorNode.children[branchIndex] = this.insertNode(colorNode.children[branchIndex], color, level + 1);
        }
        return colorNode;
    }
    
    protected IndexColorModel getIndexColorModel() {
        int currSize = this.currSize;
        if (this.transColor != null) {
            ++currSize;
        }
        final byte[] array = new byte[currSize];
        final byte[] array2 = new byte[currSize];
        final byte[] array3 = new byte[currSize];
        int n = 0;
        this.palette = new ColorNode[currSize];
        if (this.transColor != null) {
            ++n;
        }
        if (this.root != null) {
            this.findPaletteEntry(this.root, n, array, array2, array3);
        }
        IndexColorModel indexColorModel;
        if (this.transColor != null) {
            indexColorModel = new IndexColorModel(8, currSize, array, array2, array3, 0);
        }
        else {
            indexColorModel = new IndexColorModel(8, this.currSize, array, array2, array3);
        }
        return indexColorModel;
    }
    
    protected int findPaletteEntry(final ColorNode colorNode, int paletteEntry, final byte[] array, final byte[] array2, final byte[] array3) {
        if (colorNode.isLeaf) {
            array[paletteEntry] = (byte)(colorNode.red / colorNode.colorCount);
            array2[paletteEntry] = (byte)(colorNode.green / colorNode.colorCount);
            array3[paletteEntry] = (byte)(colorNode.blue / colorNode.colorCount);
            colorNode.paletteIndex = paletteEntry;
            this.palette[paletteEntry] = colorNode;
            ++paletteEntry;
        }
        else {
            for (int i = 0; i < 8; ++i) {
                if (colorNode.children[i] != null) {
                    paletteEntry = this.findPaletteEntry(colorNode.children[i], paletteEntry, array, array2, array3);
                }
            }
        }
        return paletteEntry;
    }
    
    protected int getBranchIndex(final Color color, final int n) {
        if (n > 8 || n < 0) {
            throw new IllegalArgumentException("Invalid octree node depth: " + n);
        }
        final int n2 = 8 - n;
        return (0x1 & (0xFF & color.getRed()) >> n2) << 2 | (0x1 & (0xFF & color.getGreen()) >> n2) << 1 | (0x1 & (0xFF & color.getBlue()) >> n2);
    }
    
    protected void reduceTree() {
        int n;
        for (n = this.reduceList.length - 1; this.reduceList[n] == null && n >= 0; --n) {}
        ColorNode colorNode = this.reduceList[n];
        if (colorNode == null) {
            return;
        }
        ColorNode nextReducible = colorNode;
        int n2 = nextReducible.colorCount;
        for (int n3 = 1; nextReducible.nextReducible != null; nextReducible = nextReducible.nextReducible, ++n3) {
            if (n2 > nextReducible.nextReducible.colorCount) {
                colorNode = nextReducible;
                n2 = nextReducible.colorCount;
            }
        }
        if (colorNode == this.reduceList[n]) {
            this.reduceList[n] = colorNode.nextReducible;
        }
        else {
            final ColorNode nextReducible2 = colorNode.nextReducible;
            colorNode.nextReducible = nextReducible2.nextReducible;
            colorNode = nextReducible2;
        }
        if (colorNode.isLeaf) {
            return;
        }
        final int leafChildCount = colorNode.getLeafChildCount();
        colorNode.isLeaf = true;
        this.currSize -= leafChildCount - 1;
        final int level = colorNode.level;
        for (int i = 0; i < 8; ++i) {
            colorNode.children[i] = this.freeTree(colorNode.children[i]);
        }
        colorNode.childCount = 0;
    }
    
    protected ColorNode freeTree(final ColorNode colorNode) {
        if (colorNode == null) {
            return null;
        }
        for (int i = 0; i < 8; ++i) {
            colorNode.children[i] = this.freeTree(colorNode.children[i]);
        }
        --this.numNodes;
        return null;
    }
    
    protected class ColorNode
    {
        public boolean isLeaf;
        public int childCount;
        ColorNode[] children;
        public int colorCount;
        public long red;
        public long blue;
        public long green;
        public int paletteIndex;
        public int level;
        ColorNode nextReducible;
        
        public ColorNode() {
            this.isLeaf = false;
            this.level = 0;
            this.childCount = 0;
            this.children = new ColorNode[8];
            for (int i = 0; i < 8; ++i) {
                this.children[i] = null;
            }
            this.colorCount = 0;
            final long red = 0L;
            this.blue = red;
            this.green = red;
            this.red = red;
            this.paletteIndex = 0;
        }
        
        public int getLeafChildCount() {
            if (this.isLeaf) {
                return 0;
            }
            int n = 0;
            for (int i = 0; i < this.children.length; ++i) {
                if (this.children[i] != null) {
                    if (this.children[i].isLeaf) {
                        ++n;
                    }
                    else {
                        n += this.children[i].getLeafChildCount();
                    }
                }
            }
            return n;
        }
        
        public int getRGB() {
            return 0xFF000000 | (0xFF & (int)this.red / this.colorCount) << 16 | (0xFF & (int)this.green / this.colorCount) << 8 | (0xFF & (int)this.blue / this.colorCount);
        }
    }
}
