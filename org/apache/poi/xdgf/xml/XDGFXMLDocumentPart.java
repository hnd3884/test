package org.apache.poi.xdgf.xml;

import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xdgf.usermodel.XDGFDocument;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XDGFXMLDocumentPart extends POIXMLDocumentPart
{
    protected XDGFDocument _document;
    
    public XDGFXMLDocumentPart(final PackagePart part) {
        super(part);
    }
    
    @Internal
    public void setDocument(final XDGFDocument document) {
        this._document = document;
    }
}
