package org.msgpack.template.builder.beans;

import org.apache.harmony.beans.BeansUtils;

public class Expression extends Statement
{
    boolean valueIsDefined;
    Object value;
    
    public Expression(final Object value, final Object target, final String methodName, final Object[] arguments) {
        super(target, methodName, arguments);
        this.valueIsDefined = false;
        this.value = value;
        this.valueIsDefined = true;
    }
    
    public Expression(final Object target, final String methodName, final Object[] arguments) {
        super(target, methodName, arguments);
        this.valueIsDefined = false;
        this.value = null;
        this.valueIsDefined = false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (!this.valueIsDefined) {
            sb.append("<unbound>");
        }
        else if (this.value == null) {
            sb.append("null");
        }
        else {
            final Class<?> clazz = this.value.getClass();
            sb.append((clazz == String.class) ? "\"\"" : BeansUtils.idOfClass(clazz));
        }
        sb.append('=');
        sb.append(super.toString());
        return sb.toString();
    }
    
    public void setValue(final Object value) {
        this.value = value;
        this.valueIsDefined = true;
    }
    
    public Object getValue() throws Exception {
        if (!this.valueIsDefined) {
            this.value = this.invokeMethod();
            this.valueIsDefined = true;
        }
        return this.value;
    }
}
