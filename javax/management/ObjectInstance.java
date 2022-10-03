package javax.management;

import java.io.Serializable;

public class ObjectInstance implements Serializable
{
    private static final long serialVersionUID = -4099952623687795850L;
    private ObjectName name;
    private String className;
    
    public ObjectInstance(final String s, final String s2) throws MalformedObjectNameException {
        this(new ObjectName(s), s2);
    }
    
    public ObjectInstance(final ObjectName name, final String className) {
        if (name.isPattern()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + name.toString()));
        }
        this.name = name;
        this.className = className;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ObjectInstance)) {
            return false;
        }
        final ObjectInstance objectInstance = (ObjectInstance)o;
        if (!this.name.equals(objectInstance.getObjectName())) {
            return false;
        }
        if (this.className == null) {
            return objectInstance.getClassName() == null;
        }
        return this.className.equals(objectInstance.getClassName());
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() ^ ((this.className == null) ? 0 : this.className.hashCode());
    }
    
    public ObjectName getObjectName() {
        return this.name;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    @Override
    public String toString() {
        return this.getClassName() + "[" + this.getObjectName() + "]";
    }
}
