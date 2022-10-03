package org.apache.commons.collections.functors;

import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class ChainedTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 3514945074733160196L;
    private final Transformer[] iTransformers;
    
    public static Transformer getInstance(Transformer[] transformers) {
        FunctorUtils.validate(transformers);
        if (transformers.length == 0) {
            return NOPTransformer.INSTANCE;
        }
        transformers = FunctorUtils.copy(transformers);
        return new ChainedTransformer(transformers);
    }
    
    public static Transformer getInstance(final Collection transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.INSTANCE;
        }
        final Transformer[] cmds = new Transformer[transformers.size()];
        int i = 0;
        final Iterator it = transformers.iterator();
        while (it.hasNext()) {
            cmds[i++] = it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedTransformer(cmds);
    }
    
    public static Transformer getInstance(final Transformer transformer1, final Transformer transformer2) {
        if (transformer1 == null || transformer2 == null) {
            throw new IllegalArgumentException("Transformers must not be null");
        }
        final Transformer[] transformers = { transformer1, transformer2 };
        return new ChainedTransformer(transformers);
    }
    
    public ChainedTransformer(final Transformer[] transformers) {
        this.iTransformers = transformers;
    }
    
    public Object transform(Object object) {
        for (int i = 0; i < this.iTransformers.length; ++i) {
            object = this.iTransformers[i].transform(object);
        }
        return object;
    }
    
    public Transformer[] getTransformers() {
        return this.iTransformers;
    }
}
