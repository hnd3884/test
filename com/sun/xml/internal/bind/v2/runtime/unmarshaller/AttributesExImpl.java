package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.util.AttributesImpl;

public final class AttributesExImpl extends AttributesImpl implements AttributesEx
{
    @Override
    public CharSequence getData(final int idx) {
        return this.getValue(idx);
    }
    
    @Override
    public CharSequence getData(final String nsUri, final String localName) {
        return this.getValue(nsUri, localName);
    }
}
