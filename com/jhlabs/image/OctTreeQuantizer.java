package com.jhlabs.image;

import java.io.PrintStream;
import java.util.Vector;

public class OctTreeQuantizer implements Quantizer
{
    static final int MAX_LEVEL = 5;
    private int nodes;
    private OctTreeNode root;
    private int reduceColors;
    private int maximumColors;
    private int colors;
    private Vector[] colorList;
    
    public OctTreeQuantizer() {
        this.nodes = 0;
        this.colors = 0;
        this.setup(256);
        this.colorList = new Vector[6];
        for (int i = 0; i < 6; ++i) {
            this.colorList[i] = new Vector();
        }
        this.root = new OctTreeNode();
    }
    
    public void setup(final int numColors) {
        this.maximumColors = numColors;
        this.reduceColors = Math.max(512, numColors * 2);
    }
    
    public void addPixels(final int[] pixels, final int offset, final int count) {
        for (int i = 0; i < count; ++i) {
            this.insertColor(pixels[i + offset]);
            if (this.colors > this.reduceColors) {
                this.reduceTree(this.reduceColors);
            }
        }
    }
    
    public int getIndexForColor(final int rgb) {
        final int red = rgb >> 16 & 0xFF;
        final int green = rgb >> 8 & 0xFF;
        final int blue = rgb & 0xFF;
        OctTreeNode node = this.root;
        for (int level = 0; level <= 5; ++level) {
            final int bit = 128 >> level;
            int index = 0;
            if ((red & bit) != 0x0) {
                index += 4;
            }
            if ((green & bit) != 0x0) {
                index += 2;
            }
            if ((blue & bit) != 0x0) {
                ++index;
            }
            final OctTreeNode child = node.leaf[index];
            if (child == null) {
                return node.index;
            }
            if (child.isLeaf) {
                return child.index;
            }
            node = child;
        }
        System.out.println("getIndexForColor failed");
        return 0;
    }
    
    private void insertColor(final int rgb) {
        final int red = rgb >> 16 & 0xFF;
        final int green = rgb >> 8 & 0xFF;
        final int blue = rgb & 0xFF;
        OctTreeNode node = this.root;
        for (int level = 0; level <= 5; ++level) {
            final int bit = 128 >> level;
            int index = 0;
            if ((red & bit) != 0x0) {
                index += 4;
            }
            if ((green & bit) != 0x0) {
                index += 2;
            }
            if ((blue & bit) != 0x0) {
                ++index;
            }
            OctTreeNode child = node.leaf[index];
            if (child == null) {
                final OctTreeNode octTreeNode = node;
                ++octTreeNode.children;
                child = new OctTreeNode();
                child.parent = node;
                node.leaf[index] = child;
                node.isLeaf = false;
                ++this.nodes;
                this.colorList[level].addElement(child);
                if (level == 5) {
                    child.isLeaf = true;
                    child.count = 1;
                    child.totalRed = red;
                    child.totalGreen = green;
                    child.totalBlue = blue;
                    child.level = level;
                    ++this.colors;
                    return;
                }
                node = child;
            }
            else {
                if (child.isLeaf) {
                    final OctTreeNode octTreeNode2 = child;
                    ++octTreeNode2.count;
                    final OctTreeNode octTreeNode3 = child;
                    octTreeNode3.totalRed += red;
                    final OctTreeNode octTreeNode4 = child;
                    octTreeNode4.totalGreen += green;
                    final OctTreeNode octTreeNode5 = child;
                    octTreeNode5.totalBlue += blue;
                    return;
                }
                node = child;
            }
        }
        System.out.println("insertColor failed");
    }
    
    private void reduceTree(final int numColors) {
        for (int level = 4; level >= 0; --level) {
            final Vector v = this.colorList[level];
            if (v != null && v.size() > 0) {
                for (int j = 0; j < v.size(); ++j) {
                    final OctTreeNode node = v.elementAt(j);
                    if (node.children > 0) {
                        for (int i = 0; i < 8; ++i) {
                            final OctTreeNode child = node.leaf[i];
                            if (child != null) {
                                if (!child.isLeaf) {
                                    System.out.println("not a leaf!");
                                }
                                final OctTreeNode octTreeNode = node;
                                octTreeNode.count += child.count;
                                final OctTreeNode octTreeNode2 = node;
                                octTreeNode2.totalRed += child.totalRed;
                                final OctTreeNode octTreeNode3 = node;
                                octTreeNode3.totalGreen += child.totalGreen;
                                final OctTreeNode octTreeNode4 = node;
                                octTreeNode4.totalBlue += child.totalBlue;
                                node.leaf[i] = null;
                                final OctTreeNode octTreeNode5 = node;
                                --octTreeNode5.children;
                                --this.colors;
                                --this.nodes;
                                this.colorList[level + 1].removeElement(child);
                            }
                        }
                        node.isLeaf = true;
                        ++this.colors;
                        if (this.colors <= numColors) {
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("Unable to reduce the OctTree");
    }
    
    public int[] buildColorTable() {
        final int[] table = new int[this.colors];
        this.buildColorTable(this.root, table, 0);
        return table;
    }
    
    public void buildColorTable(final int[] inPixels, final int[] table) {
        final int count = inPixels.length;
        this.maximumColors = table.length;
        for (int i = 0; i < count; ++i) {
            this.insertColor(inPixels[i]);
            if (this.colors > this.reduceColors) {
                this.reduceTree(this.reduceColors);
            }
        }
        if (this.colors > this.maximumColors) {
            this.reduceTree(this.maximumColors);
        }
        this.buildColorTable(this.root, table, 0);
    }
    
    private int buildColorTable(final OctTreeNode node, final int[] table, int index) {
        if (this.colors > this.maximumColors) {
            this.reduceTree(this.maximumColors);
        }
        if (node.isLeaf) {
            final int count = node.count;
            table[index] = (0xFF000000 | node.totalRed / count << 16 | node.totalGreen / count << 8 | node.totalBlue / count);
            node.index = index++;
        }
        else {
            for (int i = 0; i < 8; ++i) {
                if (node.leaf[i] != null) {
                    node.index = index;
                    index = this.buildColorTable(node.leaf[i], table, index);
                }
            }
        }
        return index;
    }
    
    class OctTreeNode
    {
        int children;
        int level;
        OctTreeNode parent;
        OctTreeNode[] leaf;
        boolean isLeaf;
        int count;
        int totalRed;
        int totalGreen;
        int totalBlue;
        int index;
        
        OctTreeNode() {
            this.leaf = new OctTreeNode[8];
        }
        
        public void list(final PrintStream s, final int level) {
            for (int i = 0; i < level; ++i) {
                System.out.print(' ');
            }
            if (this.count == 0) {
                System.out.println(this.index + ": count=" + this.count);
            }
            else {
                System.out.println(this.index + ": count=" + this.count + " red=" + this.totalRed / this.count + " green=" + this.totalGreen / this.count + " blue=" + this.totalBlue / this.count);
            }
            for (int i = 0; i < 8; ++i) {
                if (this.leaf[i] != null) {
                    this.leaf[i].list(s, level + 2);
                }
            }
        }
    }
}
