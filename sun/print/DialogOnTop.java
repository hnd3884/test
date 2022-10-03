package sun.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;

public class DialogOnTop implements PrintRequestAttribute
{
    private static final long serialVersionUID = -1901909867156076547L;
    long id;
    
    public DialogOnTop() {
    }
    
    public DialogOnTop(final long id) {
        this.id = id;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return DialogOnTop.class;
    }
    
    public long getID() {
        return this.id;
    }
    
    @Override
    public final String getName() {
        return "dialog-on-top";
    }
    
    @Override
    public String toString() {
        return "dialog-on-top";
    }
}
