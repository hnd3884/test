package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.ObjectType;

public final class NodeSetType extends Type
{
    protected NodeSetType() {
    }
    
    @Override
    public String toString() {
        return "node-set";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return this == other;
    }
    
    @Override
    public String toSignature() {
        return "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;";
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return new ObjectType("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator");
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        }
        else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        }
        else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        }
        else if (type == Type.Node) {
            this.translateTo(classGen, methodGen, (NodeType)type);
        }
        else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        }
        else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    @Override
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final InstructionList il = methodGen.getInstructionList();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        if (clazz.getName().equals("org.w3c.dom.NodeList")) {
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            final int convert = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "nodeList2Iterator", "(Lorg/w3c/dom/NodeList;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(new INVOKESTATIC(convert));
        }
        else if (clazz.getName().equals("org.w3c.dom.Node")) {
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            final int convert = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "node2Iterator", "(Lorg/w3c/dom/Node;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(new INVOKESTATIC(convert));
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        final FlowList falsel = this.translateToDesynthesized(classGen, methodGen, type);
        il.append(NodeSetType.ICONST_1);
        final BranchHandle truec = il.append(new GOTO(null));
        falsel.backPatch(il.append(NodeSetType.ICONST_0));
        truec.setTarget(il.append(NodeSetType.NOP));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final InstructionList il = methodGen.getInstructionList();
        this.getFirstNode(classGen, methodGen);
        il.append(NodeSetType.DUP);
        final BranchHandle falsec = il.append(new IFLT(null));
        Type.Node.translateTo(classGen, methodGen, type);
        final BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(NodeSetType.POP));
        il.append(new PUSH(classGen.getConstantPool(), ""));
        truec.setTarget(il.append(NodeSetType.NOP));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        this.translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final NodeType type) {
        this.getFirstNode(classGen, methodGen);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType type) {
        methodGen.getInstructionList().append(NodeSetType.NOP);
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        this.getFirstNode(classGen, methodGen);
        return new FlowList(il.append(new IFLT(null)));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        methodGen.getInstructionList().append(NodeSetType.NOP);
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final String className = clazz.getName();
        il.append(methodGen.loadDOM());
        il.append(NodeSetType.SWAP);
        if (className.equals("org.w3c.dom.Node")) {
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("org.w3c.dom.NodeList") || className.equals("java.lang.Object")) {
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("java.lang.String")) {
            final int next = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
            final int index2 = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
            il.append(new INVOKEINTERFACE(next, 1));
            il.append(new INVOKEINTERFACE(index2, 2));
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), className);
            classGen.getParser().reportError(2, err);
        }
    }
    
    private void getFirstNode(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I"), 1));
    }
    
    @Override
    public void translateBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }
    
    @Override
    public void translateUnBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        methodGen.getInstructionList().append(NodeSetType.NOP);
    }
    
    @Override
    public String getClassName() {
        return "com.sun.org.apache.xml.internal.dtm.DTMAxisIterator";
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
