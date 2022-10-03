package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;

public class TrueLiteral extends MagicLiteral
{
    static final char[] source;
    
    static {
        source = new char[] { 't', 'r', 'u', 'e' };
    }
    
    public TrueLiteral(final int s, final int e) {
        super(s, e);
    }
    
    @Override
    public void computeConstant() {
        this.constant = BooleanConstant.fromValue(true);
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired) {
            codeStream.generateConstant(this.constant, this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired && falseLabel == null && trueLabel != null) {
            codeStream.goto_(trueLabel);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public TypeBinding literalType(final BlockScope scope) {
        return TypeBinding.BOOLEAN;
    }
    
    @Override
    public char[] source() {
        return TrueLiteral.source;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
