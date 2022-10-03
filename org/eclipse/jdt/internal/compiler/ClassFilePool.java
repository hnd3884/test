package org.eclipse.jdt.internal.compiler;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public class ClassFilePool
{
    public static final int POOL_SIZE = 25;
    ClassFile[] classFiles;
    
    private ClassFilePool() {
        this.classFiles = new ClassFile[25];
    }
    
    public static ClassFilePool newInstance() {
        return new ClassFilePool();
    }
    
    public synchronized ClassFile acquire(final SourceTypeBinding typeBinding) {
        for (int i = 0; i < 25; ++i) {
            final ClassFile classFile = this.classFiles[i];
            if (classFile == null) {
                final ClassFile newClassFile = new ClassFile(typeBinding);
                this.classFiles[i] = newClassFile;
                newClassFile.isShared = true;
                return newClassFile;
            }
            if (!classFile.isShared) {
                classFile.reset(typeBinding);
                classFile.isShared = true;
                return classFile;
            }
        }
        return new ClassFile(typeBinding);
    }
    
    public synchronized void release(final ClassFile classFile) {
        classFile.isShared = false;
    }
    
    public void reset() {
        Arrays.fill(this.classFiles, null);
    }
}
