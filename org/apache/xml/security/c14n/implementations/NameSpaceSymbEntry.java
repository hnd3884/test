package org.apache.xml.security.c14n.implementations;

import org.w3c.dom.Attr;

class NameSpaceSymbEntry implements Cloneable
{
    int level;
    String prefix;
    String uri;
    String lastrendered;
    boolean rendered;
    Attr n;
    
    NameSpaceSymbEntry(final String uri, final Attr n, final boolean rendered, final String prefix) {
        this.level = 0;
        this.lastrendered = null;
        this.rendered = false;
        this.uri = uri;
        this.rendered = rendered;
        this.n = n;
        this.prefix = prefix;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
