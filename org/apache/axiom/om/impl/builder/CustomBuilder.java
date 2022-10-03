package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;

public interface CustomBuilder
{
    OMElement create(final String p0, final String p1, final OMContainer p2, final XMLStreamReader p3, final OMFactory p4) throws OMException;
}
