package javax.print.attribute;

import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import java.io.Serializable;

public abstract class EnumSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = -2739521845085831642L;
    private int value;
    
    protected EnumSyntax(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public Object clone() {
        return this;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final String[] stringTable = this.getStringTable();
        final int n = this.value - this.getOffset();
        return (stringTable != null && n >= 0 && n < stringTable.length) ? stringTable[n] : Integer.toString(this.value);
    }
    
    protected Object readResolve() throws ObjectStreamException {
        final EnumSyntax[] enumValueTable = this.getEnumValueTable();
        if (enumValueTable == null) {
            throw new InvalidObjectException("Null enumeration value table for class " + this.getClass());
        }
        final int offset = this.getOffset();
        final int n = this.value - offset;
        if (0 > n || n >= enumValueTable.length) {
            throw new InvalidObjectException("Integer value = " + this.value + " not in valid range " + offset + ".." + (offset + enumValueTable.length - 1) + "for class " + this.getClass());
        }
        final EnumSyntax enumSyntax = enumValueTable[n];
        if (enumSyntax == null) {
            throw new InvalidObjectException("No enumeration value for integer value = " + this.value + "for class " + this.getClass());
        }
        return enumSyntax;
    }
    
    protected String[] getStringTable() {
        return null;
    }
    
    protected EnumSyntax[] getEnumValueTable() {
        return null;
    }
    
    protected int getOffset() {
        return 0;
    }
}
