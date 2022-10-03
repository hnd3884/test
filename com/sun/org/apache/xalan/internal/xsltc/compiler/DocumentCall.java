package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class DocumentCall extends FunctionCall
{
    private Expression _arg1;
    private Expression _arg2;
    private Type _arg1Type;
    
    public DocumentCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._arg1 = null;
        this._arg2 = null;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final int ac = this.argumentCount();
        if (ac < 1 || ac > 2) {
            final ErrorMsg msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        if (this.getStylesheet() == null) {
            final ErrorMsg msg = new ErrorMsg("ILLEGAL_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        this._arg1 = this.argument(0);
        if (this._arg1 == null) {
            final ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
            throw new TypeCheckError(msg);
        }
        this._arg1Type = this._arg1.typeCheck(stable);
        if (this._arg1Type != Type.NodeSet && this._arg1Type != Type.String) {
            this._arg1 = new CastExpr(this._arg1, Type.String);
        }
        if (ac == 2) {
            this._arg2 = this.argument(1);
            if (this._arg2 == null) {
                final ErrorMsg msg = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                throw new TypeCheckError(msg);
            }
            final Type arg2Type = this._arg2.typeCheck(stable);
            if (arg2Type.identicalTo(Type.Node)) {
                this._arg2 = new CastExpr(this._arg2, Type.NodeSet);
            }
            else if (!arg2Type.identicalTo(Type.NodeSet)) {
                final ErrorMsg msg2 = new ErrorMsg("DOCUMENT_ARG_ERR", this);
                throw new TypeCheckError(msg2);
            }
        }
        return this._type = Type.NodeSet;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int ac = this.argumentCount();
        final int domField = cpg.addFieldref(classGen.getClassName(), "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
        String docParamList = null;
        if (ac == 1) {
            docParamList = "(Ljava/lang/Object;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
        }
        else {
            docParamList = "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
        }
        final int docIdx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument", "documentF", docParamList);
        this._arg1.translate(classGen, methodGen);
        if (this._arg1Type == Type.NodeSet) {
            this._arg1.startIterator(classGen, methodGen);
        }
        if (ac == 2) {
            this._arg2.translate(classGen, methodGen);
            this._arg2.startIterator(classGen, methodGen);
        }
        il.append(new PUSH(cpg, this.getStylesheet().getSystemId()));
        il.append(classGen.loadTranslet());
        il.append(DocumentCall.DUP);
        il.append(new GETFIELD(domField));
        il.append(new INVOKESTATIC(docIdx));
    }
}
