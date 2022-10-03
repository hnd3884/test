package org.apache.commons.math3.complex;

import org.apache.commons.math3.FieldElement;
import java.io.Serializable;
import org.apache.commons.math3.Field;

public class ComplexField implements Field<Complex>, Serializable
{
    private static final long serialVersionUID = -6130362688700788798L;
    
    private ComplexField() {
    }
    
    public static ComplexField getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public Complex getOne() {
        return Complex.ONE;
    }
    
    public Complex getZero() {
        return Complex.ZERO;
    }
    
    public Class<? extends FieldElement<Complex>> getRuntimeClass() {
        return Complex.class;
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final ComplexField INSTANCE;
        
        static {
            INSTANCE = new ComplexField(null);
        }
    }
}
