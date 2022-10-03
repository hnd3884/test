package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class CopyOf extends Instruction
{
    private Expression _select;
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("CopyOf");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this._select = parser.parseExpression(this, "select", null);
        if (this._select.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type tselect = this._select.typeCheck(stable);
        if (!(tselect instanceof NodeType) && !(tselect instanceof NodeSetType) && !(tselect instanceof ReferenceType)) {
            if (!(tselect instanceof ResultTreeType)) {
                this._select = new CastExpr(this._select, Type.String);
            }
        }
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final Type tselect = this._select.getType();
        final String CPY1_SIG = "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
        final int cpy1 = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
        final String CPY2_SIG = "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
        final int cpy2 = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "copy", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
        final String getDoc_SIG = "()I";
        final int getDoc = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getDocument", "()I");
        if (tselect instanceof NodeSetType) {
            il.append(methodGen.loadDOM());
            this._select.translate(classGen, methodGen);
            this._select.startIterator(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(cpy1, 3));
        }
        else if (tselect instanceof NodeType) {
            il.append(methodGen.loadDOM());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(cpy2, 3));
        }
        else if (tselect instanceof ResultTreeType) {
            this._select.translate(classGen, methodGen);
            il.append(CopyOf.DUP);
            il.append(new INVOKEINTERFACE(getDoc, 1));
            il.append(methodGen.loadHandler());
            il.append(new INVOKEINTERFACE(cpy2, 3));
        }
        else if (tselect instanceof ReferenceType) {
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadCurrentNode());
            il.append(methodGen.loadDOM());
            final int copy = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "copy", "(Ljava/lang/Object;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
            il.append(new INVOKESTATIC(copy));
        }
        else {
            il.append(classGen.loadTranslet());
            this._select.translate(classGen, methodGen);
            il.append(methodGen.loadHandler());
            il.append(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V")));
        }
    }
}
