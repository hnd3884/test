package com.adventnet.client.components.table.template;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class RowFillingVisitor extends NodeVisitor
{
    int i;
    String rowValue;
    
    public RowFillingVisitor(final String value) {
        this.i = 1;
        this.rowValue = "";
        this.rowValue = value;
    }
    
    public void visitTag(final Tag tag) {
        final String temp = tag.getAttribute("mc:value");
        if (temp != null) {
            if (temp.equals("true")) {
                try {
                    final String replaceWith = this.rowValue;
                    tag.setChildren(new NodeList((Node)new TextNode(replaceWith)));
                }
                catch (final Exception e) {
                    System.out.println("Error in filling row contents ");
                }
            }
            ++this.i;
        }
    }
}
