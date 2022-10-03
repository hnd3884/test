package com.sun.xml.internal.bind.v2.runtime.output;

import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;

public final class StAXExStreamWriterOutput extends XMLStreamWriterOutput
{
    private final XMLStreamWriterEx out;
    
    public StAXExStreamWriterOutput(final XMLStreamWriterEx out) {
        super(out, NoEscapeHandler.theInstance);
        this.out = out;
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        if (!(value instanceof Base64Data)) {
            this.out.writeCharacters(value.toString());
        }
        else {
            final Base64Data v = (Base64Data)value;
            this.out.writeBinary(v.getDataHandler());
        }
    }
}
