package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;

public final class ReferenceType extends Type
{
    protected ReferenceType() {
    }
    
    @Override
    public String toString() {
        return "reference";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        return "Ljava/lang/Object;";
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return com.sun.org.apache.bcel.internal.generic.Type.OBJECT;
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        }
        else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        }
        else if (type == Type.NodeSet) {
            this.translateTo(classGen, methodGen, (NodeSetType)type);
        }
        else if (type == Type.Node) {
            this.translateTo(classGen, methodGen, (NodeType)type);
        }
        else if (type == Type.ResultTree) {
            this.translateTo(classGen, methodGen, (ResultTreeType)type);
        }
        else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (ObjectType)type);
        }
        else if (type != Type.Reference) {
            final ErrorMsg err = new ErrorMsg("INTERNAL_ERR", type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final int current = methodGen.getLocalIndex("current");
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (current < 0) {
            il.append(new PUSH(cpg, 0));
        }
        else {
            il.append(new ILOAD(current));
        }
        il.append(methodGen.loadDOM());
        final int stringF = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "stringF", "(Ljava/lang/Object;ILcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
        il.append(new INVOKESTATIC(stringF));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(methodGen.loadDOM());
        final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "numberF", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)D");
        il.append(new INVOKESTATIC(index));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "booleanF", "(Ljava/lang/Object;)Z");
        il.append(new INVOKESTATIC(index));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final NodeSetType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeSet", "(Ljava/lang/Object;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        il.append(new INVOKESTATIC(index));
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        il.append(new INVOKEINTERFACE(index, 1));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final NodeType type) {
        this.translateTo(classGen, methodGen, Type.NodeSet);
        Type.NodeSet.translateTo(classGen, methodGen, type);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ResultTreeType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToResultTree", "(Ljava/lang/Object;)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
        il.append(new INVOKESTATIC(index));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ObjectType type) {
        methodGen.getInstructionList().append(ReferenceType.NOP);
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int referenceToLong = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToLong", "(Ljava/lang/Object;)J");
        final int referenceToDouble = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToDouble", "(Ljava/lang/Object;)D");
        final int referenceToBoolean = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToBoolean", "(Ljava/lang/Object;)Z");
        if (clazz.getName().equals("java.lang.Object")) {
            il.append(ReferenceType.NOP);
        }
        else if (clazz == Double.TYPE) {
            il.append(new INVOKESTATIC(referenceToDouble));
        }
        else if (clazz.getName().equals("java.lang.Double")) {
            il.append(new INVOKESTATIC(referenceToDouble));
            Type.Real.translateTo(classGen, methodGen, Type.Reference);
        }
        else if (clazz == Float.TYPE) {
            il.append(new INVOKESTATIC(referenceToDouble));
            il.append(ReferenceType.D2F);
        }
        else if (clazz.getName().equals("java.lang.String")) {
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToString", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Ljava/lang/String;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        }
        else if (clazz.getName().equals("org.w3c.dom.Node")) {
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNode", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/Node;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        }
        else if (clazz.getName().equals("org.w3c.dom.NodeList")) {
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "referenceToNodeList", "(Ljava/lang/Object;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lorg/w3c/dom/NodeList;");
            il.append(methodGen.loadDOM());
            il.append(new INVOKESTATIC(index));
        }
        else if (clazz.getName().equals("com.sun.org.apache.xalan.internal.xsltc.DOM")) {
            this.translateTo(classGen, methodGen, Type.ResultTree);
        }
        else if (clazz == Long.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
        }
        else if (clazz == Integer.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(ReferenceType.L2I);
        }
        else if (clazz == Short.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(ReferenceType.L2I);
            il.append(ReferenceType.I2S);
        }
        else if (clazz == Byte.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(ReferenceType.L2I);
            il.append(ReferenceType.I2B);
        }
        else if (clazz == Character.TYPE) {
            il.append(new INVOKESTATIC(referenceToLong));
            il.append(ReferenceType.L2I);
            il.append(ReferenceType.I2C);
        }
        else if (clazz == java.lang.Boolean.TYPE) {
            il.append(new INVOKESTATIC(referenceToBoolean));
        }
        else if (clazz.getName().equals("java.lang.Boolean")) {
            il.append(new INVOKESTATIC(referenceToBoolean));
            Type.Boolean.translateTo(classGen, methodGen, Type.Reference);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        if (clazz.getName().equals("java.lang.Object")) {
            methodGen.getInstructionList().append(ReferenceType.NOP);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        this.translateTo(classGen, methodGen, type);
        return new FlowList(il.append(new IFEQ(null)));
    }
    
    @Override
    public void translateBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
    }
    
    @Override
    public void translateUnBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
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
