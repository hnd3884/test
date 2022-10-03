package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class SenderMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(final Object arg0) {
        return SenderMessages.messageFactory.getMessage("sender.request.illegalValueForContentNegotiation", arg0);
    }
    
    public static String SENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(final Object arg0) {
        return SenderMessages.localizer.localize(localizableSENDER_REQUEST_ILLEGAL_VALUE_FOR_CONTENT_NEGOTIATION(arg0));
    }
    
    public static Localizable localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return SenderMessages.messageFactory.getMessage("sender.response.cannotDecodeFaultDetail", new Object[0]);
    }
    
    public static String SENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL() {
        return SenderMessages.localizer.localize(localizableSENDER_RESPONSE_CANNOT_DECODE_FAULT_DETAIL());
    }
    
    public static Localizable localizableSENDER_NESTED_ERROR(final Object arg0) {
        return SenderMessages.messageFactory.getMessage("sender.nestedError", arg0);
    }
    
    public static String SENDER_NESTED_ERROR(final Object arg0) {
        return SenderMessages.localizer.localize(localizableSENDER_NESTED_ERROR(arg0));
    }
    
    public static Localizable localizableSENDER_REQUEST_MESSAGE_NOT_READY() {
        return SenderMessages.messageFactory.getMessage("sender.request.messageNotReady", new Object[0]);
    }
    
    public static String SENDER_REQUEST_MESSAGE_NOT_READY() {
        return SenderMessages.localizer.localize(localizableSENDER_REQUEST_MESSAGE_NOT_READY());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.sender");
        localizer = new Localizer();
    }
}
