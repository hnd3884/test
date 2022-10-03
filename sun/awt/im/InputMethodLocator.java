package sun.awt.im;

import java.awt.AWTException;
import java.util.Locale;
import java.awt.im.spi.InputMethodDescriptor;

final class InputMethodLocator
{
    private InputMethodDescriptor descriptor;
    private ClassLoader loader;
    private Locale locale;
    
    InputMethodLocator(final InputMethodDescriptor descriptor, final ClassLoader loader, final Locale locale) {
        if (descriptor == null) {
            throw new NullPointerException("descriptor can't be null");
        }
        this.descriptor = descriptor;
        this.loader = loader;
        this.locale = locale;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final InputMethodLocator inputMethodLocator = (InputMethodLocator)o;
        return this.descriptor.getClass().equals(inputMethodLocator.descriptor.getClass()) && (this.loader != null || inputMethodLocator.loader == null) && (this.loader == null || this.loader.equals(inputMethodLocator.loader)) && (this.locale != null || inputMethodLocator.locale == null) && (this.locale == null || this.locale.equals(inputMethodLocator.locale));
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.descriptor.hashCode();
        if (this.loader != null) {
            hashCode |= this.loader.hashCode() << 10;
        }
        if (this.locale != null) {
            hashCode |= this.locale.hashCode() << 20;
        }
        return hashCode;
    }
    
    InputMethodDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    ClassLoader getClassLoader() {
        return this.loader;
    }
    
    Locale getLocale() {
        return this.locale;
    }
    
    boolean isLocaleAvailable(final Locale locale) {
        try {
            final Locale[] availableLocales = this.descriptor.getAvailableLocales();
            for (int i = 0; i < availableLocales.length; ++i) {
                if (availableLocales[i].equals(locale)) {
                    return true;
                }
            }
        }
        catch (final AWTException ex) {}
        return false;
    }
    
    InputMethodLocator deriveLocator(final Locale locale) {
        if (locale == this.locale) {
            return this;
        }
        return new InputMethodLocator(this.descriptor, this.loader, locale);
    }
    
    boolean sameInputMethod(final InputMethodLocator inputMethodLocator) {
        return inputMethodLocator == this || (inputMethodLocator != null && this.descriptor.getClass().equals(inputMethodLocator.descriptor.getClass()) && (this.loader != null || inputMethodLocator.loader == null) && (this.loader == null || this.loader.equals(inputMethodLocator.loader)));
    }
    
    String getActionCommandString() {
        final String name = this.descriptor.getClass().getName();
        if (this.locale == null) {
            return name;
        }
        return name + "\n" + this.locale.toString();
    }
}
