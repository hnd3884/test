package javax.security.auth.callback;

import java.util.Locale;
import java.io.Serializable;

public class LanguageCallback implements Callback, Serializable
{
    private static final long serialVersionUID = 2019050433478903213L;
    private Locale locale;
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
}
