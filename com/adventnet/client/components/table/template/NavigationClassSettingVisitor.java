package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class NavigationClassSettingVisitor extends NodeVisitor
{
    String checkAttributeName;
    String checkAttributeValue;
    String classVal;
    
    public NavigationClassSettingVisitor(final String checkAttributeName, final String checkAttributeValue, final String classVal) {
        this.checkAttributeName = checkAttributeName;
        this.checkAttributeValue = checkAttributeValue;
        this.classVal = classVal;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute(this.checkAttributeName).equalsIgnoreCase(this.checkAttributeValue)) {
            tag.setAttribute("class", tag.getAttribute("class") + this.classVal);
        }
    }
}
