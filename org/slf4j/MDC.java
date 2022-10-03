package org.slf4j;

import org.slf4j.helpers.Util;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.impl.StaticMDCBinder;
import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class MDC
{
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;
    
    private MDC() {
    }
    
    public static void put(final String key, final String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.put(key, val);
    }
    
    public static String get(final String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return MDC.mdcAdapter.get(key);
    }
    
    public static void remove(final String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.remove(key);
    }
    
    public static void clear() {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.clear();
    }
    
    public static Map<String, String> getCopyOfContextMap() {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return MDC.mdcAdapter.getCopyOfContextMap();
    }
    
    public static void setContextMap(final Map<String, String> contextMap) {
        if (MDC.mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        MDC.mdcAdapter.setContextMap(contextMap);
    }
    
    public static MDCAdapter getMDCAdapter() {
        return MDC.mdcAdapter;
    }
    
    static {
        try {
            MDC.mdcAdapter = StaticMDCBinder.SINGLETON.getMDCA();
        }
        catch (final NoClassDefFoundError ncde) {
            MDC.mdcAdapter = new NOPMDCAdapter();
            final String msg = ncde.getMessage();
            if (msg == null || msg.indexOf("StaticMDCBinder") == -1) {
                throw ncde;
            }
            Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
            Util.report("Defaulting to no-operation MDCAdapter implementation.");
            Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
        }
        catch (final Exception e) {
            Util.report("MDC binding unsuccessful.", e);
        }
    }
}
