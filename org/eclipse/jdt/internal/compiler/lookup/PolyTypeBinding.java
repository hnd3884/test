package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;

public class PolyTypeBinding extends TypeBinding
{
    Expression expression;
    boolean vanillaCompatibilty;
    
    public PolyTypeBinding(final Expression expression) {
        this.vanillaCompatibilty = true;
        this.expression = expression;
    }
    
    @Override
    public char[] constantPoolName() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PackageBinding getPackage() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding left, final Scope scope) {
        return this.vanillaCompatibilty ? this.expression.isCompatibleWith(left, scope) : this.expression.isBoxingCompatibleWith(left, scope);
    }
    
    @Override
    public boolean isPotentiallyCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return this.expression.isPotentiallyCompatibleWith(targetType, scope);
    }
    
    @Override
    public boolean isPolyType() {
        return true;
    }
    
    @Override
    public boolean isFunctionalType() {
        return this.expression.isFunctionalType();
    }
    
    @Override
    public char[] qualifiedSourceName() {
        return this.readableName();
    }
    
    @Override
    public char[] sourceName() {
        return this.readableName();
    }
    
    @Override
    public char[] readableName() {
        return this.expression.printExpression(0, new StringBuffer()).toString().toCharArray();
    }
    
    @Override
    public char[] shortReadableName() {
        return (this.expression instanceof LambdaExpression) ? ((LambdaExpression)this.expression).printExpression(0, new StringBuffer(), true).toString().toCharArray() : this.readableName();
    }
    
    @Override
    public boolean sIsMoreSpecific(final TypeBinding s, final TypeBinding t, final Scope scope) {
        return this.expression.sIsMoreSpecific(s, t, scope);
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("PolyTypeBinding for: ");
        return this.expression.printExpression(0, buffer).toString();
    }
    
    @Override
    public int kind() {
        return 65540;
    }
    
    public TypeBinding computeBoxingType() {
        final PolyTypeBinding type = new PolyTypeBinding(this.expression);
        type.vanillaCompatibilty = !this.vanillaCompatibilty;
        return type;
    }
}
