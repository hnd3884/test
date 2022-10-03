package com.sun.java.accessibility.util.java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import java.awt.Checkbox;
import javax.accessibility.AccessibleStateSet;
import com.sun.java.accessibility.util.Translator;

public class CheckboxTranslator extends Translator
{
    @Override
    public AccessibleStateSet getAccessibleStateSet() {
        final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
        if (((Checkbox)this.source).getState()) {
            accessibleStateSet.add(AccessibleState.CHECKED);
        }
        return accessibleStateSet;
    }
    
    @Override
    public String getAccessibleName() {
        return ((Checkbox)this.source).getLabel();
    }
    
    @Override
    public void setAccessibleName(final String label) {
        ((Checkbox)this.source).setLabel(label);
    }
    
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.CHECK_BOX;
    }
}
