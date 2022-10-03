package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

public abstract class Pattern extends Expression
{
    @Override
    public abstract Type typeCheck(final SymbolTable p0) throws TypeCheckError;
    
    @Override
    public abstract void translate(final ClassGenerator p0, final MethodGenerator p1);
    
    public abstract double getPriority();
}
