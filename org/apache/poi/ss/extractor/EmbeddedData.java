package org.apache.poi.ss.extractor;

import org.apache.poi.ss.usermodel.Shape;

public class EmbeddedData
{
    private String filename;
    private byte[] embeddedData;
    private Shape shape;
    private String contentType;
    
    public EmbeddedData(final String filename, final byte[] embeddedData, final String contentType) {
        this.contentType = "binary/octet-stream";
        this.setFilename(filename);
        this.setEmbeddedData(embeddedData);
        this.setContentType(contentType);
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setFilename(final String filename) {
        if (filename == null) {
            this.filename = "unknown.bin";
        }
        else {
            this.filename = filename.replaceAll("[^/\\\\]*[/\\\\]", "").trim();
        }
    }
    
    public byte[] getEmbeddedData() {
        return this.embeddedData;
    }
    
    public void setEmbeddedData(final byte[] embeddedData) {
        this.embeddedData = (byte[])((embeddedData == null) ? null : ((byte[])embeddedData.clone()));
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public void setShape(final Shape shape) {
        this.shape = shape;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
}
