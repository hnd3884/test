package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class AbsolutePathPattern extends LocationPathPattern
{
    private final RelativePathPattern _left;
    
    public AbsolutePathPattern(final RelativePathPattern left) {
        this._left = left;
        if (left != null) {
            left.setParent(this);
        }
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        if (this._left != null) {
            this._left.setParser(parser);
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return (this._left == null) ? Type.Root : this._left.typeCheck(stable);
    }
    
    @Override
    public boolean isWildcard() {
        return false;
    }
    
    @Override
    public StepPattern getKernelPattern() {
        return (this._left != null) ? this._left.getKernelPattern() : null;
    }
    
    @Override
    public void reduceKernelPattern() {
        this._left.reduceKernelPattern();
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._left != null) {
            if (this._left instanceof StepPattern) {
                final LocalVariableGen local = methodGen.addLocalVariable2("apptmp", Util.getJCRefType("I"), null);
                il.append(AbsolutePathPattern.DUP);
                local.setStart(il.append(new ISTORE(local.getIndex())));
                this._left.translate(classGen, methodGen);
                il.append(methodGen.loadDOM());
                local.setEnd(il.append(new ILOAD(local.getIndex())));
                methodGen.removeLocalVariable(local);
            }
            else {
                this._left.translate(classGen, methodGen);
            }
        }
        final int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
        final int getType = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
        final InstructionHandle begin = il.append(methodGen.loadDOM());
        il.append(AbsolutePathPattern.SWAP);
        il.append(new INVOKEINTERFACE(getParent, 2));
        if (this._left instanceof AncestorPattern) {
            il.append(methodGen.loadDOM());
            il.append(AbsolutePathPattern.SWAP);
        }
        il.append(new INVOKEINTERFACE(getType, 2));
        il.append(new PUSH(cpg, 9));
        final BranchHandle skip = il.append(new IF_ICMPEQ(null));
        this._falseList.add(il.append(new GOTO_W(null)));
        skip.setTarget(il.append(AbsolutePathPattern.NOP));
        if (this._left != null) {
            this._left.backPatchTrueList(begin);
            if (this._left instanceof AncestorPattern) {
                final AncestorPattern ancestor = (AncestorPattern)this._left;
                this._falseList.backPatch(ancestor.getLoopHandle());
            }
            this._falseList.append(this._left._falseList);
        }
    }
    
    @Override
    public String toString() {
        return "absolutePathPattern(" + ((this._left != null) ? this._left.toString() : ")");
    }
}
