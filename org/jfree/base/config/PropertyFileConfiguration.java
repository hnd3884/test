package org.jfree.base.config;

import org.jfree.util.ObjectUtilities;
import java.io.IOException;
import org.jfree.util.Log;
import java.util.Map;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class PropertyFileConfiguration extends HierarchicalConfiguration
{
    static /* synthetic */ Class class$org$jfree$base$config$PropertyFileConfiguration;
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public void load(final InputStream in) {
        if (in == null) {
            throw new NullPointerException();
        }
        try {
            final BufferedInputStream bin = new BufferedInputStream(in);
            final Properties p = new Properties();
            p.load(bin);
            this.getConfiguration().putAll(p);
            bin.close();
        }
        catch (final IOException ioe) {
            Log.warn("Unable to read configuration", ioe);
        }
    }
    
    public void load(final String resourceName) {
        final InputStream in = ObjectUtilities.getResourceRelativeAsStream(resourceName, (PropertyFileConfiguration.class$org$jfree$base$config$PropertyFileConfiguration != null) ? PropertyFileConfiguration.class$org$jfree$base$config$PropertyFileConfiguration : (PropertyFileConfiguration.class$org$jfree$base$config$PropertyFileConfiguration = class$("org.jfree.base.config.PropertyFileConfiguration")));
        if (in != null) {
            this.load(in);
        }
        else {
            Log.debug("Configuration file not found in the classpath: " + resourceName);
        }
    }
}
