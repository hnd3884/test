package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;

public class BadAttributeValueExpException extends Exception
{
    private static final long serialVersionUID = -3105272988410493376L;
    private Object val;
    
    public BadAttributeValueExpException(final Object o) {
        this.val = ((o == null) ? null : o.toString());
    }
    
    @Override
    public String toString() {
        return "BadAttributeValueException: " + this.val;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Object value = objectInputStream.readFields().get("val", null);
        if (value == null) {
            this.val = null;
        }
        else if (value instanceof String) {
            this.val = value;
        }
        else if (System.getSecurityManager() == null || value instanceof Long || value instanceof Integer || value instanceof Float || value instanceof Double || value instanceof Byte || value instanceof Short || value instanceof Boolean) {
            this.val = value.toString();
        }
        else {
            this.val = System.identityHashCode(value) + "@" + value.getClass().getName();
        }
    }
}
