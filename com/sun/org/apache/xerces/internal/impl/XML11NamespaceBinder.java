package com.sun.org.apache.xerces.internal.impl;

public class XML11NamespaceBinder extends XMLNamespaceBinder
{
    @Override
    protected boolean prefixBoundToNullURI(final String uri, final String localpart) {
        return false;
    }
}
