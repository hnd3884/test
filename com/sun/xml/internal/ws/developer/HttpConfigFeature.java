package com.sun.xml.internal.ws.developer;

import javax.xml.ws.WebServiceException;
import java.net.CookieHandler;
import java.lang.reflect.Constructor;
import javax.xml.ws.WebServiceFeature;

public final class HttpConfigFeature extends WebServiceFeature
{
    public static final String ID = "http://jax-ws.java.net/features/http-config";
    private static final Constructor cookieManagerConstructor;
    private static final Object cookiePolicy;
    private final CookieHandler cookieJar;
    
    public HttpConfigFeature() {
        this(getInternalCookieHandler());
    }
    
    public HttpConfigFeature(final CookieHandler cookieJar) {
        this.enabled = true;
        this.cookieJar = cookieJar;
    }
    
    private static CookieHandler getInternalCookieHandler() {
        try {
            return HttpConfigFeature.cookieManagerConstructor.newInstance(null, HttpConfigFeature.cookiePolicy);
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public String getID() {
        return "http://jax-ws.java.net/features/http-config";
    }
    
    public CookieHandler getCookieHandler() {
        return this.cookieJar;
    }
    
    static {
        Constructor tempConstructor;
        Object tempPolicy;
        try {
            final Class policyClass = Class.forName("java.net.CookiePolicy");
            final Class storeClass = Class.forName("java.net.CookieStore");
            tempConstructor = Class.forName("java.net.CookieManager").getConstructor(storeClass, policyClass);
            tempPolicy = policyClass.getField("ACCEPT_ALL").get(null);
        }
        catch (final Exception e) {
            try {
                final Class policyClass2 = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookiePolicy");
                final Class storeClass2 = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieStore");
                tempConstructor = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieManager").getConstructor(storeClass2, policyClass2);
                tempPolicy = policyClass2.getField("ACCEPT_ALL").get(null);
            }
            catch (final Exception ce) {
                throw new WebServiceException(ce);
            }
        }
        cookieManagerConstructor = tempConstructor;
        cookiePolicy = tempPolicy;
    }
}
