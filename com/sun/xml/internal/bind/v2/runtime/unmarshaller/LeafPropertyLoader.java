package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;

public class LeafPropertyLoader extends Loader
{
    private final TransducedAccessor xacc;
    
    public LeafPropertyLoader(final TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            this.xacc.parse(state.getPrev().getTarget(), text);
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (final RuntimeException e2) {
            Loader.handleParseConversionException(state, e2);
        }
    }
}
