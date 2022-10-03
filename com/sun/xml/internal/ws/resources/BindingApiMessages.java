package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class BindingApiMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableBINDING_API_NO_FAULT_MESSAGE_NAME() {
        return BindingApiMessages.messageFactory.getMessage("binding.api.no.fault.message.name", new Object[0]);
    }
    
    public static String BINDING_API_NO_FAULT_MESSAGE_NAME() {
        return BindingApiMessages.localizer.localize(localizableBINDING_API_NO_FAULT_MESSAGE_NAME());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.bindingApi");
        localizer = new Localizer();
    }
}
