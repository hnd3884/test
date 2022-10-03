package com.azul.crs.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version
{
    private static final String VERSION_PROPERTIES = "version.properties";
    private static final String CLIENT_VERSION = "client.version";
    private Properties properties;
    
    public Version() throws IOException {
        this.properties = new Properties();
        try (final InputStream is = this.getClass().getResourceAsStream("version.properties")) {
            this.properties.load(is);
        }
    }
    
    public String clientVersion() {
        return this.properties.getProperty("client.version");
    }
}
