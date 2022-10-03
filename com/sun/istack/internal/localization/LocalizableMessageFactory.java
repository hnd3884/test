package com.sun.istack.internal.localization;

public class LocalizableMessageFactory
{
    private final String _bundlename;
    
    public LocalizableMessageFactory(final String bundlename) {
        this._bundlename = bundlename;
    }
    
    public Localizable getMessage(final String key, final Object... args) {
        return new LocalizableMessage(this._bundlename, key, args);
    }
}
