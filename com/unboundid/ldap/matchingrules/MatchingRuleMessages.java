package com.unboundid.ldap.matchingrules;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum MatchingRuleMessages
{
    ERR_BOOLEAN_INVALID_VALUE("The provided value is invalid.  Boolean values may only be 'TRUE' or 'FALSE'."), 
    ERR_BOOLEAN_ORDERING_MATCHING_NOT_SUPPORTED("Ordering matching is not supported for Boolean values."), 
    ERR_BOOLEAN_SUBSTRING_MATCHING_NOT_SUPPORTED("Substring matching is not supported for Boolean values."), 
    ERR_CASE_IGNORE_LIST_EMPTY_ITEM("The provided value ''{0}'' is not valid according to the case-ignore list syntax because it contains a zero-length item."), 
    ERR_CASE_IGNORE_LIST_EMPTY_LIST("The provided value ''{0}'' is not valid according to the case-ignore list syntax because it does not contain any items."), 
    ERR_CASE_IGNORE_LIST_MALFORMED_HEX_CHAR("The provided value ''{0}'' is not valid according to the case-ignore list syntax because it contains a backslash which is not followed by two hexadecimal digits."), 
    ERR_CASE_IGNORE_LIST_NOT_HEX_DIGIT("Character ''{0}'' is not a valid hexadecimal digit."), 
    ERR_CASE_IGNORE_LIST_ORDERING_MATCHING_NOT_SUPPORTED("Ordering matching is not supported for case-ignore list values."), 
    ERR_CASE_IGNORE_LIST_SUBSTRING_COMPONENT_CONTAINS_DOLLAR("Substring component ''{0}'' is invalid because it contains an unescaped dollar sign."), 
    ERR_DN_ORDERING_MATCHING_NOT_SUPPORTED("Ordering matching is not supported for distinguished name values."), 
    ERR_DN_SUBSTRING_MATCHING_NOT_SUPPORTED("Substring matching is not supported for distinguished name values."), 
    ERR_GENERALIZED_TIME_INVALID_VALUE("The provided value cannot be parsed according to the generalized time syntax:  {0}"), 
    ERR_GENERALIZED_TIME_SUBSTRING_MATCHING_NOT_SUPPORTED("Substring matching is not supported for generalized time values."), 
    ERR_INTEGER_INVALID_CHARACTER("The provided value is not a valid integer because it contains an invalid character at position {0,number,0}."), 
    ERR_INTEGER_INVALID_LEADING_ZERO("Integer values are not allowed to have  leading zeroes."), 
    ERR_INTEGER_SUBSTRING_MATCHING_NOT_SUPPORTED("Substring matching is not supported for integer values."), 
    ERR_INTEGER_ZERO_LENGTH_NOT_ALLOWED("Integer values are not allowed to be zero-length strings."), 
    ERR_NUMERIC_STRING_INVALID_CHARACTER("The provided value is not a valid numeric string because it contains a character other than a space or numeric digit at position {0,number,0}."), 
    ERR_TELEPHONE_NUMBER_INVALID_CHARACTER("The provided value cannot be parsed as a telephone number because it contains an invalid character at position {0,number,0}."), 
    ERR_TELEPHONE_NUMBER_ORDERING_MATCHING_NOT_SUPPORTED("Ordering matching is not supported for telephone number values.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<MatchingRuleMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<MatchingRuleMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private MatchingRuleMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = MatchingRuleMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (MatchingRuleMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = MatchingRuleMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                MatchingRuleMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (MatchingRuleMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = MatchingRuleMessages.MESSAGES.get(this);
        if (f == null) {
            if (MatchingRuleMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(MatchingRuleMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            MatchingRuleMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (MatchingRuleMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = MatchingRuleMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (MatchingRuleMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = MatchingRuleMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                MatchingRuleMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-matchingrules");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<MatchingRuleMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<MatchingRuleMessages, MessageFormat>(100);
    }
}
