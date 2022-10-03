package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class HttpserverMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableUNEXPECTED_HTTP_METHOD(final Object arg0) {
        return HttpserverMessages.messageFactory.getMessage("unexpected.http.method", arg0);
    }
    
    public static String UNEXPECTED_HTTP_METHOD(final Object arg0) {
        return HttpserverMessages.localizer.localize(localizableUNEXPECTED_HTTP_METHOD(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.httpserver");
        localizer = new Localizer();
    }
}
