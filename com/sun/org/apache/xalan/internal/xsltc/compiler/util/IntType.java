package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.IF_ICMPLE;
import com.sun.org.apache.bcel.internal.generic.IFLE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPLT;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGT;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class IntType extends NumberType
{
    protected IntType() {
    }
    
    @Override
    public String toString() {
        return "int";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        return "I";
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return com.sun.org.apache.bcel.internal.generic.Type.INT;
    }
    
    @Override
    public int distanceTo(final Type type) {
        if (type == this) {
            return 0;
        }
        if (type == Type.Real) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        }
        else if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        }
        else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        methodGen.getInstructionList().append(IntType.I2D);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKESTATIC(cpg.addMethodref("java.lang.Integer", "toString", "(I)Ljava/lang/String;")));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        final BranchHandle falsec = il.append(new IFEQ(null));
        il.append(IntType.ICONST_1);
        final BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(IntType.ICONST_0));
        truec.setTarget(il.append(IntType.NOP));
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        return new FlowList(il.append(new IFEQ(null)));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("java.lang.Integer")));
        il.append(IntType.DUP_X1);
        il.append(IntType.SWAP);
        il.append(new INVOKESPECIAL(cpg.addMethodref("java.lang.Integer", "<init>", "(I)V")));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final InstructionList il = methodGen.getInstructionList();
        if (clazz == Character.TYPE) {
            il.append(IntType.I2C);
        }
        else if (clazz == Byte.TYPE) {
            il.append(IntType.I2B);
        }
        else if (clazz == Short.TYPE) {
            il.append(IntType.I2S);
        }
        else if (clazz == Integer.TYPE) {
            il.append(IntType.NOP);
        }
        else if (clazz == Long.TYPE) {
            il.append(IntType.I2L);
        }
        else if (clazz == Float.TYPE) {
            il.append(IntType.I2F);
        }
        else if (clazz == Double.TYPE) {
            il.append(IntType.I2D);
        }
        else if (clazz.isAssignableFrom(Double.class)) {
            il.append(IntType.I2D);
            Type.Real.translateTo(classGen, methodGen, Type.Reference);
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
        il.append(new CHECKCAST(cpg.addClass("java.lang.Integer")));
        final int index = cpg.addMethodref("java.lang.Integer", "intValue", "()I");
        il.append(new INVOKEVIRTUAL(index));
    }
    
    @Override
    public Instruction ADD() {
        return InstructionConstants.IADD;
    }
    
    @Override
    public Instruction SUB() {
        return InstructionConstants.ISUB;
    }
    
    @Override
    public Instruction MUL() {
        return InstructionConstants.IMUL;
    }
    
    @Override
    public Instruction DIV() {
        return InstructionConstants.IDIV;
    }
    
    @Override
    public Instruction REM() {
        return InstructionConstants.IREM;
    }
    
    @Override
    public Instruction NEG() {
        return InstructionConstants.INEG;
    }
    
    @Override
    public Instruction LOAD(final int slot) {
        return new ILOAD(slot);
    }
    
    @Override
    public Instruction STORE(final int slot) {
        return new ISTORE(slot);
    }
    
    @Override
    public BranchInstruction GT(final boolean tozero) {
        return tozero ? new IFGT(null) : new IF_ICMPGT(null);
    }
    
    @Override
    public BranchInstruction GE(final boolean tozero) {
        return tozero ? new IFGE(null) : new IF_ICMPGE(null);
    }
    
    @Override
    public BranchInstruction LT(final boolean tozero) {
        return tozero ? new IFLT(null) : new IF_ICMPLT(null);
    }
    
    @Override
    public BranchInstruction LE(final boolean tozero) {
        return tozero ? new IFLE(null) : new IF_ICMPLE(null);
    }
}
