package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Node;
import javax.xml.transform.Source;
import org.xml.sax.InputSource;
import org.apache.axiom.om.util.StAXParserConfiguration;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.soap.SOAPFactory;

public interface OMMetaFactory
{
    OMFactory getOMFactory();
    
    SOAPFactory getSOAP11Factory();
    
    SOAPFactory getSOAP12Factory();
    
    OMXMLParserWrapper createStAXOMBuilder(final OMFactory p0, final XMLStreamReader p1);
    
    OMXMLParserWrapper createOMBuilder(final OMFactory p0, final StAXParserConfiguration p1, final InputSource p2);
    
    OMXMLParserWrapper createOMBuilder(final OMFactory p0, final Source p1);
    
    OMXMLParserWrapper createOMBuilder(final OMFactory p0, final Node p1, final boolean p2);
    
    OMXMLParserWrapper createOMBuilder(final OMFactory p0, final SAXSource p1, final boolean p2);
    
    OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration p0, final OMFactory p1, final InputSource p2, final MimePartProvider p3);
    
    SOAPModelBuilder createStAXSOAPModelBuilder(final XMLStreamReader p0);
    
    SOAPModelBuilder createSOAPModelBuilder(final StAXParserConfiguration p0, final InputSource p1);
    
    SOAPModelBuilder createSOAPModelBuilder(final Source p0);
    
    SOAPModelBuilder createSOAPModelBuilder(final StAXParserConfiguration p0, final SOAPFactory p1, final InputSource p2, final MimePartProvider p3);
}
