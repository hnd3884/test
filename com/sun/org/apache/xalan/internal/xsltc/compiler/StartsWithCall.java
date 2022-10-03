package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class StartsWithCall extends FunctionCall
{
    private Expression _base;
    private Expression _token;
    
    public StartsWithCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._base = null;
        this._token = null;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this.argumentCount() != 2) {
            final ErrorMsg err = new ErrorMsg("ILLEGAL_ARG_ERR", this.getName(), this);
            throw new TypeCheckError(err);
        }
        this._base = this.argument(0);
        final Type baseType = this._base.typeCheck(stable);
        if (baseType != Type.String) {
            this._base = new CastExpr(this._base, Type.String);
        }
        this._token = this.argument(1);
        final Type tokenType = this._token.typeCheck(stable);
        if (tokenType != Type.String) {
            this._token = new CastExpr(this._token, Type.String);
        }
        return this._type = Type.Boolean;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        this._base.translate(classGen, methodGen);
        this._token.translate(classGen, methodGen);
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.String", "startsWith", "(Ljava/lang/String;)Z")));
    }
}
