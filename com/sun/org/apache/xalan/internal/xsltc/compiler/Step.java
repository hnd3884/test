package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Collection;
import java.util.Vector;

final class Step extends RelativeLocationPath
{
    private int _axis;
    private Vector _predicates;
    private boolean _hadPredicates;
    private int _nodeType;
    
    public Step(final int axis, final int nodeType, final Vector predicates) {
        this._hadPredicates = false;
        this._axis = axis;
        this._nodeType = nodeType;
        this._predicates = predicates;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        if (this._predicates != null) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Predicate exp = this._predicates.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }
    
    @Override
    public int getAxis() {
        return this._axis;
    }
    
    @Override
    public void setAxis(final int axis) {
        this._axis = axis;
    }
    
    public int getNodeType() {
        return this._nodeType;
    }
    
    public Vector getPredicates() {
        return this._predicates;
    }
    
    public void addPredicates(final Vector predicates) {
        if (this._predicates == null) {
            this._predicates = predicates;
        }
        else {
            this._predicates.addAll(predicates);
        }
    }
    
    private boolean hasParentPattern() {
        final SyntaxTreeNode parent = this.getParent();
        return parent instanceof ParentPattern || parent instanceof ParentLocationPath || parent instanceof UnionPathExpr || parent instanceof FilterParentPath;
    }
    
    private boolean hasParentLocationPath() {
        return this.getParent() instanceof ParentLocationPath;
    }
    
    private boolean hasPredicates() {
        return this._predicates != null && this._predicates.size() > 0;
    }
    
