package org.glassfish.jersey.jaxb.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableSAX_CANNOT_DISABLE_PARAMETER_ENTITY_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("sax.cannot.disable.parameter.entity.processing.feature", new Object[] { arg0 });
    }
    
    public static String SAX_CANNOT_DISABLE_PARAMETER_ENTITY_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSAX_CANNOT_DISABLE_PARAMETER_ENTITY_PROCESSING_FEATURE(arg0));
    }
    
    public static Localizable localizableERROR_READING_ENTITY_MISSING() {
        return LocalizationMessages.messageFactory.getMessage("error.reading.entity.missing", new Object[0]);
    }
    
    public static String ERROR_READING_ENTITY_MISSING() {
        return LocalizationMessages.localizer.localize(localizableERROR_READING_ENTITY_MISSING());
    }
    
    public static Localizable localizableERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.unmarshalling.jaxb", new Object[] { arg0 });
    }
    
    public static String ERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_UNMARSHALLING_JAXB(arg0));
    }
    
    public static Localizable localizableSAX_CANNOT_DISABLE_GENERAL_ENTITY_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("sax.cannot.disable.general.entity.processing.feature", new Object[] { arg0 });
    }
    
    public static String SAX_CANNOT_DISABLE_GENERAL_ENTITY_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSAX_CANNOT_DISABLE_GENERAL_ENTITY_PROCESSING_FEATURE(arg0));
    }
    
    public static Localizable localizableSAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("sax.cannot.enable.secure.processing.feature", new Object[] { arg0 });
    }
    
    public static String SAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSAX_CANNOT_ENABLE_SECURE_PROCESSING_FEATURE(arg0));
    }
    
    public static Localizable localizableUNABLE_TO_SECURE_XML_TRANSFORMER_PROCESSING() {
        return LocalizationMessages.messageFactory.getMessage("unable.to.secure.xml.transformer.processing", new Object[0]);
    }
    
    public static String UNABLE_TO_SECURE_XML_TRANSFORMER_PROCESSING() {
        return LocalizationMessages.localizer.localize(localizableUNABLE_TO_SECURE_XML_TRANSFORMER_PROCESSING());
    }
    
    public static Localizable localizableSAX_XDK_NO_SECURITY_FEATURES() {
        return LocalizationMessages.messageFactory.getMessage("sax.xdk.no.security.features", new Object[0]);
    }
    
    public static String SAX_XDK_NO_SECURITY_FEATURES() {
        return LocalizationMessages.localizer.localize(localizableSAX_XDK_NO_SECURITY_FEATURES());
    }
    
    public static Localizable localizableUNABLE_TO_ACCESS_METHODS_OF_CLASS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unable.to.access.methods.of.class", new Object[] { arg0 });
    }
    
    public static String UNABLE_TO_ACCESS_METHODS_OF_CLASS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNABLE_TO_ACCESS_METHODS_OF_CLASS(arg0));
    }
    
    public static Localizable localizableNO_PARAM_CONSTRUCTOR_MISSING(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("no.param.constructor.missing", new Object[] { arg0 });
    }
    
    public static String NO_PARAM_CONSTRUCTOR_MISSING(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNO_PARAM_CONSTRUCTOR_MISSING(arg0));
    }
    
    public static Localizable localizableUNABLE_TO_INSTANTIATE_CLASS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unable.to.instantiate.class", new Object[] { arg0 });
    }
    
    public static String UNABLE_TO_INSTANTIATE_CLASS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNABLE_TO_INSTANTIATE_CLASS(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.jaxb.internal.localization");
        localizer = new Localizer();
    }
}
