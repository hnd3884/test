package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class ResultTreeType extends Type
{
    private final String _methodName;
    
    protected ResultTreeType() {
        this._methodName = null;
    }
    
    public ResultTreeType(final String methodName) {
        this._methodName = methodName;
    }
    
    @Override
    public String toString() {
        return "result-tree";
    }
    
    @Override
    public boolean identicalTo(final Type other) {
        return other instanceof ResultTreeType;
    }
    
    @Override
    public String toSignature() {
        return "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
    }
    
    @Override
    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return Util.getJCRefType(this.toSignature());
    }
    
    public String getMethodName() {
        return this._methodName;
    }
    
    @Override
    public boolean implementedAsMethod() {
        return this._methodName != null;
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
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(ResultTreeType.POP);
        il.append(ResultTreeType.ICONST_1);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final StringType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._methodName == null) {
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getStringValue", "()Ljava/lang/String;");
            il.append(new INVOKEINTERFACE(index, 1));
        }
        else {
            final String className = classGen.getClassName();
            final int current = methodGen.getLocalIndex("current");
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
                il.append(new CHECKCAST(cpg.addClass(className)));
            }
            il.append(ResultTreeType.DUP);
            il.append(new GETFIELD(cpg.addFieldref(className, "_dom", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
            int index2 = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "<init>", "()V");
            il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler")));
            il.append(ResultTreeType.DUP);
            il.append(ResultTreeType.DUP);
            il.append(new INVOKESPECIAL(index2));
            final LocalVariableGen handler = methodGen.addLocalVariable("rt_to_string_handler", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;"), null, null);
            handler.setStart(il.append(new ASTORE(handler.getIndex())));
            index2 = cpg.addMethodref(className, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
            il.append(new INVOKEVIRTUAL(index2));
            handler.setEnd(il.append(new ALOAD(handler.getIndex())));
            index2 = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;");
            il.append(new INVOKEVIRTUAL(index2));
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final RealType type) {
        this.translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ReferenceType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._methodName == null) {
            il.append(ResultTreeType.NOP);
        }
        else {
            final String className = classGen.getClassName();
            final int current = methodGen.getLocalIndex("current");
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
                il.append(new CHECKCAST(cpg.addClass(className)));
            }
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadDOM());
            int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
            il.append(new PUSH(cpg, 32));
            il.append(new PUSH(cpg, false));
            il.append(new INVOKEINTERFACE(index, 3));
            il.append(ResultTreeType.DUP);
            final LocalVariableGen newDom = methodGen.addLocalVariable("rt_to_reference_dom", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
            il.append(new CHECKCAST(cpg.addClass("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;")));
            newDom.setStart(il.append(new ASTORE(newDom.getIndex())));
            index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
            il.append(new INVOKEINTERFACE(index, 1));
            il.append(ResultTreeType.DUP);
            il.append(ResultTreeType.DUP);
            final LocalVariableGen domBuilder = methodGen.addLocalVariable("rt_to_reference_handler", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;"), null, null);
            domBuilder.setStart(il.append(new ASTORE(domBuilder.getIndex())));
            index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
            il.append(new INVOKEINTERFACE(index, 1));
            index = cpg.addMethodref(className, this._methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
            il.append(new INVOKEVIRTUAL(index));
            domBuilder.setEnd(il.append(new ALOAD(domBuilder.getIndex())));
            index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
            il.append(new INVOKEINTERFACE(index, 1));
            newDom.setEnd(il.append(new ALOAD(newDom.getIndex())));
        }
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final NodeSetType type) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(ResultTreeType.DUP);
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
        final int mapping = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "setupMapping", "([Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
        il.append(new INVOKEINTERFACE(mapping, 5));
        il.append(ResultTreeType.DUP);
        final int iter = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        il.append(new INVOKEINTERFACE(iter, 1));
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final ObjectType type) {
        methodGen.getInstructionList().append(ResultTreeType.NOP);
    }
    
    @Override
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        this.translateTo(classGen, methodGen, Type.Boolean);
        return new FlowList(il.append(new IFEQ(null)));
    }
    
    @Override
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final String className = clazz.getName();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (className.equals("org.w3c.dom.Node")) {
            this.translateTo(classGen, methodGen, Type.NodeSet);
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNode", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/Node;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("org.w3c.dom.NodeList")) {
            this.translateTo(classGen, methodGen, Type.NodeSet);
            final int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "makeNodeList", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lorg/w3c/dom/NodeList;");
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("java.lang.Object")) {
            il.append(ResultTreeType.NOP);
        }
        else if (className.equals("java.lang.String")) {
            this.translateTo(classGen, methodGen, Type.String);
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
        methodGen.getInstructionList().append(ResultTreeType.NOP);
    }
    
    @Override
    public String getClassName() {
        return "com.sun.org.apache.xalan.internal.xsltc.DOM";
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
