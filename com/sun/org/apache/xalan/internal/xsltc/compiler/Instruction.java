package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

abstract class Instruction extends SyntaxTreeNode
{
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return this.typeCheckContents(stable);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ErrorMsg msg = new ErrorMsg("NOT_IMPLEMENTED_ERR", this.getClass(), this);
        this.getParser().reportError(2, msg);
    }
}
