package org.apache.commons.text.lookup;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;

final class PropertiesStringLookup extends AbstractStringLookup
{
    static final PropertiesStringLookup INSTANCE;
    
    private PropertiesStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split("::");
        final int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad properties key format [%s]; expected format is DocumentPath::Key.", key);
        }
        final String documentPath = keys[0];
        final String propertyKey = this.substringAfter(key, "::");
        try {
            final Properties properties = new Properties();
            try (final InputStream inputStream = Files.newInputStream(Paths.get(documentPath, new String[0]), new OpenOption[0])) {
                properties.load(inputStream);
            }
            return properties.getProperty(propertyKey);
        }
        catch (final Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up properties [%s] and key [%s].", documentPath, propertyKey);
        }
    }
    
    static {
        INSTANCE = new PropertiesStringLookup();
    }
}
