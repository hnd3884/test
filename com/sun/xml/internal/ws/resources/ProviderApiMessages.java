package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class ProviderApiMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableNULL_ADDRESS_SERVICE_ENDPOINT() {
        return ProviderApiMessages.messageFactory.getMessage("null.address.service.endpoint", new Object[0]);
    }
    
    public static String NULL_ADDRESS_SERVICE_ENDPOINT() {
        return ProviderApiMessages.localizer.localize(localizableNULL_ADDRESS_SERVICE_ENDPOINT());
    }
    
    public static Localizable localizableNO_WSDL_NO_PORT(final Object arg0) {
        return ProviderApiMessages.messageFactory.getMessage("no.wsdl.no.port", arg0);
    }
    
    public static String NO_WSDL_NO_PORT(final Object arg0) {
        return ProviderApiMessages.localizer.localize(localizableNO_WSDL_NO_PORT(arg0));
    }
    
    public static Localizable localizableNULL_SERVICE() {
        return ProviderApiMessages.messageFactory.getMessage("null.service", new Object[0]);
    }
    
    public static String NULL_SERVICE() {
        return ProviderApiMessages.localizer.localize(localizableNULL_SERVICE());
    }
    
    public static Localizable localizableNULL_ADDRESS() {
        return ProviderApiMessages.messageFactory.getMessage("null.address", new Object[0]);
    }
    
    public static String NULL_ADDRESS() {
        return ProviderApiMessages.localizer.localize(localizableNULL_ADDRESS());
    }
    
    public static Localizable localizableNULL_PORTNAME() {
        return ProviderApiMessages.messageFactory.getMessage("null.portname", new Object[0]);
    }
    
    public static String NULL_PORTNAME() {
        return ProviderApiMessages.localizer.localize(localizableNULL_PORTNAME());
    }
    
    public static Localizable localizableNOTFOUND_SERVICE_IN_WSDL(final Object arg0, final Object arg1) {
        return ProviderApiMessages.messageFactory.getMessage("notfound.service.in.wsdl", arg0, arg1);
    }
    
    public static String NOTFOUND_SERVICE_IN_WSDL(final Object arg0, final Object arg1) {
        return ProviderApiMessages.localizer.localize(localizableNOTFOUND_SERVICE_IN_WSDL(arg0, arg1));
    }
    
    public static Localizable localizableNULL_EPR() {
        return ProviderApiMessages.messageFactory.getMessage("null.epr", new Object[0]);
    }
    
    public static String NULL_EPR() {
        return ProviderApiMessages.localizer.localize(localizableNULL_EPR());
    }
    
    public static Localizable localizableNULL_WSDL() {
        return ProviderApiMessages.messageFactory.getMessage("null.wsdl", new Object[0]);
    }
    
    public static String NULL_WSDL() {
        return ProviderApiMessages.localizer.localize(localizableNULL_WSDL());
    }
    
    public static Localizable localizableNOTFOUND_PORT_IN_WSDL(final Object arg0, final Object arg1, final Object arg2) {
        return ProviderApiMessages.messageFactory.getMessage("notfound.port.in.wsdl", arg0, arg1, arg2);
    }
    
    public static String NOTFOUND_PORT_IN_WSDL(final Object arg0, final Object arg1, final Object arg2) {
        return ProviderApiMessages.localizer.localize(localizableNOTFOUND_PORT_IN_WSDL(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_WSDL(final Object arg0) {
        return ProviderApiMessages.messageFactory.getMessage("error.wsdl", arg0);
    }
    
    public static String ERROR_WSDL(final Object arg0) {
        return ProviderApiMessages.localizer.localize(localizableERROR_WSDL(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.providerApi");
        localizer = new Localizer();
    }
}
