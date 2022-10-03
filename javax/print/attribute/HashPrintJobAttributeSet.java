package javax.print.attribute;

import java.io.Serializable;

public class HashPrintJobAttributeSet extends HashAttributeSet implements PrintJobAttributeSet, Serializable
{
    private static final long serialVersionUID = -4204473656070350348L;
    
    public HashPrintJobAttributeSet() {
        super(PrintJobAttribute.class);
    }
    
    public HashPrintJobAttributeSet(final PrintJobAttribute printJobAttribute) {
        super(printJobAttribute, PrintJobAttribute.class);
    }
    
    public HashPrintJobAttributeSet(final PrintJobAttribute[] array) {
        super(array, PrintJobAttribute.class);
    }
    
    public HashPrintJobAttributeSet(final PrintJobAttributeSet set) {
        super(set, PrintJobAttribute.class);
    }
}
