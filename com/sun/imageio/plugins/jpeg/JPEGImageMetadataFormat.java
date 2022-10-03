package com.sun.imageio.plugins.jpeg;

import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.ImageTypeSpecifier;
import java.awt.color.ICC_Profile;
import java.util.List;
import java.util.ArrayList;

public class JPEGImageMetadataFormat extends JPEGMetadataFormat
{
    private static JPEGImageMetadataFormat theInstance;
    
    private JPEGImageMetadataFormat() {
        super("javax_imageio_jpeg_image_1.0", 1);
        this.addElement("JPEGvariety", "javax_imageio_jpeg_image_1.0", 3);
        this.addElement("markerSequence", "javax_imageio_jpeg_image_1.0", 4);
        this.addElement("app0JFIF", "JPEGvariety", 2);
        this.addStreamElements("markerSequence");
        this.addElement("app14Adobe", "markerSequence", 0);
        this.addElement("sof", "markerSequence", 1, 4);
        this.addElement("sos", "markerSequence", 1, 4);
        this.addElement("JFXX", "app0JFIF", 1, Integer.MAX_VALUE);
        this.addElement("app0JFXX", "JFXX", 3);
        this.addElement("app2ICC", "app0JFIF", 0);
        this.addAttribute("app0JFIF", "majorVersion", 2, false, "1", "0", "255", true, true);
        this.addAttribute("app0JFIF", "minorVersion", 2, false, "2", "0", "255", true, true);
        final ArrayList list = new ArrayList();
        list.add("0");
        list.add("1");
        list.add("2");
        this.addAttribute("app0JFIF", "resUnits", 2, false, "0", list);
        this.addAttribute("app0JFIF", "Xdensity", 2, false, "1", "1", "65535", true, true);
        this.addAttribute("app0JFIF", "Ydensity", 2, false, "1", "1", "65535", true, true);
        this.addAttribute("app0JFIF", "thumbWidth", 2, false, "0", "0", "255", true, true);
        this.addAttribute("app0JFIF", "thumbHeight", 2, false, "0", "0", "255", true, true);
        this.addElement("JFIFthumbJPEG", "app0JFXX", 2);
        this.addElement("JFIFthumbPalette", "app0JFXX", 0);
        this.addElement("JFIFthumbRGB", "app0JFXX", 0);
        final ArrayList list2 = new ArrayList();
        list2.add("16");
        list2.add("17");
        list2.add("19");
        this.addAttribute("app0JFXX", "extensionCode", 2, false, null, list2);
        this.addChildElement("markerSequence", "JFIFthumbJPEG");
        this.addAttribute("JFIFthumbPalette", "thumbWidth", 2, false, null, "0", "255", true, true);
        this.addAttribute("JFIFthumbPalette", "thumbHeight", 2, false, null, "0", "255", true, true);
        this.addAttribute("JFIFthumbRGB", "thumbWidth", 2, false, null, "0", "255", true, true);
        this.addAttribute("JFIFthumbRGB", "thumbHeight", 2, false, null, "0", "255", true, true);
        this.addObjectValue("app2ICC", ICC_Profile.class, false, null);
        this.addAttribute("app14Adobe", "version", 2, false, "100", "100", "255", true, true);
        this.addAttribute("app14Adobe", "flags0", 2, false, "0", "0", "65535", true, true);
        this.addAttribute("app14Adobe", "flags1", 2, false, "0", "0", "65535", true, true);
        final ArrayList list3 = new ArrayList();
        list3.add("0");
        list3.add("1");
        list3.add("2");
        this.addAttribute("app14Adobe", "transform", 2, true, null, list3);
        this.addElement("componentSpec", "sof", 0);
        final ArrayList list4 = new ArrayList();
        list4.add("0");
        list4.add("1");
        list4.add("2");
        this.addAttribute("sof", "process", 2, false, null, list4);
        this.addAttribute("sof", "samplePrecision", 2, false, "8");
        this.addAttribute("sof", "numLines", 2, false, null, "0", "65535", true, true);
        this.addAttribute("sof", "samplesPerLine", 2, false, null, "0", "65535", true, true);
        final ArrayList list5 = new ArrayList();
        list5.add("1");
        list5.add("2");
        list5.add("3");
        list5.add("4");
        this.addAttribute("sof", "numFrameComponents", 2, false, null, list5);
        this.addAttribute("componentSpec", "componentId", 2, true, null, "0", "255", true, true);
        this.addAttribute("componentSpec", "HsamplingFactor", 2, true, null, "1", "255", true, true);
        this.addAttribute("componentSpec", "VsamplingFactor", 2, true, null, "1", "255", true, true);
        final ArrayList list6 = new ArrayList();
        list6.add("0");
        list6.add("1");
        list6.add("2");
        list6.add("3");
        this.addAttribute("componentSpec", "QtableSelector", 2, true, null, list6);
        this.addElement("scanComponentSpec", "sos", 0);
        this.addAttribute("sos", "numScanComponents", 2, true, null, list5);
        this.addAttribute("sos", "startSpectralSelection", 2, false, "0", "0", "63", true, true);
        this.addAttribute("sos", "endSpectralSelection", 2, false, "63", "0", "63", true, true);
        this.addAttribute("sos", "approxHigh", 2, false, "0", "0", "15", true, true);
        this.addAttribute("sos", "approxLow", 2, false, "0", "0", "15", true, true);
        this.addAttribute("scanComponentSpec", "componentSelector", 2, true, null, "0", "255", true, true);
        this.addAttribute("scanComponentSpec", "dcHuffTable", 2, true, null, list6);
        this.addAttribute("scanComponentSpec", "acHuffTable", 2, true, null, list6);
    }
    
    @Override
    public boolean canNodeAppear(final String s, final ImageTypeSpecifier imageTypeSpecifier) {
        return s.equals(this.getRootName()) || s.equals("JPEGvariety") || this.isInSubtree(s, "markerSequence") || (this.isInSubtree(s, "app0JFIF") && JPEG.isJFIFcompliant(imageTypeSpecifier, true));
    }
    
    public static synchronized IIOMetadataFormat getInstance() {
        if (JPEGImageMetadataFormat.theInstance == null) {
            JPEGImageMetadataFormat.theInstance = new JPEGImageMetadataFormat();
        }
        return JPEGImageMetadataFormat.theInstance;
    }
    
    static {
        JPEGImageMetadataFormat.theInstance = null;
    }
}
