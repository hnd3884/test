package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JParameter;
import java.lang.reflect.Modifier;
import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;

public final class ConstructorImpl extends InvokableImpl implements MConstructor
{
    ConstructorImpl(final ClassImpl containingClass) {
        super(containingClass);
        this.setSimpleName(containingClass.getSimpleName());
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getQualifiedName() {
        final StringWriter sbuf = new StringWriter();
        sbuf.write(Modifier.toString(this.getModifiers()));
        sbuf.write(32);
        sbuf.write(this.getSimpleName());
        sbuf.write(40);
        final JParameter[] params = this.getParameters();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                sbuf.write(params[i].getType().getQualifiedName());
                if (i < params.length - 1) {
                    sbuf.write(44);
                }
            }
        }
        sbuf.write(41);
        return sbuf.toString();
    }
}
