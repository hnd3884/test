package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public abstract class Literal extends Expression
{
    public Literal(final int s, final int e) {
        this.sourceStart = s;
        this.sourceEnd = e;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    public abstract void computeConstant();
    
    public abstract TypeBinding literalType(final BlockScope p0);
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return output.append(this.source());
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.resolvedType = this.literalType(scope);
        this.computeConstant();
        if (this.constant == null) {
            scope.problemReporter().constantOutOfRange(this, this.resolvedType);
            this.constant = Constant.NotAConstant;
        }
        return this.resolvedType;
    }
    
    public abstract char[] source();
}
