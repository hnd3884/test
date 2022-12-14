package com.unboundid.ldap.protocol;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ProtocolMessages
{
    ERR_ABANDON_REQUEST_CANNOT_DECODE("Unable to read or decode an abandon request protocol op:  {0}"), 
    ERR_ADD_REQUEST_CANNOT_DECODE("Unable to read or decode an add request protocol op:  {0}"), 
    ERR_ADD_RESPONSE_CANNOT_DECODE("Unable to read or decode an add response protocol op:  {0}"), 
    ERR_BIND_REQUEST_CANNOT_CREATE_WITH_PASSWORD_PROVIDER("Unable to create a bind request protocol op from a simple bind request object that uses a password provider rather than a statically-defined password."), 
    ERR_BIND_REQUEST_CANNOT_DECODE("Unable to read or decode a bind request protocol op:  {0}"), 
    ERR_BIND_REQUEST_INVALID_CRED_TYPE("Invalid credentials type {0} in a bind request protocol op."), 
    ERR_BIND_RESPONSE_CANNOT_DECODE("Unable to read or decode a bind response protocol op:  {0}"), 
    ERR_BIND_RESPONSE_INVALID_ELEMENT("Invalid element type {0} in a bind response protocol op."), 
    ERR_COMPARE_REQUEST_CANNOT_DECODE("Unable to read or decode a compare request protocol op:  {0}"), 
    ERR_COMPARE_RESPONSE_CANNOT_DECODE("Unable to read or decode a compare response protocol op:  {0}"), 
    ERR_DELETE_REQUEST_CANNOT_DECODE("Unable to read or decode a delete request protocol op:  {0}"), 
    ERR_DELETE_RESPONSE_CANNOT_DECODE("Unable to read or decode a delete response protocol op:  {0}"), 
    ERR_EXTENDED_REQUEST_CANNOT_DECODE("Unable to read or decode an extended request protocol op:  {0}"), 
    ERR_EXTENDED_RESPONSE_CANNOT_DECODE("Unable to read or decode an extended response protocol op:  {0}"), 
    ERR_EXTENDED_RESPONSE_INVALID_ELEMENT("Invalid element type {0} in an extended response protocol op."), 
    ERR_INTERMEDIATE_RESPONSE_CANNOT_DECODE("Unable to read or decode an intermediate response:  {0}"), 
    ERR_INTERMEDIATE_RESPONSE_INVALID_ELEMENT("Invalid element type {0} in an extended response protocol op."), 
    ERR_MESSAGE_CANNOT_DECODE("Unable to read or decode an LDAP message:  {0}"), 
    ERR_MESSAGE_DECODE_ERROR("An error occurred while attempting to decode the provided ASN.1 element as an LDAP message:  {0}"), 
    ERR_MESSAGE_DECODE_INVALID_PROTOCOL_OP_TYPE("Unable to decode the provided ASN.1 element as an LDAP message because it has an invalid protocol op type of {0}."), 
    ERR_MESSAGE_DECODE_VALUE_SEQUENCE_INVALID_ELEMENT_COUNT("Unable to decode the provided ASN.1 element as an LDAP message because the sequence had an invalid element count of {0,number,0}."), 
    ERR_MESSAGE_INVALID_PROTOCOL_OP_TYPE("Invalid protocol op type {0} encountered in an LDAP message."), 
    ERR_MESSAGE_IO_ERROR("An I/O error occurred while trying to read the response from the server:  {0}"), 
    ERR_MESSAGE_PROTOCOL_OP_TYPE_NOT_RESPONSE("Request protocol op type {0} encountered in an LDAP message when a response type was expected."), 
    ERR_MODIFY_DN_REQUEST_CANNOT_DECODE("Unable to read or decode a modify DN request protocol op:  {0}"), 
    ERR_MODIFY_DN_RESPONSE_CANNOT_DECODE("Unable to read or decode a modify DN response protocol op:  {0}"), 
    ERR_MODIFY_REQUEST_CANNOT_DECODE("Unable to read or decode a modify request protocol op:  {0}"), 
    ERR_MODIFY_RESPONSE_CANNOT_DECODE("Unable to read or decode a modify response protocol op:  {0}"), 
    ERR_RESPONSE_CANNOT_DECODE("Unable to read or decode an LDAP response:  {0}"), 
    ERR_SEARCH_DONE_CANNOT_DECODE("Unable to read or decode a search result done protocol op:  {0}"), 
    ERR_SEARCH_ENTRY_CANNOT_DECODE("Unable to read or decode a search result entry protocol op:  {0}"), 
    ERR_SEARCH_REFERENCE_CANNOT_DECODE("Unable to read or decode a search result reference protocol op:  {0}"), 
    ERR_SEARCH_REQUEST_CANNOT_DECODE("Unable to read or decode a search request protocol op:  {0}"), 
    ERR_UNBIND_REQUEST_CANNOT_DECODE("Unable to read or decode an unbind request protocol op:  {0}");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ProtocolMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ProtocolMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ProtocolMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ProtocolMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ProtocolMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ProtocolMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ProtocolMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ProtocolMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ProtocolMessages.MESSAGES.get(this);
        if (f == null) {
            if (ProtocolMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ProtocolMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ProtocolMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ProtocolMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ProtocolMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ProtocolMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ProtocolMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ProtocolMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-protocol");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ProtocolMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ProtocolMessages, MessageFormat>(100);
    }
}
