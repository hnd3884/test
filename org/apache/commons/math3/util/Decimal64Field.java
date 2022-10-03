package org.apache.commons.math3.util;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.Field;

public class Decimal64Field implements Field<Decimal64>
{
    private static final Decimal64Field INSTANCE;
    
    private Decimal64Field() {
    }
    
    public static final Decimal64Field getInstance() {
        return Decimal64Field.INSTANCE;
    }
    
    public Decimal64 getZero() {
        return Decimal64.ZERO;
    }
    
    public Decimal64 getOne() {
        return Decimal64.ONE;
    }
    
    public Class<? extends FieldElement<Decimal64>> getRuntimeClass() {
        return Decimal64.class;
    }
    
    static {
        INSTANCE = new Decimal64Field();
    }
}
