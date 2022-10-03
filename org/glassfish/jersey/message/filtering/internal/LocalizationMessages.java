package org.glassfish.jersey.message.filtering.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableMERGING_FILTERING_SCOPES() {
        return LocalizationMessages.messageFactory.getMessage("merging.filtering.scopes", new Object[0]);
    }
    
    public static String MERGING_FILTERING_SCOPES() {
        return LocalizationMessages.localizer.localize(localizableMERGING_FILTERING_SCOPES());
    }
    
    public static Localizable localizableENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("entity.filtering.scope.not.annotations", new Object[] { arg0 });
    }
    
    public static String ENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.message.filtering.internal.localization");
        localizer = new Localizer();
    }
}
