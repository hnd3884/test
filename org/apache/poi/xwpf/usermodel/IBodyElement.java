package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;

public interface IBodyElement
{
    IBody getBody();
    
    POIXMLDocumentPart getPart();
    
    BodyType getPartType();
    
    BodyElementType getElementType();
}
