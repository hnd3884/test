package javax.print.attribute;

import java.io.Serializable;

public abstract class IntegerSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = 3644574816328081943L;
    private int value;
    
    protected IntegerSyntax(final int value) {
        this.value = value;
    }
    
    protected IntegerSyntax(final int value, final int n, final int n2) {
        if (n > value || value > n2) {
            throw new IllegalArgumentException("Value " + value + " not in range " + n + ".." + n2);
        }
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof IntegerSyntax && this.value == ((IntegerSyntax)o).value;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "" + this.value;
    }
}
