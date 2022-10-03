package com.sun.xml.internal.txw2;

final class Attribute
{
    final String nsUri;
    final String localName;
    Attribute next;
    final StringBuilder value;
    
    Attribute(final String nsUri, final String localName) {
        this.value = new StringBuilder();
        assert nsUri != null && localName != null;
        this.nsUri = nsUri;
        this.localName = localName;
    }
    
    boolean hasName(final String nsUri, final String localName) {
        return this.localName.equals(localName) && this.nsUri.equals(nsUri);
    }
}
