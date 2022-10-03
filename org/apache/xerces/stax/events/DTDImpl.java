package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.events.DTD;

public final class DTDImpl extends XMLEventImpl implements DTD
{
    private final String fDTD;
    
    public DTDImpl(final String s, final Location location) {
        super(11, location);
        this.fDTD = ((s != null) ? s : null);
    }
    
    public String getDocumentTypeDeclaration() {
        return this.fDTD;
    }
    
    public Object getProcessedDTD() {
        return null;
    }
    
    public List getNotations() {
        return Collections.EMPTY_LIST;
    }
    
    public List getEntities() {
        return Collections.EMPTY_LIST;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(this.fDTD);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
