package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class ForEach extends Instruction
{
    private Expression _select;
    private Type _type;
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("ForEach");
        this.indent(indent + 4);
        Util.println("select " + this._select.toString());
        this.displayContents(indent + 4);
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this._select = parser.parseExpression(this, "select", null);
        this.parseChildren(parser);
        if (this._select.isDummy()) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "select");
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this._type = this._select.typeCheck(stable);
        if (this._type instanceof ReferenceType || this._type instanceof NodeType) {
            this._select = new CastExpr(this._select, Type.NodeSet);
            this.typeCheckContents(stable);
            return Type.Void;
        }
        if (this._type instanceof NodeSetType || this._type instanceof ResultTreeType) {
            this.typeCheckContents(stable);
            return Type.Void;
        }
        throw new TypeCheckError(this);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        final Vector sortObjects = new Vector();
        final Iterator<SyntaxTreeNode> children = this.elements();
        while (children.hasNext()) {
            final SyntaxTreeNode child = children.next();
            if (child instanceof Sort) {
                sortObjects.addElement(child);
            }
        }
        if (this._type != null && this._type instanceof ResultTreeType) {
            il.append(methodGen.loadDOM());
            if (sortObjects.size() > 0) {
                final ErrorMsg msg = new ErrorMsg("RESULT_TREE_SORT_ERR", this);
                this.getParser().reportError(4, msg);
            }
            this._select.translate(classGen, methodGen);
            this._type.translateTo(classGen, methodGen, Type.NodeSet);
            il.append(ForEach.SWAP);
            il.append(methodGen.storeDOM());
        }
        else {
            if (sortObjects.size() > 0) {
                Sort.translateSortIterator(classGen, methodGen, this._select, sortObjects);
            }
            else {
                this._select.translate(classGen, methodGen);
            }
            if (!(this._type instanceof ReferenceType)) {
                il.append(methodGen.loadContextNode());
                il.append(methodGen.setStartNode());
            }
        }
        il.append(methodGen.storeIterator());
        this.initializeVariables(classGen, methodGen);
        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(ForEach.NOP);
        this.translateContents(classGen, methodGen);
        nextNode.setTarget(il.append(methodGen.loadIterator()));
        il.append(methodGen.nextNode());
        il.append(ForEach.DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGT(loop));
        if (this._type != null && this._type instanceof ResultTreeType) {
            il.append(methodGen.storeDOM());
        }
        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
    }
    
    public void initializeVariables(final ClassGenerator classGen, final MethodGenerator methodGen) {
        for (int n = this.elementCount(), i = 0; i < n; ++i) {
            final SyntaxTreeNode child = this.getContents().get(i);
            if (child instanceof Variable) {
                final Variable var = (Variable)child;
                var.initialize(classGen, methodGen);
            }
        }
    }
}
