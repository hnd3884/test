package org.apache.commons.math3.exception.util;

import java.util.Locale;

public class DummyLocalizable implements Localizable
{
    private static final long serialVersionUID = 8843275624471387299L;
    private final String source;
    
    public DummyLocalizable(final String source) {
        this.source = source;
    }
    
    public String getSourceString() {
        return this.source;
    }
    
    public String getLocalizedString(final Locale locale) {
        return this.source;
    }
    
    @Override
    public String toString() {
        return this.source;
    }
}
