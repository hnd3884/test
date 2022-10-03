package org.htmlparser.visitors;

import org.htmlparser.Node;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.Remark;

public class UrlModifyingVisitor extends NodeVisitor
{
    private String linkPrefix;
    private StringBuffer modifiedResult;
    
    public UrlModifyingVisitor(final String linkPrefix) {
        super(true, true);
        this.linkPrefix = linkPrefix;
        this.modifiedResult = new StringBuffer();
    }
    
    public void visitRemarkNode(final Remark remarkNode) {
        this.modifiedResult.append(remarkNode.toHtml());
    }
    
    public void visitStringNode(final Text stringNode) {
        this.modifiedResult.append(stringNode.toHtml());
    }
    
    public void visitTag(final Tag tag) {
        if (tag instanceof LinkTag) {
            ((LinkTag)tag).setLink(this.linkPrefix + ((LinkTag)tag).getLink());
        }
        else if (tag instanceof ImageTag) {
            ((ImageTag)tag).setImageURL(this.linkPrefix + ((ImageTag)tag).getImageURL());
        }
        if (null == tag.getParent() && (!(tag instanceof CompositeTag) || null == ((CompositeTag)tag).getEndTag())) {
            this.modifiedResult.append(tag.toHtml());
        }
    }
    
    public void visitEndTag(final Tag tag) {
        final Node parent = tag.getParent();
        if (null == parent) {
            this.modifiedResult.append(tag.toHtml());
        }
        else if (null == parent.getParent()) {
            this.modifiedResult.append(parent.toHtml());
        }
    }
    
    public String getModifiedResult() {
        return this.modifiedResult.toString();
    }
}
