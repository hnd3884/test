package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Integer extends DefaultTransducedAccessor
{
    @Override
    public String print(final Object o) {
        return DatatypeConverterImpl._printInt(((Bean)o).get_int());
    }
    
    @Override
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).set_int(DatatypeConverterImpl._parseInt(lexical));
    }
    
    @Override
    public boolean hasValue(final Object o) {
        return true;
    }
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final Object o, final String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
        w.leafElement(tagName, ((Bean)o).get_int(), fieldName);
    }
}
