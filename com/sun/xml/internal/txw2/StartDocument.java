package com.sun.xml.internal.txw2;

final class StartDocument extends Content
{
    @Override
    boolean concludesPendingStartTag() {
        return true;
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onStartDocument();
    }
}
