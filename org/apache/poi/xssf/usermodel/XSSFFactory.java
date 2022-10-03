package org.apache.poi.xssf.usermodel;

import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLFactory;

public class XSSFFactory extends POIXMLFactory
{
    private static final XSSFFactory inst;
    
    public static XSSFFactory getInstance() {
        return XSSFFactory.inst;
    }
    
    protected XSSFFactory() {
    }
    
    @Override
    protected POIXMLRelation getDescriptor(final String relationshipType) {
        return XSSFRelation.getInstance(relationshipType);
    }
    
    static {
        inst = new XSSFFactory();
    }
}
