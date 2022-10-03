package com.unboundid.ldap.listener.interceptor;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum InterceptorMessages
{
    ERR_DS_INTERCEPTOR_REQUEST_ERROR("An unexpected error was encountered during request processing for operation {0} in an in-memory operation interceptor of type {1}:  {2}"), 
    ERR_DS_INTERCEPTOR_RESULT_ERROR("An unexpected error was encountered during result processing for operation {0} in an in-memory operation interceptor of type {1}:  {2}");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<InterceptorMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<InterceptorMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private InterceptorMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = InterceptorMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (InterceptorMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = InterceptorMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                InterceptorMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (InterceptorMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = InterceptorMessages.MESSAGES.get(this);
        if (f == null) {
            if (InterceptorMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(InterceptorMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            InterceptorMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (InterceptorMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = InterceptorMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (InterceptorMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = InterceptorMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                InterceptorMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-interceptor");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<InterceptorMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<InterceptorMessages, MessageFormat>(100);
    }
}
