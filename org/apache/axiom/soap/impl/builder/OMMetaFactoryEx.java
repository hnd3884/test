package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMMetaFactory;

public interface OMMetaFactoryEx extends OMMetaFactory
{
    SOAPMessage createSOAPMessage(final OMXMLParserWrapper p0);
}
