package com.sun.xml.internal.txw2;

abstract class Text extends Content
{
    protected final StringBuilder buffer;
    
    protected Text(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        document.writeValue(obj, nsResolver, this.buffer = new StringBuilder());
    }
    
    @Override
    boolean concludesPendingStartTag() {
        return false;
    }
}
