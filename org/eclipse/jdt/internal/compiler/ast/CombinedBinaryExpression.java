package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class CombinedBinaryExpression extends BinaryExpression
{
    public int arity;
    public int arityMax;
    public static final int ARITY_MAX_MAX = 160;
    public static final int ARITY_MAX_MIN = 20;
    public static int defaultArityMaxStartingValue;
    public BinaryExpression[] referencesTable;
    
    static {
        CombinedBinaryExpression.defaultArityMaxStartingValue = 20;
    }
    
    public CombinedBinaryExpression(final Expression left, final Expression right, final int operator, final int arity) {
        super(left, right, operator);
        this.initArity(left, arity);
    }
    
    public CombinedBinaryExpression(final CombinedBinaryExpression expression) {
        super(expression);
        this.initArity(expression.left, expression.arity);
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.referencesTable == null) {
            return super.analyseCode(currentScope, flowContext, flowInfo);
        }
        try {
            BinaryExpression cursor;
            if ((cursor = this.referencesTable[0]).resolvedType.id != 11) {
                cursor.left.checkNPE(currentScope, flowContext, flowInfo);
            }
            flowInfo = cursor.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            for (int i = 0, end = this.arity; i < end; ++i) {
                if ((cursor = this.referencesTable[i]).resolvedType.id != 11) {
                    cursor.right.checkNPE(currentScope, flowContext, flowInfo);
                }
                flowInfo = cursor.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            }
            if (this.resolvedType.id != 11) {
                this.right.checkNPE(currentScope, flowContext, flowInfo);
            }
            return this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
        finally {
            flowContext.recordAbruptExit();
        }
    }
    
    @Override
    public void generateOptimizedStringConcatenation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        if (this.referencesTable == null) {
            super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
        }
        else if ((this.bits & 0xFC0) >> 6 == 14 && (this.bits & 0xF) == 0xB) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & 0xF);
            }
            else {
                BinaryExpression cursor = this.referencesTable[0];
                int restart = 0;
                int pc = codeStream.position;
                for (restart = this.arity - 1; restart >= 0; --restart) {
                    if ((cursor = this.referencesTable[restart]).constant != Constant.NotAConstant) {
                        codeStream.generateConstant(cursor.constant, cursor.implicitConversion);
                        codeStream.invokeStringConcatenationAppendForType(cursor.implicitConversion & 0xF);
                        break;
                    }
                }
                if (++restart == 0) {
                    cursor.left.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.left.implicitConversion & 0xF);
                }
                for (int i = restart; i < this.arity; ++i) {
                    codeStream.recordPositionsFrom(pc, (cursor = this.referencesTable[i]).left.sourceStart);
                    final int pcAux = codeStream.position;
                    cursor.right.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.right.implicitConversion & 0xF);
                    codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
                }
                codeStream.recordPositionsFrom(pc, this.left.sourceStart);
                pc = codeStream.position;
                this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.right.sourceStart);
            }
        }
        else {
            super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
        }
    }
    
    @Override
    public void generateOptimizedStringConcatenationCreation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        if (this.referencesTable == null) {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        }
        else if ((this.bits & 0xFC0) >> 6 == 14 && (this.bits & 0xF) == 0xB && this.constant == Constant.NotAConstant) {
            int pc = codeStream.position;
            BinaryExpression cursor = this.referencesTable[this.arity - 1];
            int restart;
            for (restart = 0, restart = this.arity - 1; restart >= 0; --restart) {
                if (((cursor = this.referencesTable[restart]).bits & 0xFC0) >> 6 != 14 || (cursor.bits & 0xF) != 0xB) {
                    cursor.generateOptimizedStringConcatenationCreation(blockScope, codeStream, cursor.implicitConversion & 0xF);
                    break;
                }
                if (cursor.constant != Constant.NotAConstant) {
                    codeStream.newStringContatenation();
                    codeStream.dup();
                    codeStream.ldc(cursor.constant.stringValue());
                    codeStream.invokeStringConcatenationStringConstructor();
                    break;
                }
            }
            if (++restart == 0) {
                cursor.left.generateOptimizedStringConcatenationCreation(blockScope, codeStream, cursor.left.implicitConversion & 0xF);
            }
            for (int i = restart; i < this.arity; ++i) {
                codeStream.recordPositionsFrom(pc, (cursor = this.referencesTable[i]).left.sourceStart);
                final int pcAux = codeStream.position;
                cursor.right.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
            }
            codeStream.recordPositionsFrom(pc, this.left.sourceStart);
            pc = codeStream.position;
            this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
            codeStream.recordPositionsFrom(pc, this.right.sourceStart);
        }
        else {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        }
    }
    
    private void initArity(final Expression expression, final int value) {
        this.arity = value;
        if (value > 1) {
            (this.referencesTable = new BinaryExpression[value])[value - 1] = (BinaryExpression)expression;
            for (int i = value - 1; i > 0; --i) {
                this.referencesTable[i - 1] = (BinaryExpression)this.referencesTable[i].left;
            }
        }
        else {
            this.arityMax = CombinedBinaryExpression.defaultArityMaxStartingValue;
        }
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, StringBuffer output) {
        if (this.referencesTable == null) {
            return super.printExpressionNoParenthesis(indent, output);
        }
        final String operatorString = this.operatorToString();
        for (int i = this.arity - 1; i >= 0; --i) {
            output.append('(');
        }
        output = this.referencesTable[0].left.printExpression(indent, output);
        for (int i = 0, end = this.arity; i < end; ++i) {
            output.append(' ').append(operatorString).append(' ');
            output = this.referencesTable[i].right.printExpression(0, output);
            output.append(')');
        }
        output.append(' ').append(operatorString).append(' ');
        return this.right.printExpression(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if (this.referencesTable == null) {
            return super.resolveType(scope);
        }
        final BinaryExpression cursor;
        if ((cursor = this.referencesTable[0]).left instanceof CastExpression) {
            final Expression left = cursor.left;
            left.bits |= 0x20;
        }
        cursor.left.resolveType(scope);
        for (int i = 0, end = this.arity; i < end; ++i) {
            this.referencesTable[i].nonRecursiveResolveTypeUpwards(scope);
        }
        this.nonRecursiveResolveTypeUpwards(scope);
        return this.resolvedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (this.referencesTable == null) {
            super.traverse(visitor, scope);
        }
        else {
            if (visitor.visit(this, scope)) {
                int restart;
                for (restart = this.arity - 1; restart >= 0; --restart) {
                    if (!visitor.visit(this.referencesTable[restart], scope)) {
                        visitor.endVisit(this.referencesTable[restart], scope);
                        break;
                    }
                }
                if (++restart == 0) {
                    this.referencesTable[0].left.traverse(visitor, scope);
                }
                for (int i = restart, end = this.arity; i < end; ++i) {
                    this.referencesTable[i].right.traverse(visitor, scope);
                    visitor.endVisit(this.referencesTable[i], scope);
                }
                this.right.traverse(visitor, scope);
            }
            visitor.endVisit(this, scope);
        }
    }
    
    public void tuneArityMax() {
        if (this.arityMax < 160) {
            this.arityMax *= 2;
        }
    }
}
