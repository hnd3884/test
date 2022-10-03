package org.apache.axiom.om.impl.builder;

import javax.xml.namespace.QName;

public interface CustomBuilderSupport
{
    CustomBuilder registerCustomBuilder(final QName p0, final int p1, final CustomBuilder p2);
    
    CustomBuilder registerCustomBuilderForPayload(final CustomBuilder p0);
}
