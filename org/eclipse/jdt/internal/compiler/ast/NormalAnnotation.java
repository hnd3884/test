package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;

public class NormalAnnotation extends Annotation
{
    public MemberValuePair[] memberValuePairs;
    
    public NormalAnnotation(final TypeReference type, final int sourceStart) {
        this.type = type;
        this.sourceStart = sourceStart;
        this.sourceEnd = type.sourceEnd;
    }
    
    @Override
    public ElementValuePair[] computeElementValuePairs() {
        final int numberOfPairs = (this.memberValuePairs == null) ? 0 : this.memberValuePairs.length;
        if (numberOfPairs == 0) {
            return Binding.NO_ELEMENT_VALUE_PAIRS;
        }
        final ElementValuePair[] pairs = new ElementValuePair[numberOfPairs];
        for (int i = 0; i < numberOfPairs; ++i) {
            pairs[i] = this.memberValuePairs[i].compilerElementPair;
        }
        return pairs;
    }
    
    @Override
    public MemberValuePair[] memberValuePairs() {
        return (this.memberValuePairs == null) ? NormalAnnotation.NoValuePairs : this.memberValuePairs;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        super.printExpression(indent, output);
        output.append('(');
        if (this.memberValuePairs != null) {
            for (int i = 0, max = this.memberValuePairs.length; i < max; ++i) {
                if (i > 0) {
                    output.append(',');
                }
                this.memberValuePairs[i].print(indent, output);
            }
        }
        output.append(')');
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.memberValuePairs != null) {
                for (int memberValuePairsLength = this.memberValuePairs.length, i = 0; i < memberValuePairsLength; ++i) {
                    this.memberValuePairs[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.memberValuePairs != null) {
                for (int memberValuePairsLength = this.memberValuePairs.length, i = 0; i < memberValuePairsLength; ++i) {
                    this.memberValuePairs[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
