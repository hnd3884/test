package com.btr.proxy.search;

import java.text.MessageFormat;
import com.btr.proxy.selector.misc.ProxyListFallbackSelector;
import com.btr.proxy.selector.misc.BufferedProxySelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import java.util.Iterator;
import com.btr.proxy.util.ProxyException;
import java.net.ProxySelector;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.search.java.JavaProxySearchStrategy;
import com.btr.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.btr.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.btr.proxy.search.desktop.win.WinProxySearchStrategy;
import com.btr.proxy.search.env.EnvProxySearchStrategy;
import com.btr.proxy.search.browser.ie.IEProxySearchStrategy;
import com.btr.proxy.search.browser.firefox.FirefoxProxySearchStrategy;
import com.btr.proxy.search.desktop.DesktopProxySearchStrategy;
import com.btr.proxy.util.Logger;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

public class ProxySearch implements ProxySearchStrategy
{
    private static final int DEFAULT_PAC_CACHE_SIZE = 20;
    private static final long DEFAULT_PAC_CACHE_TTL = 600000L;
    private List<ProxySearchStrategy> strategies;
    private int pacCacheSize;
    private long pacCacheTTL;
    
    public ProxySearch() {
        this.strategies = new ArrayList<ProxySearchStrategy>();
        this.pacCacheSize = 20;
        this.pacCacheTTL = 600000L;
    }
    
    public static ProxySearch getDefaultProxySearch() {
        final ProxySearch s = new ProxySearch();
        final boolean headless = GraphicsEnvironment.isHeadless();
        if (headless) {
            s.addStrategy(Strategy.JAVA);
            s.addStrategy(Strategy.OS_DEFAULT);
            s.addStrategy(Strategy.ENV_VAR);
        }
        else {
            s.addStrategy(Strategy.JAVA);
            s.addStrategy(Strategy.BROWSER);
            s.addStrategy(Strategy.OS_DEFAULT);
            s.addStrategy(Strategy.ENV_VAR);
        }
        Logger.log(ProxySearch.class, Logger.LogLevel.TRACE, "Using default search priority: {0}", s);
        return s;
    }
    
    public void addStrategy(final Strategy strategy) {
        switch (strategy) {
            case OS_DEFAULT: {
                this.strategies.add(new DesktopProxySearchStrategy());
                break;
            }
            case BROWSER: {
                this.strategies.add(this.getDefaultBrowserStrategy());
                break;
            }
            case FIREFOX: {
                this.strategies.add(new FirefoxProxySearchStrategy());
                break;
            }
            case IE: {
                this.strategies.add(new IEProxySearchStrategy());
                break;
            }
            case ENV_VAR: {
                this.strategies.add(new EnvProxySearchStrategy());
                break;
            }
            case WIN: {
                this.strategies.add(new WinProxySearchStrategy());
                break;
            }
            case KDE: {
                this.strategies.add(new KdeProxySearchStrategy());
                break;
            }
            case GNOME: {
                this.strategies.add(new GnomeProxySearchStrategy());
                break;
            }
            case JAVA: {
                this.strategies.add(new JavaProxySearchStrategy());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown strategy code!");
            }
        }
    }
    
    public void setPacCacheSettings(final int size, final long ttl) {
        this.pacCacheSize = size;
        this.pacCacheTTL = ttl;
    }
    
    private ProxySearchStrategy getDefaultBrowserStrategy() {
        switch (PlatformUtil.getDefaultBrowser()) {
            case IE: {
                return new IEProxySearchStrategy();
            }
            case FIREFOX: {
                return new FirefoxProxySearchStrategy();
            }
            default: {
                return null;
            }
        }
    }
    
    public ProxySelector getProxySelector() {
        Logger.log(this.getClass(), Logger.LogLevel.TRACE, "Executing search strategies to find proxy selector", new Object[0]);
        for (final ProxySearchStrategy strat : this.strategies) {
            try {
                ProxySelector selector = strat.getProxySelector();
                if (selector != null) {
                    selector = this.installBufferingAndFallbackBehaviour(selector);
                    return selector;
                }
                continue;
            }
            catch (final ProxyException e) {
                Logger.log(this.getClass(), Logger.LogLevel.DEBUG, "Strategy {0} failed trying next one.", e);
            }
        }
        return null;
    }
    
    private ProxySelector installBufferingAndFallbackBehaviour(ProxySelector selector) {
        if (selector instanceof PacProxySelector) {
            if (this.pacCacheSize > 0) {
                selector = new BufferedProxySelector(this.pacCacheSize, this.pacCacheTTL, selector);
            }
            selector = new ProxyListFallbackSelector(selector);
        }
        return selector;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Proxy search: ");
        for (final ProxySearchStrategy strat : this.strategies) {
            sb.append(strat);
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public static void main(final String[] args) {
        final ProxySearch ps = getDefaultProxySearch();
        Logger.setBackend(new Logger.LogBackEnd() {
            public void log(final Class<?> clazz, final Logger.LogLevel loglevel, final String msg, final Object... params) {
                System.out.println(MessageFormat.format(msg, params));
            }
            
            public boolean isLogginEnabled(final Logger.LogLevel logLevel) {
                return true;
            }
        });
        ps.getProxySelector();
    }
    
    public enum Strategy
    {
        OS_DEFAULT, 
        BROWSER, 
        FIREFOX, 
        IE, 
        ENV_VAR, 
        WIN, 
        KDE, 
        GNOME, 
        JAVA;
    }
}
