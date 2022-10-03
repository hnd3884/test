package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public final class MemberTypeBinding extends NestedTypeBinding
{
    public MemberTypeBinding(final char[][] compoundName, final ClassScope scope, final SourceTypeBinding enclosingType) {
        super(compoundName, scope, enclosingType);
        this.tagBits |= 0x80CL;
    }
    
    public MemberTypeBinding(final MemberTypeBinding prototype) {
        super(prototype);
    }
    
    void checkSyntheticArgsAndFields() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isStatic()) {
            return;
        }
        if (this.isInterface()) {
            return;
        }
        if (!this.isPrototype()) {
            ((MemberTypeBinding)this.prototype).checkSyntheticArgsAndFields();
            return;
        }
        this.addSyntheticArgumentAndField(this.enclosingType);
    }
    
    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        if (!this.isPrototype()) {
            return this.prototype.constantPoolName();
        }
        return this.constantPoolName = CharOperation.concat(this.enclosingType().constantPoolName(), this.sourceName, '$');
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        final MemberTypeBinding copy = new MemberTypeBinding(this);
        copy.enclosingType = (SourceTypeBinding)outerType;
        return copy;
    }
    
    @Override
    public void initializeDeprecatedAnnotationTagBits() {
        if (!this.isPrototype()) {
            this.prototype.initializeDeprecatedAnnotationTagBits();
            return;
        }
        if ((this.tagBits & 0x400000000L) == 0x0L) {
            super.initializeDeprecatedAnnotationTagBits();
            if ((this.tagBits & 0x400000000000L) == 0x0L) {
                final ReferenceBinding enclosing;
                if (((enclosing = this.enclosingType()).tagBits & 0x400000000L) == 0x0L) {
                    enclosing.initializeDeprecatedAnnotationTagBits();
                }
                if (enclosing.isViewedAsDeprecated()) {
                    this.modifiers |= 0x200000;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        return "Member type : " + new String(this.sourceName()) + " " + super.toString();
    }
}
