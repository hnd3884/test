package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class EncodingMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableFAILED_TO_READ_RESPONSE(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("failed.to.read.response", arg0);
    }
    
    public static String FAILED_TO_READ_RESPONSE(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableFAILED_TO_READ_RESPONSE(arg0));
    }
    
    public static Localizable localizableEXCEPTION_INCORRECT_TYPE(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("exception.incorrectType", arg0);
    }
    
    public static String EXCEPTION_INCORRECT_TYPE(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableEXCEPTION_INCORRECT_TYPE(arg0));
    }
    
    public static Localizable localizableEXCEPTION_NOTFOUND(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("exception.notfound", arg0);
    }
    
    public static String EXCEPTION_NOTFOUND(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableEXCEPTION_NOTFOUND(arg0));
    }
    
    public static Localizable localizableXSD_UNEXPECTED_ELEMENT_NAME(final Object arg0, final Object arg1) {
        return EncodingMessages.messageFactory.getMessage("xsd.unexpectedElementName", arg0, arg1);
    }
    
    public static String XSD_UNEXPECTED_ELEMENT_NAME(final Object arg0, final Object arg1) {
        return EncodingMessages.localizer.localize(localizableXSD_UNEXPECTED_ELEMENT_NAME(arg0, arg1));
    }
    
    public static Localizable localizableNESTED_DESERIALIZATION_ERROR(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("nestedDeserializationError", arg0);
    }
    
    public static String NESTED_DESERIALIZATION_ERROR(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableNESTED_DESERIALIZATION_ERROR(arg0));
    }
    
    public static Localizable localizableNESTED_ENCODING_ERROR(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("nestedEncodingError", arg0);
    }
    
    public static String NESTED_ENCODING_ERROR(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableNESTED_ENCODING_ERROR(arg0));
    }
    
    public static Localizable localizableXSD_UNKNOWN_PREFIX(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("xsd.unknownPrefix", arg0);
    }
    
    public static String XSD_UNKNOWN_PREFIX(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableXSD_UNKNOWN_PREFIX(arg0));
    }
    
    public static Localizable localizableNESTED_SERIALIZATION_ERROR(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("nestedSerializationError", arg0);
    }
    
    public static String NESTED_SERIALIZATION_ERROR(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableNESTED_SERIALIZATION_ERROR(arg0));
    }
    
    public static Localizable localizableNO_SUCH_CONTENT_ID(final Object arg0) {
        return EncodingMessages.messageFactory.getMessage("noSuchContentId", arg0);
    }
    
    public static String NO_SUCH_CONTENT_ID(final Object arg0) {
        return EncodingMessages.localizer.localize(localizableNO_SUCH_CONTENT_ID(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.encoding");
        localizer = new Localizer();
    }
}
