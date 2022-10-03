package org.glassfish.jersey.internal.l10n;

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
