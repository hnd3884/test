package com.sun.imageio.plugins.gif;

import javax.imageio.ImageTypeSpecifier;
import java.util.Arrays;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFStreamMetadataFormat extends IIOMetadataFormatImpl
{
    private static IIOMetadataFormat instance;
    
    private GIFStreamMetadataFormat() {
        super("javax_imageio_gif_stream_1.0", 2);
        this.addElement("Version", "javax_imageio_gif_stream_1.0", 0);
        this.addAttribute("Version", "value", 0, true, null, Arrays.asList(GIFStreamMetadata.versionStrings));
        this.addElement("LogicalScreenDescriptor", "javax_imageio_gif_stream_1.0", 0);
        this.addAttribute("LogicalScreenDescriptor", "logicalScreenWidth", 2, true, null, "1", "65535", true, true);
        this.addAttribute("LogicalScreenDescriptor", "logicalScreenHeight", 2, true, null, "1", "65535", true, true);
        this.addAttribute("LogicalScreenDescriptor", "colorResolution", 2, true, null, "1", "8", true, true);
        this.addAttribute("LogicalScreenDescriptor", "pixelAspectRatio", 2, true, null, "0", "255", true, true);
        this.addElement("GlobalColorTable", "javax_imageio_gif_stream_1.0", 2, 256);
        this.addAttribute("GlobalColorTable", "sizeOfGlobalColorTable", 2, true, null, Arrays.asList(GIFStreamMetadata.colorTableSizes));
        this.addAttribute("GlobalColorTable", "backgroundColorIndex", 2, true, null, "0", "255", true, true);
        this.addBooleanAttribute("GlobalColorTable", "sortFlag", false, false);
        this.addElement("ColorTableEntry", "GlobalColorTable", 0);
        this.addAttribute("ColorTableEntry", "index", 2, true, null, "0", "255", true, true);
        this.addAttribute("ColorTableEntry", "red", 2, true, null, "0", "255", true, true);
        this.addAttribute("ColorTableEntry", "green", 2, true, null, "0", "255", true, true);
        this.addAttribute("ColorTableEntry", "blue", 2, true, null, "0", "255", true, true);
    }
    
    @Override
    public boolean canNodeAppear(final String s, final ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }
    
    public static synchronized IIOMetadataFormat getInstance() {
        if (GIFStreamMetadataFormat.instance == null) {
            GIFStreamMetadataFormat.instance = new GIFStreamMetadataFormat();
        }
        return GIFStreamMetadataFormat.instance;
    }
    
    static {
        GIFStreamMetadataFormat.instance = null;
    }
}
