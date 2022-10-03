package javax.print.attribute;

import java.io.Serializable;

public class HashPrintRequestAttributeSet extends HashAttributeSet implements PrintRequestAttributeSet, Serializable
{
    private static final long serialVersionUID = 2364756266107751933L;
    
    public HashPrintRequestAttributeSet() {
        super(PrintRequestAttribute.class);
    }
    
    public HashPrintRequestAttributeSet(final PrintRequestAttribute printRequestAttribute) {
        super(printRequestAttribute, PrintRequestAttribute.class);
    }
    
    public HashPrintRequestAttributeSet(final PrintRequestAttribute[] array) {
        super(array, PrintRequestAttribute.class);
    }
    
    public HashPrintRequestAttributeSet(final PrintRequestAttributeSet set) {
        super(set, PrintRequestAttribute.class);
    }
}
