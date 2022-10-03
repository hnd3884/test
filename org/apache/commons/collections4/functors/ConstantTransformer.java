package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public class ConstantTransformer<I, O> implements Transformer<I, O>, Serializable
{
    private static final long serialVersionUID = 6374440726369055124L;
    public static final Transformer NULL_INSTANCE;
    private final O iConstant;
    
    public static <I, O> Transformer<I, O> nullTransformer() {
        return ConstantTransformer.NULL_INSTANCE;
    }
    
    public static <I, O> Transformer<I, O> constantTransformer(final O constantToReturn) {
        if (constantToReturn == null) {
            return nullTransformer();
        }
        return new ConstantTransformer<I, O>(constantToReturn);
    }
    
    public ConstantTransformer(final O constantToReturn) {
        this.iConstant = constantToReturn;
    }
    
    @Override
    public O transform(final I input) {
        return this.iConstant;
    }
    
    public O getConstant() {
        return this.iConstant;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConstantTransformer)) {
            return false;
        }
        final Object otherConstant = ((ConstantTransformer)obj).getConstant();
        return otherConstant == this.getConstant() || (otherConstant != null && otherConstant.equals(this.getConstant()));
    }
    
    @Override
    public int hashCode() {
        int result = "ConstantTransformer".hashCode() << 2;
        if (this.getConstant() != null) {
            result |= this.getConstant().hashCode();
        }
        return result;
    }
    
    static {
        NULL_INSTANCE = new ConstantTransformer(null);
    }
}
