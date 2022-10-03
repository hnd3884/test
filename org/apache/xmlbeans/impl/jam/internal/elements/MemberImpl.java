package org.apache.xmlbeans.impl.jam.internal.elements;

import java.lang.reflect.Modifier;
import org.apache.xmlbeans.impl.jam.JElement;
import org.apache.xmlbeans.impl.jam.JMember;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.mutable.MMember;

public abstract class MemberImpl extends AnnotatedElementImpl implements MMember
{
    private int mModifiers;
    
    protected MemberImpl(final ElementImpl parent) {
        super(parent);
        this.mModifiers = 0;
    }
    
    protected MemberImpl(final ElementContext ctx) {
        super(ctx);
        this.mModifiers = 0;
    }
    
    @Override
    public JClass getContainingClass() {
        final JElement p = this.getParent();
        if (p instanceof JClass) {
            return (JClass)p;
        }
        if (p instanceof JMember) {
            return ((JMember)p).getContainingClass();
        }
        return null;
    }
    
    @Override
    public int getModifiers() {
        return this.mModifiers;
    }
    
    @Override
    public boolean isPackagePrivate() {
        return !this.isPrivate() && !this.isPublic() && !this.isProtected();
    }
    
    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }
    
    @Override
    public boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }
    
    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }
    
    @Override
    public void setModifiers(final int modifiers) {
        this.mModifiers = modifiers;
    }
}
