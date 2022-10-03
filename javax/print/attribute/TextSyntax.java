package javax.print.attribute;

import java.util.Locale;
import java.io.Serializable;

public abstract class TextSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = -8130648736378144102L;
    private String value;
    private Locale locale;
    
    protected TextSyntax(final String s, final Locale locale) {
        this.value = verify(s);
        this.locale = verify(locale);
    }
    
    private static String verify(final String s) {
        if (s == null) {
            throw new NullPointerException(" value is null");
        }
        return s;
    }
    
    private static Locale verify(final Locale locale) {
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode() ^ this.locale.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof TextSyntax && this.value.equals(((TextSyntax)o).value) && this.locale.equals(((TextSyntax)o).locale);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
