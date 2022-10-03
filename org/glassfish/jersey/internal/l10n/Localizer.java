package org.glassfish.jersey.internal.l10n;

import java.net.URL;
import org.glassfish.jersey.internal.OsgiRegistry;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import org.glassfish.hk2.osgiresourcelocator.ResourceFinder;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Locale;

public class Localizer
{
    private final Locale _locale;
    private final HashMap<String, ResourceBundle> _resourceBundles;
    
    public Localizer() {
        this(Locale.getDefault());
    }
    
    public Localizer(final Locale l) {
        this._locale = l;
        this._resourceBundles = new HashMap<String, ResourceBundle>();
    }
    
    public Locale getLocale() {
        return this._locale;
    }
    
    public String localize(final Localizable l) {
        String key = l.getKey();
        if ("\u0000".equals(key)) {
            return (String)l.getArguments()[0];
        }
        final String bundlename = l.getResourceBundleName();
        try {
            ResourceBundle bundle = this._resourceBundles.get(bundlename);
            if (bundle == null) {
                Label_0188: {
                    try {
                        bundle = ResourceBundle.getBundle(bundlename, this._locale);
                    }
                    catch (final MissingResourceException e) {
                        final int i = bundlename.lastIndexOf(46);
                        if (i != -1) {
                            final String alternateBundleName = bundlename.substring(i + 1);
                            try {
                                bundle = ResourceBundle.getBundle(alternateBundleName, this._locale);
                            }
                            catch (final MissingResourceException e2) {
                                final OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
                                if (osgiRegistry != null) {
                                    bundle = osgiRegistry.getResourceBundle(bundlename);
                                    break Label_0188;
                                }
                                final String path = bundlename.replace('.', '/') + ".properties";
                                final URL bundleUrl = ResourceFinder.findEntry(path);
                                if (bundleUrl == null) {
                                    break Label_0188;
                                }
                                try {
                                    bundle = new PropertyResourceBundle(bundleUrl.openStream());
                                }
                                catch (final IOException ex) {}
                            }
                        }
                    }
                }
                if (bundle == null) {
                    return this.getDefaultMessage(l);
                }
                this._resourceBundles.put(bundlename, bundle);
            }
            if (key == null) {
                key = "undefined";
            }
            String msg;
            try {
                msg = bundle.getString(key);
            }
            catch (final MissingResourceException e3) {
                msg = bundle.getString("undefined");
            }
            final Object[] args = l.getArguments();
            for (int j = 0; j < args.length; ++j) {
                if (args[j] instanceof Localizable) {
                    args[j] = this.localize((Localizable)args[j]);
                }
            }
            final String message = MessageFormat.format(msg, args);
            return message;
        }
        catch (final MissingResourceException e4) {
            return this.getDefaultMessage(l);
        }
    }
    
    private String getDefaultMessage(final Localizable l) {
        final String key = l.getKey();
        final Object[] args = l.getArguments();
        final StringBuilder sb = new StringBuilder();
        sb.append("[failed to localize] ");
        sb.append(key);
        if (args != null) {
            sb.append('(');
            for (int i = 0; i < args.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(String.valueOf(args[i]));
            }
            sb.append(')');
        }
        return sb.toString();
    }
}
