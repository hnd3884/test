package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class UnparsedEntityUriCall extends FunctionCall
{
    private Expression _entity;
    
    public UnparsedEntityUriCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._entity = this.argument();
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type entity = this._entity.typeCheck(stable);
        if (!(entity instanceof StringType)) {
            this._entity = new CastExpr(this._entity, Type.String);
        }
        return this._type = Type.String;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadDOM());
        this._entity.translate(classGen, methodGen);
        il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getUnparsedEntityURI", "(Ljava/lang/String;)Ljava/lang/String;"), 2));
    }
}
