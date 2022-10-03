package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.w3c.dom.Node;
import java.net.URL;
import javax.xml.bind.ValidationEventLocator;
import org.xml.sax.Locator;

public interface LocatorEx extends Locator
{
    ValidationEventLocator getLocation();
    
    public static final class Snapshot implements LocatorEx, ValidationEventLocator
    {
        private final int columnNumber;
        private final int lineNumber;
        private final int offset;
        private final String systemId;
        private final String publicId;
        private final URL url;
        private final Object object;
        private final Node node;
        
        public Snapshot(final LocatorEx loc) {
            this.columnNumber = loc.getColumnNumber();
            this.lineNumber = loc.getLineNumber();
            this.systemId = loc.getSystemId();
            this.publicId = loc.getPublicId();
            final ValidationEventLocator vel = loc.getLocation();
            this.offset = vel.getOffset();
            this.url = vel.getURL();
            this.object = vel.getObject();
            this.node = vel.getNode();
        }
        
        @Override
        public Object getObject() {
            return this.object;
        }
        
        @Override
        public Node getNode() {
            return this.node;
        }
        
        @Override
        public int getOffset() {
            return this.offset;
        }
        
        @Override
        public URL getURL() {
            return this.url;
        }
        
        @Override
        public int getColumnNumber() {
            return this.columnNumber;
        }
        
        @Override
        public int getLineNumber() {
            return this.lineNumber;
        }
        
        @Override
        public String getSystemId() {
            return this.systemId;
        }
        
        @Override
        public String getPublicId() {
            return this.publicId;
        }
        
        @Override
        public ValidationEventLocator getLocation() {
            return this;
        }
    }
}
