package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class XmlmessageMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableXML_NULL_HEADERS() {
        return XmlmessageMessages.messageFactory.getMessage("xml.null.headers", new Object[0]);
    }
    
    public static String XML_NULL_HEADERS() {
        return XmlmessageMessages.localizer.localize(localizableXML_NULL_HEADERS());
    }
    
    public static Localizable localizableXML_SET_PAYLOAD_ERR() {
        return XmlmessageMessages.messageFactory.getMessage("xml.set.payload.err", new Object[0]);
    }
    
    public static String XML_SET_PAYLOAD_ERR() {
        return XmlmessageMessages.localizer.localize(localizableXML_SET_PAYLOAD_ERR());
    }
    
    public static Localizable localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART() {
        return XmlmessageMessages.messageFactory.getMessage("xml.content-type.mustbe.multipart", new Object[0]);
    }
    
    public static String XML_CONTENT_TYPE_MUSTBE_MULTIPART() {
        return XmlmessageMessages.localizer.localize(localizableXML_CONTENT_TYPE_MUSTBE_MULTIPART());
    }
    
    public static Localizable localizableXML_UNKNOWN_CONTENT_TYPE() {
        return XmlmessageMessages.messageFactory.getMessage("xml.unknown.Content-Type", new Object[0]);
    }
    
    public static String XML_UNKNOWN_CONTENT_TYPE() {
        return XmlmessageMessages.localizer.localize(localizableXML_UNKNOWN_CONTENT_TYPE());
    }
    
    public static Localizable localizableXML_GET_DS_ERR() {
        return XmlmessageMessages.messageFactory.getMessage("xml.get.ds.err", new Object[0]);
    }
    
    public static String XML_GET_DS_ERR() {
        return XmlmessageMessages.localizer.localize(localizableXML_GET_DS_ERR());
    }
    
    public static Localizable localizableXML_CONTENT_TYPE_PARSE_ERR() {
        return XmlmessageMessages.messageFactory.getMessage("xml.Content-Type.parse.err", new Object[0]);
    }
    
    public static String XML_CONTENT_TYPE_PARSE_ERR() {
        return XmlmessageMessages.localizer.localize(localizableXML_CONTENT_TYPE_PARSE_ERR());
    }
    
    public static Localizable localizableXML_GET_SOURCE_ERR() {
        return XmlmessageMessages.messageFactory.getMessage("xml.get.source.err", new Object[0]);
    }
    
    public static String XML_GET_SOURCE_ERR() {
        return XmlmessageMessages.localizer.localize(localizableXML_GET_SOURCE_ERR());
    }
    
    public static Localizable localizableXML_CANNOT_INTERNALIZE_MESSAGE() {
        return XmlmessageMessages.messageFactory.getMessage("xml.cannot.internalize.message", new Object[0]);
    }
    
    public static String XML_CANNOT_INTERNALIZE_MESSAGE() {
        return XmlmessageMessages.localizer.localize(localizableXML_CANNOT_INTERNALIZE_MESSAGE());
    }
    
    public static Localizable localizableXML_NO_CONTENT_TYPE() {
        return XmlmessageMessages.messageFactory.getMessage("xml.no.Content-Type", new Object[0]);
    }
    
    public static String XML_NO_CONTENT_TYPE() {
        return XmlmessageMessages.localizer.localize(localizableXML_NO_CONTENT_TYPE());
    }
    
    public static Localizable localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(final Object arg0) {
        return XmlmessageMessages.messageFactory.getMessage("xml.root.part.invalid.Content-Type", arg0);
    }
    
    public static String XML_ROOT_PART_INVALID_CONTENT_TYPE(final Object arg0) {
        return XmlmessageMessages.localizer.localize(localizableXML_ROOT_PART_INVALID_CONTENT_TYPE(arg0));
    }
    
    public static Localizable localizableXML_INVALID_CONTENT_TYPE(final Object arg0) {
        return XmlmessageMessages.messageFactory.getMessage("xml.invalid.content-type", arg0);
    }
    
    public static String XML_INVALID_CONTENT_TYPE(final Object arg0) {
        return XmlmessageMessages.localizer.localize(localizableXML_INVALID_CONTENT_TYPE(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.xmlmessage");
        localizer = new Localizer();
    }
}
