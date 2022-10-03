package com.btr.proxy.search.browser.firefox;

import java.util.Hashtable;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

class FirefoxSettingParser
{
    public FirefoxSettingParser() {
    }
    
    public Properties parseSettings(final FirefoxProfileSource source) throws IOException {
        final File profileFolder = source.getProfileFolder();
        final File settingsFile = new File(profileFolder, "prefs.js");
        final BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile)));
        final Properties result = new Properties();
        try {
            for (String line = fin.readLine(); line != null; line = fin.readLine()) {
                line = line.trim();
                if (line.startsWith("user_pref(\"network.proxy")) {
                    line = line.substring(10, line.length() - 2);
                    final int index = line.indexOf(",");
                    String key = line.substring(0, index).trim();
                    if (key.startsWith("\"")) {
                        key = key.substring(1);
                    }
                    if (key.endsWith("\"")) {
                        key = key.substring(0, key.length() - 1);
                    }
                    String value = line.substring(index + 1).trim();
                    if (value.startsWith("\"")) {
                        value = value.substring(1);
                    }
                    if (value.endsWith("\"")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    ((Hashtable<String, String>)result).put(key, value);
                }
            }
        }
        finally {
            fin.close();
        }
        return result;
    }
}
