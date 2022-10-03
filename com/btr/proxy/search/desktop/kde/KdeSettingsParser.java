package com.btr.proxy.search.desktop.kde;

import com.btr.proxy.util.Logger;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Properties;

class KdeSettingsParser
{
    public KdeSettingsParser() {
    }
    
    public Properties parseSettings() throws IOException {
        final File settingsFile = this.findSettingsFile();
        if (settingsFile == null) {
            return null;
        }
        final BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile)));
        final Properties result = new Properties();
        try {
            String line;
            for (line = fin.readLine(); line != null && !"[Proxy Settings]".equals(line.trim()); line = fin.readLine()) {}
            if (line == null) {
                return result;
            }
            for (line = ""; line != null && !line.trim().startsWith("["); line = fin.readLine()) {
                line = line.trim();
                final int index = line.indexOf(61);
                if (index > 0) {
                    final String key = line.substring(0, index).trim();
                    final String value = line.substring(index + 1).trim();
                    result.setProperty(key, value);
                }
            }
        }
        finally {
            fin.close();
        }
        return result;
    }
    
    private File findSettingsFile() {
        final File userDir = new File(System.getProperty("user.home"));
        final File settingsFile = new File(userDir, ".kde" + File.separator + "share" + File.separator + "config" + File.separator + "kioslaverc");
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Searching Kde settings in {0}", settingsFile);
        if (!settingsFile.exists()) {
            Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Settings not found", new Object[0]);
            return null;
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Settings found", new Object[0]);
        return settingsFile;
    }
}
