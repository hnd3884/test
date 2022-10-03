package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public final class StartDocumentImpl extends XMLEventImpl implements StartDocument
{
    private final String fCharEncoding;
    private final boolean fEncodingSet;
    private final String fVersion;
    private final boolean fIsStandalone;
    private final boolean fStandaloneSet;
    
    public StartDocumentImpl(final String fCharEncoding, final boolean fEncodingSet, final boolean fIsStandalone, final boolean fStandaloneSet, final String fVersion, final Location location) {
        super(7, location);
        this.fCharEncoding = fCharEncoding;
        this.fEncodingSet = fEncodingSet;
        this.fIsStandalone = fIsStandalone;
        this.fStandaloneSet = fStandaloneSet;
        this.fVersion = fVersion;
    }
    
    public String getSystemId() {
        return this.getLocation().getSystemId();
    }
    
    public String getCharacterEncodingScheme() {
        return this.fCharEncoding;
    }
    
    public boolean encodingSet() {
        return this.fEncodingSet;
    }
    
    public boolean isStandalone() {
        return this.fIsStandalone;
    }
    
    public boolean standaloneSet() {
        return this.fStandaloneSet;
    }
    
    public String getVersion() {
        return this.fVersion;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<?xml version=\"");
            writer.write((this.fVersion != null && this.fVersion.length() > 0) ? this.fVersion : "1.0");
            writer.write(34);
            if (this.encodingSet()) {
                writer.write(" encoding=\"");
                writer.write(this.fCharEncoding);
                writer.write(34);
            }
            if (this.standaloneSet()) {
                writer.write(" standalone=\"");
                writer.write(this.fIsStandalone ? "yes" : "no");
                writer.write(34);
            }
            writer.write("?>");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
