package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class KeyCall extends FunctionCall
{
    private Expression _name;
    private Expression _value;
    private Type _valueType;
    private QName _resolvedQName;
    
    public KeyCall(final QName fname, final Vector arguments) {
        super(fname, arguments);
        this._resolvedQName = null;
        switch (this.argumentCount()) {
            case 1: {
                this._name = null;
                this._value = this.argument(0);
                break;
            }
            case 2: {
                this._name = this.argument(0);
                this._value = this.argument(1);
                break;
            }
            default: {
                final Expression expression = null;
                this._value = expression;
                this._name = expression;
                break;
            }
        }
    }
    
    public void addParentDependency() {
        if (this._resolvedQName == null) {
            return;
        }
        SyntaxTreeNode node;
        for (node = this; node != null && !(node instanceof TopLevelElement); node = node.getParent()) {}
        final TopLevelElement parent = (TopLevelElement)node;
        if (parent != null) {
            parent.addDependency(this.getSymbolTable().getKey(this._resolvedQName));
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type returnType = super.typeCheck(stable);
        if (this._name != null) {
            final Type nameType = this._name.typeCheck(stable);
            if (this._name instanceof LiteralExpr) {
                final LiteralExpr literal = (LiteralExpr)this._name;
                this._resolvedQName = this.getParser().getQNameIgnoreDefaultNs(literal.getValue());
            }
            else if (!(nameType instanceof StringType)) {
                this._name = new CastExpr(this._name, Type.String);
            }
        }
        this._valueType = this._value.typeCheck(stable);
        if (this._valueType != Type.NodeSet && this._valueType != Type.Reference && this._valueType != Type.String) {
            this._value = new CastExpr(this._value, Type.String);
            this._valueType = this._value.typeCheck(stable);
        }
        this.addParentDependency();
        return returnType;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int getKeyIndex = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
        final int keyDom = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "setDom", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)V");
        final int getKeyIterator = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "getKeyIndexIterator", "(" + this._valueType.toSignature() + "Z)" + "Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex$KeyIndexIterator;");
        il.append(classGen.loadTranslet());
        if (this._name == null) {
            il.append(new PUSH(cpg, "##id"));
        }
        else if (this._resolvedQName != null) {
            il.append(new PUSH(cpg, this._resolvedQName.toString()));
        }
        else {
            this._name.translate(classGen, methodGen);
        }
        il.append(new INVOKEVIRTUAL(getKeyIndex));
        il.append(KeyCall.DUP);
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEVIRTUAL(keyDom));
        this._value.translate(classGen, methodGen);
        il.append((this._name != null) ? KeyCall.ICONST_1 : KeyCall.ICONST_0);
        il.append(new INVOKEVIRTUAL(getKeyIterator));
    }
}
