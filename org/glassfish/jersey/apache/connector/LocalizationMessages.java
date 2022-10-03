package org.glassfish.jersey.apache.connector;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableEXPECTED_CONNECTOR_PROVIDER_NOT_USED() {
        return LocalizationMessages.messageFactory.getMessage("expected.connector.provider.not.used", new Object[0]);
    }
    
    public static String EXPECTED_CONNECTOR_PROVIDER_NOT_USED() {
        return LocalizationMessages.localizer.localize(localizableEXPECTED_CONNECTOR_PROVIDER_NOT_USED());
    }
    
    public static Localizable localizableERROR_BUFFERING_ENTITY() {
        return LocalizationMessages.messageFactory.getMessage("error.buffering.entity", new Object[0]);
    }
    
    public static String ERROR_BUFFERING_ENTITY() {
        return LocalizationMessages.localizer.localize(localizableERROR_BUFFERING_ENTITY());
    }
    
    public static Localizable localizableINVALID_CONFIGURABLE_COMPONENT_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("invalid.configurable.component.type", new Object[] { arg0 });
    }
    
    public static String INVALID_CONFIGURABLE_COMPONENT_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINVALID_CONFIGURABLE_COMPONENT_TYPE(arg0));
    }
    
    public static Localizable localizableIGNORING_VALUE_OF_PROPERTY(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("ignoring.value.of.property", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String IGNORING_VALUE_OF_PROPERTY(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableIGNORING_VALUE_OF_PROPERTY(arg0, arg1, arg2));
    }
    
    public static Localizable localizableWRONG_PROXY_URI_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("wrong.proxy.uri.type", new Object[] { arg0 });
    }
    
    public static String WRONG_PROXY_URI_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWRONG_PROXY_URI_TYPE(arg0));
    }
    
    public static Localizable localizableFAILED_TO_STOP_CLIENT() {
        return LocalizationMessages.messageFactory.getMessage("failed.to.stop.client", new Object[0]);
    }
    
    public static String FAILED_TO_STOP_CLIENT() {
        return LocalizationMessages.localizer.localize(localizableFAILED_TO_STOP_CLIENT());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.apache.connector.localization");
        localizer = new Localizer();
    }
}
