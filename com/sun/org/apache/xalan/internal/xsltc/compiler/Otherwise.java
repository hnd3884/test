package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Otherwise extends Instruction
{
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Otherwise");
        this.indent(indent + 4);
        this.displayContents(indent + 4);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final Parser parser = this.getParser();
        final ErrorMsg err = new ErrorMsg("STRAY_OTHERWISE_ERR", this);
        parser.reportError(3, err);
    }
}
