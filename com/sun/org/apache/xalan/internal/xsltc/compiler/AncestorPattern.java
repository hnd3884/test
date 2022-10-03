package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;

final class AncestorPattern extends RelativePathPattern
{
    private final Pattern _left;
    private final RelativePathPattern _right;
    private InstructionHandle _loop;
    
    public AncestorPattern(final RelativePathPattern right) {
        this(null, right);
    }
    
    public AncestorPattern(final Pattern left, final RelativePathPattern right) {
        this._left = left;
        (this._right = right).setParent(this);
        if (left != null) {
            left.setParent(this);
        }
    }
    
    public InstructionHandle getLoopHandle() {
        return this._loop;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        if (this._left != null) {
            this._left.setParser(parser);
        }
        this._right.setParser(parser);
    }
    
    @Override
    public boolean isWildcard() {
        return false;
    }
    
    @Override
    public StepPattern getKernelPattern() {
        return this._right.getKernelPattern();
    }
    
    @Override
    public void reduceKernelPattern() {
        this._right.reduceKernelPattern();
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._left != null) {
            this._left.typeCheck(stable);
        }
        return this._right.typeCheck(stable);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen local = methodGen.addLocalVariable2("app", Util.getJCRefType("I"), il.getEnd());
        final Instruction loadLocal = new ILOAD(local.getIndex());
        final Instruction storeLocal = new ISTORE(local.getIndex());
        if (this._right instanceof StepPattern) {
            il.append(AncestorPattern.DUP);
            il.append(storeLocal);
            this._right.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            il.append(loadLocal);
        }
        else {
            this._right.translate(classGen, methodGen);
            if (this._right instanceof AncestorPattern) {
                il.append(methodGen.loadDOM());
                il.append(AncestorPattern.SWAP);
            }
        }
        if (this._left != null) {
            final int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
            final InstructionHandle parent = il.append(new INVOKEINTERFACE(getParent, 2));
            il.append(AncestorPattern.DUP);
            il.append(storeLocal);
            this._falseList.add(il.append(new IFLT(null)));
            il.append(loadLocal);
            this._left.translate(classGen, methodGen);
            final SyntaxTreeNode p = this.getParent();
            if (p != null && !(p instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Instruction)) {
                if (!(p instanceof TopLevelElement)) {
                    il.append(loadLocal);
                }
            }
            final BranchHandle exit = il.append(new GOTO(null));
            this._loop = il.append(methodGen.loadDOM());
            il.append(loadLocal);
            local.setEnd(this._loop);
            il.append(new GOTO(parent));
            exit.setTarget(il.append(AncestorPattern.NOP));
            this._left.backPatchFalseList(this._loop);
            this._trueList.append(this._left._trueList);
        }
        else {
            il.append(AncestorPattern.POP2);
        }
        if (this._right instanceof AncestorPattern) {
            final AncestorPattern ancestor = (AncestorPattern)this._right;
            this._falseList.backPatch(ancestor.getLoopHandle());
        }
        this._trueList.append(this._right._trueList);
        this._falseList.append(this._right._falseList);
    }
    
    @Override
    public String toString() {
        return "AncestorPattern(" + this._left + ", " + this._right + ')';
    }
}
