package org.apache.xmlbeans.impl.jam.annotation;

import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.xmlbeans.impl.jam.internal.elements.AnnotationValueImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.JClass;
import java.util.List;

public abstract class TypedAnnotationProxyBase extends AnnotationProxy
{
    private List mValues;
    
    protected TypedAnnotationProxyBase() {
        this.mValues = null;
    }
    
    @Override
    public void setValue(final String name, final Object value, final JClass type) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (this.mValues == null) {
            this.mValues = new ArrayList();
        }
        this.mValues.add(new AnnotationValueImpl((ElementContext)this.mContext, name, value, type));
        final Method m = this.getSetterFor(name, value.getClass());
        if (m == null) {
            return;
        }
        try {
            m.invoke(this, value);
        }
        catch (final IllegalAccessException e) {
            this.getLogger().warning(e);
        }
        catch (final InvocationTargetException e2) {
            this.getLogger().warning(e2);
        }
    }
    
    @Override
    public JAnnotationValue[] getValues() {
        if (this.mValues == null) {
            return new JAnnotationValue[0];
        }
        final JAnnotationValue[] out = new JAnnotationValue[this.mValues.size()];
        this.mValues.toArray(out);
        return out;
    }
    
    protected Method getSetterFor(final String memberName, final Class valueType) {
        try {
            return this.getClass().getMethod("set" + memberName, valueType);
        }
        catch (final NoSuchMethodException nsme) {
            this.getLogger().warning(nsme);
            return null;
        }
    }
}
