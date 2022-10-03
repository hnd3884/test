package com.adventnet.tools.prevalent;

public final class WebGet
{
    private static WebGet wget;
    
    private WebGet() {
    }
    
    public static WebGet getInstance() {
        if (WebGet.wget == null) {
            WebGet.wget = new WebGet();
        }
        return WebGet.wget;
    }
    
    public String getValues(final String str, final String t) {
        String d = null;
        String a = null;
        final ProcessGet pget = ProcessGet.getInstance();
        if (t.equals("WEB")) {
            d = str.substring(16, 20);
            a = str.substring(8, 12);
        }
        d = new StringBuffer(pget.getStringValue(d)).reverse().toString();
        a = pget.getStringValue(a);
        return d + "," + a;
    }
    
    static {
        WebGet.wget = null;
    }
}
