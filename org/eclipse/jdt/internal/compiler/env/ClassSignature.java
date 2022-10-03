package org.eclipse.jdt.internal.compiler.env;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;

public class ClassSignature
{
    char[] className;
    
    public ClassSignature(final char[] className) {
        this.className = className;
    }
    
    public char[] getTypeName() {
        return this.className;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.className);
        buffer.append(".class");
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + CharOperation.hashCode(this.className);
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
        final ClassSignature other = (ClassSignature)obj;
        return Arrays.equals(this.className, other.className);
    }
}
