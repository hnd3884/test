package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaTypeElementSequencer
{
    boolean next(final QName p0);
    
    boolean peek(final QName p0);
}
