package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Vector;

final class ProcessingInstructionPattern extends StepPattern
{
    private String _name;
    private boolean _typeChecked;
    
    public ProcessingInstructionPattern(final String name) {
        super(3, 7, null);
        this._name = null;
        this._typeChecked = false;
        this._name = name;
    }
    
    @Override
    public double getDefaultPriority() {
        return (this._name != null) ? 0.0 : -0.5;
    }
    
    @Override
    public String toString() {
        if (this._predicates == null) {
            return "processing-instruction(" + this._name + ")";
        }
        return "processing-instruction(" + this._name + ")" + this._predicates;
    }
    
    @Override
    public void reduceKernelPattern() {
        this._typeChecked = true;
    }
    
    @Override
    public boolean isWildcard() {
        return false;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this.hasPredicates()) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Predicate pred = this._predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }
        return Type.NodeSet;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int gname = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeName", "(I)Ljava/lang/String;");
        final int cmp = cpg.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
        il.append(methodGen.loadCurrentNode());
        il.append(ProcessingInstructionPattern.SWAP);
        il.append(methodGen.storeCurrentNode());
        if (!this._typeChecked) {
            il.append(methodGen.loadCurrentNode());
            final int getType = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKEINTERFACE(getType, 2));
            il.append(new PUSH(cpg, 7));
            this._falseList.add(il.append(new IF_ICMPEQ(null)));
        }
        il.append(new PUSH(cpg, this._name));
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(gname, 2));
        il.append(new INVOKEVIRTUAL(cmp));
        this._falseList.add(il.append(new IFEQ(null)));
        if (this.hasPredicates()) {
            for (int n = this._predicates.size(), i = 0; i < n; ++i) {
                final Predicate pred = this._predicates.elementAt(i);
                final Expression exp = pred.getExpr();
                exp.translateDesynthesized(classGen, methodGen);
                this._trueList.append(exp._trueList);
                this._falseList.append(exp._falseList);
            }
        }
        InstructionHandle restore = il.append(methodGen.storeCurrentNode());
        this.backPatchTrueList(restore);
        final BranchHandle skipFalse = il.append(new GOTO(null));
        restore = il.append(methodGen.storeCurrentNode());
        this.backPatchFalseList(restore);
        this._falseList.add(il.append(new GOTO(null)));
        skipFalse.setTarget(il.append(ProcessingInstructionPattern.NOP));
    }
}
