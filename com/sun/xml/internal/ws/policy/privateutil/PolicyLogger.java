package com.sun.xml.internal.ws.policy.privateutil;

import java.lang.reflect.Field;
import com.sun.istack.internal.logging.Logger;

public final class PolicyLogger extends Logger
{
    private static final String POLICY_PACKAGE_ROOT = "com.sun.xml.internal.ws.policy";
    
    private PolicyLogger(final String policyLoggerName, final String className) {
        super(policyLoggerName, className);
    }
    
    public static PolicyLogger getLogger(final Class<?> componentClass) {
        final String componentClassName = componentClass.getName();
        if (componentClassName.startsWith("com.sun.xml.internal.ws.policy")) {
            return new PolicyLogger(getLoggingSubsystemName() + componentClassName.substring("com.sun.xml.internal.ws.policy".length()), componentClassName);
        }
        return new PolicyLogger(getLoggingSubsystemName() + "." + componentClassName, componentClassName);
    }
    
    private static String getLoggingSubsystemName() {
        String loggingSubsystemName = "wspolicy";
        try {
            final Class jaxwsConstants = Class.forName("com.sun.xml.internal.ws.util.Constants");
            final Field loggingDomainField = jaxwsConstants.getField("LoggingDomain");
            final Object loggingDomain = loggingDomainField.get(null);
            loggingSubsystemName = loggingDomain.toString().concat(".wspolicy");
        }
        catch (final RuntimeException ex) {}
        catch (final Exception ex2) {}
        return loggingSubsystemName;
    }
}
