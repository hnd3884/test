package org.apache.xerces.jaxp.validation;

import org.apache.xerces.impl.Constants;

public final class XMLSchema11Factory extends BaseSchemaFactory
{
    public XMLSchema11Factory() {
        super(Constants.W3C_XML_SCHEMA11_NS_URI);
    }
    
    public boolean isSchemaLanguageSupported(final String s) {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaLanguageNull", null));
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaLanguageLengthZero", null));
        }
        return s.equals(Constants.W3C_XML_SCHEMA11_NS_URI);
    }
}
