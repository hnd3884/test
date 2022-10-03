package org.apache.axiom.om.ds;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractPullOMDataSource extends AbstractOMDataSource
{
    public final boolean isDestructiveWrite() {
        return this.isDestructiveRead();
    }
    
    public final void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        final StreamingOMSerializer serializer = new StreamingOMSerializer();
        final XMLStreamReader reader = this.getReader();
        try {
            serializer.serialize(reader, xmlWriter);
        }
        finally {
            reader.close();
        }
    }
}
