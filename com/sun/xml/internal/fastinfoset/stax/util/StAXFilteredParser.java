package com.sun.xml.internal.fastinfoset.stax.util;

import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.StreamFilter;

public class StAXFilteredParser extends StAXParserWrapper
{
    private StreamFilter _filter;
    
    public StAXFilteredParser() {
    }
    
    public StAXFilteredParser(final XMLStreamReader reader, final StreamFilter filter) {
        super(reader);
        this._filter = filter;
    }
    
    public void setFilter(final StreamFilter filter) {
        this._filter = filter;
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (this.hasNext()) {
            return super.next();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        while (super.hasNext()) {
            if (this._filter.accept(this.getReader())) {
                return true;
            }
            super.next();
        }
        return false;
    }
}
