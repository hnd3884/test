package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGStreamMetadataFormat extends JPEGMetadataFormat
{
    private static JPEGStreamMetadataFormat theInstance;
    
    private JPEGStreamMetadataFormat() {
        super("javax_imageio_jpeg_stream_1.0", 4);
        this.addStreamElements(this.getRootName());
    }
    
    public static synchronized IIOMetadataFormat getInstance() {
        if (JPEGStreamMetadataFormat.theInstance == null) {
            JPEGStreamMetadataFormat.theInstance = new JPEGStreamMetadataFormat();
        }
        return JPEGStreamMetadataFormat.theInstance;
    }
    
    static {
        JPEGStreamMetadataFormat.theInstance = null;
    }
}
