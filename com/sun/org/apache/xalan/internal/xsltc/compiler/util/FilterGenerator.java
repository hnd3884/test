package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class FilterGenerator extends ClassGenerator
{
    private static int TRANSLET_INDEX;
    private final Instruction _aloadTranslet;
    
    public FilterGenerator(final String className, final String superClassName, final String fileName, final int accessFlags, final String[] interfaces, final Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
        this._aloadTranslet = new ALOAD(FilterGenerator.TRANSLET_INDEX);
    }
    
    @Override
    public final Instruction loadTranslet() {
        return this._aloadTranslet;
    }
    
    @Override
    public boolean isExternal() {
        return true;
    }
    
    static {
        FilterGenerator.TRANSLET_INDEX = 5;
    }
}
