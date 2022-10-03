package org.apache.xerces.impl.dv;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatatypeException extends Exception
{
    static final long serialVersionUID = 1940805832730465578L;
    protected final String key;
    protected final Object[] args;
    
    public DatatypeException(final String key, final Object[] args) {
        super(key);
        this.key = key;
        this.args = args;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public Object[] getArgs() {
        return this.args;
    }
    
    public String getMessage() {
        final ResourceBundle bundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLSchemaMessages");
        if (bundle == null) {
            throw new MissingResourceException("Property file not found!", "org.apache.xerces.impl.msg.XMLSchemaMessages", this.key);
        }
        String s = bundle.getString(this.key);
        if (s == null) {
            throw new MissingResourceException(bundle.getString("BadMessageKey"), "org.apache.xerces.impl.msg.XMLSchemaMessages", this.key);
        }
        if (this.args != null) {
            try {
                s = MessageFormat.format(s, this.args);
            }
            catch (final Exception ex) {
                s = bundle.getString("FormatFailed") + " " + bundle.getString(this.key);
            }
        }
        return s;
    }
}
