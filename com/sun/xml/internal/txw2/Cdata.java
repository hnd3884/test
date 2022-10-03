package com.sun.xml.internal.txw2;

final class Cdata extends Text
{
    Cdata(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        super(document, nsResolver, obj);
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onCdata(this.buffer);
    }
}
