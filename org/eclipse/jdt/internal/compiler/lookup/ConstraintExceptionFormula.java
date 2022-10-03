package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;

public class ConstraintExceptionFormula extends ConstraintFormula
{
    FunctionalExpression left;
    
    public ConstraintExceptionFormula(final FunctionalExpression left, final TypeBinding type) {
        this.left = left;
        this.right = type;
        this.relation = 7;
    }
    
    @Override
    public Object reduce(final InferenceContext18 inferenceContext) {
        final Scope scope = inferenceContext.scope;
        if (!this.right.isFunctionalInterface(scope)) {
            return ConstraintExceptionFormula.FALSE;
        }
        final MethodBinding sam = this.right.getSingleAbstractMethod(scope, true);
        if (sam == null) {
            return ConstraintExceptionFormula.FALSE;
        }
        if (this.left instanceof LambdaExpression) {
            if (((LambdaExpression)this.left).argumentsTypeElided()) {
                for (int nParam = sam.parameters.length, i = 0; i < nParam; ++i) {
                    if (!sam.parameters[i].isProperType(true)) {
                        return ConstraintExceptionFormula.FALSE;
                    }
                }
            }
            if (sam.returnType != TypeBinding.VOID && !sam.returnType.isProperType(true)) {
                return ConstraintExceptionFormula.FALSE;
            }
        }
        else if (!((ReferenceExpression)this.left).isExactMethodReference()) {
            for (int nParam = sam.parameters.length, i = 0; i < nParam; ++i) {
                if (!sam.parameters[i].isProperType(true)) {
                    return ConstraintExceptionFormula.FALSE;
                }
            }
            if (sam.returnType != TypeBinding.VOID && !sam.returnType.isProperType(true)) {
                return ConstraintExceptionFormula.FALSE;
            }
        }
        final TypeBinding[] thrown = sam.thrownExceptions;
        final InferenceVariable[] e = new InferenceVariable[thrown.length];
        int n = 0;
        for (int j = 0; j < thrown.length; ++j) {
            if (!thrown[j].isProperType(true)) {
                e[n++] = (InferenceVariable)thrown[j];
            }
        }
        if (n == 0) {
            return ConstraintExceptionFormula.TRUE;
        }
        TypeBinding[] ePrime = null;
        if (this.left instanceof LambdaExpression) {
            final LambdaExpression lambda = ((LambdaExpression)this.left).resolveExpressionExpecting(this.right, inferenceContext.scope, inferenceContext);
            if (lambda == null) {
                return ConstraintExceptionFormula.TRUE;
            }
            final Set<TypeBinding> ePrimeSet = lambda.getThrownExceptions();
            ePrime = ePrimeSet.toArray(new TypeBinding[ePrimeSet.size()]);
        }
        else {
            final ReferenceExpression referenceExpression = ((ReferenceExpression)this.left).resolveExpressionExpecting(this.right, scope, inferenceContext);
            final MethodBinding method = (referenceExpression != null) ? referenceExpression.binding : null;
            if (method != null) {
                ePrime = method.thrownExceptions;
            }
        }
        if (ePrime == null) {
            return ConstraintExceptionFormula.TRUE;
        }
        final int m = ePrime.length;
        final List<ConstraintFormula> result = new ArrayList<ConstraintFormula>();
    Label_0528:
        for (int k = 0; k < m; ++k) {
            if (!ePrime[k].isUncheckedException(false)) {
                for (int l = 0; l < thrown.length; ++l) {
                    if (thrown[l].isProperType(true) && ePrime[k].isCompatibleWith(thrown[l])) {
                        continue Label_0528;
                    }
                }
                for (int l = 0; l < n; ++l) {
                    result.add(ConstraintTypeFormula.create(ePrime[k], e[l], 2));
                }
            }
        }
        for (int j2 = 0; j2 < n; ++j2) {
            inferenceContext.currentBounds.inThrows.add(e[j2].prototype());
        }
        return result.toArray(new ConstraintFormula[result.size()]);
    }
    
    @Override
    Collection<InferenceVariable> inputVariables(final InferenceContext18 context) {
        if (this.left instanceof LambdaExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList(this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                final LambdaExpression lambda = (LambdaExpression)this.left;
                final MethodBinding sam = this.right.getSingleAbstractMethod(context.scope, true);
                final Set<InferenceVariable> variables = new HashSet<InferenceVariable>();
                if (lambda.argumentsTypeElided()) {
                    for (int len = sam.parameters.length, i = 0; i < len; ++i) {
                        sam.parameters[i].collectInferenceVariables(variables);
                    }
                }
                if (sam.returnType != TypeBinding.VOID) {
                    sam.returnType.collectInferenceVariables(variables);
                }
                return variables;
            }
        }
        else if (this.left instanceof ReferenceExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList(this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                final MethodBinding sam2 = this.right.getSingleAbstractMethod(context.scope, true);
                final Set<InferenceVariable> variables2 = new HashSet<InferenceVariable>();
                for (int len2 = sam2.parameters.length, j = 0; j < len2; ++j) {
                    sam2.parameters[j].collectInferenceVariables(variables2);
                }
                sam2.returnType.collectInferenceVariables(variables2);
                return variables2;
            }
        }
        return ConstraintExceptionFormula.EMPTY_VARIABLE_LIST;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer().append('\u27e8');
        this.left.printExpression(4, buf);
        buf.append(" \u2286throws ");
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}
