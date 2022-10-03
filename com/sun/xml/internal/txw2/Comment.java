package com.sun.xml.internal.txw2;

final class Comment extends Content
{
    private final StringBuilder buffer;
    
    public Comment(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        document.writeValue(obj, nsResolver, this.buffer = new StringBuilder());
    }
    
    @Override
    boolean concludesPendingStartTag() {
        return false;
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onComment(this.buffer);
    }
}
