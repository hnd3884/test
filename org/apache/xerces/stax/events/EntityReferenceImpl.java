package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public final class EntityReferenceImpl extends XMLEventImpl implements EntityReference
{
    private final String fName;
    private final EntityDeclaration fDecl;
    
    public EntityReferenceImpl(final EntityDeclaration entityDeclaration, final Location location) {
        this((entityDeclaration != null) ? entityDeclaration.getName() : "", entityDeclaration, location);
    }
    
    public EntityReferenceImpl(final String s, final EntityDeclaration fDecl, final Location location) {
        super(9, location);
        this.fName = ((s != null) ? s : "");
        this.fDecl = fDecl;
    }
    
    public EntityDeclaration getDeclaration() {
        return this.fDecl;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(this.fName);
            writer.write(59);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
