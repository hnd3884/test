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
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFEQ;

public final class BooleanType extends Type
{
    protected BooleanType() {
    }
    
    @Override
    public String toString() {
        return "boolean";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        return "Z";
    }
    
    @Override
    public boolean isSimple() {
        return true;
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN;
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        }
        else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final BranchHandle falsec = il.append(new IFEQ(null));
        il.append(new PUSH(cpg, "true"));
        final BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(new PUSH(cpg, "false")));
        truec.setTarget(il.append(BooleanType.NOP));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        methodGen.getInstructionList().append(BooleanType.I2D);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("java.lang.Boolean")));
        il.append(BooleanType.DUP_X1);
        il.append(BooleanType.SWAP);
        il.append(new INVOKESPECIAL(cpg.addMethodref("java.lang.Boolean", "<init>", "(Z)V")));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        if (clazz == java.lang.Boolean.TYPE) {
            methodGen.getInstructionList().append(BooleanType.NOP);
        }
        else if (clazz.isAssignableFrom(Boolean.class)) {
            this.translateTo(classGen, methodGen, Type.Reference);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        this.translateTo(classGen, methodGen, clazz);
    }
    
    @Override
    public void translateBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }
    
    @Override
    public void translateUnBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new CHECKCAST(cpg.addClass("java.lang.Boolean")));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.Boolean", "booleanValue", "()Z")));
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
