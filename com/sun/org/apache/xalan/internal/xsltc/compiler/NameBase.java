package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

class NameBase extends FunctionCall
{
    private Expression _param;
    private Type _paramType;
    
    public NameBase(final QName fname) {
        super(fname);
        this._param = null;
        this._paramType = Type.Node;
    }
    
    public NameBase(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._param = null;
        this._paramType = Type.Node;
        this._param = this.argument(0);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        switch (this.argumentCount()) {
            case 0: {
                this._paramType = Type.Node;
                break;
            }
            case 1: {
                this._paramType = this._param.typeCheck(stable);
                break;
            }
            default: {
                throw new TypeCheckError(this);
            }
        }
        if (this._paramType != Type.NodeSet && this._paramType != Type.Node && this._paramType != Type.Reference) {
            throw new TypeCheckError(this);
        }
        return this._type = Type.String;
    }
    
    @Override
    public Type getType() {
        return this._type;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadDOM());
        if (this.argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
        }
        else if (this._paramType == Type.Node) {
            this._param.translate(classGen, methodGen);
        }
        else if (this._paramType == Type.Reference) {
            this._param.translate(classGen, methodGen);
            il.append(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;")));
            il.append(methodGen.nextNode());
        }
        else {
            this._param.translate(classGen, methodGen);
            this._param.startIterator(classGen, methodGen);
            il.append(methodGen.nextNode());
        }
    }
}
