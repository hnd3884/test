package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public final class DefaultValueLoaderDecorator extends Loader
{
    private final Loader l;
    private final String defaultValue;
    
    public DefaultValueLoaderDecorator(final Loader l, final String defaultValue) {
        this.l = l;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        if (state.getElementDefaultValue() == null) {
            state.setElementDefaultValue(this.defaultValue);
        }
        state.setLoader(this.l);
        this.l.startElement(state, ea);
    }
}
