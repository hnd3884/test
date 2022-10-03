package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;

public final class RealType extends NumberType
{
    protected RealType() {
    }
    
    @Override
    public String toString() {
        return "real";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        return "D";
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return com.sun.org.apache.bcel.internal.generic.Type.DOUBLE;
    }
    
    @Override
    public int distanceTo(final Type type) {
        if (type == this) {
            return 0;
        }
        if (type == Type.Int) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        }
        else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        }
        else if (type == Type.Int) {
            this.translateTo(classGen, methodGen, (IntType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToString", "(D)Ljava/lang/String;")));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        final FlowList falsel = this.translateToDesynthesized(classGen, methodGen, type);
        il.append(RealType.ICONST_1);
        final BranchHandle truec = il.append(new GOTO(null));
        falsel.backPatch(il.append(RealType.ICONST_0));
        truec.setTarget(il.append(RealType.NOP));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final IntType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKESTATIC(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "realToInt", "(D)I")));
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final FlowList flowlist = new FlowList();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(RealType.DUP2);
        final LocalVariableGen local = methodGen.addLocalVariable("real_to_boolean_tmp", com.sun.org.apache.bcel.internal.generic.Type.DOUBLE, null, null);
        local.setStart(il.append(new DSTORE(local.getIndex())));
        il.append(RealType.DCONST_0);
        il.append(RealType.DCMPG);
        flowlist.add(il.append(new IFEQ(null)));
        il.append(new DLOAD(local.getIndex()));
        local.setEnd(il.append(new DLOAD(local.getIndex())));
        il.append(RealType.DCMPG);
        flowlist.add(il.append(new IFNE(null)));
        return flowlist;
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("java.lang.Double")));
        il.append(RealType.DUP_X2);
        il.append(RealType.DUP_X2);
        il.append(RealType.POP);
        il.append(new INVOKESPECIAL(cpg.addMethodref("java.lang.Double", "<init>", "(D)V")));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final InstructionList il = methodGen.getInstructionList();
        if (clazz == Character.TYPE) {
            il.append(RealType.D2I);
            il.append(RealType.I2C);
        }
        else if (clazz == Byte.TYPE) {
            il.append(RealType.D2I);
            il.append(RealType.I2B);
        }
        else if (clazz == Short.TYPE) {
            il.append(RealType.D2I);
            il.append(RealType.I2S);
        }
        else if (clazz == Integer.TYPE) {
            il.append(RealType.D2I);
        }
        else if (clazz == Long.TYPE) {
            il.append(RealType.D2L);
        }
        else if (clazz == Float.TYPE) {
            il.append(RealType.D2F);
        }
        else if (clazz == Double.TYPE) {
            il.append(RealType.NOP);
        }
        else if (clazz.isAssignableFrom(Double.class)) {
            this.translateTo(classGen, methodGen, Type.Reference);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final InstructionList il = methodGen.getInstructionList();
        if (clazz == Character.TYPE || clazz == Byte.TYPE || clazz == Short.TYPE || clazz == Integer.TYPE) {
            il.append(RealType.I2D);
        }
        else if (clazz == Long.TYPE) {
            il.append(RealType.L2D);
        }
        else if (clazz == Float.TYPE) {
            il.append(RealType.F2D);
        }
        else if (clazz == Double.TYPE) {
            il.append(RealType.NOP);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }
    
    @Override
    public void translateUnBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new CHECKCAST(cpg.addClass("java.lang.Double")));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.Double", "doubleValue", "()D")));
    }
    
    @Override
    public Instruction ADD() {
        return InstructionConstants.DADD;
    }
    
    @Override
    public Instruction SUB() {
        return InstructionConstants.DSUB;
    }
    
    @Override
    public Instruction MUL() {
        return InstructionConstants.DMUL;
    }
    
    @Override
    public Instruction DIV() {
        return InstructionConstants.DDIV;
    }
    
    @Override
    public Instruction REM() {
        return InstructionConstants.DREM;
    }
    
    @Override
    public Instruction NEG() {
        return InstructionConstants.DNEG;
    }
    
    @Override
    public Instruction LOAD(final int slot) {
        return new DLOAD(slot);
    }
    
    @Override
    public Instruction STORE(final int slot) {
        return new DSTORE(slot);
    }
    
    @Override
    public Instruction POP() {
        return RealType.POP2;
    }
    
    @Override
    public Instruction CMP(final boolean less) {
        return less ? InstructionConstants.DCMPG : InstructionConstants.DCMPL;
    }
    
    @Override
    public Instruction DUP() {
        return RealType.DUP2;
    }
}
