package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class BooleanCall extends FunctionCall
{
    private Expression _arg;
    
    public BooleanCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._arg = null;
        this._arg = this.argument(0);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this._arg.typeCheck(stable);
        return this._type = Type.Boolean;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._arg.translate(classGen, methodGen);
        final Type targ = this._arg.getType();
        if (!targ.identicalTo(Type.Boolean)) {
            this._arg.startIterator(classGen, methodGen);
            targ.translateTo(classGen, methodGen, Type.Boolean);
        }
    }
}
