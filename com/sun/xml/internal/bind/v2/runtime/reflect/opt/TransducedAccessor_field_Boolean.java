package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Boolean extends DefaultTransducedAccessor
{
    @Override
    public String print(final Object o) {
        return DatatypeConverterImpl._printBoolean(((Bean)o).f_boolean);
    }
    
    @Override
    public void parse(final Object o, final CharSequence lexical) {
        final Boolean b = DatatypeConverterImpl._parseBoolean(lexical);
        if (b != null) {
            ((Bean)o).f_boolean = b;
        }
    }
    
    @Override
    public boolean hasValue(final Object o) {
        return true;
    }
}
