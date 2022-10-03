package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class UnionPathExpr extends Expression
{
    private final Expression _pathExpr;
    private final Expression _rest;
    private boolean _reverse;
    private Expression[] _components;
    
    public UnionPathExpr(final Expression pathExpr, final Expression rest) {
        this._reverse = false;
        this._pathExpr = pathExpr;
        this._rest = rest;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        final Vector components = new Vector();
        this.flatten(components);
        final int size = components.size();
        this._components = components.toArray(new Expression[size]);
        for (int i = 0; i < size; ++i) {
            this._components[i].setParser(parser);
            this._components[i].setParent(this);
            if (this._components[i] instanceof Step) {
                final Step step = (Step)this._components[i];
                final int axis = step.getAxis();
                final int type = step.getNodeType();
                if (axis == 2 || type == 2) {
                    this._components[i] = this._components[0];
                    this._components[0] = step;
                }
                if (Axis.isReverse(axis)) {
                    this._reverse = true;
                }
            }
        }
        if (this.getParent() instanceof Expression) {
            this._reverse = false;
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        for (int length = this._components.length, i = 0; i < length; ++i) {
            if (this._components[i].typeCheck(stable) != Type.NodeSet) {
                this._components[i] = new CastExpr(this._components[i], Type.NodeSet);
            }
        }
        return this._type = Type.NodeSet;
    }
    
    @Override
    public String toString() {
        return "union(" + this._pathExpr + ", " + this._rest + ')';
    }
    
    private void flatten(final Vector components) {
        components.addElement(this._pathExpr);
        if (this._rest != null) {
            if (this._rest instanceof UnionPathExpr) {
                ((UnionPathExpr)this._rest).flatten(components);
            }
            else {
                components.addElement(this._rest);
            }
        }
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int init = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)V");
        final int iter = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator", "addIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/UnionIterator;");
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.UnionIterator")));
        il.append(UnionPathExpr.DUP);
        il.append(methodGen.loadDOM());
        il.append(new INVOKESPECIAL(init));
        for (int length = this._components.length, i = 0; i < length; ++i) {
            this._components[i].translate(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(iter));
        }
        if (this._reverse) {
            final int order = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(UnionPathExpr.SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}
