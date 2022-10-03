package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.LocalizableMessageFactory;

public final class ManagementMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableWSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(final Object arg0) {
        return ManagementMessages.messageFactory.getMessage("WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE", arg0);
    }
    
    public static String WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(final Object arg0) {
        return ManagementMessages.localizer.localize(localizableWSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(arg0));
    }
    
    public static Localizable localizableWSM_1004_EXPECTED_XML_TAG(final Object arg0, final Object arg1) {
        return ManagementMessages.messageFactory.getMessage("WSM_1004_EXPECTED_XML_TAG", arg0, arg1);
    }
    
    public static String WSM_1004_EXPECTED_XML_TAG(final Object arg0, final Object arg1) {
        return ManagementMessages.localizer.localize(localizableWSM_1004_EXPECTED_XML_TAG(arg0, arg1));
    }
    
    public static Localizable localizableWSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION() {
        return ManagementMessages.messageFactory.getMessage("WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION", new Object[0]);
    }
    
    public static String WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION() {
        return ManagementMessages.localizer.localize(localizableWSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION());
    }
    
    public static Localizable localizableWSM_1002_EXPECTED_MANAGEMENT_ASSERTION(final Object arg0) {
        return ManagementMessages.messageFactory.getMessage("WSM_1002_EXPECTED_MANAGEMENT_ASSERTION", arg0);
    }
    
    public static String WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(final Object arg0) {
        return ManagementMessages.localizer.localize(localizableWSM_1002_EXPECTED_MANAGEMENT_ASSERTION(arg0));
    }
    
    public static Localizable localizableWSM_1006_CLIENT_MANAGEMENT_ENABLED() {
        return ManagementMessages.messageFactory.getMessage("WSM_1006_CLIENT_MANAGEMENT_ENABLED", new Object[0]);
    }
    
    public static String WSM_1006_CLIENT_MANAGEMENT_ENABLED() {
        return ManagementMessages.localizer.localize(localizableWSM_1006_CLIENT_MANAGEMENT_ENABLED());
    }
    
    public static Localizable localizableWSM_1001_FAILED_ASSERTION(final Object arg0) {
        return ManagementMessages.messageFactory.getMessage("WSM_1001_FAILED_ASSERTION", arg0);
    }
    
    public static String WSM_1001_FAILED_ASSERTION(final Object arg0) {
        return ManagementMessages.localizer.localize(localizableWSM_1001_FAILED_ASSERTION(arg0));
    }
    
    public static Localizable localizableWSM_1005_EXPECTED_COMMUNICATION_CHILD() {
        return ManagementMessages.messageFactory.getMessage("WSM_1005_EXPECTED_COMMUNICATION_CHILD", new Object[0]);
    }
    
    public static String WSM_1005_EXPECTED_COMMUNICATION_CHILD() {
        return ManagementMessages.localizer.localize(localizableWSM_1005_EXPECTED_COMMUNICATION_CHILD());
    }
    
    public static Localizable localizableWSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(final Object arg0) {
        return ManagementMessages.messageFactory.getMessage("WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID", arg0);
    }
    
    public static String WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(final Object arg0) {
        return ManagementMessages.localizer.localize(localizableWSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(arg0));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.management");
        localizer = new Localizer();
    }
}
