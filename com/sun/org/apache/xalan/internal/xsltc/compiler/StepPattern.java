package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

class StepPattern extends RelativePathPattern
{
    private static final int NO_CONTEXT = 0;
    private static final int SIMPLE_CONTEXT = 1;
    private static final int GENERAL_CONTEXT = 2;
    protected final int _axis;
    protected final int _nodeType;
    protected Vector _predicates;
    private Step _step;
    private boolean _isEpsilon;
    private int _contextCase;
    private double _priority;
    
    public StepPattern(final int axis, final int nodeType, final Vector predicates) {
        this._step = null;
        this._isEpsilon = false;
        this._priority = Double.MAX_VALUE;
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
    
    public int getNodeType() {
        return this._nodeType;
    }
    
    public void setPriority(final double priority) {
        this._priority = priority;
    }
    
    @Override
    public StepPattern getKernelPattern() {
        return this;
    }
    
    @Override
    public boolean isWildcard() {
        return this._isEpsilon && !this.hasPredicates();
    }
    
    public StepPattern setPredicates(final Vector predicates) {
        this._predicates = predicates;
        return this;
    }
    
    protected boolean hasPredicates() {
        return this._predicates != null && this._predicates.size() > 0;
    }
    
    @Override
    public double getDefaultPriority() {
        if (this._priority != Double.MAX_VALUE) {
            return this._priority;
        }
        if (this.hasPredicates()) {
            return 0.5;
        }
        switch (this._nodeType) {
            case -1: {
                return -0.5;
            }
            case 0: {
                return 0.0;
            }
            default: {
                return (this._nodeType >= 14) ? 0.0 : -0.5;
            }
        }
    }
    
    @Override
    public int getAxis() {
        return this._axis;
    }
    
    @Override
    public void reduceKernelPattern() {
        this._isEpsilon = true;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("stepPattern(\"");
        buffer.append(Axis.getNames(this._axis)).append("\", ").append(this._isEpsilon ? ("epsilon{" + Integer.toString(this._nodeType) + "}") : Integer.toString(this._nodeType));
        if (this._predicates != null) {
            buffer.append(", ").append(this._predicates.toString());
        }
        return buffer.append(')').toString();
    }
    
    private int analyzeCases() {
        boolean noContext = true;
        final int n = this._predicates.size();
        for (int i = 0; i < n && noContext; ++i) {
            final Predicate pred = this._predicates.elementAt(i);
            if (pred.isNthPositionFilter() || pred.hasPositionCall() || pred.hasLastCall()) {
                noContext = false;
            }
        }
        if (noContext) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return 2;
    }
    
    private String getNextFieldName() {
        return "__step_pattern_iter_" + this.getXSLTC().nextStepPatternSerial();
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this.hasPredicates()) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Predicate pred = this._predicates.elementAt(i);
                pred.typeCheck(stable);
            }
            this._contextCase = this.analyzeCases();
            Step step = null;
            if (this._contextCase == 1) {
                final Predicate pred = this._predicates.elementAt(0);
                if (pred.isNthPositionFilter()) {
                    this._contextCase = 2;
                    step = new Step(this._axis, this._nodeType, this._predicates);
                }
                else {
                    step = new Step(this._axis, this._nodeType, null);
                }
            }
            else if (this._contextCase == 2) {
                for (int len = this._predicates.size(), j = 0; j < len; ++j) {
                    this._predicates.elementAt(j).dontOptimize();
                }
                step = new Step(this._axis, this._nodeType, this._predicates);
            }
            if (step != null) {
                step.setParser(this.getParser());
                step.typeCheck(stable);
                this._step = step;
            }
        }
        return (this._axis == 3) ? Type.Element : Type.Attribute;
    }
    
    private void translateKernel(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._nodeType == 1) {
            final int check = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isElement", "(I)Z");
            il.append(methodGen.loadDOM());
            il.append(StepPattern.SWAP);
            il.append(new INVOKEINTERFACE(check, 2));
            final BranchHandle icmp = il.append(new IFNE(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(StepPattern.NOP));
        }
        else if (this._nodeType == 2) {
            final int check = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "isAttribute", "(I)Z");
            il.append(methodGen.loadDOM());
            il.append(StepPattern.SWAP);
            il.append(new INVOKEINTERFACE(check, 2));
            final BranchHandle icmp = il.append(new IFNE(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(StepPattern.NOP));
        }
        else {
            final int getEType = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
            il.append(methodGen.loadDOM());
            il.append(StepPattern.SWAP);
            il.append(new INVOKEINTERFACE(getEType, 2));
            il.append(new PUSH(cpg, this._nodeType));
            final BranchHandle icmp = il.append(new IF_ICMPEQ(null));
            this._falseList.add(il.append(new GOTO_W(null)));
            icmp.setTarget(il.append(StepPattern.NOP));
        }
    }
    
    private void translateNoContext(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadCurrentNode());
        il.append(StepPattern.SWAP);
        il.append(methodGen.storeCurrentNode());
        if (!this._isEpsilon) {
            il.append(methodGen.loadCurrentNode());
            this.translateKernel(classGen, methodGen);
        }
        for (int n = this._predicates.size(), i = 0; i < n; ++i) {
            final Predicate pred = this._predicates.elementAt(i);
            final Expression exp = pred.getExpr();
            exp.translateDesynthesized(classGen, methodGen);
            this._trueList.append(exp._trueList);
            this._falseList.append(exp._falseList);
        }
        InstructionHandle restore = il.append(methodGen.storeCurrentNode());
        this.backPatchTrueList(restore);
        final BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeCurrentNode());
        this.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(StepPattern.NOP));
    }
    
    private void translateSimpleContext(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen match = methodGen.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
        match.setStart(il.append(new ISTORE(match.getIndex())));
        if (!this._isEpsilon) {
            il.append(new ILOAD(match.getIndex()));
            this.translateKernel(classGen, methodGen);
        }
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());
        int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator", "<init>", "(ILcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        this._step.translate(classGen, methodGen);
        final LocalVariableGen stepIteratorTemp = methodGen.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        stepIteratorTemp.setStart(il.append(new ASTORE(stepIteratorTemp.getIndex())));
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator")));
        il.append(StepPattern.DUP);
        il.append(new ILOAD(match.getIndex()));
        stepIteratorTemp.setEnd(il.append(new ALOAD(stepIteratorTemp.getIndex())));
        il.append(new INVOKESPECIAL(index));
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(match.getIndex()));
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(index, 2));
        il.append(methodGen.setStartNode());
        il.append(methodGen.storeIterator());
        match.setEnd(il.append(new ILOAD(match.getIndex())));
        il.append(methodGen.storeCurrentNode());
        final Predicate pred = this._predicates.elementAt(0);
        final Expression exp = pred.getExpr();
        exp.translateDesynthesized(classGen, methodGen);
        InstructionHandle restore = il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
        exp.backPatchTrueList(restore);
        final BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
        exp.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(StepPattern.NOP));
    }
    
    private void translateGeneralContext(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        int iteratorIndex = 0;
        BranchHandle ifBlock = null;
        final String iteratorName = this.getNextFieldName();
        final LocalVariableGen node = methodGen.addLocalVariable("step_pattern_tmp1", Util.getJCRefType("I"), null, null);
        node.setStart(il.append(new ISTORE(node.getIndex())));
        final LocalVariableGen iter = methodGen.addLocalVariable("step_pattern_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        if (!classGen.isExternal()) {
            final Field iterator = new Field(2, cpg.addUtf8(iteratorName), cpg.addUtf8("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, cpg.getConstantPool());
            classGen.addField(iterator);
            iteratorIndex = cpg.addFieldref(classGen.getClassName(), iteratorName, "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(classGen.loadTranslet());
            il.append(new GETFIELD(iteratorIndex));
            il.append(StepPattern.DUP);
            iter.setStart(il.append(new ASTORE(iter.getIndex())));
            ifBlock = il.append(new IFNONNULL(null));
            il.append(classGen.loadTranslet());
        }
        this._step.translate(classGen, methodGen);
        final InstructionHandle iterStore = il.append(new ASTORE(iter.getIndex()));
        if (!classGen.isExternal()) {
            il.append(new ALOAD(iter.getIndex()));
            il.append(new PUTFIELD(iteratorIndex));
            ifBlock.setTarget(il.append(StepPattern.NOP));
        }
        else {
            iter.setStart(iterStore);
        }
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(node.getIndex()));
        final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(index, 2));
        il.append(new ALOAD(iter.getIndex()));
        il.append(StepPattern.SWAP);
        il.append(methodGen.setStartNode());
        final LocalVariableGen node2 = methodGen.addLocalVariable("step_pattern_tmp3", Util.getJCRefType("I"), null, null);
        final BranchHandle skipNext = il.append(new GOTO(null));
        final InstructionHandle next = il.append(new ALOAD(iter.getIndex()));
        node2.setStart(next);
        final InstructionHandle begin = il.append(methodGen.nextNode());
        il.append(StepPattern.DUP);
        il.append(new ISTORE(node2.getIndex()));
        this._falseList.add(il.append(new IFLT(null)));
        il.append(new ILOAD(node2.getIndex()));
        il.append(new ILOAD(node.getIndex()));
        iter.setEnd(il.append(new IF_ICMPLT(next)));
        node2.setEnd(il.append(new ILOAD(node2.getIndex())));
        node.setEnd(il.append(new ILOAD(node.getIndex())));
        this._falseList.add(il.append(new IF_ICMPNE(null)));
        skipNext.setTarget(begin);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this.hasPredicates()) {
            switch (this._contextCase) {
                case 0: {
                    this.translateNoContext(classGen, methodGen);
                    break;
                }
                case 1: {
                    this.translateSimpleContext(classGen, methodGen);
                    break;
                }
                default: {
                    this.translateGeneralContext(classGen, methodGen);
                    break;
                }
            }
        }
        else if (this.isWildcard()) {
            il.append(StepPattern.POP);
        }
        else {
            this.translateKernel(classGen, methodGen);
        }
    }
}
