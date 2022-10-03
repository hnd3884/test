package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;

public final class EndElementImpl extends ElementImpl implements EndElement
{
    public EndElementImpl(final QName qName, final Iterator iterator, final Location location) {
        super(qName, false, iterator, location);
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            final QName name = this.getName();
            final String prefix = name.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(name.getLocalPart());
            writer.write(62);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
