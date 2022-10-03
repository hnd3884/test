package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMFactory;

public interface OMFactoryEx extends OMFactory
{
    OMDocument createOMDocument(final OMXMLParserWrapper p0);
    
    OMElement createOMElement(final String p0, final OMContainer p1, final OMXMLParserWrapper p2);
    
    OMText createOMText(final OMContainer p0, final Object p1, final boolean p2, final boolean p3);
    
    OMText createOMText(final OMContainer p0, final String p1, final int p2, final boolean p3);
    
    OMComment createOMComment(final OMContainer p0, final String p1, final boolean p2);
    
    OMDocType createOMDocType(final OMContainer p0, final String p1, final String p2, final String p3, final String p4, final boolean p5);
    
    OMProcessingInstruction createOMProcessingInstruction(final OMContainer p0, final String p1, final String p2, final boolean p3);
    
    OMEntityReference createOMEntityReference(final OMContainer p0, final String p1, final String p2, final boolean p3);
    
    OMNode importNode(final OMNode p0);
}
