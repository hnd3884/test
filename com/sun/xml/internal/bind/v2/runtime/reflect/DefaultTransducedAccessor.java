package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;

public abstract class DefaultTransducedAccessor<T> extends TransducedAccessor<T>
{
    @Override
    public abstract String print(final T p0) throws AccessorException, SAXException;
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
        w.leafElement(tagName, this.print(o), fieldName);
    }
    
    @Override
    public void writeText(final XMLSerializer w, final T o, final String fieldName) throws AccessorException, SAXException, IOException, XMLStreamException {
        w.text(this.print(o), fieldName);
    }
}
