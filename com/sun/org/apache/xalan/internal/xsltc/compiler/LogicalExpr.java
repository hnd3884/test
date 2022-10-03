package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class LogicalExpr extends Expression
{
    public static final int OR = 0;
    public static final int AND = 1;
    private final int _op;
    private Expression _left;
    private Expression _right;
    private static final String[] Ops;
    
    public LogicalExpr(final int op, final Expression left, final Expression right) {
        this._op = op;
        (this._left = left).setParent(this);
        (this._right = right).setParent(this);
    }
    
    @Override
    public boolean hasPositionCall() {
        return this._left.hasPositionCall() || this._right.hasPositionCall();
    }
    
    @Override
    public boolean hasLastCall() {
        return this._left.hasLastCall() || this._right.hasLastCall();
    }
    
    @Override
    public Object evaluateAtCompileTime() {
        final Object leftb = this._left.evaluateAtCompileTime();
        final Object rightb = this._right.evaluateAtCompileTime();
        if (leftb == null || rightb == null) {
            return null;
        }
        if (this._op == 1) {
            return (leftb == Boolean.TRUE && rightb == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
        }
        return (leftb == Boolean.TRUE || rightb == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public int getOp() {
        return this._op;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._left.setParser(parser);
        this._right.setParser(parser);
    }
    
    @Override
    public String toString() {
        return LogicalExpr.Ops[this._op] + '(' + this._left + ", " + this._right + ')';
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type tleft = this._left.typeCheck(stable);
        final Type tright = this._right.typeCheck(stable);
        final MethodType wantType = new MethodType(Type.Void, tleft, tright);
        final MethodType haveType = this.lookupPrimop(stable, LogicalExpr.Ops[this._op], wantType);
        if (haveType != null) {
            final Type arg1 = haveType.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                this._left = new CastExpr(this._left, arg1);
            }
            final Type arg2 = haveType.argsType().elementAt(1);
            if (!arg2.identicalTo(tright)) {
                this._right = new CastExpr(this._right, arg1);
            }
            return this._type = haveType.resultType();
        }
        throw new TypeCheckError(this);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this.translateDesynthesized(classGen, methodGen);
        this.synthesize(classGen, methodGen);
    }
    
    @Override
    public void translateDesynthesized(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        final SyntaxTreeNode parent = this.getParent();
        if (this._op == 1) {
            this._left.translateDesynthesized(classGen, methodGen);
            final InstructionHandle middle = il.append(LogicalExpr.NOP);
            this._right.translateDesynthesized(classGen, methodGen);
            final InstructionHandle after = il.append(LogicalExpr.NOP);
            this._falseList.append(this._right._falseList.append(this._left._falseList));
            if (this._left instanceof LogicalExpr && ((LogicalExpr)this._left).getOp() == 0) {
                this._left.backPatchTrueList(middle);
            }
            else if (this._left instanceof NotCall) {
                this._left.backPatchTrueList(middle);
            }
            else {
                this._trueList.append(this._left._trueList);
            }
            if (this._right instanceof LogicalExpr && ((LogicalExpr)this._right).getOp() == 0) {
                this._right.backPatchTrueList(after);
            }
            else if (this._right instanceof NotCall) {
                this._right.backPatchTrueList(after);
            }
            else {
                this._trueList.append(this._right._trueList);
            }
        }
        else {
            this._left.translateDesynthesized(classGen, methodGen);
            final InstructionHandle ih = il.append(new GOTO(null));
            this._right.translateDesynthesized(classGen, methodGen);
            this._left._trueList.backPatch(ih);
            this._left._falseList.backPatch(ih.getNext());
            this._falseList.append(this._right._falseList);
            this._trueList.add(ih).append(this._right._trueList);
        }
    }
    
    static {
        Ops = new String[] { "or", "and" };
    }
}
