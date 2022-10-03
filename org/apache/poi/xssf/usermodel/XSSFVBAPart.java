package org.apache.poi.xssf.usermodel;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFVBAPart extends POIXMLDocumentPart
{
    protected XSSFVBAPart() {
    }
    
    protected XSSFVBAPart(final PackagePart part) {
        super(part);
    }
    
    @Override
    protected void prepareForCommit() {
    }
}
