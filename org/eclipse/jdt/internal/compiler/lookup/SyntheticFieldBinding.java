package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class SyntheticFieldBinding extends FieldBinding
{
    public int index;
    
    public SyntheticFieldBinding(final char[] name, final TypeBinding type, final int modifiers, final ReferenceBinding declaringClass, final Constant constant, final int index) {
        super(name, type, modifiers, declaringClass, constant);
        this.index = index;
        this.tagBits |= 0x600000000L;
    }
}
