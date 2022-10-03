package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class FilterParentPath extends Expression
{
    private Expression _filterExpr;
    private Expression _path;
    private boolean _hasDescendantAxis;
    
    public FilterParentPath(final Expression filterExpr, final Expression path) {
        this._hasDescendantAxis = false;
        (this._path = path).setParent(this);
        (this._filterExpr = filterExpr).setParent(this);
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._filterExpr.setParser(parser);
        this._path.setParser(parser);
    }
    
    @Override
    public String toString() {
        return "FilterParentPath(" + this._filterExpr + ", " + this._path + ')';
    }
    
    public void setDescendantAxis() {
        this._hasDescendantAxis = true;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type ftype = this._filterExpr.typeCheck(stable);
        if (!(ftype instanceof NodeSetType)) {
            if (ftype instanceof ReferenceType) {
                this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
            }
            else {
                if (!(ftype instanceof NodeType)) {
                    throw new TypeCheckError(this);
                }
                this._filterExpr = new CastExpr(this._filterExpr, Type.NodeSet);
            }
        }
        final Type ptype = this._path.typeCheck(stable);
        if (!(ptype instanceof NodeSetType)) {
            this._path = new CastExpr(this._path, Type.NodeSet);
        }
        return this._type = Type.NodeSet;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int initSI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        this._filterExpr.translate(classGen, methodGen);
        final LocalVariableGen filterTemp = methodGen.addLocalVariable("filter_parent_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));
        this._path.translate(classGen, methodGen);
        final LocalVariableGen pathTemp = methodGen.addLocalVariable("filter_parent_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
        il.append(FilterParentPath.DUP);
        filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));
        il.append(new INVOKESPECIAL(initSI));
        if (this._hasDescendantAxis) {
            final int incl = cpg.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(new INVOKEVIRTUAL(incl));
        }
        final SyntaxTreeNode parent = this.getParent();
        final boolean parentAlreadyOrdered = parent instanceof RelativeLocationPath || parent instanceof FilterParentPath || parent instanceof KeyCall || parent instanceof CurrentCall || parent instanceof DocumentCall;
        if (!parentAlreadyOrdered) {
            final int order = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(FilterParentPath.SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}
