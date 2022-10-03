package org.owasp.esapi.util;

import java.text.MessageFormat;
import java.util.Locale;
import org.owasp.esapi.ESAPI;
import java.util.ResourceBundle;

public class DefaultMessageUtil
{
    private final String DEFAULT_LOCALE_LANG = "en";
    private final String DEFAULT_LOCALE_LOC = "US";
    private ResourceBundle messages;
    
    public DefaultMessageUtil() {
        this.messages = null;
    }
    
    public void initialize() {
        try {
            this.messages = ResourceBundle.getBundle("ESAPI", ESAPI.authenticator().getCurrentUser().getLocale());
        }
        catch (final Exception e) {
            this.messages = ResourceBundle.getBundle("ESAPI", new Locale("en", "US"));
        }
    }
    
    public String getMessage(final String msgKey, final Object[] arguments) {
        this.initialize();
        return MessageFormat.format(this.messages.getString(msgKey), arguments);
    }
}
