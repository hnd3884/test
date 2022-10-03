package com.sun.xml.internal.txw2;

final class EndDocument extends Content
{
    @Override
    boolean concludesPendingStartTag() {
        return true;
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onEndDocument();
    }
}
