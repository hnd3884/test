package org.apache.axiom.ext.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;

public interface CharacterDataReader
{
    public static final String PROPERTY = CharacterDataReader.class.getName();
    
    void writeTextTo(final Writer p0) throws XMLStreamException, IOException;
}