    private boolean isPredicate() {
        SyntaxTreeNode parent = this;
        while (parent != null) {
            parent = parent.getParent();
            if (parent instanceof Predicate) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAbbreviatedDot() {
        return this._nodeType == -1 && this._axis == 13;
    }
    
    public boolean isAbbreviatedDDot() {
        return this._nodeType == -1 && this._axis == 10;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this._hadPredicates = this.hasPredicates();
        if (this.isAbbreviatedDot()) {
            this._type = ((this.hasParentPattern() || this.hasPredicates() || this.hasParentLocationPath()) ? Type.NodeSet : Type.Node);
        }
        else {
            this._type = Type.NodeSet;
        }
        if (this._predicates != null) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Expression pred = this._predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }
        return this._type;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateStep(classGen, methodGen, this.hasPredicates() ? (this._predicates.size() - 1) : -1);
    }
    
    private void translateStep(final ClassGenerator classGen, final MethodGenerator methodGen, final int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (predicateIndex >= 0) {
            this.translatePredicates(classGen, methodGen, predicateIndex);
        }
        else {
            int star = 0;
            String name = null;
            final XSLTC xsltc = this.getParser().getXSLTC();
            if (this._nodeType >= 14) {
                final Vector ni = xsltc.getNamesIndex();
                name = ni.elementAt(this._nodeType - 14);
                star = name.lastIndexOf(42);
            }
            if (this._axis == 2 && this._nodeType != 2 && this._nodeType != -1 && !this.hasParentPattern() && star == 0) {
                final int iter = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                il.append(methodGen.loadDOM());
                il.append(new PUSH(cpg, 2));
                il.append(new PUSH(cpg, this._nodeType));
                il.append(new INVOKEINTERFACE(iter, 3));
                return;
            }
            final SyntaxTreeNode parent = this.getParent();
            if (this.isAbbreviatedDot()) {
                if (this._type == Type.Node) {
                    il.append(methodGen.loadContextNode());
                }
                else if (parent instanceof ParentLocationPath) {
                    final int init = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
                    il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
                    il.append(Step.DUP);
                    il.append(methodGen.loadContextNode());
                    il.append(new INVOKESPECIAL(init));
                }
                else {
                    final int git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                    il.append(methodGen.loadDOM());
                    il.append(new PUSH(cpg, this._axis));
                    il.append(new INVOKEINTERFACE(git, 2));
                }
                return;
            }
            if (parent instanceof ParentLocationPath && parent.getParent() instanceof ParentLocationPath && this._nodeType == 1 && !this._hadPredicates) {
                this._nodeType = -1;
            }
            switch (this._nodeType) {
                case 2: {
                    this._axis = 2;
                }
                case -1: {
                    final int git = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                    il.append(methodGen.loadDOM());
                    il.append(new PUSH(cpg, this._axis));
                    il.append(new INVOKEINTERFACE(git, 2));
                    break;
                }
                default: {
                    if (star > 1) {
                        String namespace;
                        if (this._axis == 2) {
                            namespace = name.substring(0, star - 2);
                        }
                        else {
                            namespace = name.substring(0, star - 1);
                        }
                        final int nsType = xsltc.registerNamespace(namespace);
                        final int ns = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                        il.append(methodGen.loadDOM());
                        il.append(new PUSH(cpg, this._axis));
                        il.append(new PUSH(cpg, nsType));
                        il.append(new INVOKEINTERFACE(ns, 3));
                        break;
                    }
                }
                case 1: {
                    final int ty = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                    il.append(methodGen.loadDOM());
                    il.append(new PUSH(cpg, this._axis));
                    il.append(new PUSH(cpg, this._nodeType));
                    il.append(new INVOKEINTERFACE(ty, 3));
                    break;
                }
            }
        }
    }
    
    public void translatePredicates(final ClassGenerator classGen, final MethodGenerator methodGen, int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        int idx = 0;
        if (predicateIndex < 0) {
            this.translateStep(classGen, methodGen, predicateIndex);
        }
        else {
            final Predicate predicate = this._predicates.get(predicateIndex--);
            if (predicate.isNodeValueTest()) {
                final Step step = predicate.getStep();
                il.append(methodGen.loadDOM());
                if (step.isAbbreviatedDot()) {
                    this.translateStep(classGen, methodGen, predicateIndex);
                    il.append(new ICONST(0));
                }
                else {
                    final ParentLocationPath path = new ParentLocationPath(this, step);
                    final Step step2 = step;
                    final ParentLocationPath parentLocationPath = path;
                    step2._parent = parentLocationPath;
                    this._parent = parentLocationPath;
                    try {
                        path.typeCheck(this.getParser().getSymbolTable());
                    }
                    catch (final TypeCheckError typeCheckError) {}
                    this.translateStep(classGen, methodGen, predicateIndex);
                    path.translateStep(classGen, methodGen);
                    il.append(new ICONST(1));
                }
                predicate.translate(classGen, methodGen);
                idx = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeValueIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ILjava/lang/String;Z)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                il.append(new INVOKEINTERFACE(idx, 5));
            }
            else if (predicate.isNthDescendant()) {
                il.append(methodGen.loadDOM());
                il.append(new PUSH(cpg, predicate.getPosType()));
                predicate.translate(classGen, methodGen);
                il.append(new ICONST(0));
                idx = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNthDescendant", "(IIZ)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                il.append(new INVOKEINTERFACE(idx, 4));
            }
            else if (predicate.isNthPositionFilter()) {
                idx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
                this.translatePredicates(classGen, methodGen, predicateIndex);
                final LocalVariableGen iteratorTemp = methodGen.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
                iteratorTemp.setStart(il.append(new ASTORE(iteratorTemp.getIndex())));
                predicate.translate(classGen, methodGen);
                final LocalVariableGen predicateValueTemp = methodGen.addLocalVariable("step_tmp2", Util.getJCRefType("I"), null, null);
                predicateValueTemp.setStart(il.append(new ISTORE(predicateValueTemp.getIndex())));
                il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
                il.append(Step.DUP);
                iteratorTemp.setEnd(il.append(new ALOAD(iteratorTemp.getIndex())));
                predicateValueTemp.setEnd(il.append(new ILOAD(predicateValueTemp.getIndex())));
                il.append(new INVOKESPECIAL(idx));
            }
            else {
                idx = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
                this.translatePredicates(classGen, methodGen, predicateIndex);
                final LocalVariableGen iteratorTemp = methodGen.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
                iteratorTemp.setStart(il.append(new ASTORE(iteratorTemp.getIndex())));
                predicate.translateFilter(classGen, methodGen);
                final LocalVariableGen filterTemp = methodGen.addLocalVariable("step_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
                filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));
                il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
                il.append(Step.DUP);
                iteratorTemp.setEnd(il.append(new ALOAD(iteratorTemp.getIndex())));
                filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
                il.append(methodGen.loadCurrentNode());
                il.append(classGen.loadTranslet());
                if (classGen.isExternal()) {
                    final String className = classGen.getClassName();
                    il.append(new CHECKCAST(cpg.addClass(className)));
                }
                il.append(new INVOKESPECIAL(idx));
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("step(\"");
        buffer.append(Axis.getNames(this._axis)).append("\", ").append(this._nodeType);
        if (this._predicates != null) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Predicate pred = this._predicates.elementAt(i);
                buffer.append(", ").append(pred.toString());
            }
        }
        return buffer.append(')').toString();
    }
}
