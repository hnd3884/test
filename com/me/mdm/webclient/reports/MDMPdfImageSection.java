package com.me.mdm.webclient.reports;

import java.io.ByteArrayOutputStream;

public class MDMPdfImageSection
{
    public ByteArrayOutputStream imageByteArrayStream;
    public float imageScalePercentage;
    public float imageOffsetX;
    public float imageOffsetY;
    public int horizontalAlignment;
    public int verticalAlignment;
    
    public MDMPdfImageSection() {
        this.imageScalePercentage = 100.0f;
        this.imageOffsetX = 0.0f;
        this.imageOffsetY = 0.0f;
        this.horizontalAlignment = 0;
        this.verticalAlignment = 4;
    }
}
