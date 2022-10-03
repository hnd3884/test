package com.unboundid.ldap.sdk.extensions;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ExtOpMessages
{
    ERR_ABORTED_TXN_NO_VALUE("Unable to decode the provided generic extended result as an aborted transaction result because it did not have a value."), 
    ERR_CANCEL_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE("Cancel operations are not supported on connections operating in synchronous mode"), 
    ERR_CANCEL_REQUEST_CANNOT_DECODE("The provided extended request cannot be decoded as a cancel request because an error occurred while attempting to parse the value:  {0}"), 
    ERR_CANCEL_REQUEST_NO_VALUE("The provided extended request cannot be decoded as a cancel request because it does not have a value."), 
    ERR_END_TXN_REQUEST_CANNOT_DECODE("The provided extended request cannot be decoded as an end transaction request because an error occurred while attempting to parse the value:  {0}"), 
    ERR_END_TXN_REQUEST_NO_VALUE("The provided extended request cannot be decoded as an end transaction request because it does not have a value."), 
    ERR_END_TXN_RESPONSE_CANNOT_DECODE_MSGID("Unable to decode the message ID from the end transaction value sequence:  {0}"), 
    ERR_END_TXN_RESPONSE_CONTROLS_ELEMENT_NOT_SEQUENCE("Unable to decode the controls element of an updateControls sequence as an ASN.1 sequence:  {0}"), 
    ERR_END_TXN_RESPONSE_CONTROLS_NOT_SEQUENCE("Unable to decode the updatesControls element in the end transaction value sequence as an ASN.1 sequence:  {0}"), 
    ERR_END_TXN_RESPONSE_CONTROL_INVALID_ELEMENT_COUNT("Invalid number of elements in an updateControls element sequence (expected 2, got {0,number,0})."), 
    ERR_END_TXN_RESPONSE_CONTROL_MSGID_NOT_INT("Unable to decode the message ID element of an updateControls sequence as an integer:  {0}"), 
    ERR_END_TXN_RESPONSE_CONTROL_NOT_SEQUENCE("Unable to decode an updateControls sequence element in the end transaction value as an ASN.1 sequence:  {0}"), 
    ERR_END_TXN_RESPONSE_INVALID_ELEMENT_COUNT("Too many elements in the end transaction value sequence (expected 1 or 2, got {0,number,0})."), 
    ERR_END_TXN_RESPONSE_INVALID_TYPE("Unexpected element type {0} encountered in the end transaction value sequence."), 
    ERR_END_TXN_RESPONSE_VALUE_NOT_SEQUENCE("Cannot decode the end transaction value as an ASN.1 sequence:  {0}"), 
    ERR_PW_MODIFY_REQUEST_CANNOT_DECODE("The provided extended request cannot be decoded as a password modify request because an error occurred while attempting to parse the value:  {0}"), 
    ERR_PW_MODIFY_REQUEST_INVALID_TYPE("The provided extended request cannot be decoded as a password modify request because an element in the value sequence had an invalid BER type of {0}."), 
    ERR_PW_MODIFY_REQUEST_NO_VALUE("The provided extended request cannot be decoded as a password modify request because it does not have a value."), 
    ERR_PW_MODIFY_RESPONSE_MULTIPLE_ELEMENTS("Unable to decode the provided extended result as a password modify extended result because the value sequence contained multiple elements."), 
    ERR_PW_MODIFY_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the provided extended result as a password modify extended result because the value element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_STARTTLS_REQUEST_CANNOT_CREATE_DEFAULT_CONTEXT("An error occurred while attempting to create a default SSL context:  {0}"), 
    ERR_STARTTLS_REQUEST_HAS_VALUE("The provided extended cannot request be decoded as a StartTLS request because it has a value."), 
    ERR_START_TXN_REQUEST_HAS_VALUE("The provided extended cannot request be decoded as a start transaction request because it has a value."), 
    ERR_WHO_AM_I_REQUEST_HAS_VALUE("The provided extended request cannot be decoded as a Who Am I? request because it has a value."), 
    INFO_EXTENDED_REQUEST_NAME_CANCEL("Cancel Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_END_TXN("End Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_PASSWORD_MODIFY("Password Modify Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_START_TLS("StartTLS Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_START_TXN("Start Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_WHO_AM_I("Who Am I? Extended Request"), 
    INFO_EXTENDED_RESULT_NAME_ABORTED_TXN("Aborted Transaction Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_END_TXN("End Transaction Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_NOTICE_OF_DISCONNECT("Notice Of Disconnection Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_PASSWORD_MODIFY("Password Modify Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_START_TXN("Start Transaction Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_WHO_AM_I("Who Am I? Extended Result");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ExtOpMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ExtOpMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ExtOpMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ExtOpMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ExtOpMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ExtOpMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ExtOpMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ExtOpMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ExtOpMessages.MESSAGES.get(this);
        if (f == null) {
            if (ExtOpMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ExtOpMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ExtOpMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ExtOpMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ExtOpMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ExtOpMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ExtOpMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ExtOpMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-extop");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ExtOpMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ExtOpMessages, MessageFormat>(100);
    }
}
