package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.mutable.MComment;

public final class CommentImpl extends ElementImpl implements MComment
{
    private String mText;
    
    CommentImpl(final ElementImpl parent) {
        super(parent);
        this.mText = null;
    }
    
    @Override
    public void setText(final String text) {
        this.mText = text;
    }
    
    @Override
    public String getText() {
        return (this.mText == null) ? "" : this.mText;
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getQualifiedName() {
        return this.getParent().getQualifiedName() + ".{comment}";
    }
}
