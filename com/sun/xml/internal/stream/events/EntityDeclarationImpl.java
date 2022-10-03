package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl extends DummyEvent implements EntityDeclaration
{
    private XMLResourceIdentifier fXMLResourceIdentifier;
    private String fEntityName;
    private String fReplacementText;
    private String fNotationName;
    
    public EntityDeclarationImpl() {
        this.init();
    }
    
    public EntityDeclarationImpl(final String entityName, final String replacementText) {
        this(entityName, replacementText, null);
    }
    
    public EntityDeclarationImpl(final String entityName, final String replacementText, final XMLResourceIdentifier resourceIdentifier) {
        this.init();
        this.fEntityName = entityName;
        this.fReplacementText = replacementText;
        this.fXMLResourceIdentifier = resourceIdentifier;
    }
    
    public void setEntityName(final String entityName) {
        this.fEntityName = entityName;
    }
    
    public String getEntityName() {
        return this.fEntityName;
    }
    
    public void setEntityReplacementText(final String replacementText) {
        this.fReplacementText = replacementText;
    }
    
    public void setXMLResourceIdentifier(final XMLResourceIdentifier resourceIdentifier) {
        this.fXMLResourceIdentifier = resourceIdentifier;
    }
    
    public XMLResourceIdentifier getXMLResourceIdentifier() {
        return this.fXMLResourceIdentifier;
    }
    
    @Override
    public String getSystemId() {
        if (this.fXMLResourceIdentifier != null) {
            return this.fXMLResourceIdentifier.getLiteralSystemId();
        }
        return null;
    }
    
    @Override
    public String getPublicId() {
        if (this.fXMLResourceIdentifier != null) {
            return this.fXMLResourceIdentifier.getPublicId();
        }
        return null;
    }
    
    @Override
    public String getBaseURI() {
        if (this.fXMLResourceIdentifier != null) {
            return this.fXMLResourceIdentifier.getBaseSystemId();
        }
        return null;
    }
    
    @Override
    public String getName() {
        return this.fEntityName;
    }
    
    @Override
    public String getNotationName() {
        return this.fNotationName;
    }
    
    public void setNotationName(final String notationName) {
        this.fNotationName = notationName;
    }
    
    @Override
    public String getReplacementText() {
        return this.fReplacementText;
    }
    
    protected void init() {
        this.setEventType(15);
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write("<!ENTITY ");
        writer.write(this.fEntityName);
        if (this.fReplacementText != null) {
            writer.write(" \"");
            this.charEncode(writer, this.fReplacementText);
        }
        else {
            final String pubId = this.getPublicId();
            if (pubId != null) {
                writer.write(" PUBLIC \"");
                writer.write(pubId);
            }
            else {
                writer.write(" SYSTEM \"");
                writer.write(this.getSystemId());
            }
        }
        writer.write("\"");
        if (this.fNotationName != null) {
            writer.write(" NDATA ");
            writer.write(this.fNotationName);
        }
        writer.write(">");
    }
}
