package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;

public class DatatypeException extends Exception
{
    static final long serialVersionUID = 1940805832730465578L;
    protected String key;
    protected Object[] args;
    
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
    
    @Override
    public String getMessage() {
        ResourceBundle resourceBundle = null;
        resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
        if (resourceBundle == null) {
            throw new MissingResourceException("Property file not found!", "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", this.key);
        }
        String msg = resourceBundle.getString(this.key);
        if (msg == null) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", this.key);
        }
        if (this.args != null) {
            try {
                msg = MessageFormat.format(msg, this.args);
            }
            catch (final Exception e) {
                msg = resourceBundle.getString("FormatFailed");
                msg = msg + " " + resourceBundle.getString(this.key);
            }
        }
        return msg;
    }
}
