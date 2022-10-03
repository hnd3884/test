package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;

public final class EntityDeclarationImpl extends XMLEventImpl implements EntityDeclaration
{
    private final String fPublicId;
    private final String fSystemId;
    private final String fName;
    private final String fNotationName;
    
    public EntityDeclarationImpl(final String fPublicId, final String fSystemId, final String fName, final String fNotationName, final Location location) {
        super(15, location);
        this.fPublicId = fPublicId;
        this.fSystemId = fSystemId;
        this.fName = fName;
        this.fNotationName = fNotationName;
    }
    
    public String getPublicId() {
        return this.fPublicId;
    }
    
    public String getSystemId() {
        return this.fSystemId;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNotationName() {
        return this.fNotationName;
    }
    
    public String getReplacementText() {
        return null;
    }
    
    public String getBaseURI() {
        return null;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(this.fName);
            if (this.fPublicId != null) {
                writer.write(" PUBLIC \"");
                writer.write(this.fPublicId);
                writer.write("\" \"");
                writer.write(this.fSystemId);
                writer.write(34);
            }
            else {
                writer.write(" SYSTEM \"");
                writer.write(this.fSystemId);
                writer.write(34);
            }
            if (this.fNotationName != null) {
                writer.write(" NDATA ");
                writer.write(this.fNotationName);
            }
            writer.write(62);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
