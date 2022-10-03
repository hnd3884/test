package org.apache.poi.xssf.binary;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;
import org.apache.poi.ooxml.POIXMLRelation;

@Internal
public class XSSFBRelation extends POIXMLRelation
{
    private static final POILogger log;
    static final XSSFBRelation SHARED_STRINGS_BINARY;
    public static final XSSFBRelation STYLES_BINARY;
    
    private XSSFBRelation(final String type, final String rel, final String defaultName) {
        super(type, rel, defaultName);
    }
    
    static {
        log = POILogFactory.getLogger((Class)XSSFBRelation.class);
        SHARED_STRINGS_BINARY = new XSSFBRelation("application/vnd.ms-excel.sharedStrings", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings", "/xl/sharedStrings.bin");
        STYLES_BINARY = new XSSFBRelation("application/vnd.ms-excel.styles", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles", "/xl/styles.bin");
    }
}
