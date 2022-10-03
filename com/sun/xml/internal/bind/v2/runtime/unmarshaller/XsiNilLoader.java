package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import javax.xml.namespace.QName;
import java.util.Collection;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.DatatypeConverterImpl;

public class XsiNilLoader extends ProxyLoader
{
    private final Loader defaultLoader;
    
    public XsiNilLoader(final Loader defaultLoader) {
        this.defaultLoader = defaultLoader;
        assert defaultLoader != null;
    }
    
    @Override
    protected Loader selectLoader(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final int idx = ea.atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "nil");
        if (idx != -1) {
            final Boolean b = DatatypeConverterImpl._parseBoolean(ea.atts.getValue(idx));
            if (b != null && b) {
                this.onNil(state);
                final boolean hasOtherAttributes = ea.atts.getLength() - 1 > 0;
                if (!hasOtherAttributes || !(state.getPrev().getTarget() instanceof JAXBElement)) {
                    return Discarder.INSTANCE;
                }
            }
        }
        return this.defaultLoader;
    }
    
    @Override
    public Collection<QName> getExpectedChildElements() {
        return this.defaultLoader.getExpectedChildElements();
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        return this.defaultLoader.getExpectedAttributes();
    }
    
    protected void onNil(final UnmarshallingContext.State state) throws SAXException {
    }
    
    public static final class Single extends XsiNilLoader
    {
        private final Accessor acc;
        
        public Single(final Loader l, final Accessor acc) {
            super(l);
            this.acc = acc;
        }
        
        @Override
        protected void onNil(final UnmarshallingContext.State state) throws SAXException {
            try {
                this.acc.set(state.getPrev().getTarget(), null);
                state.getPrev().setNil(true);
            }
            catch (final AccessorException e) {
                Loader.handleGenericException(e, true);
            }
        }
    }
    
    public static final class Array extends XsiNilLoader
    {
        public Array(final Loader core) {
            super(core);
        }
        
        @Override
        protected void onNil(final UnmarshallingContext.State state) {
            state.setTarget(null);
        }
    }
}
