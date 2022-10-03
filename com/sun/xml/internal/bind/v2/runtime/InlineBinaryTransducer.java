package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.istack.internal.NotNull;

public class InlineBinaryTransducer<V> extends FilterTransducer<V>
{
    public InlineBinaryTransducer(final Transducer<V> core) {
        super(core);
    }
    
    @NotNull
    @Override
    public CharSequence print(@NotNull final V o) throws AccessorException {
        final XMLSerializer w = XMLSerializer.getInstance();
        final boolean old = w.setInlineBinaryFlag(true);
        try {
            return this.core.print(o);
        }
        finally {
            w.setInlineBinaryFlag(old);
        }
    }
    
    @Override
    public void writeText(final XMLSerializer w, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final boolean old = w.setInlineBinaryFlag(true);
        try {
            this.core.writeText(w, o, fieldName);
        }
        finally {
            w.setInlineBinaryFlag(old);
        }
    }
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final boolean old = w.setInlineBinaryFlag(true);
        try {
            this.core.writeLeafElement(w, tagName, o, fieldName);
        }
        finally {
            w.setInlineBinaryFlag(old);
        }
    }
}
