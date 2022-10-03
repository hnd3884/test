package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class POIFSReaderEvent
{
    private final DocumentInputStream stream;
    private final POIFSDocumentPath path;
    private final String documentName;
    
    POIFSReaderEvent(final DocumentInputStream stream, final POIFSDocumentPath path, final String documentName) {
        this.stream = stream;
        this.path = path;
        this.documentName = documentName;
    }
    
    public DocumentInputStream getStream() {
        return this.stream;
    }
    
    public POIFSDocumentPath getPath() {
        return this.path;
    }
    
    public String getName() {
        return this.documentName;
    }
}
