package org.apache.poi.extractor;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import java.io.Closeable;
import org.apache.poi.POIDocument;

public abstract class POIOLE2TextExtractor extends POITextExtractor
{
    protected POIDocument document;
    
    public POIOLE2TextExtractor(final POIDocument document) {
        this.setFilesystem(this.document = document);
    }
    
    protected POIOLE2TextExtractor(final POIOLE2TextExtractor otherExtractor) {
        this.document = otherExtractor.document;
    }
    
    public DocumentSummaryInformation getDocSummaryInformation() {
        return this.document.getDocumentSummaryInformation();
    }
    
    public SummaryInformation getSummaryInformation() {
        return this.document.getSummaryInformation();
    }
    
    @Override
    public POITextExtractor getMetadataTextExtractor() {
        return new HPSFPropertiesExtractor(this);
    }
    
    public DirectoryEntry getRoot() {
        return this.document.getDirectory();
    }
    
    @Override
    public POIDocument getDocument() {
        return this.document;
    }
}
