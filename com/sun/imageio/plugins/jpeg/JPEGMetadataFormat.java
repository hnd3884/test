package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.metadata.IIOMetadataFormatImpl;

abstract class JPEGMetadataFormat extends IIOMetadataFormatImpl
{
    private static final int MAX_JPEG_DATA_SIZE = 65533;
    String resourceBaseName;
    
    JPEGMetadataFormat(final String s, final int n) {
        super(s, n);
        this.setResourceBaseName(this.resourceBaseName = this.getClass().getName() + "Resources");
    }
    
    void addStreamElements(final String s) {
        this.addElement("dqt", s, 1, 4);
        this.addElement("dqtable", "dqt", 0);
        this.addAttribute("dqtable", "elementPrecision", 2, false, "0");
        final ArrayList list = new ArrayList();
        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        this.addAttribute("dqtable", "qtableId", 2, true, null, list);
        this.addObjectValue("dqtable", JPEGQTable.class, true, null);
        this.addElement("dht", s, 1, 4);
        this.addElement("dhtable", "dht", 0);
        final ArrayList list2 = new ArrayList();
        list2.add("0");
        list2.add("1");
        this.addAttribute("dhtable", "class", 2, true, null, list2);
        this.addAttribute("dhtable", "htableId", 2, true, null, list);
        this.addObjectValue("dhtable", JPEGHuffmanTable.class, true, null);
        this.addElement("dri", s, 0);
        this.addAttribute("dri", "interval", 2, true, null, "0", "65535", true, true);
        this.addElement("com", s, 0);
        this.addAttribute("com", "comment", 0, false, null);
        this.addObjectValue("com", byte[].class, 1, 65533);
        this.addElement("unknown", s, 0);
        this.addAttribute("unknown", "MarkerTag", 2, true, null, "0", "255", true, true);
        this.addObjectValue("unknown", byte[].class, 1, 65533);
    }
    
    @Override
    public boolean canNodeAppear(final String s, final ImageTypeSpecifier imageTypeSpecifier) {
        return this.isInSubtree(s, this.getRootName());
    }
    
    protected boolean isInSubtree(final String s, final String s2) {
        if (s.equals(s2)) {
            return true;
        }
        final String[] childNames = this.getChildNames(s);
        for (int i = 0; i < childNames.length; ++i) {
            if (this.isInSubtree(s, childNames[i])) {
                return true;
            }
        }
        return false;
    }
}
