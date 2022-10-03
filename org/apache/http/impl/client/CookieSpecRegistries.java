package org.apache.http.impl.client;

import org.apache.http.config.Lookup;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.util.PublicSuffixMatcher;

public final class CookieSpecRegistries
{
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder(final PublicSuffixMatcher publicSuffixMatcher) {
        final CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
        final CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        final CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return (RegistryBuilder<CookieSpecProvider>)RegistryBuilder.create().register("default", (Object)defaultProvider).register("best-match", (Object)defaultProvider).register("compatibility", (Object)defaultProvider).register("standard", (Object)laxStandardProvider).register("standard-strict", (Object)strictStandardProvider).register("netscape", (Object)new NetscapeDraftSpecProvider()).register("ignoreCookies", (Object)new IgnoreSpecProvider());
    }
    
    public static RegistryBuilder<CookieSpecProvider> createDefaultBuilder() {
        return createDefaultBuilder(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault() {
        return createDefault(PublicSuffixMatcherLoader.getDefault());
    }
    
    public static Lookup<CookieSpecProvider> createDefault(final PublicSuffixMatcher publicSuffixMatcher) {
        return (Lookup<CookieSpecProvider>)createDefaultBuilder(publicSuffixMatcher).build();
    }
    
    private CookieSpecRegistries() {
    }
}
