package org.eclipse.jdt.internal.compiler.env;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;

public class EnumConstantSignature
{
    char[] typeName;
    char[] constName;
    
    public EnumConstantSignature(final char[] typeName, final char[] constName) {
        this.typeName = typeName;
        this.constName = constName;
    }
    
    public char[] getTypeName() {
        return this.typeName;
    }
    
    public char[] getEnumConstantName() {
        return this.constName;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.typeName);
        buffer.append('.');
        buffer.append(this.constName);
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + CharOperation.hashCode(this.constName);
        result = 31 * result + CharOperation.hashCode(this.typeName);
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
        final EnumConstantSignature other = (EnumConstantSignature)obj;
        return Arrays.equals(this.constName, other.constName) && Arrays.equals(this.typeName, other.typeName);
    }
}
