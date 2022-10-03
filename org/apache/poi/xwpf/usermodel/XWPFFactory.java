package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLFactory;

public final class XWPFFactory extends POIXMLFactory
{
    private static final XWPFFactory inst;
    
    public static XWPFFactory getInstance() {
        return XWPFFactory.inst;
    }
    
    private XWPFFactory() {
    }
    
    @Override
    protected POIXMLRelation getDescriptor(final String relationshipType) {
        return XWPFRelation.getInstance(relationshipType);
    }
    
    static {
        inst = new XWPFFactory();
    }
}
