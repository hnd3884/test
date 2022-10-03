package org.apache.poi.hpsf.extractor;

import java.io.IOException;
import java.io.File;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.SummaryInformation;
import java.util.Iterator;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIDocument;
import org.apache.poi.extractor.POIOLE2TextExtractor;

public class HPSFPropertiesExtractor extends POIOLE2TextExtractor
{
    public HPSFPropertiesExtractor(final POIOLE2TextExtractor mainExtractor) {
        super(mainExtractor);
    }
    
    public HPSFPropertiesExtractor(final POIDocument doc) {
        super(doc);
    }
    
    public HPSFPropertiesExtractor(final POIFSFileSystem fs) {
        super(new HPSFPropertiesOnlyDocument(fs));
    }
    
    public String getDocumentSummaryInformationText() {
        if (this.document == null) {
            return "";
        }
        final DocumentSummaryInformation dsi = this.document.getDocumentSummaryInformation();
        final StringBuilder text = new StringBuilder();
        text.append(getPropertiesText(dsi));
        final CustomProperties cps = (dsi == null) ? null : dsi.getCustomProperties();
        if (cps != null) {
            for (final String key : cps.nameSet()) {
                final String val = getPropertyValueText(cps.get(key));
                text.append(key).append(" = ").append(val).append("\n");
            }
        }
        return text.toString();
    }
    
    public String getSummaryInformationText() {
        if (this.document == null) {
            return "";
        }
        final SummaryInformation si = this.document.getSummaryInformation();
        return getPropertiesText(si);
    }
    
    private static String getPropertiesText(final PropertySet ps) {
        if (ps == null) {
            return "";
        }
        final StringBuilder text = new StringBuilder();
        final PropertyIDMap idMap = ps.getPropertySetIDMap();
        final Property[] properties;
        final Property[] props = properties = ps.getProperties();
        for (final Property prop : properties) {
            String type = Long.toString(prop.getID());
            final Object typeObj = (idMap == null) ? null : idMap.get((Object)prop.getID());
            if (typeObj != null) {
                type = typeObj.toString();
            }
            final String val = getPropertyValueText(prop.getValue());
            text.append(type).append(" = ").append(val).append("\n");
        }
        return text.toString();
    }
    
    @Override
    public String getText() {
        return this.getSummaryInformationText() + this.getDocumentSummaryInformationText();
    }
    
    @Override
    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }
    
    private static String getPropertyValueText(final Object val) {
        return (val == null) ? "(not set)" : PropertySet.getPropertyStringValue(val);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static void main(final String[] args) throws IOException {
        for (final String file : args) {
            try (final HPSFPropertiesExtractor ext = new HPSFPropertiesExtractor(new POIFSFileSystem(new File(file)))) {
                System.out.println(ext.getText());
            }
        }
    }
}
