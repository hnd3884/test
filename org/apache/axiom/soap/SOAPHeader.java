package org.apache.axiom.soap;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;

public interface SOAPHeader extends OMElement
{
    SOAPHeaderBlock addHeaderBlock(final String p0, final OMNamespace p1) throws OMException;
    
    SOAPHeaderBlock addHeaderBlock(final QName p0) throws OMException;
    
    Iterator getHeadersToProcess(final RolePlayer p0);
    
    Iterator examineHeaderBlocks(final String p0);
    
    Iterator extractHeaderBlocks(final String p0);
    
    Iterator examineMustUnderstandHeaderBlocks(final String p0);
    
    Iterator examineAllHeaderBlocks();
    
    Iterator extractAllHeaderBlocks();
    
    ArrayList getHeaderBlocksWithNSURI(final String p0);
    
    Iterator getHeadersToProcess(final RolePlayer p0, final String p1);
}
