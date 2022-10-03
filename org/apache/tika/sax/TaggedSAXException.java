package org.apache.tika.sax;

import org.xml.sax.SAXException;

public class TaggedSAXException extends SAXException
{
    private final Object tag;
    
    public TaggedSAXException(final SAXException original, final Object tag) {
        super(original.getMessage(), original);
        this.tag = tag;
    }
    
    public Object getTag() {
        return this.tag;
    }
    
    @Override
    public SAXException getCause() {
        return (SAXException)super.getCause();
    }
}
