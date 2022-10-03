package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public final class NodeSortRecordFactGenerator extends ClassGenerator
{
    public NodeSortRecordFactGenerator(final String className, final String superClassName, final String fileName, final int accessFlags, final String[] interfaces, final Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
    }
    
    @Override
    public boolean isExternal() {
        return true;
    }
}
