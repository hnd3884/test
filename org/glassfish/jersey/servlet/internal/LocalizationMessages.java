package org.glassfish.jersey.servlet.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableHEADER_VALUE_READ_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("header.value.read.failed", new Object[0]);
    }
    
    public static String HEADER_VALUE_READ_FAILED() {
        return LocalizationMessages.localizer.localize(localizableHEADER_VALUE_READ_FAILED());
    }
    
    public static Localizable localizableFORM_PARAM_CONSUMED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("form.param.consumed", new Object[] { arg0 });
    }
    
    public static String FORM_PARAM_CONSUMED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableFORM_PARAM_CONSUMED(arg0));
    }
    
    public static Localizable localizableFILTER_CONTEXT_PATH_MISSING() {
        return LocalizationMessages.messageFactory.getMessage("filter.context.path.missing", new Object[0]);
    }
    
    public static String FILTER_CONTEXT_PATH_MISSING() {
        return LocalizationMessages.localizer.localize(localizableFILTER_CONTEXT_PATH_MISSING());
    }
    
    public static Localizable localizableSERVLET_PATH_MISMATCH(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("servlet.path.mismatch", new Object[] { arg0, arg1 });
    }
    
    public static String SERVLET_PATH_MISMATCH(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSERVLET_PATH_MISMATCH(arg0, arg1));
    }
    
    public static Localizable localizableASYNC_PROCESSING_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("async.processing.not.supported", new Object[0]);
    }
    
    public static String ASYNC_PROCESSING_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableASYNC_PROCESSING_NOT_SUPPORTED());
    }
    
    public static Localizable localizableSERVLET_REQUEST_SUSPEND_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("servlet.request.suspend.failed", new Object[0]);
    }
    
    public static String SERVLET_REQUEST_SUSPEND_FAILED() {
        return LocalizationMessages.localizer.localize(localizableSERVLET_REQUEST_SUSPEND_FAILED());
    }
    
    public static Localizable localizableEXCEPTION_SENDING_ERROR_RESPONSE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("exception.sending.error.response", new Object[] { arg0, arg1 });
    }
    
    public static String EXCEPTION_SENDING_ERROR_RESPONSE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_SENDING_ERROR_RESPONSE(arg0, arg1));
    }
    
    public static Localizable localizableNO_THREAD_LOCAL_VALUE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("no.thread.local.value", new Object[] { arg0 });
    }
    
    public static String NO_THREAD_LOCAL_VALUE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNO_THREAD_LOCAL_VALUE(arg0));
    }
    
    public static Localizable localizableINIT_PARAM_REGEX_SYNTAX_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("init.param.regex.syntax.invalid", new Object[] { arg0, arg1 });
    }
    
    public static String INIT_PARAM_REGEX_SYNTAX_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINIT_PARAM_REGEX_SYNTAX_INVALID(arg0, arg1));
    }
    
    public static Localizable localizableRESOURCE_CONFIG_UNABLE_TO_LOAD(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.config.unable.to.load", new Object[] { arg0 });
    }
    
    public static String RESOURCE_CONFIG_UNABLE_TO_LOAD(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_CONFIG_UNABLE_TO_LOAD(arg0));
    }
    
    public static Localizable localizablePERSISTENCE_UNIT_NOT_CONFIGURED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("persistence.unit.not.configured", new Object[] { arg0 });
    }
    
    public static String PERSISTENCE_UNIT_NOT_CONFIGURED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePERSISTENCE_UNIT_NOT_CONFIGURED(arg0));
    }
    
    public static Localizable localizableRESOURCE_CONFIG_PARENT_CLASS_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("resource.config.parent.class.invalid", new Object[] { arg0, arg1 });
    }
    
    public static String RESOURCE_CONFIG_PARENT_CLASS_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_CONFIG_PARENT_CLASS_INVALID(arg0, arg1));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.servlet.internal.localization");
        localizer = new Localizer();
    }
}
