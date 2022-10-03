package org.apache.commons.collections.functors;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.collections.Closure;

public class ForClosure implements Closure, Serializable
{
    private static final long serialVersionUID = -1190120533393621674L;
    private final int iCount;
    private final Closure iClosure;
    
    public static Closure getInstance(final int count, final Closure closure) {
        if (count <= 0 || closure == null) {
            return NOPClosure.INSTANCE;
        }
        if (count == 1) {
            return closure;
        }
        return new ForClosure(count, closure);
    }
    
    public ForClosure(final int count, final Closure closure) {
        this.iCount = count;
        this.iClosure = closure;
    }
    
    public void execute(final Object input) {
        for (int i = 0; i < this.iCount; ++i) {
            this.iClosure.execute(input);
        }
    }
    
    public Closure getClosure() {
        return this.iClosure;
    }
    
    public int getCount() {
        return this.iCount;
    }
    
    private void writeObject(final ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(ForClosure.class);
        os.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(ForClosure.class);
        is.defaultReadObject();
    }
}
