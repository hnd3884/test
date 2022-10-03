package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class HandlerMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(final Object arg0, final Object arg1) {
        return HandlerMessages.messageFactory.getMessage("handler.messageContext.invalid.class", arg0, arg1);
    }
    
    public static String HANDLER_MESSAGE_CONTEXT_INVALID_CLASS(final Object arg0, final Object arg1) {
        return HandlerMessages.localizer.localize(localizableHANDLER_MESSAGE_CONTEXT_INVALID_CLASS(arg0, arg1));
    }
    
    public static Localizable localizableCANNOT_EXTEND_HANDLER_DIRECTLY(final Object arg0) {
        return HandlerMessages.messageFactory.getMessage("cannot.extend.handler.directly", arg0);
    }
    
    public static String CANNOT_EXTEND_HANDLER_DIRECTLY(final Object arg0) {
        return HandlerMessages.localizer.localize(localizableCANNOT_EXTEND_HANDLER_DIRECTLY(arg0));
    }
    
    public static Localizable localizableHANDLER_NOT_VALID_TYPE(final Object arg0) {
        return HandlerMessages.messageFactory.getMessage("handler.not.valid.type", arg0);
    }
    
    public static String HANDLER_NOT_VALID_TYPE(final Object arg0) {
        return HandlerMessages.localizer.localize(localizableHANDLER_NOT_VALID_TYPE(arg0));
    }
    
    public static Localizable localizableCANNOT_INSTANTIATE_HANDLER(final Object arg0, final Object arg1) {
        return HandlerMessages.messageFactory.getMessage("cannot.instantiate.handler", arg0, arg1);
    }
    
    public static String CANNOT_INSTANTIATE_HANDLER(final Object arg0, final Object arg1) {
        return HandlerMessages.localizer.localize(localizableCANNOT_INSTANTIATE_HANDLER(arg0, arg1));
    }
    
    public static Localizable localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(final Object arg0) {
        return HandlerMessages.messageFactory.getMessage("handler.chain.contains.handler.only", arg0);
    }
    
    public static String HANDLER_CHAIN_CONTAINS_HANDLER_ONLY(final Object arg0) {
        return HandlerMessages.localizer.localize(localizableHANDLER_CHAIN_CONTAINS_HANDLER_ONLY(arg0));
    }
    
    public static Localizable localizableHANDLER_NESTED_ERROR(final Object arg0) {
        return HandlerMessages.messageFactory.getMessage("handler.nestedError", arg0);
    }
    
    public static String HANDLER_NESTED_ERROR(final Object arg0) {
        return HandlerMessages.localizer.localize(localizableHANDLER_NESTED_ERROR(arg0));
    }
    
    public static Localizable localizableHANDLER_PREDESTROY_IGNORE(final Object arg0) {
        return HandlerMessages.messageFactory.getMessage("handler.predestroy.ignore", arg0);
    }
    
    public static String HANDLER_PREDESTROY_IGNORE(final Object arg0) {
        return HandlerMessages.localizer.localize(localizableHANDLER_PREDESTROY_IGNORE(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.handler");
        localizer = new Localizer();
    }
}
