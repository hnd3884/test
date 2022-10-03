package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DTDReader;

abstract class AbstractDTDReader implements DTDReader
{
    private final XMLStreamReader reader;
    private String rootName;
    private String publicId;
    private String systemId;
    
    AbstractDTDReader(final XMLStreamReader reader) {
        this.reader = reader;
    }
    
    protected abstract String getDocumentTypeDeclaration(final XMLStreamReader p0);
    
    private void parse() {
        if (this.rootName == null) {
            try {
                final Scanner scanner = new Scanner(this.getDocumentTypeDeclaration(this.reader));
                scanner.expect("<!DOCTYPE");
                scanner.skipSpace();
                this.rootName = scanner.getName();
                scanner.skipSpace();
                switch (scanner.peek()) {
                    case 83: {
                        scanner.expect("SYSTEM");
                        scanner.skipSpace();
                        this.systemId = scanner.getQuotedString();
                        break;
                    }
                    case 80: {
                        scanner.expect("PUBLIC");
                        scanner.skipSpace();
                        this.publicId = scanner.getQuotedString();
                        scanner.skipSpace();
                        this.systemId = scanner.getQuotedString();
                        break;
                    }
                }
            }
            catch (final XMLStreamException ex) {
                throw new RuntimeException("Unable to parse DOCTYPE declaration", ex);
            }
        }
    }
    
    public String getRootName() {
        this.parse();
        return this.rootName;
    }
    
    public String getPublicId() {
        this.parse();
        return this.publicId;
    }
    
    public String getSystemId() {
        this.parse();
        return this.systemId;
    }
}
