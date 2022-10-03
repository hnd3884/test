package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;

final class RelationalExpr extends Expression
{
    private int _op;
    private Expression _left;
    private Expression _right;
    
    public RelationalExpr(final int op, final Expression left, final Expression right) {
        this._op = op;
        (this._left = left).setParent(this);
        (this._right = right).setParent(this);
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
        this._right.setParser(parser);
    }
    
    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall() || this._right.hasPositionCall();
    }
    
    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall() || this._right.hasLastCall();
    }
    
    public boolean hasReferenceArgs() {
        return this._left.getType() instanceof ReferenceType || this._right.getType() instanceof ReferenceType;
    }
    
    public boolean hasNodeArgs() {
        return this._left.getType() instanceof NodeType || this._right.getType() instanceof NodeType;
    }
    
    public boolean hasNodeSetArgs() {
        return this._left.getType() instanceof NodeSetType || this._right.getType() instanceof NodeSetType;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        Type tleft = this._left.typeCheck(stable);
        Type tright = this._right.typeCheck(stable);
        if (tleft instanceof ResultTreeType && tright instanceof ResultTreeType) {
            this._right = new CastExpr(this._right, Type.Real);
            this._left = new CastExpr(this._left, Type.Real);
            return this._type = Type.Boolean;
        }
        if (this.hasReferenceArgs()) {
            Type type = null;
            Type typeL = null;
            Type typeR = null;
            if (tleft instanceof ReferenceType && this._left instanceof VariableRefBase) {
                final VariableRefBase ref = (VariableRefBase)this._left;
                final VariableBase var = ref.getVariable();
                typeL = var.getType();
            }
            if (tright instanceof ReferenceType && this._right instanceof VariableRefBase) {
                final VariableRefBase ref = (VariableRefBase)this._right;
                final VariableBase var = ref.getVariable();
                typeR = var.getType();
            }
            if (typeL == null) {
                type = typeR;
            }
            else if (typeR == null) {
                type = typeL;
            }
            else {
                type = Type.Real;
            }
            if (type == null) {
                type = Type.Real;
            }
            this._right = new CastExpr(this._right, type);
            this._left = new CastExpr(this._left, type);
            return this._type = Type.Boolean;
        }
        if (this.hasNodeSetArgs()) {
            if (tright instanceof NodeSetType) {
                final Expression temp = this._right;
                this._right = this._left;
                this._left = temp;
                this._op = ((this._op == 2) ? 3 : ((this._op == 3) ? 2 : ((this._op == 4) ? 5 : 4)));
                tright = this._right.getType();
            }
            if (tright instanceof NodeType) {
                this._right = new CastExpr(this._right, Type.NodeSet);
            }
            if (tright instanceof IntType) {
                this._right = new CastExpr(this._right, Type.Real);
            }
            if (tright instanceof ResultTreeType) {
                this._right = new CastExpr(this._right, Type.String);
            }
            return this._type = Type.Boolean;
        }
        if (this.hasNodeArgs()) {
            if (tleft instanceof BooleanType) {
                this._right = new CastExpr(this._right, Type.Boolean);
                tright = Type.Boolean;
            }
            if (tright instanceof BooleanType) {
                this._left = new CastExpr(this._left, Type.Boolean);
                tleft = Type.Boolean;
            }
        }
        final MethodType ptype = this.lookupPrimop(stable, Operators.getOpNames(this._op), new MethodType(Type.Void, tleft, tright));
        if (ptype != null) {
            final Type arg1 = ptype.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            final Type arg2 = ptype.argsType().elementAt(1);
            if (!arg2.identicalTo(tright)) {
                this._right = new CastExpr(this._right, arg1);
            }
            return this._type = ptype.resultType();
        }
        throw new TypeCheckError(this);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this.hasNodeSetArgs() || this.hasReferenceArgs()) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            this._left.translate(classGen, methodGen);
            this._left.startIterator(classGen, methodGen);
            this._right.translate(classGen, methodGen);
            this._right.startIterator(classGen, methodGen);
            il.append(new PUSH(cpg, this._op));
            il.append(methodGen.loadDOM());
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + this._left.getType().toSignature() + this._right.getType().toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
            il.append(new INVOKESTATIC(index));
        }
        else {
            this.translateDesynthesized(classGen, methodGen);
            this.synthesize(classGen, methodGen);
        }
    }
    
    @Override
    public void translateDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this.hasNodeSetArgs() || this.hasReferenceArgs()) {
            this.translate(classGen, methodGen);
            this.desynthesize(classGen, methodGen);
        }
        else {
            BranchInstruction bi = null;
            final InstructionList il = methodGen.getInstructionList();
            this._left.translate(classGen, methodGen);
            this._right.translate(classGen, methodGen);
            boolean tozero = false;
            Type tleft = this._left.getType();
            if (tleft instanceof RealType) {
                il.append(tleft.CMP(this._op == 3 || this._op == 5));
                tleft = Type.Int;
                tozero = true;
            }
            switch (this._op) {
                case 3: {
                    bi = tleft.GE(tozero);
                    break;
                }
                case 2: {
                    bi = tleft.LE(tozero);
                    break;
                }
                case 5: {
                    bi = tleft.GT(tozero);
                    break;
                }
                case 4: {
                    bi = tleft.LT(tozero);
                    break;
                }
                default: {
                    final ErrorMsg msg = new ErrorMsg("ILLEGAL_RELAT_OP_ERR", this);
                    this.getParser().reportError(2, msg);
                    break;
                }
            }
            this._falseList.add(il.append(bi));
        }
    }
    
    @Override
    public String toString() {
        return Operators.getOpNames(this._op) + '(' + this._left + ", " + this._right + ')';
    }
}
