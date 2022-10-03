package com.sun.java.accessibility.util.java.awt;

import javax.accessibility.AccessibleRole;
import java.awt.Label;
import com.sun.java.accessibility.util.Translator;

public class LabelTranslator extends Translator
{
    @Override
    public String getAccessibleName() {
        return ((Label)this.source).getText();
    }
    
    @Override
    public void setAccessibleName(final String text) {
        ((Label)this.source).setText(text);
    }
    
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.LABEL;
    }
}
