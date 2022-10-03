package org.apache.commons.collections4.functors;

import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public class ChainedTransformer<T> implements Transformer<T, T>, Serializable
{
    private static final long serialVersionUID = 3514945074733160196L;
    private final Transformer<? super T, ? extends T>[] iTransformers;
    
    public static <T> Transformer<T, T> chainedTransformer(final Transformer<? super T, ? extends T>... transformers) {
        FunctorUtils.validate((Transformer<?, ?>[])transformers);
        if (transformers.length == 0) {
            return NOPTransformer.nopTransformer();
        }
        return new ChainedTransformer<T>(transformers);
    }
    
    public static <T> Transformer<T, T> chainedTransformer(final Collection<? extends Transformer<? super T, ? extends T>> transformers) {
        if (transformers == null) {
            throw new NullPointerException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.nopTransformer();
        }
        final Transformer<T, T>[] cmds = transformers.toArray(new Transformer[transformers.size()]);
        FunctorUtils.validate((Transformer<?, ?>[])cmds);
        return new ChainedTransformer<T>(false, (Transformer<? super T, ? extends T>[])cmds);
    }
    
    private ChainedTransformer(final boolean clone, final Transformer<? super T, ? extends T>[] transformers) {
        this.iTransformers = (clone ? FunctorUtils.copy(transformers) : transformers);
    }
    
    public ChainedTransformer(final Transformer<? super T, ? extends T>... transformers) {
        this(true, transformers);
    }
    
    @Override
    public T transform(T object) {
        for (final Transformer<? super T, ? extends T> iTransformer : this.iTransformers) {
            object = (T)iTransformer.transform(object);
        }
        return object;
    }
    
    public Transformer<? super T, ? extends T>[] getTransformers() {
        return FunctorUtils.copy(this.iTransformers);
    }
}
