package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class IdPattern extends IdKeyPattern
{
    public IdPattern(final String id) {
        super("##id", id);
    }
}
