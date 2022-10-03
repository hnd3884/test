package org.dom4j.tree;

import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.CDATA;

public class FlyweightCDATA extends AbstractCDATA implements CDATA
{
    protected String text;
    
    public FlyweightCDATA(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultCDATA(parent, this.getText());
    }
}
