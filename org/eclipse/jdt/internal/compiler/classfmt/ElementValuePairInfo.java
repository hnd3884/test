package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;

public class ElementValuePairInfo implements IBinaryElementValuePair
{
    static final ElementValuePairInfo[] NoMembers;
    private char[] name;
    private Object value;
    
    static {
        NoMembers = new ElementValuePairInfo[0];
    }
    
    ElementValuePairInfo(final char[] name, final Object value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public char[] getName() {
        return this.name;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.name);
        buffer.append('=');
        if (this.value instanceof Object[]) {
            final Object[] values = (Object[])this.value;
            buffer.append('{');
            for (int i = 0, l = values.length; i < l; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(values[i]);
            }
            buffer.append('}');
        }
        else {
            buffer.append(this.value);
        }
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + CharOperation.hashCode(this.name);
        result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ElementValuePairInfo other = (ElementValuePairInfo)obj;
        if (!Arrays.equals(this.name, other.name)) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        }
        else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
