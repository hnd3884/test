package org.apache.commons.chain.web;

import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;

public abstract class AbstractSetLocaleCommand implements Command
{
    private String localeKey;
    
    public AbstractSetLocaleCommand() {
        this.localeKey = "locale";
    }
    
    public String getLocaleKey() {
        return this.localeKey;
    }
    
    public void setLocaleKey(final String localeKey) {
        this.localeKey = localeKey;
    }
    
    public boolean execute(final Context context) throws Exception {
        this.setLocale(context, context.get(this.getLocaleKey()));
        return false;
    }
    
    protected abstract void setLocale(final Context p0, final Locale p1);
}
