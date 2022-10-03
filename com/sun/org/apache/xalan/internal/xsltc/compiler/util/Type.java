package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.AccessController;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;

public abstract class Type implements Constants
{
    public static final Type Int;
    public static final Type Real;
    public static final Type Boolean;
    public static final Type NodeSet;
    public static final Type String;
    public static final Type ResultTree;
    public static final Type Reference;
    public static final Type Void;
    public static final Type Object;
    public static final Type ObjectString;
    public static final Type Node;
    public static final Type Root;
    public static final Type Element;
    public static final Type Attribute;
    public static final Type Text;
    public static final Type Comment;
    public static final Type Processing_Instruction;
    
    public static Type newObjectType(final String javaClassName) {
        if (javaClassName == "java.lang.Object") {
            return Type.Object;
        }
        if (javaClassName == "java.lang.String") {
            return Type.ObjectString;
        }
        final AccessControlContext acc = AccessController.getContext();
        acc.checkPermission(new RuntimePermission("getContextClassLoader"));
        return new ObjectType(javaClassName);
    }
    
    public static Type newObjectType(final Class clazz) {
        if (clazz == Object.class) {
            return Type.Object;
        }
        if (clazz == String.class) {
            return Type.ObjectString;
        }
        return new ObjectType(clazz);
    }
    
    @Override
    public abstract String toString();
    
    public abstract boolean identicalTo(final Type p0);
    
    public boolean isNumber() {
        return false;
    }
    
    public boolean implementedAsMethod() {
        return false;
    }
    
    public boolean isSimple() {
        return false;
    }
    
    public abstract com.sun.org.apache.bcel.internal.generic.Type toJCType();
    
    public int distanceTo(final Type type) {
        return (type == this) ? 0 : Integer.MAX_VALUE;
    }
    
    public abstract String toSignature();
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
        classGen.getParser().reportError(2, err);
    }
    
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final Type type) {
        FlowList fl = null;
        if (type == Type.Boolean) {
            fl = this.translateToDesynthesized(classGen, methodGen, (BooleanType)type);
        }
        else {
            this.translateTo(classGen, methodGen, type);
        }
        return fl;
    }
    
    public FlowList translateToDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen, final BooleanType type) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), type.toString());
        classGen.getParser().reportError(2, err);
        return null;
    }
    
    public void translateTo(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), clazz.getClass().toString());
        classGen.getParser().reportError(2, err);
    }
    
    public void translateFrom(final ClassGenerator classGen, final MethodGenerator methodGen, final Class clazz) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", clazz.getClass().toString(), this.toString());
        classGen.getParser().reportError(2, err);
    }
    
    public void translateBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", this.toString(), "[" + this.toString() + "]");
        classGen.getParser().reportError(2, err);
    }
    
    public void translateUnBox(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", "[" + this.toString() + "]", this.toString());
        classGen.getParser().reportError(2, err);
    }
    
    public String getClassName() {
        return "";
    }
    
    public Instruction ADD() {
        return null;
    }
    
    public Instruction SUB() {
        return null;
    }
    
    public Instruction MUL() {
        return null;
    }
    
    public Instruction DIV() {
        return null;
    }
    
    public Instruction REM() {
        return null;
    }
    
    public Instruction NEG() {
        return null;
    }
    
    public Instruction LOAD(final int slot) {
        return null;
    }
    
    public Instruction STORE(final int slot) {
        return null;
    }
    
    public Instruction POP() {
        return Type.POP;
    }
    
    public BranchInstruction GT(final boolean tozero) {
        return null;
    }
    
    public BranchInstruction GE(final boolean tozero) {
        return null;
    }
    
    public BranchInstruction LT(final boolean tozero) {
        return null;
    }
    
    public BranchInstruction LE(final boolean tozero) {
        return null;
    }
    
    public Instruction CMP(final boolean less) {
        return null;
    }
    
    public Instruction DUP() {
        return Type.DUP;
    }
    
    static {
        Int = new IntType();
        Real = new RealType();
        Boolean = new BooleanType();
        NodeSet = new NodeSetType();
        String = new StringType();
        ResultTree = new ResultTreeType();
        Reference = new ReferenceType();
        Void = new VoidType();
        Object = new ObjectType(Object.class);
        ObjectString = new ObjectType(String.class);
        Node = new NodeType(-1);
        Root = new NodeType(9);
        Element = new NodeType(1);
        Attribute = new NodeType(2);
        Text = new NodeType(3);
        Comment = new NodeType(8);
        Processing_Instruction = new NodeType(7);
    }
}
