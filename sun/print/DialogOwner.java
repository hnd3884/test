package sun.print;

import java.awt.Frame;
import javax.print.attribute.PrintRequestAttribute;

public final class DialogOwner implements PrintRequestAttribute
{
    private Frame dlgOwner;
    
    public DialogOwner(final Frame dlgOwner) {
        this.dlgOwner = dlgOwner;
    }
    
    public Frame getOwner() {
        return this.dlgOwner;
    }
    
    @Override
    public final Class getCategory() {
        return DialogOwner.class;
    }
    
    @Override
    public final String getName() {
        return "dialog-owner";
    }
}
