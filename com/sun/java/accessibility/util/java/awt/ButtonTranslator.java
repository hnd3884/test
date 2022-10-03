package com.sun.java.accessibility.util.java.awt;

import javax.accessibility.AccessibleRole;
import java.awt.Button;
import com.sun.java.accessibility.util.Translator;

public class ButtonTranslator extends Translator
{
    @Override
    public String getAccessibleName() {
        return ((Button)this.source).getLabel();
    }
    
    @Override
    public void setAccessibleName(final String label) {
        ((Button)this.source).setLabel(label);
    }
    
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.PUSH_BUTTON;
    }
}
