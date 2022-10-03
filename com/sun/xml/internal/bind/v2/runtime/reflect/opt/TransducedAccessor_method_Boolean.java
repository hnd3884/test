package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Boolean extends DefaultTransducedAccessor
{
    @Override
    public String print(final Object o) {
        return DatatypeConverterImpl._printBoolean(((Bean)o).get_boolean());
    }
    
    @Override
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).set_boolean(DatatypeConverterImpl._parseBoolean(lexical));
    }
    
    @Override
    public boolean hasValue(final Object o) {
        return true;
    }
}
