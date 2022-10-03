package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class SoapMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableSOAP_FAULT_CREATE_ERR(final Object arg0) {
        return SoapMessages.messageFactory.getMessage("soap.fault.create.err", arg0);
    }
    
    public static String SOAP_FAULT_CREATE_ERR(final Object arg0) {
        return SoapMessages.localizer.localize(localizableSOAP_FAULT_CREATE_ERR(arg0));
    }
    
    public static Localizable localizableSOAP_MSG_FACTORY_CREATE_ERR(final Object arg0) {
        return SoapMessages.messageFactory.getMessage("soap.msg.factory.create.err", arg0);
    }
    
    public static String SOAP_MSG_FACTORY_CREATE_ERR(final Object arg0) {
        return SoapMessages.localizer.localize(localizableSOAP_MSG_FACTORY_CREATE_ERR(arg0));
    }
    
    public static Localizable localizableSOAP_MSG_CREATE_ERR(final Object arg0) {
        return SoapMessages.messageFactory.getMessage("soap.msg.create.err", arg0);
    }
    
    public static String SOAP_MSG_CREATE_ERR(final Object arg0) {
        return SoapMessages.localizer.localize(localizableSOAP_MSG_CREATE_ERR(arg0));
    }
    
    public static Localizable localizableSOAP_FACTORY_CREATE_ERR(final Object arg0) {
        return SoapMessages.messageFactory.getMessage("soap.factory.create.err", arg0);
    }
    
    public static String SOAP_FACTORY_CREATE_ERR(final Object arg0) {
        return SoapMessages.localizer.localize(localizableSOAP_FACTORY_CREATE_ERR(arg0));
    }
    
    public static Localizable localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(final Object arg0) {
        return SoapMessages.messageFactory.getMessage("soap.protocol.invalidFaultCode", arg0);
    }
    
    public static String SOAP_PROTOCOL_INVALID_FAULT_CODE(final Object arg0) {
        return SoapMessages.localizer.localize(localizableSOAP_PROTOCOL_INVALID_FAULT_CODE(arg0));
    }
    
    public static Localizable localizableSOAP_VERSION_MISMATCH_ERR(final Object arg0, final Object arg1) {
        return SoapMessages.messageFactory.getMessage("soap.version.mismatch.err", arg0, arg1);
    }
    
    public static String SOAP_VERSION_MISMATCH_ERR(final Object arg0, final Object arg1) {
        return SoapMessages.localizer.localize(localizableSOAP_VERSION_MISMATCH_ERR(arg0, arg1));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.soap");
        localizer = new Localizer();
    }
}
