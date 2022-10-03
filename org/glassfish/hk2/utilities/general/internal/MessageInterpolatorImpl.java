package org.glassfish.hk2.utilities.general.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Enumeration;
import java.util.Set;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.Payload;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.ResourceBundle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.validation.MessageInterpolator;

public class MessageInterpolatorImpl implements MessageInterpolator
{
    public static final String DEFAULT_VALIDATION_MESSAGES = "org.hibernate.validator.ValidationMessages";
    public static final String USER_VALIDATION_MESSAGES = "ValidationMessages";
    private static final Pattern MESSAGE_PARAMETER_PATTERN;
    private final Locale defaultLocale;
    private final Map<LocalisedMessage, String> resolvedMessages;
    private final boolean cacheMessages = true;
    
    public MessageInterpolatorImpl() {
        this.defaultLocale = Locale.getDefault();
        this.resolvedMessages = new WeakHashMap<LocalisedMessage, String>();
    }
    
    public String interpolate(final String message, final MessageInterpolator.Context context) {
        return this.interpolate(message, context, this.defaultLocale);
    }
    
    public String interpolate(final String message, final MessageInterpolator.Context context, final Locale locale) {
        final Map<String, Object> annotationParameters = context.getConstraintDescriptor().getAttributes();
        final LocalisedMessage localisedMessage = new LocalisedMessage(message, locale);
        String resolvedMessage = null;
        resolvedMessage = this.resolvedMessages.get(localisedMessage);
        if (resolvedMessage == null) {
            final ResourceBundle userResourceBundle = new ContextResourceBundle(context, locale);
            ClassLoader cl;
            if (System.getSecurityManager() != null) {
                cl = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return MessageInterpolator.class.getClassLoader();
                    }
                });
            }
            else {
                cl = MessageInterpolator.class.getClassLoader();
            }
            final ResourceBundle defaultResourceBundle = ResourceBundle.getBundle("org.hibernate.validator.ValidationMessages", locale, cl);
            resolvedMessage = message;
            boolean evaluatedDefaultBundleOnce = false;
            while (true) {
                final String userBundleResolvedMessage = this.replaceVariables(resolvedMessage, userResourceBundle, locale, true);
                if (evaluatedDefaultBundleOnce && !this.hasReplacementTakenPlace(userBundleResolvedMessage, resolvedMessage)) {
                    break;
                }
                resolvedMessage = this.replaceVariables(userBundleResolvedMessage, defaultResourceBundle, locale, false);
                evaluatedDefaultBundleOnce = true;
                this.resolvedMessages.put(localisedMessage, resolvedMessage);
            }
        }
        resolvedMessage = this.replaceAnnotationAttributes(resolvedMessage, annotationParameters);
        resolvedMessage = resolvedMessage.replace("\\{", "{");
        resolvedMessage = resolvedMessage.replace("\\}", "}");
        resolvedMessage = resolvedMessage.replace("\\\\", "\\");
        return resolvedMessage;
    }
    
    private boolean hasReplacementTakenPlace(final String origMessage, final String newMessage) {
        return !origMessage.equals(newMessage);
    }
    
    private String replaceVariables(final String message, final ResourceBundle bundle, final Locale locale, final boolean recurse) {
        final Matcher matcher = MessageInterpolatorImpl.MESSAGE_PARAMETER_PATTERN.matcher(message);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String parameter = matcher.group(1);
            final String resolvedParameterValue = this.resolveParameter(parameter, bundle, locale, recurse);
            matcher.appendReplacement(sb, this.escapeMetaCharacters(resolvedParameterValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private String replaceAnnotationAttributes(final String message, final Map<String, Object> annotationParameters) {
        final Matcher matcher = MessageInterpolatorImpl.MESSAGE_PARAMETER_PATTERN.matcher(message);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String parameter = matcher.group(1);
            final Object variable = annotationParameters.get(this.removeCurlyBrace(parameter));
            String resolvedParameterValue;
            if (variable != null) {
                resolvedParameterValue = this.escapeMetaCharacters(variable.toString());
            }
            else {
                resolvedParameterValue = parameter;
            }
            matcher.appendReplacement(sb, resolvedParameterValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private String resolveParameter(final String parameterName, final ResourceBundle bundle, final Locale locale, final boolean recurse) {
        String parameterValue;
        try {
            if (bundle != null) {
                parameterValue = bundle.getString(this.removeCurlyBrace(parameterName));
                if (recurse) {
                    parameterValue = this.replaceVariables(parameterValue, bundle, locale, recurse);
                }
            }
            else {
                parameterValue = parameterName;
            }
        }
        catch (final MissingResourceException e) {
            parameterValue = parameterName;
        }
        return parameterValue;
    }
    
    private String removeCurlyBrace(final String parameter) {
        return parameter.substring(1, parameter.length() - 1);
    }
    
    private String escapeMetaCharacters(final String s) {
        String escapedString = s.replace("\\", "\\\\");
        escapedString = escapedString.replace("$", "\\$");
        return escapedString;
    }
    
    static {
        MESSAGE_PARAMETER_PATTERN = Pattern.compile("(\\{[^\\}]+?\\})");
    }
    
    private static class ContextResourceBundle extends ResourceBundle
    {
        ResourceBundle contextBundle;
        ResourceBundle userBundle;
        
        ContextResourceBundle(final MessageInterpolator.Context context, final Locale locale) {
            final ConstraintDescriptor<?> descriptor = (ConstraintDescriptor<?>)context.getConstraintDescriptor();
            final Set<Class<? extends Payload>> payload = descriptor.getPayload();
            if (!payload.isEmpty()) {
                final Class<?> payloadClass = payload.iterator().next();
                final String baseName = payloadClass.getPackage().getName() + ".LocalStrings";
                ClassLoader cl;
                if (System.getSecurityManager() != null) {
                    cl = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                        @Override
                        public ClassLoader run() {
                            return payloadClass.getClassLoader();
                        }
                    });
                }
                else {
                    cl = payloadClass.getClassLoader();
                }
                try {
                    this.contextBundle = ResourceBundle.getBundle(baseName, locale, cl);
                }
                catch (final MissingResourceException mre) {
                    this.contextBundle = null;
                }
            }
            try {
                final ClassLoader cl2 = (System.getSecurityManager() == null) ? Thread.currentThread().getContextClassLoader() : AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
                this.userBundle = ResourceBundle.getBundle("ValidationMessages", locale, cl2);
            }
            catch (final MissingResourceException mre2) {
                this.userBundle = null;
            }
            if (this.userBundle != null) {
                this.setParent(this.userBundle);
            }
        }
        
        @Override
        protected Object handleGetObject(final String key) {
            if (this.contextBundle != null) {
                return this.contextBundle.getObject(key);
            }
            return null;
        }
        
        @Override
        public Enumeration<String> getKeys() {
            final Set<String> keys = new TreeSet<String>();
            if (this.contextBundle != null) {
                keys.addAll(Collections.list(this.contextBundle.getKeys()));
            }
            if (this.userBundle != null) {
                keys.addAll(Collections.list(this.userBundle.getKeys()));
            }
            return Collections.enumeration(keys);
        }
    }
    
    private static class LocalisedMessage
    {
        private final String message;
        private final Locale locale;
        
        LocalisedMessage(final String message, final Locale locale) {
            this.message = message;
            this.locale = locale;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final LocalisedMessage that = (LocalisedMessage)o;
            Label_0062: {
                if (this.locale != null) {
                    if (this.locale.equals(that.locale)) {
                        break Label_0062;
                    }
                }
                else if (that.locale == null) {
                    break Label_0062;
                }
                return false;
            }
            if (this.message != null) {
                if (this.message.equals(that.message)) {
                    return true;
                }
            }
            else if (that.message == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.message != null) ? this.message.hashCode() : 0;
            result = 31 * result + ((this.locale != null) ? this.locale.hashCode() : 0);
            return result;
        }
    }
}
