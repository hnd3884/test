package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

class FilterExpr extends Expression
{
    private Expression _primary;
    private final Vector _predicates;
    
    public FilterExpr(final Expression primary, final Vector predicates) {
        this._primary = primary;
        this._predicates = predicates;
        primary.setParent(this);
    }
    
    protected Expression getExpr() {
        if (this._primary instanceof CastExpr) {
            return ((CastExpr)this._primary).getExpr();
        }
        return this._primary;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._primary.setParser(parser);
        if (this._predicates != null) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Expression exp = this._predicates.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }
    
    @Override
    public String toString() {
        return "filter-expr(" + this._primary + ", " + this._predicates + ")";
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type ptype = this._primary.typeCheck(stable);
        final boolean canOptimize = this._primary instanceof KeyCall;
        if (!(ptype instanceof NodeSetType)) {
            if (!(ptype instanceof ReferenceType)) {
                throw new TypeCheckError(this);
            }
            this._primary = new CastExpr(this._primary, Type.NodeSet);
        }
        for (int n = this._predicates.size(), i = 0; i < n; ++i) {
            final Predicate pred = this._predicates.elementAt(i);
            if (!canOptimize) {
                pred.dontOptimize();
            }
            pred.typeCheck(stable);
        }
        return this._type = Type.NodeSet;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateFilterExpr(classGen, methodGen, (this._predicates == null) ? -1 : (this._predicates.size() - 1));
    }
    
    private void translateFilterExpr(final ClassGenerator classGen, final MethodGenerator methodGen, final int predicateIndex) {
        if (predicateIndex >= 0) {
            this.translatePredicates(classGen, methodGen, predicateIndex);
        }
        else {
            this._primary.translate(classGen, methodGen);
        }
    }
    
    public void translatePredicates(final ClassGenerator classGen, final MethodGenerator methodGen, int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (predicateIndex < 0) {
            this.translateFilterExpr(classGen, methodGen, predicateIndex);
        }
        else {
            final Predicate predicate = this._predicates.get(predicateIndex--);
            this.translatePredicates(classGen, methodGen, predicateIndex);
            if (predicate.isNthPositionFilter()) {
                final int nthIteratorIdx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
                final LocalVariableGen iteratorTemp = methodGen.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
                iteratorTemp.setStart(il.append(new ASTORE(iteratorTemp.getIndex())));
                predicate.translate(classGen, methodGen);
                final LocalVariableGen predicateValueTemp = methodGen.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("I"), null, null);
                predicateValueTemp.setStart(il.append(new ISTORE(predicateValueTemp.getIndex())));
                il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
                il.append(FilterExpr.DUP);
                iteratorTemp.setEnd(il.append(new ALOAD(iteratorTemp.getIndex())));
                predicateValueTemp.setEnd(il.append(new ILOAD(predicateValueTemp.getIndex())));
                il.append(new INVOKESPECIAL(nthIteratorIdx));
            }
            else {
                final int initCNLI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ZLcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
                final LocalVariableGen nodeIteratorTemp = methodGen.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
                nodeIteratorTemp.setStart(il.append(new ASTORE(nodeIteratorTemp.getIndex())));
                predicate.translate(classGen, methodGen);
                final LocalVariableGen filterTemp = methodGen.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
                filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));
                il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
                il.append(FilterExpr.DUP);
                nodeIteratorTemp.setEnd(il.append(new ALOAD(nodeIteratorTemp.getIndex())));
                il.append(FilterExpr.ICONST_1);
                filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
                il.append(methodGen.loadCurrentNode());
                il.append(classGen.loadTranslet());
                il.append(new INVOKESPECIAL(initCNLI));
            }
        }
    }
}
