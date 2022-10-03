package org.apache.xmlbeans.impl.jam.annotation;

import org.apache.xmlbeans.impl.jam.internal.elements.AnnotationValueImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import java.util.ArrayList;
import java.util.List;

public class DefaultAnnotationProxy extends AnnotationProxy
{
    private List mValues;
    
    public DefaultAnnotationProxy() {
        this.mValues = new ArrayList();
    }
    
    @Override
    public JAnnotationValue[] getValues() {
        final JAnnotationValue[] out = new JAnnotationValue[this.mValues.size()];
        this.mValues.toArray(out);
        return out;
    }
    
    @Override
    public void setValue(String name, final Object value, final JClass type) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        name = name.trim();
        this.mValues.add(new AnnotationValueImpl((ElementContext)this.getLogger(), name, value, type));
    }
}
