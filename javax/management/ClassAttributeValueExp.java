package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;

class ClassAttributeValueExp extends AttributeValueExp
{
    private static final long oldSerialVersionUID = -2212731951078526753L;
    private static final long newSerialVersionUID = -1081892073854801359L;
    private static final long serialVersionUID;
    private String attr;
    
    public ClassAttributeValueExp() {
        super("Class");
        this.attr = "Class";
    }
    
    @Override
    public ValueExp apply(final ObjectName objectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
        final Object value = this.getValue(objectName);
        if (value instanceof String) {
            return new StringValueExp((String)value);
        }
        throw new BadAttributeValueExpException(value);
    }
    
    @Override
    public String toString() {
        return this.attr;
    }
    
    protected Object getValue(final ObjectName objectName) {
        try {
            return QueryEval.getMBeanServer().getObjectInstance(objectName).getClassName();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    static {
        int n = 0;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            n = ((s != null && s.equals("1.0")) ? 1 : 0);
        }
        catch (final Exception ex) {}
        if (n != 0) {
            serialVersionUID = -2212731951078526753L;
        }
        else {
            serialVersionUID = -1081892073854801359L;
        }
    }
}
