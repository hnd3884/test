package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class NodeCounterGenerator extends ClassGenerator
{
    private Instruction _aloadTranslet;
    
    public NodeCounterGenerator(final String className, final String superClassName, final String fileName, final int accessFlags, final String[] interfaces, final Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
    }
    
    public void setTransletIndex(final int index) {
        this._aloadTranslet = new ALOAD(index);
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
