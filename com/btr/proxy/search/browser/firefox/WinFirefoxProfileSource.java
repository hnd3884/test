package com.btr.proxy.search.browser.firefox;

import java.io.IOException;
import com.btr.proxy.util.Logger;
import java.io.File;
import com.btr.proxy.search.desktop.win.Win32ProxyUtils;

class WinFirefoxProfileSource implements FirefoxProfileSource
{
    public WinFirefoxProfileSource() {
    }
    
    private String getAppFolder() {
        return new Win32ProxyUtils().readUserHomedir();
    }
    
    public File getProfileFolder() throws IOException {
        final File appDataDir = new File(this.getAppFolder());
        final File cfgDir = new File(appDataDir, "Mozilla" + File.separator + "Firefox" + File.separator + "Profiles");
        if (!cfgDir.exists()) {
            Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Firefox windows settings folder not found.", new Object[0]);
            return null;
        }
        final File[] profiles = cfgDir.listFiles();
        if (profiles == null || profiles.length == 0) {
            Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Firefox windows settings folder not found.", new Object[0]);
            return null;
        }
        for (final File p : profiles) {
            if (p.getName().endsWith(".default")) {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox windows settings folder is {0}.", p);
                return p;
            }
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox windows settings folder is {0}.", profiles[0]);
        return profiles[0];
    }
}
