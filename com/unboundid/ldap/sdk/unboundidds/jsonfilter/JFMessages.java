package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum JFMessages
{
    ERR_CONTAINS_FIELD_FILTER_UNRECOGNIZED_EXPECTED_TYPE("Unable to parse JSON object {0} as a filter of type ''{1}'' because value ''{2}'' of field ''{3}'' is not a valid expected type value."), 
    ERR_JSON_MATCHING_RULE_ORDERING_NOT_SUPPORTED("The JSON matching rule does not support ordering matching."), 
    ERR_JSON_MATCHING_RULE_SUBSTRING_NOT_SUPPORTED("The JSON matching rule does not support substring matching."), 
    ERR_OBJECT_FILTER_ARRAY_ELEMENT_NOT_FILTER("Unable to parse JSON object {0} as a filter of type ''{1}'' because object {2} in the array of values for field ''{3}'' cannot be parsed as a valid JSON object filter:  {4}"), 
    ERR_OBJECT_FILTER_ARRAY_ELEMENT_NOT_OBJECT("Unable to parse JSON object {0} as a filter of type ''{1}'' because the array value of field ''{2}'' contains at least one element that is not a JSON object."), 
    ERR_OBJECT_FILTER_INVALID_FILTER_TYPE("Unable to parse JSON object {0} as a filter because it has an unrecognized value for the ''{1}'' field."), 
    ERR_OBJECT_FILTER_MISSING_FILTER_TYPE("Unable to parse JSON object {0} as a filter because it is missing the required ''{1}'' field."), 
    ERR_OBJECT_FILTER_MISSING_REQUIRED_FIELD("Unable to parse JSON object {0} as a filter of type ''{1}'' because it is missing required field ''{2}''."), 
    ERR_OBJECT_FILTER_UNRECOGNIZED_FIELD("Unable to parse JSON object {0} as a filter of type ''{1}'' because it includes unrecognized field ''{2}''."), 
    ERR_OBJECT_FILTER_VALUE_EMPTY_ARRAY("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' was not expected to be an empty array."), 
    ERR_OBJECT_FILTER_VALUE_NOT_ARRAY("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' is not an array of JSON objects."), 
    ERR_OBJECT_FILTER_VALUE_NOT_BOOLEAN("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' is not a Boolean."), 
    ERR_OBJECT_FILTER_VALUE_NOT_FILTER("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' cannot be decoded as a JSON object filter:  {3}"), 
    ERR_OBJECT_FILTER_VALUE_NOT_OBJECT("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' is not an object."), 
    ERR_OBJECT_FILTER_VALUE_NOT_STRING("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' is not a string."), 
    ERR_OBJECT_FILTER_VALUE_NOT_STRINGS("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' is not a string or an array of strings."), 
    ERR_REGEX_FILTER_DECODE_INVALID_REGEX("Unable to parse JSON object {0} as a filter of type ''{1}'' because the value of field ''{2}'' cannot be parsed as a valid regular expression:  {3}"), 
    ERR_REGEX_FILTER_INVALID_REGEX("String ''{0}'' cannot be parsed as a valid regular expression:  {1}"), 
    ERR_SUBSTRING_FILTER_NO_COMPONENTS("Unable to parse JSON object {0} as a filter of type ''{1}'' because it does not have a value for at least one of the {2}, {3}, or {4} fields.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<JFMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<JFMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private JFMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = JFMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (JFMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = JFMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                JFMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (JFMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = JFMessages.MESSAGES.get(this);
        if (f == null) {
            if (JFMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(JFMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            JFMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (JFMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = JFMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (JFMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = JFMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                JFMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-jsonfilter");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<JFMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<JFMessages, MessageFormat>(100);
    }
}
