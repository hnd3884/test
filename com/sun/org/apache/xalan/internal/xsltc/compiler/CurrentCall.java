package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;

final class CurrentCall extends FunctionCall
{
    public CurrentCall(final QName fname) {
        super(fname);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        methodGen.getInstructionList().append(methodGen.loadCurrentNode());
    }
}
