package org.apache.commons.collections.functors;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class CloneTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = -8188742709499652567L;
    public static final Transformer INSTANCE;
    
    public static Transformer getInstance() {
        return CloneTransformer.INSTANCE;
    }
    
    private CloneTransformer() {
    }
    
    public Object transform(final Object input) {
        if (input == null) {
            return null;
        }
        return PrototypeFactory.getInstance(input).create();
    }
    
    private void writeObject(final ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(CloneTransformer.class);
        os.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(CloneTransformer.class);
        is.defaultReadObject();
    }
    
    static {
        INSTANCE = new CloneTransformer();
    }
}
