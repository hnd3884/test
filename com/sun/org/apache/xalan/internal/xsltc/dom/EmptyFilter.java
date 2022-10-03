package com.sun.org.apache.xalan.internal.xsltc.dom;

public final class EmptyFilter implements Filter
{
    @Override
    public boolean test(final int node) {
        return true;
    }
}
