package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class ClientMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableFAILED_TO_PARSE(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("failed.to.parse", arg0, arg1);
    }
    
    public static String FAILED_TO_PARSE(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableFAILED_TO_PARSE(arg0, arg1));
    }
    
    public static Localizable localizableINVALID_BINDING_ID(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("invalid.binding.id", arg0, arg1);
    }
    
    public static String INVALID_BINDING_ID(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableINVALID_BINDING_ID(arg0, arg1));
    }
    
    public static Localizable localizableEPR_WITHOUT_ADDRESSING_ON() {
        return ClientMessages.messageFactory.getMessage("epr.without.addressing.on", new Object[0]);
    }
    
    public static String EPR_WITHOUT_ADDRESSING_ON() {
        return ClientMessages.localizer.localize(localizableEPR_WITHOUT_ADDRESSING_ON());
    }
    
    public static Localizable localizableINVALID_SERVICE_NO_WSDL(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("invalid.service.no.wsdl", arg0);
    }
    
    public static String INVALID_SERVICE_NO_WSDL(final Object arg0) {
        return ClientMessages.localizer.localize(localizableINVALID_SERVICE_NO_WSDL(arg0));
    }
    
    public static Localizable localizableINVALID_SOAP_ROLE_NONE() {
        return ClientMessages.messageFactory.getMessage("invalid.soap.role.none", new Object[0]);
    }
    
    public static String INVALID_SOAP_ROLE_NONE() {
        return ClientMessages.localizer.localize(localizableINVALID_SOAP_ROLE_NONE());
    }
    
    public static Localizable localizableUNDEFINED_BINDING(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("undefined.binding", arg0);
    }
    
    public static String UNDEFINED_BINDING(final Object arg0) {
        return ClientMessages.localizer.localize(localizableUNDEFINED_BINDING(arg0));
    }
    
    public static Localizable localizableHTTP_NOT_FOUND(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("http.not.found", arg0);
    }
    
    public static String HTTP_NOT_FOUND(final Object arg0) {
        return ClientMessages.localizer.localize(localizableHTTP_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableINVALID_EPR_PORT_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("invalid.epr.port.name", arg0, arg1);
    }
    
    public static String INVALID_EPR_PORT_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableINVALID_EPR_PORT_NAME(arg0, arg1));
    }
    
    public static Localizable localizableFAILED_TO_PARSE_WITH_MEX(final Object arg0, final Object arg1, final Object arg2) {
        return ClientMessages.messageFactory.getMessage("failed.to.parseWithMEX", arg0, arg1, arg2);
    }
    
    public static String FAILED_TO_PARSE_WITH_MEX(final Object arg0, final Object arg1, final Object arg2) {
        return ClientMessages.localizer.localize(localizableFAILED_TO_PARSE_WITH_MEX(arg0, arg1, arg2));
    }
    
    public static Localizable localizableHTTP_STATUS_CODE(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("http.status.code", arg0, arg1);
    }
    
    public static String HTTP_STATUS_CODE(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableHTTP_STATUS_CODE(arg0, arg1));
    }
    
    public static Localizable localizableINVALID_ADDRESS(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("invalid.address", arg0);
    }
    
    public static String INVALID_ADDRESS(final Object arg0) {
        return ClientMessages.localizer.localize(localizableINVALID_ADDRESS(arg0));
    }
    
    public static Localizable localizableUNDEFINED_PORT_TYPE(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("undefined.portType", arg0);
    }
    
    public static String UNDEFINED_PORT_TYPE(final Object arg0) {
        return ClientMessages.localizer.localize(localizableUNDEFINED_PORT_TYPE(arg0));
    }
    
    public static Localizable localizableWSDL_CONTAINS_NO_SERVICE(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("wsdl.contains.no.service", arg0);
    }
    
    public static String WSDL_CONTAINS_NO_SERVICE(final Object arg0) {
        return ClientMessages.localizer.localize(localizableWSDL_CONTAINS_NO_SERVICE(arg0));
    }
    
    public static Localizable localizableINVALID_SOAP_ACTION() {
        return ClientMessages.messageFactory.getMessage("invalid.soap.action", new Object[0]);
    }
    
    public static String INVALID_SOAP_ACTION() {
        return ClientMessages.localizer.localize(localizableINVALID_SOAP_ACTION());
    }
    
    public static Localizable localizableNON_LOGICAL_HANDLER_SET(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("non.logical.handler.set", arg0);
    }
    
    public static String NON_LOGICAL_HANDLER_SET(final Object arg0) {
        return ClientMessages.localizer.localize(localizableNON_LOGICAL_HANDLER_SET(arg0));
    }
    
    public static Localizable localizableLOCAL_CLIENT_FAILED(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("local.client.failed", arg0);
    }
    
    public static String LOCAL_CLIENT_FAILED(final Object arg0) {
        return ClientMessages.localizer.localize(localizableLOCAL_CLIENT_FAILED(arg0));
    }
    
    public static Localizable localizableRUNTIME_WSDLPARSER_INVALID_WSDL(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return ClientMessages.messageFactory.getMessage("runtime.wsdlparser.invalidWSDL", arg0, arg1, arg2, arg3);
    }
    
    public static String RUNTIME_WSDLPARSER_INVALID_WSDL(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return ClientMessages.localizer.localize(localizableRUNTIME_WSDLPARSER_INVALID_WSDL(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableWSDL_NOT_FOUND(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("wsdl.not.found", arg0);
    }
    
    public static String WSDL_NOT_FOUND(final Object arg0) {
        return ClientMessages.localizer.localize(localizableWSDL_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableHTTP_CLIENT_FAILED(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("http.client.failed", arg0);
    }
    
    public static String HTTP_CLIENT_FAILED(final Object arg0) {
        return ClientMessages.localizer.localize(localizableHTTP_CLIENT_FAILED(arg0));
    }
    
    public static Localizable localizableINVALID_SERVICE_NAME_NULL(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("invalid.service.name.null", arg0);
    }
    
    public static String INVALID_SERVICE_NAME_NULL(final Object arg0) {
        return ClientMessages.localizer.localize(localizableINVALID_SERVICE_NAME_NULL(arg0));
    }
    
    public static Localizable localizableINVALID_WSDL_URL(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("invalid.wsdl.url", arg0);
    }
    
    public static String INVALID_WSDL_URL(final Object arg0) {
        return ClientMessages.localizer.localize(localizableINVALID_WSDL_URL(arg0));
    }
    
    public static Localizable localizableINVALID_PORT_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("invalid.port.name", arg0, arg1);
    }
    
    public static String INVALID_PORT_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableINVALID_PORT_NAME(arg0, arg1));
    }
    
    public static Localizable localizableINVALID_SERVICE_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.messageFactory.getMessage("invalid.service.name", arg0, arg1);
    }
    
    public static String INVALID_SERVICE_NAME(final Object arg0, final Object arg1) {
        return ClientMessages.localizer.localize(localizableINVALID_SERVICE_NAME(arg0, arg1));
    }
    
    public static Localizable localizableUNSUPPORTED_OPERATION(final Object arg0, final Object arg1, final Object arg2) {
        return ClientMessages.messageFactory.getMessage("unsupported.operation", arg0, arg1, arg2);
    }
    
    public static String UNSUPPORTED_OPERATION(final Object arg0, final Object arg1, final Object arg2) {
        return ClientMessages.localizer.localize(localizableUNSUPPORTED_OPERATION(arg0, arg1, arg2));
    }
    
    public static Localizable localizableFAILED_TO_PARSE_EPR(final Object arg0) {
        return ClientMessages.messageFactory.getMessage("failed.to.parse.epr", arg0);
    }
    
    public static String FAILED_TO_PARSE_EPR(final Object arg0) {
        return ClientMessages.localizer.localize(localizableFAILED_TO_PARSE_EPR(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.client");
        localizer = new Localizer();
    }
}
