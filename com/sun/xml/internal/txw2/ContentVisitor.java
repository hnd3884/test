package com.sun.xml.internal.txw2;

interface ContentVisitor
{
    void onStartDocument();
    
    void onEndDocument();
    
    void onEndTag();
    
    void onPcdata(final StringBuilder p0);
    
    void onCdata(final StringBuilder p0);
    
    void onStartTag(final String p0, final String p1, final Attribute p2, final NamespaceDecl p3);
    
    void onComment(final StringBuilder p0);
}
