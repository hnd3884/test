package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import org.apache.xerces.util.XMLChar;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public final class CharactersImpl extends XMLEventImpl implements Characters
{
    private final String fData;
    
    public CharactersImpl(final String s, final int n, final Location location) {
        super(n, location);
        this.fData = ((s != null) ? s : "");
    }
    
    public String getData() {
        return this.fData;
    }
    
    public boolean isWhiteSpace() {
        final int n = (this.fData != null) ? this.fData.length() : 0;
        if (n == 0) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (!XMLChar.isSpace(this.fData.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isCData() {
        return 12 == this.getEventType();
    }
    
    public boolean isIgnorableWhiteSpace() {
        return 6 == this.getEventType();
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(this.fData);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
