package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.util.FloatUtil;
import org.eclipse.jdt.core.compiler.CharOperation;

public class DoubleLiteral extends NumberLiteral
{
    double value;
    
    public DoubleLiteral(final char[] token, final int s, final int e) {
        super(token, s, e);
    }
    
    @Override
    public void computeConstant() {
        final boolean containsUnderscores = CharOperation.indexOf('_', this.source) > 0;
        if (containsUnderscores) {
            this.source = CharOperation.remove(this.source, '_');
        }
        Double computedValue;
        try {
            computedValue = Double.valueOf(String.valueOf(this.source));
        }
        catch (final NumberFormatException ex) {
            try {
                final double v = FloatUtil.valueOfHexDoubleLiteral(this.source);
                if (v == Double.POSITIVE_INFINITY) {
                    return;
                }
                if (Double.isNaN(v)) {
                    return;
                }
                this.value = v;
                this.constant = DoubleConstant.fromValue(v);
            }
            catch (final NumberFormatException ex2) {}
            return;
        }
        final double doubleValue = computedValue;
        if (doubleValue > Double.MAX_VALUE) {
            return;
        }
        Label_0268: {
            if (doubleValue < Double.MIN_VALUE) {
                boolean isHexaDecimal = false;
                for (int i = 0; i < this.source.length; ++i) {
                    switch (this.source[i]) {
                        case '.':
                        case '0': {
                            break;
                        }
                        case 'X':
                        case 'x': {
                            isHexaDecimal = true;
                            break;
                        }
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'd':
                        case 'e':
                        case 'f': {
                            if (isHexaDecimal) {
                                return;
                            }
                            break Label_0268;
                        }
                        case 'P':
                        case 'p': {
                            break Label_0268;
                        }
                        default: {
                            return;
                        }
                    }
                }
            }
        }
        this.value = doubleValue;
        this.constant = DoubleConstant.fromValue(this.value);
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
    public TypeBinding literalType(final BlockScope scope) {
        return TypeBinding.DOUBLE;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
