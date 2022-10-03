package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.xs.XSObjectList;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.w3c.dom.DOMException;
import org.apache.xerces.xs.ElementPSVI;
import org.w3c.dom.Element;

public interface XSAssertionXPath2Value
{
    String computeStringValueOf$value(final Element p0, final ElementPSVI p1) throws DOMException;
    
    void setXDMTypedValueOf$value(final Element p0, final String p1, final XSSimpleTypeDefinition p2, final XSTypeDefinition p3, final boolean p4, final DynamicContext p5) throws Exception;
    
    void setXDMTypedValueOf$valueForSTVarietyAtomic(final String p0, final short p1, final DynamicContext p2);
    
    void setXDMTypedValueOf$valueForSTVarietyList(final Element p0, final String p1, final XSSimpleTypeDefinition p2, final boolean p3, final DynamicContext p4) throws Exception;
    
    void setXDMTypedValueOf$valueForSTVarietyUnion(final String p0, final XSObjectList p1, final DynamicContext p2) throws Exception;
}
