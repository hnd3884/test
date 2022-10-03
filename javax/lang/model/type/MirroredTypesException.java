package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class MirroredTypesException extends RuntimeException
{
    private static final long serialVersionUID = 269L;
    transient List<? extends TypeMirror> types;
    
    MirroredTypesException(final String s, final TypeMirror typeMirror) {
        super(s);
        final ArrayList list = new ArrayList();
        list.add(typeMirror);
        this.types = (List<? extends TypeMirror>)Collections.unmodifiableList((List<?>)list);
    }
    
    public MirroredTypesException(final List<? extends TypeMirror> list) {
        final ArrayList list2;
        super("Attempt to access Class objects for TypeMirrors " + (list2 = new ArrayList((Collection<? extends E>)list)).toString());
        this.types = (List<? extends TypeMirror>)Collections.unmodifiableList((List<?>)list2);
    }
    
    public List<? extends TypeMirror> getTypeMirrors() {
        return this.types;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.types = null;
    }
}
