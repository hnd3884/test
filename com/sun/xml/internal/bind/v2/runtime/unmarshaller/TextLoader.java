package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;

public class TextLoader extends Loader
{
    private final Transducer xducer;
    
    public TextLoader(final Transducer xducer) {
        super(true);
        this.xducer = xducer;
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            state.setTarget(this.xducer.parse(text));
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (final RuntimeException e2) {
            Loader.handleParseConversionException(state, e2);
        }
    }
}
