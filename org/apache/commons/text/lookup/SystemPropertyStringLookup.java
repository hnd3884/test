package org.apache.commons.text.lookup;

final class SystemPropertyStringLookup extends AbstractStringLookup
{
    static final SystemPropertyStringLookup INSTANCE;
    
    private SystemPropertyStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        try {
            return System.getProperty(key);
        }
        catch (final SecurityException | NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }
    
    static {
        INSTANCE = new SystemPropertyStringLookup();
    }
}
