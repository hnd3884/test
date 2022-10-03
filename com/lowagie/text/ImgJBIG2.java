package com.lowagie.text;

import java.security.MessageDigest;
import java.net.URL;

public class ImgJBIG2 extends Image
{
    private byte[] global;
    private byte[] globalHash;
    
    ImgJBIG2(final Image image) {
        super(image);
    }
    
    public ImgJBIG2() {
        super((Image)null);
    }
    
    public ImgJBIG2(final int width, final int height, final byte[] data, final byte[] globals) {
        super((URL)null);
        this.type = 36;
        this.originalType = 9;
        this.setTop(this.scaledHeight = (float)height);
        this.setRight(this.scaledWidth = (float)width);
        this.bpc = 1;
        this.colorspace = 1;
        this.rawData = data;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
        if (globals != null) {
            this.global = globals;
            try {
                final MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(this.global);
                this.globalHash = md.digest();
            }
            catch (final Exception ex) {}
        }
    }
    
    public byte[] getGlobalBytes() {
        return this.global;
    }
    
    public byte[] getGlobalHash() {
        return this.globalHash;
    }
}
