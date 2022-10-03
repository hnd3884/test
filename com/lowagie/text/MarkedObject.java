package com.lowagie.text;

import java.util.ArrayList;
import java.util.Properties;

public class MarkedObject implements Element
{
    protected Element element;
    protected Properties markupAttributes;
    
    protected MarkedObject() {
        this.markupAttributes = new Properties();
        this.element = null;
    }
    
    public MarkedObject(final Element element) {
        this.markupAttributes = new Properties();
        this.element = element;
    }
    
    @Override
    public ArrayList getChunks() {
        return this.element.getChunks();
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this.element);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 50;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    public Properties getMarkupAttributes() {
        return this.markupAttributes;
    }
    
    public void setMarkupAttribute(final String key, final String value) {
        this.markupAttributes.setProperty(key, value);
    }
}
