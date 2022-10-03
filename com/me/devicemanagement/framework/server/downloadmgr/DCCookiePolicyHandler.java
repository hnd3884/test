package com.me.devicemanagement.framework.server.downloadmgr;

import HTTPClient.CookieModule;
import HTTPClient.RoResponse;
import HTTPClient.RoRequest;
import HTTPClient.Cookie;
import HTTPClient.CookiePolicyHandler;

class DCCookiePolicyHandler implements CookiePolicyHandler
{
    public boolean sendCookie(final Cookie cookie, final RoRequest req) {
        return true;
    }
    
    public boolean acceptCookie(final Cookie cookie, final RoRequest req, final RoResponse resp) {
        CookieModule.addCookie(cookie);
        return true;
    }
}
