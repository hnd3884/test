package org.apache.axiom.om.impl.common.serializer.pull;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import org.apache.axiom.ext.stax.CharacterDataReader;

final class NullCharacterDataReader implements CharacterDataReader
{
    static final NullCharacterDataReader INSTANCE;
    
    static {
        INSTANCE = new NullCharacterDataReader();
    }
    
    private NullCharacterDataReader() {
    }
    
    public void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        throw new IllegalStateException();
    }
}
