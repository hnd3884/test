package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class ConstantTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 6374440726369055124L;
    public static final Transformer NULL_INSTANCE;
    private final Object iConstant;
    
    public static Transformer getInstance(final Object constantToReturn) {
        if (constantToReturn == null) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        return new ConstantTransformer(constantToReturn);
    }
    
    public ConstantTransformer(final Object constantToReturn) {
        this.iConstant = constantToReturn;
    }
    
    public Object transform(final Object input) {
        return this.iConstant;
    }
    
    public Object getConstant() {
        return this.iConstant;
    }
    
    static {
        NULL_INSTANCE = new ConstantTransformer(null);
    }
}
