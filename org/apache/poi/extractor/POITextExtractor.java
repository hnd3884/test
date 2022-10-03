package org.apache.poi.extractor;

import java.io.IOException;
import java.io.Closeable;

public abstract class POITextExtractor implements Closeable
{
    private Closeable fsToClose;
    
    public abstract String getText();
    
    public abstract POITextExtractor getMetadataTextExtractor();
    
    public void setFilesystem(final Closeable fs) {
        this.fsToClose = fs;
    }
    
    @Override
    public void close() throws IOException {
        if (this.fsToClose != null) {
            this.fsToClose.close();
        }
    }
    
    public abstract Object getDocument();
}
