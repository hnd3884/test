package sun.print;

import javax.print.attribute.Attribute;
import javax.print.AttributeException;
import javax.print.PrintException;

class PrintJobAttributeException extends PrintException implements AttributeException
{
    private Attribute attr;
    private Class category;
    
    PrintJobAttributeException(final String s, final Class category, final Attribute attr) {
        super(s);
        this.attr = attr;
        this.category = category;
    }
    
    @Override
    public Class[] getUnsupportedAttributes() {
        if (this.category == null) {
            return null;
        }
        return new Class[] { this.category };
    }
    
    @Override
    public Attribute[] getUnsupportedValues() {
        if (this.attr == null) {
            return null;
        }
        return new Attribute[] { this.attr };
    }
}
