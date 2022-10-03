package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Long extends DefaultTransducedAccessor
{
    @Override
    public String print(final Object o) {
        return DatatypeConverterImpl._printLong(((Bean)o).get_long());
    }
    
    @Override
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).set_long(DatatypeConverterImpl._parseLong(lexical));
    }
    
    @Override
    public boolean hasValue(final Object o) {
        return true;
    }
}
