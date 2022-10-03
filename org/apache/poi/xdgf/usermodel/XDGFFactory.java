package org.apache.poi.xdgf.usermodel;

import org.apache.poi.xdgf.xml.XDGFXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLFactory;

public class XDGFFactory extends POIXMLFactory
{
    private final XDGFDocument document;
    
    public XDGFFactory(final XDGFDocument document) {
        this.document = document;
    }
    
    @Override
    protected POIXMLRelation getDescriptor(final String relationshipType) {
        return XDGFRelation.getInstance(relationshipType);
    }
    
    @Override
    public POIXMLDocumentPart createDocumentPart(final POIXMLDocumentPart parent, final PackagePart part) {
        final POIXMLDocumentPart newPart = super.createDocumentPart(parent, part);
        if (newPart instanceof XDGFXMLDocumentPart) {
            ((XDGFXMLDocumentPart)newPart).setDocument(this.document);
        }
        return newPart;
    }
    
    @Override
    public POIXMLDocumentPart newDocumentPart(final POIXMLRelation descriptor) {
        final POIXMLDocumentPart newPart = super.newDocumentPart(descriptor);
        if (newPart instanceof XDGFXMLDocumentPart) {
            ((XDGFXMLDocumentPart)newPart).setDocument(this.document);
        }
        return newPart;
    }
}
