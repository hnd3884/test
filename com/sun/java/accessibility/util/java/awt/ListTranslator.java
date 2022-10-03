package com.sun.java.accessibility.util.java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import java.awt.List;
import javax.accessibility.AccessibleStateSet;
import com.sun.java.accessibility.util.Translator;

public class ListTranslator extends Translator
{
    @Override
    public AccessibleStateSet getAccessibleStateSet() {
        final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
        if (((List)this.source).isMultipleMode()) {
            accessibleStateSet.add(AccessibleState.MULTISELECTABLE);
        }
        if (((List)this.source).getSelectedItems().length > 0) {
            accessibleStateSet.add(AccessibleState.SELECTED);
        }
        return accessibleStateSet;
    }
    
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.LIST;
    }
}
