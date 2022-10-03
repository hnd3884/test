package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class ParentPattern extends RelativePathPattern
{
    private final Pattern _left;
    private final RelativePathPattern _right;
    
    public ParentPattern(final Pattern left, final RelativePathPattern right) {
        (this._left = left).setParent(this);
        (this._right = right).setParent(this);
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
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
        this._left.typeCheck(stable);
        return this._right.typeCheck(stable);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen local = methodGen.addLocalVariable2("ppt", Util.getJCRefType("I"), null);
        final Instruction loadLocal = new ILOAD(local.getIndex());
        final Instruction storeLocal = new ISTORE(local.getIndex());
        if (this._right.isWildcard()) {
            il.append(methodGen.loadDOM());
            il.append(ParentPattern.SWAP);
        }
        else if (this._right instanceof StepPattern) {
            il.append(ParentPattern.DUP);
            local.setStart(il.append(storeLocal));
            this._right.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            local.setEnd(il.append(loadLocal));
        }
        else {
            this._right.translate(classGen, methodGen);
            if (this._right instanceof AncestorPattern) {
                il.append(methodGen.loadDOM());
                il.append(ParentPattern.SWAP);
            }
        }
        final int getParent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getParent", "(I)I");
        il.append(new INVOKEINTERFACE(getParent, 2));
        final SyntaxTreeNode p = this.getParent();
        if (p == null || p instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Instruction || p instanceof TopLevelElement) {
            this._left.translate(classGen, methodGen);
        }
        else {
            il.append(ParentPattern.DUP);
            final InstructionHandle storeInst = il.append(storeLocal);
            if (local.getStart() == null) {
                local.setStart(storeInst);
            }
            this._left.translate(classGen, methodGen);
            il.append(methodGen.loadDOM());
            local.setEnd(il.append(loadLocal));
        }
        methodGen.removeLocalVariable(local);
        if (this._right instanceof AncestorPattern) {
            final AncestorPattern ancestor = (AncestorPattern)this._right;
            this._left.backPatchFalseList(ancestor.getLoopHandle());
        }
        this._trueList.append(this._right._trueList.append(this._left._trueList));
        this._falseList.append(this._right._falseList.append(this._left._falseList));
    }
    
    @Override
    public String toString() {
        return "Parent(" + this._left + ", " + this._right + ')';
    }
}
