package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;

public interface AttributesEx extends Attributes
{
    CharSequence getData(final int p0);
    
    CharSequence getData(final String p0, final String p1);
}
