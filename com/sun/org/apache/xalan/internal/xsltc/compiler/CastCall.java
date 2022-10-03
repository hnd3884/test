package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class CastCall extends FunctionCall
{
    private String _className;
    private Expression _right;
    
    public CastCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this.argumentCount() != 2) {
            throw new TypeCheckError(new ErrorMsg("ILLEGAL_ARG_ERR", this.getName(), this));
        }
        final Expression exp = this.argument(0);
        if (!(exp instanceof LiteralExpr)) {
            throw new TypeCheckError(new ErrorMsg("NEED_LITERAL_ERR", this.getName(), this));
        }
        this._className = ((LiteralExpr)exp).getValue();
        this._type = Type.newObjectType(this._className);
        this._right = this.argument(1);
        final Type tright = this._right.typeCheck(stable);
        if (tright != Type.Reference && !(tright instanceof ObjectType)) {
            throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", tright, this._type, this));
        }
        return this._type;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        this._right.translate(classGen, methodGen);
        il.append(new CHECKCAST(cpg.addClass(this._className)));
    }
}
