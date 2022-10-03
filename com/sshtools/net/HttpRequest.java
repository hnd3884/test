package com.sshtools.net;

import com.maverick.util.Base64;

public class HttpRequest extends HttpHeader
{
    public void setHeaderBegin(final String begin) {
        super.begin = begin;
    }
    
    public void setBasicAuthentication(final String s, final String s2) {
        this.setHeaderField("Proxy-Authorization", "Basic " + Base64.encodeBytes((s + ":" + s2).getBytes(), true));
    }
}
