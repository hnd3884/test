package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class AddressingMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableNON_ANONYMOUS_RESPONSE_ONEWAY() {
        return AddressingMessages.messageFactory.getMessage("nonAnonymous.response.oneway", new Object[0]);
    }
    
    public static String NON_ANONYMOUS_RESPONSE_ONEWAY() {
        return AddressingMessages.localizer.localize(localizableNON_ANONYMOUS_RESPONSE_ONEWAY());
    }
    
    public static Localizable localizableNULL_WSA_HEADERS() {
        return AddressingMessages.messageFactory.getMessage("null.wsa.headers", new Object[0]);
    }
    
    public static String NULL_WSA_HEADERS() {
        return AddressingMessages.localizer.localize(localizableNULL_WSA_HEADERS());
    }
    
    public static Localizable localizableUNKNOWN_WSA_HEADER() {
        return AddressingMessages.messageFactory.getMessage("unknown.wsa.header", new Object[0]);
    }
    
    public static String UNKNOWN_WSA_HEADER() {
        return AddressingMessages.localizer.localize(localizableUNKNOWN_WSA_HEADER());
    }
    
    public static Localizable localizableNULL_ACTION() {
        return AddressingMessages.messageFactory.getMessage("null.action", new Object[0]);
    }
    
    public static String NULL_ACTION() {
        return AddressingMessages.localizer.localize(localizableNULL_ACTION());
    }
    
    public static Localizable localizableINVALID_WSAW_ANONYMOUS(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("invalid.wsaw.anonymous", arg0);
    }
    
    public static String INVALID_WSAW_ANONYMOUS(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableINVALID_WSAW_ANONYMOUS(arg0));
    }
    
    public static Localizable localizableNULL_SOAP_VERSION() {
        return AddressingMessages.messageFactory.getMessage("null.soap.version", new Object[0]);
    }
    
    public static String NULL_SOAP_VERSION() {
        return AddressingMessages.localizer.localize(localizableNULL_SOAP_VERSION());
    }
    
    public static Localizable localizableWSDL_BOUND_OPERATION_NOT_FOUND(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("wsdlBoundOperation.notFound", arg0);
    }
    
    public static String WSDL_BOUND_OPERATION_NOT_FOUND(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableWSDL_BOUND_OPERATION_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableNON_UNIQUE_OPERATION_SIGNATURE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return AddressingMessages.messageFactory.getMessage("non.unique.operation.signature", arg0, arg1, arg2, arg3);
    }
    
    public static String NON_UNIQUE_OPERATION_SIGNATURE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return AddressingMessages.localizer.localize(localizableNON_UNIQUE_OPERATION_SIGNATURE(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableNON_ANONYMOUS_RESPONSE() {
        return AddressingMessages.messageFactory.getMessage("nonAnonymous.response", new Object[0]);
    }
    
    public static String NON_ANONYMOUS_RESPONSE() {
        return AddressingMessages.localizer.localize(localizableNON_ANONYMOUS_RESPONSE());
    }
    
    public static Localizable localizableVALIDATION_SERVER_NULL_ACTION() {
        return AddressingMessages.messageFactory.getMessage("validation.server.nullAction", new Object[0]);
    }
    
    public static String VALIDATION_SERVER_NULL_ACTION() {
        return AddressingMessages.localizer.localize(localizableVALIDATION_SERVER_NULL_ACTION());
    }
    
    public static Localizable localizableFAULT_TO_CANNOT_PARSE() {
        return AddressingMessages.messageFactory.getMessage("faultTo.cannot.parse", new Object[0]);
    }
    
    public static String FAULT_TO_CANNOT_PARSE() {
        return AddressingMessages.localizer.localize(localizableFAULT_TO_CANNOT_PARSE());
    }
    
    public static Localizable localizableVALIDATION_CLIENT_NULL_ACTION() {
        return AddressingMessages.messageFactory.getMessage("validation.client.nullAction", new Object[0]);
    }
    
    public static String VALIDATION_CLIENT_NULL_ACTION() {
        return AddressingMessages.localizer.localize(localizableVALIDATION_CLIENT_NULL_ACTION());
    }
    
    public static Localizable localizableNULL_MESSAGE() {
        return AddressingMessages.messageFactory.getMessage("null.message", new Object[0]);
    }
    
    public static String NULL_MESSAGE() {
        return AddressingMessages.localizer.localize(localizableNULL_MESSAGE());
    }
    
    public static Localizable localizableACTION_NOT_SUPPORTED_EXCEPTION(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("action.not.supported.exception", arg0);
    }
    
    public static String ACTION_NOT_SUPPORTED_EXCEPTION(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableACTION_NOT_SUPPORTED_EXCEPTION(arg0));
    }
    
    public static Localizable localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("nonAnonymous.response.nullHeaders", arg0);
    }
    
    public static String NON_ANONYMOUS_RESPONSE_NULL_HEADERS(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableNON_ANONYMOUS_RESPONSE_NULL_HEADERS(arg0));
    }
    
    public static Localizable localizableNON_ANONYMOUS_RESPONSE_SENDING(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("nonAnonymous.response.sending", arg0);
    }
    
    public static String NON_ANONYMOUS_RESPONSE_SENDING(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableNON_ANONYMOUS_RESPONSE_SENDING(arg0));
    }
    
    public static Localizable localizableREPLY_TO_CANNOT_PARSE() {
        return AddressingMessages.messageFactory.getMessage("replyTo.cannot.parse", new Object[0]);
    }
    
    public static String REPLY_TO_CANNOT_PARSE() {
        return AddressingMessages.localizer.localize(localizableREPLY_TO_CANNOT_PARSE());
    }
    
    public static Localizable localizableINVALID_ADDRESSING_HEADER_EXCEPTION(final Object arg0, final Object arg1) {
        return AddressingMessages.messageFactory.getMessage("invalid.addressing.header.exception", arg0, arg1);
    }
    
    public static String INVALID_ADDRESSING_HEADER_EXCEPTION(final Object arg0, final Object arg1) {
        return AddressingMessages.localizer.localize(localizableINVALID_ADDRESSING_HEADER_EXCEPTION(arg0, arg1));
    }
    
    public static Localizable localizableWSAW_ANONYMOUS_PROHIBITED() {
        return AddressingMessages.messageFactory.getMessage("wsaw.anonymousProhibited", new Object[0]);
    }
    
    public static String WSAW_ANONYMOUS_PROHIBITED() {
        return AddressingMessages.localizer.localize(localizableWSAW_ANONYMOUS_PROHIBITED());
    }
    
    public static Localizable localizableNULL_WSDL_PORT() {
        return AddressingMessages.messageFactory.getMessage("null.wsdlPort", new Object[0]);
    }
    
    public static String NULL_WSDL_PORT() {
        return AddressingMessages.localizer.localize(localizableNULL_WSDL_PORT());
    }
    
    public static Localizable localizableADDRESSING_SHOULD_BE_ENABLED() {
        return AddressingMessages.messageFactory.getMessage("addressing.should.be.enabled.", new Object[0]);
    }
    
    public static String ADDRESSING_SHOULD_BE_ENABLED() {
        return AddressingMessages.localizer.localize(localizableADDRESSING_SHOULD_BE_ENABLED());
    }
    
    public static Localizable localizableNULL_ADDRESSING_VERSION() {
        return AddressingMessages.messageFactory.getMessage("null.addressing.version", new Object[0]);
    }
    
    public static String NULL_ADDRESSING_VERSION() {
        return AddressingMessages.localizer.localize(localizableNULL_ADDRESSING_VERSION());
    }
    
    public static Localizable localizableMISSING_HEADER_EXCEPTION(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("missing.header.exception", arg0);
    }
    
    public static String MISSING_HEADER_EXCEPTION(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableMISSING_HEADER_EXCEPTION(arg0));
    }
    
    public static Localizable localizableNULL_PACKET() {
        return AddressingMessages.messageFactory.getMessage("null.packet", new Object[0]);
    }
    
    public static String NULL_PACKET() {
        return AddressingMessages.localizer.localize(localizableNULL_PACKET());
    }
    
    public static Localizable localizableWRONG_ADDRESSING_VERSION(final Object arg0, final Object arg1) {
        return AddressingMessages.messageFactory.getMessage("wrong.addressing.version", arg0, arg1);
    }
    
    public static String WRONG_ADDRESSING_VERSION(final Object arg0, final Object arg1) {
        return AddressingMessages.localizer.localize(localizableWRONG_ADDRESSING_VERSION(arg0, arg1));
    }
    
    public static Localizable localizableADDRESSING_NOT_ENABLED(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("addressing.notEnabled", arg0);
    }
    
    public static String ADDRESSING_NOT_ENABLED(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableADDRESSING_NOT_ENABLED(arg0));
    }
    
    public static Localizable localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(final Object arg0) {
        return AddressingMessages.messageFactory.getMessage("nonAnonymous.unknown.protocol", arg0);
    }
    
    public static String NON_ANONYMOUS_UNKNOWN_PROTOCOL(final Object arg0) {
        return AddressingMessages.localizer.localize(localizableNON_ANONYMOUS_UNKNOWN_PROTOCOL(arg0));
    }
    
    public static Localizable localizableNULL_HEADERS() {
        return AddressingMessages.messageFactory.getMessage("null.headers", new Object[0]);
    }
    
    public static String NULL_HEADERS() {
        return AddressingMessages.localizer.localize(localizableNULL_HEADERS());
    }
    
    public static Localizable localizableNULL_BINDING() {
        return AddressingMessages.messageFactory.getMessage("null.binding", new Object[0]);
    }
    
    public static String NULL_BINDING() {
        return AddressingMessages.localizer.localize(localizableNULL_BINDING());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.addressing");
        localizer = new Localizer();
    }
}
