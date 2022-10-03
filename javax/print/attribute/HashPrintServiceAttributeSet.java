package javax.print.attribute;

import java.io.Serializable;

public class HashPrintServiceAttributeSet extends HashAttributeSet implements PrintServiceAttributeSet, Serializable
{
    private static final long serialVersionUID = 6642904616179203070L;
    
    public HashPrintServiceAttributeSet() {
        super(PrintServiceAttribute.class);
    }
    
    public HashPrintServiceAttributeSet(final PrintServiceAttribute printServiceAttribute) {
        super(printServiceAttribute, PrintServiceAttribute.class);
    }
    
    public HashPrintServiceAttributeSet(final PrintServiceAttribute[] array) {
        super(array, PrintServiceAttribute.class);
    }
    
    public HashPrintServiceAttributeSet(final PrintServiceAttributeSet set) {
        super(set, PrintServiceAttribute.class);
    }
}
