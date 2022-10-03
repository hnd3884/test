package org.apache.xerces.impl;

public class XML11NamespaceBinder extends XMLNamespaceBinder
{
    protected boolean prefixBoundToNullURI(final String s, final String s2) {
        return false;
    }
}
