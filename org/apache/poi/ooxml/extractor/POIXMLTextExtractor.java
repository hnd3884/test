package org.apache.poi.ooxml.extractor;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.extractor.POITextExtractor;

public abstract class POIXMLTextExtractor extends POITextExtractor
{
    private final POIXMLDocument _document;
    
    public POIXMLTextExtractor(final POIXMLDocument document) {
        this._document = document;
    }
    
    public POIXMLProperties.CoreProperties getCoreProperties() {
        return this._document.getProperties().getCoreProperties();
    }
    
    public POIXMLProperties.ExtendedProperties getExtendedProperties() {
        return this._document.getProperties().getExtendedProperties();
    }
    
    public POIXMLProperties.CustomProperties getCustomProperties() {
        return this._document.getProperties().getCustomProperties();
    }
    
    public final POIXMLDocument getDocument() {
        return this._document;
    }
    
    public OPCPackage getPackage() {
        return this._document.getPackage();
    }
    
    public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        return new POIXMLPropertiesTextExtractor(this._document);
    }
    
    public void close() throws IOException {
        if (this._document != null) {
            final OPCPackage pkg = this._document.getPackage();
            if (pkg != null) {
                pkg.revert();
            }
        }
        super.close();
    }
    
    protected void checkMaxTextSize(final CharSequence text, final String string) {
        if (string == null) {
            return;
        }
        final int size = text.length() + string.length();
        if (size > ZipSecureFile.getMaxTextSize()) {
            throw new IllegalStateException("The text would exceed the max allowed overall size of extracted text. By default this is prevented as some documents may exhaust available memory and it may indicate that the file is used to inflate memory usage and thus could pose a security risk. You can adjust this limit via ZipSecureFile.setMaxTextSize() if you need to work with files which have a lot of text. Size: " + size + ", limit: MAX_TEXT_SIZE: " + ZipSecureFile.getMaxTextSize());
        }
    }
}
