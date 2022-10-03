package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import javax.activation.MimeType;

public final class MimeTypedTransducer<V> extends FilterTransducer<V>
{
    private final MimeType expectedMimeType;
    
    public MimeTypedTransducer(final Transducer<V> core, final MimeType expectedMimeType) {
        super(core);
        this.expectedMimeType = expectedMimeType;
    }
    
    @Override
    public CharSequence print(final V o) throws AccessorException {
        final XMLSerializer w = XMLSerializer.getInstance();
        final MimeType old = w.setExpectedMimeType(this.expectedMimeType);
        try {
            return this.core.print(o);
        }
        finally {
            w.setExpectedMimeType(old);
        }
    }
    
    @Override
    public void writeText(final XMLSerializer w, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final MimeType old = w.setExpectedMimeType(this.expectedMimeType);
        try {
            this.core.writeText(w, o, fieldName);
        }
        finally {
            w.setExpectedMimeType(old);
        }
    }
    
    @Override
    public void writeLeafElement(final XMLSerializer w, final Name tagName, final V o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        final MimeType old = w.setExpectedMimeType(this.expectedMimeType);
        try {
            this.core.writeLeafElement(w, tagName, o, fieldName);
        }
        finally {
            w.setExpectedMimeType(old);
        }
    }
}
