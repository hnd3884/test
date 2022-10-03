package org.apache.axiom.om.util;

import org.apache.axiom.util.stax.dialect.StAXDialect;
import javax.xml.stream.XMLOutputFactory;

public interface StAXWriterConfiguration
{
    public static final StAXWriterConfiguration DEFAULT = new StAXWriterConfiguration() {
        public XMLOutputFactory configure(final XMLOutputFactory factory, final StAXDialect dialect) {
            return factory;
        }
        
        @Override
        public String toString() {
            return "DEFAULT";
        }
    };
    
    XMLOutputFactory configure(final XMLOutputFactory p0, final StAXDialect p1);
}
