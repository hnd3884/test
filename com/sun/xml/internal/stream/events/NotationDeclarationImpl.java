package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import javax.xml.stream.events.NotationDeclaration;

public class NotationDeclarationImpl extends DummyEvent implements NotationDeclaration
{
    String fName;
    String fPublicId;
    String fSystemId;
    
    public NotationDeclarationImpl() {
        this.fName = null;
        this.fPublicId = null;
        this.fSystemId = null;
        this.setEventType(14);
    }
    
    public NotationDeclarationImpl(final String name, final String publicId, final String systemId) {
        this.fName = null;
        this.fPublicId = null;
        this.fSystemId = null;
        this.fName = name;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        this.setEventType(14);
    }
    
    public NotationDeclarationImpl(final XMLNotationDecl notation) {
        this.fName = null;
        this.fPublicId = null;
        this.fSystemId = null;
        this.fName = notation.name;
        this.fPublicId = notation.publicId;
        this.fSystemId = notation.systemId;
        this.setEventType(14);
    }
    
    @Override
    public String getName() {
        return this.fName;
    }
    
    @Override
    public String getPublicId() {
        return this.fPublicId;
    }
    
    @Override
    public String getSystemId() {
        return this.fSystemId;
    }
    
    void setPublicId(final String publicId) {
        this.fPublicId = publicId;
    }
    
    void setSystemId(final String systemId) {
        this.fSystemId = systemId;
    }
    
    void setName(final String name) {
        this.fName = name;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write("<!NOTATION ");
        writer.write(this.getName());
        if (this.fPublicId != null) {
            writer.write(" PUBLIC \"");
            writer.write(this.fPublicId);
            writer.write("\"");
        }
        else if (this.fSystemId != null) {
            writer.write(" SYSTEM");
            writer.write(" \"");
            writer.write(this.fSystemId);
            writer.write("\"");
        }
        writer.write(62);
    }
}
