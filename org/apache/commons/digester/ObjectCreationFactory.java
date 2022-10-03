package org.apache.commons.digester;

import org.xml.sax.Attributes;

public interface ObjectCreationFactory
{
    Object createObject(final Attributes p0) throws Exception;
    
    Digester getDigester();
    
    void setDigester(final Digester p0);
}
