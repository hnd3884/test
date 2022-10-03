package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class DefaultClassSettingVisitor extends NodeVisitor
{
    int count;
    String cssClassName;
    
    public DefaultClassSettingVisitor(final String cssClassName) {
        this.count = 0;
        this.cssClassName = "";
        this.cssClassName = cssClassName;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute("mc:column") != null && tag.getAttribute("mc:column").equals("DefaultReferenceColumn")) {
            tag.setAttribute("class", this.cssClassName);
        }
    }
}
