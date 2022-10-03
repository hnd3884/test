package org.apache.axiom.om.dom;

import org.w3c.dom.DOMImplementation;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axiom.om.OMMetaFactory;

public interface DOMMetaFactory extends OMMetaFactory
{
    DocumentBuilderFactory newDocumentBuilderFactory();
    
    DOMImplementation getDOMImplementation();
}
