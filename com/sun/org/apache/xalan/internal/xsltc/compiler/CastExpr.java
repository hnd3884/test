package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;

final class CastExpr extends Expression
{
    private final Expression _left;
    private static final MultiHashtable<Type, Type> InternalTypeMap;
    private boolean _typeTest;
    
    public CastExpr(final Expression left, final Type type) throws TypeCheckError {
        this._typeTest = false;
        this._left = left;
        this._type = type;
        if (this._left instanceof Step && this._type == Type.Boolean) {
            final Step step = (Step)this._left;
            if (step.getAxis() == 13 && step.getNodeType() != -1) {
                this._typeTest = true;
            }
        }
        this.setParser(left.getParser());
        this.setParent(left.getParent());
        left.setParent(this);
        this.typeCheck(left.getParser().getSymbolTable());
    }
    
    public Expression getExpr() {
        return this._left;
    }
    
    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall();
    }
    
    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall();
    }
    
    @Override
    public String toString() {
        return "cast(" + this._left + ", " + this._type + ")";
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        Type tleft = this._left.getType();
        if (tleft == null) {
            tleft = this._left.typeCheck(stable);
        }
        if (tleft instanceof NodeType) {
            tleft = Type.Node;
        }
        else if (tleft instanceof ResultTreeType) {
            tleft = Type.ResultTree;
        }
        if (CastExpr.InternalTypeMap.maps(tleft, this._type) != null) {
            return this._type;
        }
        throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", tleft.toString(), this._type.toString()));
    }
    
    @Override
    public void translateDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final Type ltype = this._left.getType();
        if (this._typeTest) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            final int idx = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
            il.append(new SIPUSH((short)((Step)this._left).getNodeType()));
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(idx, 2));
            this._falseList.add(il.append(new IF_ICMPNE(null)));
        }
        else {
            this._left.translate(classGen, methodGen);
            if (this._type != ltype) {
                this._left.startIterator(classGen, methodGen);
                if (this._type instanceof BooleanType) {
                    final FlowList fl = ltype.translateToDesynthesized(classGen, methodGen, this._type);
                    if (fl != null) {
                        this._falseList.append(fl);
                    }
                }
                else {
                    ltype.translateTo(classGen, methodGen, this._type);
                }
            }
        }
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final Type ltype = this._left.getType();
        this._left.translate(classGen, methodGen);
        if (!this._type.identicalTo(ltype)) {
            this._left.startIterator(classGen, methodGen);
            ltype.translateTo(classGen, methodGen, this._type);
        }
    }
    
    static {
        (InternalTypeMap = new MultiHashtable<Type, Type>()).put(Type.Boolean, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.Boolean, Type.Real);
        CastExpr.InternalTypeMap.put(Type.Boolean, Type.String);
        CastExpr.InternalTypeMap.put(Type.Boolean, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.Boolean, Type.Object);
        CastExpr.InternalTypeMap.put(Type.Real, Type.Real);
        CastExpr.InternalTypeMap.put(Type.Real, Type.Int);
        CastExpr.InternalTypeMap.put(Type.Real, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.Real, Type.String);
        CastExpr.InternalTypeMap.put(Type.Real, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.Real, Type.Object);
        CastExpr.InternalTypeMap.put(Type.Int, Type.Int);
        CastExpr.InternalTypeMap.put(Type.Int, Type.Real);
        CastExpr.InternalTypeMap.put(Type.Int, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.Int, Type.String);
        CastExpr.InternalTypeMap.put(Type.Int, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.Int, Type.Object);
        CastExpr.InternalTypeMap.put(Type.String, Type.String);
        CastExpr.InternalTypeMap.put(Type.String, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.String, Type.Real);
        CastExpr.InternalTypeMap.put(Type.String, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.String, Type.Object);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.Real);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.String);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.Node);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.NodeSet, Type.Object);
        CastExpr.InternalTypeMap.put(Type.Node, Type.Node);
        CastExpr.InternalTypeMap.put(Type.Node, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.Node, Type.Real);
        CastExpr.InternalTypeMap.put(Type.Node, Type.String);
        CastExpr.InternalTypeMap.put(Type.Node, Type.NodeSet);
        CastExpr.InternalTypeMap.put(Type.Node, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.Node, Type.Object);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.Real);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.String);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.ResultTree, Type.Object);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Reference);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Boolean);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Int);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Real);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.String);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Node);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.NodeSet);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.ResultTree);
        CastExpr.InternalTypeMap.put(Type.Reference, Type.Object);
        CastExpr.InternalTypeMap.put(Type.Object, Type.String);
        CastExpr.InternalTypeMap.put(Type.Void, Type.String);
        CastExpr.InternalTypeMap.makeUnmodifiable();
    }
}
