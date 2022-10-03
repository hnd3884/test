package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MirroredTypeException extends MirroredTypesException
{
    private static final long serialVersionUID = 269L;
    private transient TypeMirror type;
    
    public MirroredTypeException(final TypeMirror type) {
        super("Attempt to access Class object for TypeMirror " + type.toString(), type);
        this.type = type;
    }
    
    public TypeMirror getTypeMirror() {
        return this.type;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.type = null;
        this.types = null;
    }
}
