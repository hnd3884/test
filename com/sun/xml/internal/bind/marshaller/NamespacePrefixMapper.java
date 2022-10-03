package com.sun.xml.internal.bind.marshaller;

public abstract class NamespacePrefixMapper
{
    private static final String[] EMPTY_STRING;
    
    public abstract String getPreferredPrefix(final String p0, final String p1, final boolean p2);
    
    public String[] getPreDeclaredNamespaceUris() {
        return NamespacePrefixMapper.EMPTY_STRING;
    }
    
    public String[] getPreDeclaredNamespaceUris2() {
        return NamespacePrefixMapper.EMPTY_STRING;
    }
    
    public String[] getContextualNamespaceDecls() {
        return NamespacePrefixMapper.EMPTY_STRING;
    }
    
    static {
        EMPTY_STRING = new String[0];
    }
}
