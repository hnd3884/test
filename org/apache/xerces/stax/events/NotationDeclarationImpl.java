package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.NotationDeclaration;

public final class NotationDeclarationImpl extends XMLEventImpl implements NotationDeclaration
{
    private final String fSystemId;
    private final String fPublicId;
    private final String fName;
    
    public NotationDeclarationImpl(final String fName, final String fPublicId, final String fSystemId, final Location location) {
        super(14, location);
        this.fName = fName;
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!NOTATION ");
            if (this.fPublicId != null) {
                writer.write("PUBLIC \"");
                writer.write(this.fPublicId);
                writer.write(34);
                if (this.fSystemId != null) {
                    writer.write(" \"");
                    writer.write(this.fSystemId);
                    writer.write(34);
                }
            }
            else {
                writer.write("SYSTEM \"");
                writer.write(this.fSystemId);
                writer.write(34);
            }
            writer.write(this.fName);
            writer.write(62);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
