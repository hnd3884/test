package org.apache.poi.xslf.usermodel;

import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLFactory;

public final class XSLFFactory extends POIXMLFactory
{
    private static final XSLFFactory inst;
    
    public static XSLFFactory getInstance() {
        return XSLFFactory.inst;
    }
    
    private XSLFFactory() {
    }
    
    @Override
    protected POIXMLRelation getDescriptor(final String relationshipType) {
        return XSLFRelation.getInstance(relationshipType);
    }
    
    static {
        inst = new XSLFFactory();
    }
}
