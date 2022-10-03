package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.Comment;

public final class CommentImpl extends XMLEventImpl implements Comment
{
    private final String fText;
    
    public CommentImpl(final String s, final Location location) {
        super(5, location);
        this.fText = ((s != null) ? s : "");
    }
    
    public String getText() {
        return this.fText;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(this.fText);
            writer.write("-->");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
