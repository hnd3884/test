package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import java.util.Hashtable;

public class XMLAssertAdapter extends XSAssertionXPath2ValueImpl implements XMLAssertHandler
{
    private Hashtable properties;
    
    public XMLAssertAdapter() {
        this.properties = null;
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws Exception {
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws Exception {
    }
    
    public void characters(final XMLString xmlString) {
    }
    
    public void comment(final XMLString xmlString) {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString) {
    }
    
    public void setProperty(final String s, final Object o) throws IllegalArgumentException {
        if (o == null) {
            if (this.properties != null) {
                this.properties.remove(s);
            }
            return;
        }
        if (this.properties == null) {
            this.properties = new Hashtable();
        }
        this.properties.put(s, o);
    }
    
    public Object getProperty(final String s) throws IllegalArgumentException {
        if (this.properties == null) {
            return null;
        }
        final Object value = this.properties.get(s);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("the property " + s + " is not set. can't find it's value");
    }
}
