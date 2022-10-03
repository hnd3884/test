package org.dom4j.tree;

import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Comment;

public class FlyweightComment extends AbstractComment implements Comment
{
    protected String text;
    
    public FlyweightComment(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultComment(parent, this.getText());
    }
}
