package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract class ProxyLoader extends Loader
{
    public ProxyLoader() {
        super(false);
    }
    
    @Override
    public final void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final Loader loader = this.selectLoader(state, ea);
        state.setLoader(loader);
        loader.startElement(state, ea);
    }
    
    protected abstract Loader selectLoader(final UnmarshallingContext.State p0, final TagName p1) throws SAXException;
    
    @Override
    public final void leaveElement(final UnmarshallingContext.State state, final TagName ea) {
        throw new IllegalStateException();
    }
}
