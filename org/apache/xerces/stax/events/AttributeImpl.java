package org.apache.xerces.stax.events;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeImpl extends XMLEventImpl implements Attribute
{
    private final boolean fIsSpecified;
    private final QName fName;
    private final String fValue;
    private final String fDtdType;
    
    public AttributeImpl(final QName qName, final String s, final String s2, final boolean b, final Location location) {
        this(10, qName, s, s2, b, location);
    }
    
    protected AttributeImpl(final int n, final QName fName, final String fValue, final String fDtdType, final boolean fIsSpecified, final Location location) {
        super(n, location);
        this.fName = fName;
        this.fValue = fValue;
        this.fDtdType = fDtdType;
        this.fIsSpecified = fIsSpecified;
    }
    
    public final QName getName() {
        return this.fName;
    }
    
    public final String getValue() {
        return this.fValue;
    }
    
    public final String getDTDType() {
        return this.fDtdType;
    }
    
    public final boolean isSpecified() {
        return this.fIsSpecified;
    }
    
    public final void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            final String prefix = this.fName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.fName.getLocalPart());
            writer.write("=\"");
            writer.write(this.fValue);
            writer.write(34);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
