package org.apache.commons.chain.web;

import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;

public abstract class AbstractGetLocaleCommand implements Command
{
    private String localeKey;
    
    public AbstractGetLocaleCommand() {
        this.localeKey = "locale";
    }
    
    public String getLocaleKey() {
        return this.localeKey;
    }
    
    public void setLocaleKey(final String localeKey) {
        this.localeKey = localeKey;
    }
    
    public boolean execute(final Context context) throws Exception {
        context.put(this.getLocaleKey(), this.getLocale(context));
        return false;
    }
    
    protected abstract Locale getLocale(final Context p0);
}
