package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;

public class ValuePropertyLoader extends Loader
{
    private final TransducedAccessor xacc;
    
    public ValuePropertyLoader(final TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            this.xacc.parse(state.getTarget(), text);
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (final RuntimeException e2) {
            if (state.getPrev() != null) {
                if (!(state.getPrev().getTarget() instanceof JAXBElement)) {
                    Loader.handleParseConversionException(state, e2);
                }
            }
            else {
                Loader.handleParseConversionException(state, e2);
            }
        }
    }
}
