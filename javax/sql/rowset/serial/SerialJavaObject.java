package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Arrays;
import sun.reflect.CallerSensitive;
import sun.reflect.misc.ReflectUtil;
import sun.reflect.Reflection;
import javax.sql.rowset.RowSetWarning;
import java.util.Vector;
import java.lang.reflect.Field;
import java.io.Serializable;

public class SerialJavaObject implements Serializable, Cloneable
{
    private Object obj;
    private transient Field[] fields;
    static final long serialVersionUID = -1465795139032831023L;
    Vector<RowSetWarning> chain;
    
    public SerialJavaObject(final Object obj) throws SerialException {
        final Class<?> class1 = obj.getClass();
        if (!(obj instanceof Serializable)) {
            this.setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
        }
        this.fields = class1.getFields();
        if (hasStaticFields(this.fields)) {
            throw new SerialException("Located static fields in object instance. Cannot serialize");
        }
        this.obj = obj;
    }
    
    public Object getObject() throws SerialException {
        return this.obj;
    }
    
    @CallerSensitive
    public Field[] getFields() throws SerialException {
        if (this.fields != null) {
            final Class<?> class1 = this.obj.getClass();
            if (System.getSecurityManager() != null && ReflectUtil.needsPackageAccessCheck(Reflection.getCallerClass().getClassLoader(), class1.getClassLoader())) {
                ReflectUtil.checkPackageAccess(class1);
            }
            return class1.getFields();
        }
        throw new SerialException("SerialJavaObject does not contain a serialized object instance");
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof SerialJavaObject && this.obj.equals(((SerialJavaObject)o).obj));
    }
    
    @Override
    public int hashCode() {
        return 31 + this.obj.hashCode();
    }
    
    public Object clone() {
        try {
            final SerialJavaObject serialJavaObject = (SerialJavaObject)super.clone();
            serialJavaObject.fields = Arrays.copyOf(this.fields, this.fields.length);
            if (this.chain != null) {
                serialJavaObject.chain = new Vector<RowSetWarning>(this.chain);
            }
            return serialJavaObject;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void setWarning(final RowSetWarning rowSetWarning) {
        if (this.chain == null) {
            this.chain = new Vector<RowSetWarning>();
        }
        this.chain.add(rowSetWarning);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Vector vector = (Vector)fields.get("chain", null);
        if (vector != null) {
            this.chain = new Vector<RowSetWarning>(vector);
        }
        this.obj = fields.get("obj", null);
        if (this.obj == null) {
            throw new IOException("Object cannot be null!");
        }
        this.fields = this.obj.getClass().getFields();
        if (hasStaticFields(this.fields)) {
            throw new IOException("Located static fields in object instance. Cannot serialize");
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("obj", this.obj);
        putFields.put("chain", this.chain);
        objectOutputStream.writeFields();
    }
    
    private static boolean hasStaticFields(final Field[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i].getModifiers() == 8) {
                return true;
            }
        }
        return false;
    }
}
