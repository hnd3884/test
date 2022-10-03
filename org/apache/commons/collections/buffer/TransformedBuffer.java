package org.apache.commons.collections.buffer;

import java.util.Collection;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.TransformedCollection;

public class TransformedBuffer extends TransformedCollection implements Buffer
{
    private static final long serialVersionUID = -7901091318986132033L;
    
    public static Buffer decorate(final Buffer buffer, final Transformer transformer) {
        return new TransformedBuffer(buffer, transformer);
    }
    
    protected TransformedBuffer(final Buffer buffer, final Transformer transformer) {
        super(buffer, transformer);
    }
    
    protected Buffer getBuffer() {
        return (Buffer)this.collection;
    }
    
    public Object get() {
        return this.getBuffer().get();
    }
    
    public Object remove() {
        return this.getBuffer().remove();
    }
}
