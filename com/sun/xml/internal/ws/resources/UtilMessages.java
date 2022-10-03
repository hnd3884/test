package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class UtilMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableUTIL_LOCATION(final Object arg0, final Object arg1) {
        return UtilMessages.messageFactory.getMessage("util.location", arg0, arg1);
    }
    
    public static String UTIL_LOCATION(final Object arg0, final Object arg1) {
        return UtilMessages.localizer.localize(localizableUTIL_LOCATION(arg0, arg1));
    }
    
    public static Localizable localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(final Object arg0, final Object arg1) {
        return UtilMessages.messageFactory.getMessage("util.failed.to.parse.handlerchain.file", arg0, arg1);
    }
    
    public static String UTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(final Object arg0, final Object arg1) {
        return UtilMessages.localizer.localize(localizableUTIL_FAILED_TO_PARSE_HANDLERCHAIN_FILE(arg0, arg1));
    }
    
    public static Localizable localizableUTIL_PARSER_WRONG_ELEMENT(final Object arg0, final Object arg1, final Object arg2) {
        return UtilMessages.messageFactory.getMessage("util.parser.wrong.element", arg0, arg1, arg2);
    }
    
    public static String UTIL_PARSER_WRONG_ELEMENT(final Object arg0, final Object arg1, final Object arg2) {
        return UtilMessages.localizer.localize(localizableUTIL_PARSER_WRONG_ELEMENT(arg0, arg1, arg2));
    }
    
    public static Localizable localizableUTIL_HANDLER_CLASS_NOT_FOUND(final Object arg0) {
        return UtilMessages.messageFactory.getMessage("util.handler.class.not.found", arg0);
    }
    
    public static String UTIL_HANDLER_CLASS_NOT_FOUND(final Object arg0) {
        return UtilMessages.localizer.localize(localizableUTIL_HANDLER_CLASS_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(final Object arg0) {
        return UtilMessages.messageFactory.getMessage("util.handler.endpoint.interface.no.webservice", arg0);
    }
    
    public static String UTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(final Object arg0) {
        return UtilMessages.localizer.localize(localizableUTIL_HANDLER_ENDPOINT_INTERFACE_NO_WEBSERVICE(arg0));
    }
    
    public static Localizable localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(final Object arg0) {
        return UtilMessages.messageFactory.getMessage("util.handler.no.webservice.annotation", arg0);
    }
    
    public static String UTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(final Object arg0) {
        return UtilMessages.localizer.localize(localizableUTIL_HANDLER_NO_WEBSERVICE_ANNOTATION(arg0));
    }
    
    public static Localizable localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(final Object arg0, final Object arg1) {
        return UtilMessages.messageFactory.getMessage("util.failed.to.find.handlerchain.file", arg0, arg1);
    }
    
    public static String UTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(final Object arg0, final Object arg1) {
        return UtilMessages.localizer.localize(localizableUTIL_FAILED_TO_FIND_HANDLERCHAIN_FILE(arg0, arg1));
    }
    
    public static Localizable localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
        return UtilMessages.messageFactory.getMessage("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
    }
    
    public static String UTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS() {
        return UtilMessages.localizer.localize(localizableUTIL_HANDLER_CANNOT_COMBINE_SOAPMESSAGEHANDLERS());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.util");
        localizer = new Localizer();
    }
}
