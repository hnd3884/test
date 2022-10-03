package sun.misc;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class ClassFileTransformer
{
    private static final List<ClassFileTransformer> transformers;
    
    public static void add(final ClassFileTransformer classFileTransformer) {
        synchronized (ClassFileTransformer.transformers) {
            ClassFileTransformer.transformers.add(classFileTransformer);
        }
    }
    
    public static ClassFileTransformer[] getTransformers() {
        synchronized (ClassFileTransformer.transformers) {
            return ClassFileTransformer.transformers.toArray(new ClassFileTransformer[ClassFileTransformer.transformers.size()]);
        }
    }
    
    public abstract byte[] transform(final byte[] p0, final int p1, final int p2) throws ClassFormatError;
    
    static {
        transformers = new ArrayList<ClassFileTransformer>();
    }
}
