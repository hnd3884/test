package org.apache.commons.text.lookup;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class ResourceBundleStringLookup extends AbstractStringLookup
{
    private final String bundleName;
    static final ResourceBundleStringLookup INSTANCE;
    
    private ResourceBundleStringLookup() {
        this(null);
    }
    
    ResourceBundleStringLookup(final String bundleName) {
        this.bundleName = bundleName;
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(ResourceBundleStringLookup.SPLIT_STR);
        final int keyLen = keys.length;
        final boolean anyBundle = this.bundleName == null;
        if (anyBundle && keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad resource bundle key format [%s]; expected format is BundleName:KeyName.", key);
        }
        if (this.bundleName != null && keyLen != 1) {
            throw IllegalArgumentExceptions.format("Bad resource bundle key format [%s]; expected format is KeyName.", key);
        }
        final String keyBundleName = anyBundle ? keys[0] : this.bundleName;
        final String bundleKey = anyBundle ? keys[1] : keys[0];
        try {
            return ResourceBundle.getBundle(keyBundleName).getString(bundleKey);
        }
        catch (final MissingResourceException e) {
            return null;
        }
        catch (final Exception e2) {
            throw IllegalArgumentExceptions.format(e2, "Error looking up resource bundle [%s] and key [%s].", keyBundleName, bundleKey);
        }
    }
    
    static {
        INSTANCE = new ResourceBundleStringLookup();
    }
}
