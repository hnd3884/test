package org.apache.xmlbeans.impl.inst2xsd;

import org.apache.xmlbeans.impl.inst2xsd.util.TypeSystemHolder;
import org.apache.xmlbeans.XmlObject;

public interface XsdGenStrategy
{
    void processDoc(final XmlObject[] p0, final Inst2XsdOptions p1, final TypeSystemHolder p2);
}
