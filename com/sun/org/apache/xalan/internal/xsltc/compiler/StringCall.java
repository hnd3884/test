package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class StringCall extends FunctionCall
{
    public StringCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final int argc = this.argumentCount();
        if (argc > 1) {
            final ErrorMsg err = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(err);
        }
        if (argc > 0) {
            this.argument().typeCheck(stable);
        }
        return this._type = Type.String;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        Type targ;
        if (this.argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
            targ = Type.Node;
        }
        else {
            final Expression arg = this.argument();
            arg.translate(classGen, methodGen);
            arg.startIterator(classGen, methodGen);
            targ = arg.getType();
        }
        if (!targ.identicalTo(Type.String)) {
            targ.translateTo(classGen, methodGen, Type.String);
        }
    }
}
