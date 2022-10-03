package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableHK_2_PROVIDER_NOT_REGISTRABLE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("hk2.provider.not.registrable", new Object[] { arg0 });
    }
    
    public static String HK_2_PROVIDER_NOT_REGISTRABLE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHK_2_PROVIDER_NOT_REGISTRABLE(arg0));
    }
    
    public static Localizable localizableHK_2_REIFICATION_ERROR(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("hk2.reification.error", new Object[] { arg0, arg1 });
    }
    
    public static String HK_2_REIFICATION_ERROR(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableHK_2_REIFICATION_ERROR(arg0, arg1));
    }
    
    public static Localizable localizableHK_2_CLEARING_CACHE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("hk2.clearing.cache", new Object[] { arg0, arg1 });
    }
    
    public static String HK_2_CLEARING_CACHE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableHK_2_CLEARING_CACHE(arg0, arg1));
    }
    
    public static Localizable localizableHK_2_UNKNOWN_ERROR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("hk2.unknown.error", new Object[] { arg0 });
    }
    
    public static String HK_2_UNKNOWN_ERROR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHK_2_UNKNOWN_ERROR(arg0));
    }
    
    public static Localizable localizableHK_2_FAILURE_OUTSIDE_ERROR_SCOPE() {
        return LocalizationMessages.messageFactory.getMessage("hk2.failure.outside.error.scope", new Object[0]);
    }
    
    public static String HK_2_FAILURE_OUTSIDE_ERROR_SCOPE() {
        return LocalizationMessages.localizer.localize(localizableHK_2_FAILURE_OUTSIDE_ERROR_SCOPE());
    }
    
    public static Localizable localizableHK_2_UNKNOWN_PARENT_INJECTION_MANAGER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("hk2.unknown.parent.injection.manager", new Object[] { arg0 });
    }
    
    public static String HK_2_UNKNOWN_PARENT_INJECTION_MANAGER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHK_2_UNKNOWN_PARENT_INJECTION_MANAGER(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.inject.hk2.localization");
        localizer = new Localizer();
    }
}
