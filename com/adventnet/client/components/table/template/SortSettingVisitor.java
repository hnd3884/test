package com.adventnet.client.components.table.template;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class SortSettingVisitor extends NodeVisitor
{
    int count;
    String processingColumn;
    boolean isSortEnabled;
    
    public SortSettingVisitor(final String processingColumn, final boolean isSortEnabled) {
        this.count = 0;
        this.processingColumn = null;
        this.processingColumn = processingColumn;
        this.isSortEnabled = isSortEnabled;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute("mc:columnName") != null) {
            if (tag.getAttribute("mc:columnName").equals("DefaultColumn")) {
                String replaceWith;
                if (this.isSortEnabled) {
                    replaceWith = "<a href=# onclick=\"return sortData_template(this)\"><span mc:value=\"true\" class=\"tableHeader\"> </span></a><input mc:sortButton=\"true\"  type=\"button\" class=\"sortButtonASC\" onclick=\"return sortData_template(this)\"/>";
                }
                else {
                    replaceWith = "<div mc:value=\"true\" class=\"tableHeader\"> </div>";
                }
                tag.setChildren(new NodeList((Node)new TextNode(replaceWith)));
            }
            ++this.count;
        }
    }
}
