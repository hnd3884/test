package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public abstract class VariableBinding extends Binding
{
    public int modifiers;
    public TypeBinding type;
    public char[] name;
    protected Constant constant;
    public int id;
    public long tagBits;
    
    public VariableBinding(final char[] name, final TypeBinding type, final int modifiers, final Constant constant) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.constant = constant;
        if (type != null) {
            this.tagBits |= (type.tagBits & 0x80L);
        }
    }
    
    public Constant constant() {
        return this.constant;
    }
    
    public Constant constant(final Scope scope) {
        return this.constant();
    }
    
    @Override
    public abstract AnnotationBinding[] getAnnotations();
    
    public final boolean isBlankFinal() {
        return (this.modifiers & 0x4000000) != 0x0;
    }
    
    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0x0;
    }
    
    public final boolean isEffectivelyFinal() {
        return (this.tagBits & 0x800L) != 0x0L;
    }
    
    public boolean isNonNull() {
        return (this.tagBits & 0x100000000000000L) != 0x0L || (this.type != null && (this.type.tagBits & 0x100000000000000L) != 0x0L);
    }
    
    public boolean isNullable() {
        return (this.tagBits & 0x80000000000000L) != 0x0L || (this.type != null && (this.type.tagBits & 0x80000000000000L) != 0x0L);
    }
    
    @Override
    public char[] readableName() {
        return this.name;
    }
    
    public void setConstant(final Constant constant) {
        this.constant = constant;
    }
    
    @Override
    public String toString() {
        final StringBuffer output = new StringBuffer(10);
        ASTNode.printModifiers(this.modifiers, output);
        if ((this.modifiers & 0x2000000) != 0x0) {
            output.append("[unresolved] ");
        }
        output.append((this.type != null) ? this.type.debugName() : "<no type>");
        output.append(" ");
        output.append((this.name != null) ? new String(this.name) : "<no name>");
        return output.toString();
    }
}
