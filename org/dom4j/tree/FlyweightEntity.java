package org.dom4j.tree;

import org.dom4j.Node;
import org.dom4j.Element;

public class FlyweightEntity extends AbstractEntity
{
    protected String name;
    protected String text;
    
    protected FlyweightEntity() {
    }
    
    public FlyweightEntity(final String name) {
        this.name = name;
    }
    
    public FlyweightEntity(final String name, final String text) {
        this.name = name;
        this.text = text;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        if (this.text != null) {
            this.text = text;
            return;
        }
        throw new UnsupportedOperationException("This Entity is read-only. It cannot be modified");
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultEntity(parent, this.getName(), this.getText());
    }
}
