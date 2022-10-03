package com.sun.xml.internal.txw2;

final class NamespaceDecl
{
    final String uri;
    boolean requirePrefix;
    final String dummyPrefix;
    final char uniqueId;
    String prefix;
    boolean declared;
    NamespaceDecl next;
    
    NamespaceDecl(final char uniqueId, final String uri, final String prefix, final boolean requirePrefix) {
        this.dummyPrefix = new StringBuilder(2).append('\0').append(uniqueId).toString();
        this.uri = uri;
        this.prefix = prefix;
        this.requirePrefix = requirePrefix;
        this.uniqueId = uniqueId;
    }
}
