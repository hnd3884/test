package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class NodeSortRecordGenerator extends ClassGenerator
{
    private static final int TRANSLET_INDEX = 4;
    private final Instruction _aloadTranslet;
    
    public NodeSortRecordGenerator(final String className, final String superClassName, final String fileName, final int accessFlags, final String[] interfaces, final Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
        this._aloadTranslet = new ALOAD(4);
    }
    
    @Override
    public Instruction loadTranslet() {
        return this._aloadTranslet;
    }
    
    @Override
    public boolean isExternal() {
        return true;
    }
}
