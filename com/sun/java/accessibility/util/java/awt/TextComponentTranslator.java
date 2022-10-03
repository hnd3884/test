package com.sun.java.accessibility.util.java.awt;

import javax.accessibility.AccessibleRole;
import com.sun.java.accessibility.util.Translator;

public class TextComponentTranslator extends Translator
{
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.TEXT;
    }
}
