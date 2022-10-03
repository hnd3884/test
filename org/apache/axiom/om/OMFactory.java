package org.apache.axiom.om;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import javax.xml.namespace.QName;

public interface OMFactory
{
    OMMetaFactory getMetaFactory();
    
    OMDocument createOMDocument();
    
    OMElement createOMElement(final String p0, final OMNamespace p1);
    
    OMElement createOMElement(final String p0, final OMNamespace p1, final OMContainer p2) throws OMException;
    
    OMSourcedElement createOMElement(final OMDataSource p0);
    
    OMSourcedElement createOMElement(final OMDataSource p0, final String p1, final OMNamespace p2);
    
    OMSourcedElement createOMElement(final OMDataSource p0, final QName p1);
    
    OMElement createOMElement(final String p0, final String p1, final String p2);
    
    OMElement createOMElement(final QName p0, final OMContainer p1);
    
    OMElement createOMElement(final QName p0);
    
    OMNamespace createOMNamespace(final String p0, final String p1);
    
    OMText createOMText(final OMContainer p0, final String p1);
    
    OMText createOMText(final OMContainer p0, final OMText p1);
    
    @Deprecated
    OMText createOMText(final OMContainer p0, final QName p1);
    
    OMText createOMText(final OMContainer p0, final String p1, final int p2);
    
    @Deprecated
    OMText createOMText(final OMContainer p0, final char[] p1, final int p2);
    
    @Deprecated
    OMText createOMText(final OMContainer p0, final QName p1, final int p2);
    
    OMText createOMText(final String p0);
    
    OMText createOMText(final String p0, final int p1);
    
    OMText createOMText(final String p0, final String p1, final boolean p2);
    
    OMText createOMText(final Object p0, final boolean p1);
    
    OMText createOMText(final OMContainer p0, final String p1, final String p2, final boolean p3);
    
    OMText createOMText(final String p0, final DataHandlerProvider p1, final boolean p2);
    
    OMAttribute createOMAttribute(final String p0, final OMNamespace p1, final String p2);
    
    OMDocType createOMDocType(final OMContainer p0, final String p1, final String p2, final String p3, final String p4);
    
    OMProcessingInstruction createOMProcessingInstruction(final OMContainer p0, final String p1, final String p2);
    
    OMComment createOMComment(final OMContainer p0, final String p1);
    
    OMEntityReference createOMEntityReference(final OMContainer p0, final String p1);
}
