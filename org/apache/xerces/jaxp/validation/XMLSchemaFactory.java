package org.apache.xerces.jaxp.validation;

import org.apache.xerces.impl.Constants;

public final class XMLSchemaFactory extends BaseSchemaFactory
{
    public XMLSchemaFactory() {
        super(Constants.W3C_XML_SCHEMA10_NS_URI);
    }
    
    public boolean isSchemaLanguageSupported(final String s) {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaLanguageNull", null));
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaLanguageLengthZero", null));
        }
        return s.equals("http://www.w3.org/2001/XMLSchema") || s.equals(Constants.W3C_XML_SCHEMA10_NS_URI);
    }
}
