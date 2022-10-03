package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public final class ProcessingInstructionImpl extends XMLEventImpl implements ProcessingInstruction
{
    private final String fTarget;
    private final String fData;
    
    public ProcessingInstructionImpl(final String s, final String fData, final Location location) {
        super(3, location);
        this.fTarget = ((s != null) ? s : "");
        this.fData = fData;
    }
    
    public String getTarget() {
        return this.fTarget;
    }
    
    public String getData() {
        return this.fData;
    }
    
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(this.fTarget);
            if (this.fData != null && this.fData.length() > 0) {
                writer.write(32);
                writer.write(this.fData);
            }
            writer.write("?>");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
