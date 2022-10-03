package org.apache.axiom.om.util;

import java.io.Reader;
import org.apache.axiom.om.OMXMLBuilderFactory;
import java.io.StringReader;
import org.apache.axiom.om.OMFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;

public class AXIOMUtil
{
    public static OMElement stringToOM(final String xmlFragment) throws XMLStreamException {
        return stringToOM(OMAbstractFactory.getOMFactory(), xmlFragment);
    }
    
    public static OMElement stringToOM(final OMFactory omFactory, final String xmlFragment) throws XMLStreamException {
        if (xmlFragment != null) {
            return OMXMLBuilderFactory.createOMBuilder(omFactory, new StringReader(xmlFragment)).getDocumentElement();
        }
        return null;
    }
}
