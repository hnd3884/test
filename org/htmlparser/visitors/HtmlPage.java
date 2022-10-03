package org.htmlparser.visitors;

import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

public class HtmlPage extends NodeVisitor
{
    private String title;
    private NodeList nodesInBody;
    private NodeList tables;
    
    public HtmlPage(final Parser parser) {
        super(true);
        this.title = "";
        this.nodesInBody = new NodeList();
        this.tables = new NodeList();
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public void visitTag(final Tag tag) {
        if (this.isTable(tag)) {
            this.tables.add(tag);
        }
        else if (this.isBodyTag(tag)) {
            this.nodesInBody = tag.getChildren();
        }
        else if (this.isTitleTag(tag)) {
            this.title = ((TitleTag)tag).getTitle();
        }
    }
    
    private boolean isTable(final Tag tag) {
        return tag instanceof TableTag;
    }
    
    private boolean isBodyTag(final Tag tag) {
        return tag instanceof BodyTag;
    }
    
    private boolean isTitleTag(final Tag tag) {
        return tag instanceof TitleTag;
    }
    
    public NodeList getBody() {
        return this.nodesInBody;
    }
    
    public TableTag[] getTables() {
        final TableTag[] tableArr = new TableTag[this.tables.size()];
        this.tables.copyToNodeArray(tableArr);
        return tableArr;
    }
}
