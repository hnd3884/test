package com.unboundid.ldap.sdk.experimental;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ExperimentalMessages
{
    ERR_DIRSYNC_CONTROL_DECODE_ERROR("The provided control cannot be decoded as a DirSync control because an error was encountered while attempting to parse the control value:  {0}"), 
    ERR_DIRSYNC_CONTROL_NO_VALUE("The provided control cannot be decoded as a DirSync control because it does not have a value."), 
    ERR_LOGSCHEMA_DECODE_ABANDON_ID_ERROR("Unable to decode the provided entry ''{0}'' as an abandon access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be decoded as a valid integer."), 
    ERR_LOGSCHEMA_DECODE_ADD_CHANGE_MISSING_ATTR("Unable to decode the provided entry ''{0}'' as an add access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include an attribute name before the colon."), 
    ERR_LOGSCHEMA_DECODE_ADD_CHANGE_MISSING_COLON("Unable to decode the provided entry ''{0}'' as an add access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include a colon to separate the attribute name from the change type."), 
    ERR_LOGSCHEMA_DECODE_ADD_CHANGE_NO_SPACE_AFTER_PLUS("Unable to decode the provided entry ''{0}'' as an add access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because the character immediately after the plus sign was not a space to separate the change type from the value."), 
    ERR_LOGSCHEMA_DECODE_ADD_CHANGE_TYPE_NOT_PLUS("Unable to decode the provided entry ''{0}'' as an add access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because the character immediately after the colon was not a plus sign to indicate that the value was being added to the entry."), 
    ERR_LOGSCHEMA_DECODE_BIND_VERSION_ERROR("Unable to decode the provided entry ''{0}'' as a bind access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be decoded as a valid integer."), 
    ERR_LOGSCHEMA_DECODE_CANNOT_DECODE_TIME("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be decoded as a valid generalized time."), 
    ERR_LOGSCHEMA_DECODE_COMPARE_AVA_ERROR("Unable to decode the provided entry ''{0}'' as a compare access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value could not be decoded as a valid attribute value assertion."), 
    ERR_LOGSCHEMA_DECODE_CONTROL_ERROR("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because an error occurred while attempting to decode an LDAP control contained in the {1} attribute:  {2}."), 
    ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_ATTR("Unable to decode the provided entry ''{0}'' as a delete access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include an attribute name before the colon."), 
    ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_COLON("Unable to decode the provided entry ''{0}'' as a delete access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include a colon to separate the attribute name from its value."), 
    ERR_LOGSCHEMA_DECODE_DELETE_OLD_ATTR_MISSING_SPACE("Unable to decode the provided entry ''{0}'' as a delete access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include a space immediately after the colon used to separate the attribute name from its value."), 
    ERR_LOGSCHEMA_DECODE_EXTENDED_MALFORMED_REQ_TYPE("Unable to decode the provided entry ''{0}'' as an extended access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' was not in the expected form for an extended operation.  For an extended operation, the {1} attribute must be the string 'extended' immediately followed by the OID of the extended request."), 
    ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because the provided entry was missing the required {1} attribute."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_INVALID_CHANGE_TYPE("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it had an invalid change type indicator immediately after the colon.  The change type indicator must be one of +, -, =, or #."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_ATTR("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include an attribute name before the colon."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_CHANGE_TYPE("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include a change type indicator (+, -, =, or #) immediately after the colon."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_COLON("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include a colon to separate the attribute name from the change type."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_SPACE("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because it did not include a space between the change type indicator and the value."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_CHANGE_MISSING_VALUE("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid attribute change descriptor because a change type of {3} requires an attribute value but none was provided."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_DN_DELETE_OLD_RDN_ERROR("Unable to decode the provided entry ''{0}'' as a modify DN access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' was invalid.  The value must be either 'TRUE' or 'FALSE'."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_ATTR("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include an attribute name before the colon."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_COLON("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include a colon to separate the attribute name from its value."), 
    ERR_LOGSCHEMA_DECODE_MODIFY_OLD_ATTR_MISSING_SPACE("Unable to decode the provided entry ''{0}'' as a modify access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid old attribute descriptor because it did not include a space immediately after the colon used to separate the attribute name from its value."), 
    ERR_LOGSCHEMA_DECODE_NO_OP_TYPE("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because the provided entry was missing the required {1} attribute to indicate the operation type."), 
    ERR_LOGSCHEMA_DECODE_RESULT_CODE_ERROR("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because value ''{1}'' of attribute {2} could not be parsed as an integer."), 
    ERR_LOGSCHEMA_DECODE_SEARCH_DEREF_ERROR("Unable to decode the provided entry ''{0}'' as a search access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' was invalid.  The value must be one of 'never', 'searching', 'finding', or 'always'."), 
    ERR_LOGSCHEMA_DECODE_SEARCH_FILTER_ERROR("Unable to decode the provided entry ''{0}'' as a search access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid LDAP search filter."), 
    ERR_LOGSCHEMA_DECODE_SEARCH_INT_ERROR("Unable to decode the provided entry ''{0}'' as a search access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' could not be parsed as a valid integer."), 
    ERR_LOGSCHEMA_DECODE_SEARCH_SCOPE_ERROR("Unable to decode the provided entry ''{0}'' as a search access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' was invalid.  The value must be one of 'base', 'one', 'sub', or 'subord'."), 
    ERR_LOGSCHEMA_DECODE_SEARCH_TYPES_ONLY_ERROR("Unable to decode the provided entry ''{0}'' as a search access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} value ''{2}'' was invalid.  The value must be either 'TRUE' or 'FALSE'."), 
    ERR_LOGSCHEMA_DECODE_UNRECOGNIZED_OP_TYPE("Unable to decode the provided entry ''{0}'' as an access log entry as per the draft-chu-ldap-logschema-00 specification because the {1} attribute had a value of ''{2}'' that does not represent a recognized operation type."), 
    ERR_NOOP_REQUEST_HAS_VALUE("The provided control cannot be decoded as a no-op request control because it has a value."), 
    ERR_PWP_REQUEST_HAS_VALUE("The provided control cannot be decoded as a password policy request control because it has a value."), 
    ERR_PWP_RESPONSE_CANNOT_DECODE_ERROR("The provided control cannot be decoded as a password policy response control because the error type element could not be decoded:  {0}"), 
    ERR_PWP_RESPONSE_CANNOT_DECODE_WARNING("The provided control cannot be decoded as a password policy response control because the warning type element could not be decoded:  {0}"), 
    ERR_PWP_RESPONSE_INVALID_ELEMENT_COUNT("The provided control cannot be decoded as a password policy response control because there were too many elements in the value sequence (expected between 0 and 2, got {0,number,0})."), 
    ERR_PWP_RESPONSE_INVALID_ERROR_TYPE("The provided control cannot be decoded as a password policy response control because it had an invalid error type ({0})."), 
    ERR_PWP_RESPONSE_INVALID_TYPE("The provided control cannot be decoded as a password policy response control because the value sequence contained an element with an invalid type ({0})."), 
    ERR_PWP_RESPONSE_INVALID_WARNING_TYPE("The provided control cannot be decoded as a password policy response control because the warning type element had an invalid type ({0})."), 
    ERR_PWP_RESPONSE_MULTIPLE_ERROR("The provided control cannot be decoded as a password policy response control because the value sequence contained multiple error elements."), 
    ERR_PWP_RESPONSE_MULTIPLE_WARNING("The provided control cannot be decoded as a password policy response control because the value sequence contained multiple warning elements."), 
    ERR_PWP_RESPONSE_NO_VALUE("The provided control cannot be decoded as a password policy response control because it does not have a value."), 
    ERR_PWP_RESPONSE_VALUE_NOT_SEQUENCE("The provided control cannot be decoded as a password policy response control because the control value could not be decoded as a sequence:  {0}"), 
    INFO_CONTROL_NAME_DIRSYNC("Active Directory DirSync Control"), 
    INFO_CONTROL_NAME_NOOP_REQUEST("No-Op Request Control"), 
    INFO_CONTROL_NAME_PW_POLICY_REQUEST("Password Policy Request Control"), 
    INFO_CONTROL_NAME_PW_POLICY_RESPONSE("Password Policy Response Control");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ExperimentalMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ExperimentalMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ExperimentalMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ExperimentalMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ExperimentalMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ExperimentalMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ExperimentalMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ExperimentalMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ExperimentalMessages.MESSAGES.get(this);
        if (f == null) {
            if (ExperimentalMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ExperimentalMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ExperimentalMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ExperimentalMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ExperimentalMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ExperimentalMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ExperimentalMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ExperimentalMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-experimental");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ExperimentalMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ExperimentalMessages, MessageFormat>(100);
    }
}
