package com.me.idps.core.oauth;

public class OauthIdThreadLocal
{
    static ThreadLocal<Long> oauthId;
    static ThreadLocal<String> domainType;
    
    static void setOauthId(final Long oLong) {
        OauthIdThreadLocal.oauthId.set(oLong);
    }
    
    public static Long getOauthId() {
        return OauthIdThreadLocal.oauthId.get();
    }
    
    public static void clearOauthId() {
        OauthIdThreadLocal.oauthId.remove();
    }
    
    public static void setDomainType(final String type) {
        OauthIdThreadLocal.domainType.set(type);
    }
    
    public static String getDomainType() {
        return OauthIdThreadLocal.domainType.get();
    }
    
    public static void clearDomainType() {
        OauthIdThreadLocal.domainType.remove();
    }
    
    static {
        OauthIdThreadLocal.oauthId = new ThreadLocal<Long>();
        OauthIdThreadLocal.domainType = new ThreadLocal<String>();
    }
}
