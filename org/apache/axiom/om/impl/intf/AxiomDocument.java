package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.om.OMDocument;

public interface AxiomDocument extends OMDocument, AxiomContainer, CoreDocument, DeferringParentNode
{
    void checkChild(final OMNode p0);
    
    void checkDocumentElement(final OMElement p0);
    
    String getCharsetEncoding();
    
    OMElement getOMDocumentElement();
    
    String getXMLEncoding();
    
    String getXMLVersion();
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2, final boolean p3) throws OutputException;
    
    String isStandalone();
    
    void setCharsetEncoding(final String p0);
    
    void setComplete(final boolean p0);
    
    void setOMDocumentElement(final OMElement p0);
    
    void setStandalone(final String p0);
    
    void setXMLEncoding(final String p0);
    
    void setXMLVersion(final String p0);
}
