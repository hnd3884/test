package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class NodeType extends Type
{
    private final int _type;
    
    protected NodeType() {
        this(-1);
    }
    
    protected NodeType(final int type) {
        this._type = type;
    }
    
    public int getType() {
        return this._type;
    }
    
    @Override
    public String toString() {
        return "node-type";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return other instanceof NodeType;
    }
    
    @Override
    public int hashCode() {
        return this._type;
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
        else if (type == Type.NodeSet) {
            this.translateTo(classGen, methodGen, (NodeSetType)type);
        }
        else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        }
        else if (type == Type.Object) {
            this.translateTo(classGen, methodGen, (ObjectType)type);
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
            classGen.getParser().reportError(2, err);
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        switch (this._type) {
            case 1:
            case 9: {
                il.append(methodGen.loadDOM());
                il.append(NodeType.SWAP);
                final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getElementValue", "(I)Ljava/lang/String;");
                il.append(new INVOKEINTERFACE(index, 2));
                break;
            }
            case -1:
            case 2:
            case 7:
            case 8: {
                il.append(methodGen.loadDOM());
                il.append(NodeType.SWAP);
                final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValueX", "(I)Ljava/lang/String;");
                il.append(new INVOKEINTERFACE(index, 2));
                break;
            }
            default: {
                final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
                classGen.getParser().reportError(2, err);
                break;
            }
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        final FlowList falsel = this.translateToDesynthesized(classGen, methodGen, type);
        il.append(NodeType.ICONST_1);
        final BranchHandle truec = il.append(new GOTO(null));
        falsel.backPatch(il.append(NodeType.ICONST_0));
        truec.setTarget(il.append(NodeType.NOP));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        this.translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final NodeSetType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
        il.append(NodeType.DUP_X1);
        il.append(NodeType.SWAP);
        final int init = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
        il.append(new INVOKESPECIAL(init));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ObjectType type) {
        methodGen.getInstructionList().append(NodeType.NOP);
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        return new FlowList(il.append(new IFEQ(null)));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
        il.append(NodeType.DUP_X1);
        il.append(NodeType.SWAP);
        il.append(new PUSH(cpg, this._type));
        il.append(new INVOKESPECIAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "<init>", "(II)V")));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final String className = clazz.getName();
        if (className.equals("java.lang.String")) {
            this.translateTo(classGen, methodGen, Type.String);
            return;
        }
        il.append(methodGen.loadDOM());
        il.append(NodeType.SWAP);
        if (className.equals("org.w3c.dom.Node") || className.equals("java.lang.Object")) {
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(I)Lorg/w3c/dom/Node;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("org.w3c.dom.NodeList")) {
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(I)Lorg/w3c/dom/NodeList;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else {
            final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), className);
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
        il.append(new CHECKCAST(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.Node")));
        il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.Node", "node", "I")));
    }
    
    @Override
    public String getClassName() {
        return "com.sun.org.apache.xalan.internal.xsltc.runtime.Node";
    }
    
    @Override
    public Instruction LOAD(final int slot) {
        return new ILOAD(slot);
    }
    
    @Override
    public Instruction STORE(final int slot) {
        return new ISTORE(slot);
    }
}
