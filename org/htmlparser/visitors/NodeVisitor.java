package org.htmlparser.visitors;

import org.htmlparser.Remark;
import org.htmlparser.Text;
import org.htmlparser.Tag;

public abstract class NodeVisitor
{
    private boolean mRecurseChildren;
    private boolean mRecurseSelf;
    
    public NodeVisitor() {
        this(true);
    }
    
    public NodeVisitor(final boolean recurseChildren) {
        this(recurseChildren, true);
    }
    
    public NodeVisitor(final boolean recurseChildren, final boolean recurseSelf) {
        this.mRecurseChildren = recurseChildren;
        this.mRecurseSelf = recurseSelf;
    }
    
    public void beginParsing() {
    }
    
    public void visitTag(final Tag tag) {
    }
    
    public void visitEndTag(final Tag tag) {
    }
    
    public void visitStringNode(final Text string) {
    }
    
    public void visitRemarkNode(final Remark remark) {
    }
    
    public void finishedParsing() {
    }
    
    public boolean shouldRecurseChildren() {
        return this.mRecurseChildren;
    }
    
    public boolean shouldRecurseSelf() {
        return this.mRecurseSelf;
    }
}
