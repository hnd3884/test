package com.btr.proxy.search.browser.firefox;

import com.btr.proxy.util.Logger;
import java.io.File;

class LinuxFirefoxProfileSource implements FirefoxProfileSource
{
    public File getProfileFolder() {
        final File userDir = new File(System.getProperty("user.home"));
        final File cfgDir = new File(userDir, ".mozilla" + File.separator + "firefox" + File.separator);
        if (!cfgDir.exists()) {
            Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Firefox settings folder not found!", new Object[0]);
            return null;
        }
        final File[] profiles = cfgDir.listFiles();
        if (profiles == null || profiles.length == 0) {
            Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Firefox settings folder not found!", new Object[0]);
            return null;
        }
        for (final File p : profiles) {
            if (p.getName().endsWith(".default")) {
                Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox settings folder is {0}", p);
                return p;
            }
        }
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Firefox settings folder is {0}", profiles[0]);
        return profiles[0];
    }
}
