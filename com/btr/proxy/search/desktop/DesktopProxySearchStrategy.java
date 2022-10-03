package com.btr.proxy.search.desktop;

import com.btr.proxy.util.ProxyException;
import com.btr.proxy.search.desktop.osx.OsxProxySearchStrategy;
import com.btr.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.btr.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.btr.proxy.search.desktop.win.WinProxySearchStrategy;
import com.btr.proxy.util.Logger;
import com.btr.proxy.util.PlatformUtil;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class DesktopProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() throws ProxyException {
        final PlatformUtil.Platform pf = PlatformUtil.getCurrentPlattform();
        final PlatformUtil.Desktop dt = PlatformUtil.getCurrentDesktop();
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Detecting system settings.", new Object[0]);
        if (pf == PlatformUtil.Platform.WIN) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "We are running on Windows.", new Object[0]);
            return new WinProxySearchStrategy().getProxySelector();
        }
        if (dt == PlatformUtil.Desktop.KDE) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "We are running on KDE.", new Object[0]);
            return new KdeProxySearchStrategy().getProxySelector();
        }
        if (dt == PlatformUtil.Desktop.GNOME) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "We are running on Gnome.", new Object[0]);
            return new GnomeProxySearchStrategy().getProxySelector();
        }
        if (dt == PlatformUtil.Desktop.MAC_OS) {
            Logger.log(this.getClass(), Logger.LogLevel.TRACE, "We are running on Mac OSX.", new Object[0]);
            return new OsxProxySearchStrategy().getProxySelector();
        }
        return null;
    }
}
