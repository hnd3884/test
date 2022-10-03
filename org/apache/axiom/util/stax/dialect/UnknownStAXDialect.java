package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;

class UnknownStAXDialect implements StAXDialect
{
    public static final UnknownStAXDialect INSTANCE;
    
    public String getName() {
        return "Unknown";
    }
    
    public XMLInputFactory enableCDataReporting(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        return factory;
    }
    
    public XMLInputFactory disallowDoctypeDecl(final XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }
    
    public XMLInputFactory makeThreadSafe(final XMLInputFactory factory) {
        return factory;
    }
    
    public XMLOutputFactory makeThreadSafe(final XMLOutputFactory factory) {
        return factory;
    }
    
    public XMLInputFactory normalize(final XMLInputFactory factory) {
        return factory;
    }
    
    public XMLOutputFactory normalize(final XMLOutputFactory factory) {
        return factory;
    }
    
    static {
        INSTANCE = new UnknownStAXDialect();
    }
}
