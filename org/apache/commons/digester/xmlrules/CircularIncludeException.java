package org.apache.commons.digester.xmlrules;

public class CircularIncludeException extends XmlLoadException
{
    public CircularIncludeException(final String fileName) {
        super("Circular file inclusion detected for file: " + fileName);
    }
}
