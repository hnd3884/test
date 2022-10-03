package sun.awt.windows;

import java.awt.im.spi.InputMethod;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Locale;
import java.awt.im.spi.InputMethodDescriptor;

final class WInputMethodDescriptor implements InputMethodDescriptor
{
    @Override
    public Locale[] getAvailableLocales() {
        final Locale[] availableLocalesInternal = getAvailableLocalesInternal();
        final Locale[] array = new Locale[availableLocalesInternal.length];
        System.arraycopy(availableLocalesInternal, 0, array, 0, availableLocalesInternal.length);
        return array;
    }
    
    static Locale[] getAvailableLocalesInternal() {
        return getNativeAvailableLocales();
    }
    
    @Override
    public boolean hasDynamicLocaleList() {
        return true;
    }
    
    @Override
    public synchronized String getInputMethodDisplayName(final Locale locale, final Locale locale2) {
        String property = "System Input Methods";
        if (Locale.getDefault().equals(locale2)) {
            property = Toolkit.getProperty("AWT.HostInputMethodDisplayName", property);
        }
        return property;
    }
    
    @Override
    public Image getInputMethodIcon(final Locale locale) {
        return null;
    }
    
    @Override
    public InputMethod createInputMethod() throws Exception {
        return new WInputMethod();
    }
    
    private static native Locale[] getNativeAvailableLocales();
}
