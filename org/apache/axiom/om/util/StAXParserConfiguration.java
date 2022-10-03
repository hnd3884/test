package org.apache.axiom.om.util;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLResolver;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import javax.xml.stream.XMLInputFactory;

public interface StAXParserConfiguration
{
    public static final StAXParserConfiguration DEFAULT = new StAXParserConfiguration() {
        public XMLInputFactory configure(final XMLInputFactory factory, final StAXDialect dialect) {
            return factory;
        }
        
        @Override
        public String toString() {
            return "DEFAULT";
        }
    };
    public static final StAXParserConfiguration STANDALONE = new StAXParserConfiguration() {
        public XMLInputFactory configure(final XMLInputFactory factory, final StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
            factory.setXMLResolver(new XMLResolver() {
                public Object resolveEntity(final String publicID, final String systemID, final String baseURI, final String namespace) throws XMLStreamException {
                    return new ByteArrayInputStream(new byte[0]);
                }
            });
            return factory;
        }
        
        @Override
        public String toString() {
            return "STANDALONE";
        }
    };
    public static final StAXParserConfiguration NON_COALESCING = new StAXParserConfiguration() {
        public XMLInputFactory configure(final XMLInputFactory factory, final StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
            return factory;
        }
        
        @Override
        public String toString() {
            return "NON_COALESCING";
        }
    };
    public static final StAXParserConfiguration PRESERVE_CDATA_SECTIONS = new StAXParserConfiguration() {
        public XMLInputFactory configure(final XMLInputFactory factory, final StAXDialect dialect) {
            return dialect.enableCDataReporting(factory);
        }
        
        @Override
        public String toString() {
            return "PRESERVE_CDATA_SECTIONS";
        }
    };
    public static final StAXParserConfiguration SOAP = new StAXParserConfiguration() {
        public XMLInputFactory configure(final XMLInputFactory factory, final StAXDialect dialect) {
            return dialect.disallowDoctypeDecl(factory);
        }
        
        @Override
        public String toString() {
            return "SOAP";
        }
    };
    
    XMLInputFactory configure(final XMLInputFactory p0, final StAXDialect p1);
}
