package sun.awt.im;

import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.AWTException;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Component;
import java.awt.event.ActionListener;

abstract class InputMethodPopupMenu implements ActionListener
{
    static InputMethodPopupMenu getInstance(final Component component, final String s) {
        if (component instanceof JFrame || component instanceof JDialog) {
            return new JInputMethodPopupMenu(s);
        }
        return new AWTInputMethodPopupMenu(s);
    }
    
    abstract void show(final Component p0, final int p1, final int p2);
    
    abstract void removeAll();
    
    abstract void addSeparator();
    
    abstract void addToComponent(final Component p0);
    
    abstract Object createSubmenu(final String p0);
    
    abstract void add(final Object p0);
    
    abstract void addMenuItem(final String p0, final String p1, final String p2);
    
    abstract void addMenuItem(final Object p0, final String p1, final String p2, final String p3);
    
    void addOneInputMethodToMenu(final InputMethodLocator inputMethodLocator, final String s) {
        final InputMethodDescriptor descriptor = inputMethodLocator.getDescriptor();
        String s2 = descriptor.getInputMethodDisplayName(null, Locale.getDefault());
        String s3 = inputMethodLocator.getActionCommandString();
        Locale[] availableLocales = null;
        int length;
        try {
            availableLocales = descriptor.getAvailableLocales();
            length = availableLocales.length;
        }
        catch (final AWTException ex) {
            length = 0;
        }
        if (length == 0) {
            this.addMenuItem(s2, null, s);
        }
        else if (length == 1) {
            if (descriptor.hasDynamicLocaleList()) {
                s2 = descriptor.getInputMethodDisplayName(availableLocales[0], Locale.getDefault());
                s3 = inputMethodLocator.deriveLocator(availableLocales[0]).getActionCommandString();
            }
            this.addMenuItem(s2, s3, s);
        }
        else {
            final Object submenu = this.createSubmenu(s2);
            this.add(submenu);
            for (final Locale locale : availableLocales) {
                this.addMenuItem(submenu, this.getLocaleName(locale), inputMethodLocator.deriveLocator(locale).getActionCommandString(), s);
            }
        }
    }
    
    static boolean isSelected(final String s, final String s2) {
        if (s == null || s2 == null) {
            return false;
        }
        if (s.equals(s2)) {
            return true;
        }
        final int index = s2.indexOf(10);
        return index != -1 && s2.substring(0, index).equals(s);
    }
    
    String getLocaleName(final Locale locale) {
        final String string = locale.toString();
        String s = Toolkit.getProperty("AWT.InputMethodLanguage." + string, null);
        if (s == null) {
            s = locale.getDisplayName();
            if (s == null || s.length() == 0) {
                s = string;
            }
        }
        return s;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        ((ExecutableInputMethodManager)InputMethodManager.getInstance()).changeInputMethod(actionEvent.getActionCommand());
    }
}
