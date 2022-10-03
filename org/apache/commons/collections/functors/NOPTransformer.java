package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class NOPTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 2133891748318574490L;
    public static final Transformer INSTANCE;
    
    public static Transformer getInstance() {
        return NOPTransformer.INSTANCE;
    }
    
    private NOPTransformer() {
    }
    
    public Object transform(final Object input) {
        return input;
    }
    
    static {
        INSTANCE = new NOPTransformer();
    }
}
