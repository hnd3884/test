package java.lang.instrument;

public final class ClassDefinition
{
    private final Class<?> mClass;
    private final byte[] mClassFile;
    
    public ClassDefinition(final Class<?> mClass, final byte[] mClassFile) {
        if (mClass == null || mClassFile == null) {
            throw new NullPointerException();
        }
        this.mClass = mClass;
        this.mClassFile = mClassFile;
    }
    
    public Class<?> getDefinitionClass() {
        return this.mClass;
    }
    
    public byte[] getDefinitionClassFile() {
        return this.mClassFile;
    }
}
