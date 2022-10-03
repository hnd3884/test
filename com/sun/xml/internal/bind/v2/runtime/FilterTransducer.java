package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;

public abstract class FilterTransducer<T> implements Transducer<T>
{
    protected final Transducer<T> core;
    
    protected FilterTransducer(final Transducer<T> core) {
        this.core = core;
    }
    
    @Override
    public final boolean isDefault() {
        return false;
    }
    
    @Override
    public boolean useNamespace() {
        return this.core.useNamespace();
    }
    
    @Override
    public void declareNamespace(final T o, final XMLSerializer w) throws AccessorException {
        this.core.declareNamespace(o, w);
    }
    
    @NotNull
    @Override
    public CharSequence print(@NotNull final T o) throws AccessorException {
        return this.core.print(o);
    }
    
    @Override
    public T parse(final CharSequence lexical) throws AccessorException, SAXException {
        return this.core.parse(lexical);
    }
    
    @Override
    public void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.core.writeText(w, o, fieldName);
    }
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        this.core.writeLeafElement(w, tagName, o, fieldName);
    }
    
    @Override
    public QName getTypeName(final T instance) {
        return null;
    }
}
