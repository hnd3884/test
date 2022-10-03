package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;

public final class ObjectType extends Type
{
    private String _javaClassName;
    private Class _clazz;
    
    protected ObjectType(final String javaClassName) {
        this._javaClassName = "java.lang.Object";
        this._clazz = Object.class;
        this._javaClassName = javaClassName;
        try {
            this._clazz = ObjectFactory.findProviderClass(javaClassName, true);
        }
        catch (final ClassNotFoundException e) {
            this._clazz = null;
        }
    }
    
    protected ObjectType(final Class clazz) {
        this._javaClassName = "java.lang.Object";
        this._clazz = Object.class;
        this._clazz = clazz;
        this._javaClassName = clazz.getName();
    }
    
    @Override
    public int hashCode() {
        return Object.class.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ObjectType;
    }
    
    public String getJavaClassName() {
        return this._javaClassName;
    }
    
    public Class getJavaClass() {
        return this._clazz;
    }
    
    @Override
    public String toString() {
        return this._javaClassName;
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        final StringBuffer result = new StringBuffer("L");
        result.append(this._javaClassName.replace('.', '/')).append(';');
        return result.toString();
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return Util.getJCRefType(this.toSignature());
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(ObjectType.DUP);
        final BranchHandle ifNull = il.append(new IFNULL(null));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(this._javaClassName, "toString", "()Ljava/lang/String;")));
        final BranchHandle gotobh = il.append(new GOTO(null));
        ifNull.setTarget(il.append(ObjectType.POP));
        il.append(new PUSH(cpg, ""));
        gotobh.setTarget(il.append(ObjectType.NOP));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        if (clazz.isAssignableFrom(this._clazz)) {
            methodGen.getInstructionList().append(ObjectType.NOP);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getClass().toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        methodGen.getInstructionList().append(ObjectType.NOP);
    }
    
    @Override
    public Instruction LOAD(final int slot) {
        return new ALOAD(slot);
    }
    
    @Override
    public Instruction STORE(final int slot) {
        return new ASTORE(slot);
    }
}
