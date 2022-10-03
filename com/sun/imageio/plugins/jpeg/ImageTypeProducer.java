package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;

class ImageTypeProducer
{
    private ImageTypeSpecifier type;
    boolean failed;
    private int csCode;
    private static final ImageTypeProducer[] defaultTypes;
    
    public ImageTypeProducer(final int csCode) {
        this.type = null;
        this.failed = false;
        this.csCode = csCode;
    }
    
    public ImageTypeProducer() {
        this.type = null;
        this.failed = false;
        this.csCode = -1;
    }
    
    public synchronized ImageTypeSpecifier getType() {
        if (!this.failed && this.type == null) {
            try {
                this.type = this.produce();
            }
            catch (final Throwable t) {
                this.failed = true;
            }
        }
        return this.type;
    }
    
    public static synchronized ImageTypeProducer getTypeProducer(final int n) {
        if (n < 0 || n >= 6) {
            return null;
        }
        if (ImageTypeProducer.defaultTypes[n] == null) {
            ImageTypeProducer.defaultTypes[n] = new ImageTypeProducer(n);
        }
        return ImageTypeProducer.defaultTypes[n];
    }
    
    protected ImageTypeSpecifier produce() {
        switch (this.csCode) {
            case 1: {
                return ImageTypeSpecifier.createFromBufferedImageType(10);
            }
            case 2: {
                return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        defaultTypes = new ImageTypeProducer[6];
    }
}
