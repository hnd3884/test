package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class UnaryOpExpr extends Expression
{
    private Expression _left;
    
    public UnaryOpExpr(final Expression left) {
        (this._left = left).setParent(this);
    }
    
    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall();
    }
    
    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall();
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type tleft = this._left.typeCheck(stable);
        final MethodType ptype = this.lookupPrimop(stable, "u-", new MethodType(Type.Void, tleft));
        if (ptype != null) {
            final Type arg1 = ptype.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            return this._type = ptype.resultType();
        }
        throw new TypeCheckError(this);
    }
    
    @Override
    public String toString() {
        return "u-(" + this._left + ')';
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        this._left.translate(classGen, methodGen);
        il.append(this._type.NEG());
    }
}
