package com.unboundid.ldap.sdk.unboundidds.logs;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum LogMessages
{
    ERR_ADD_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_ADD("Unable to parse the provided lines as an add audit log message because while the lines could be parsed as a valid LDIF change record, the change record had a change type of ''{0}'' instead of the expected change type of ''{1}''."), 
    ERR_ADD_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD("Unable to parse the provided lines as an add audit log message because they could not be parsed as a valid LDIF change record:  {0}"), 
    ERR_AUDIT_LOG_MESSAGE_END_BEFORE_CLOSING_QUOTE("Unable to read the quoted string value of property ''{0}'' because the end of the string was reached before finding the closing quote."), 
    ERR_AUDIT_LOG_MESSAGE_END_BEFORE_ESCAPED("Unable to read the string value of property ''{0}'' because the end of the string was reached immediately after a backslash that was expected to escape the next character."), 
    ERR_AUDIT_LOG_MESSAGE_END_BEFORE_HEX("Unable to read the string value of property ''{0}'' because the end of the string was reached before finding both hexadecimal digits following the octothorpe (#) character."), 
    ERR_AUDIT_LOG_MESSAGE_ERROR_READING_JSON_OBJECT("An error occurred while trying to read the value of property ''{0}'' as a JSON object:  {1}"), 
    ERR_AUDIT_LOG_MESSAGE_HEADER_EMPTY_PROPERTY_NAME("The header line contains an equal sign after a property delimiter, indicating a property with an empty name."), 
    ERR_AUDIT_LOG_MESSAGE_HEADER_ENDS_WITH_PROPERTY_NAME("The header line ends with what appears to be a partial or complete property name (''{0}'') with no equal sign to denote the start of the property value."), 
    ERR_AUDIT_LOG_MESSAGE_HEADER_MALFORMED_TIMESTAMP("Unable to parse the first element of the header line as a timestamp."), 
    ERR_AUDIT_LOG_MESSAGE_HEADER_NO_SEMICOLONS("The header line does not contain any semicolons to separate header elements."), 
    ERR_AUDIT_LOG_MESSAGE_INVALID_HEX_DIGIT("Unable to read the string value of property ''{0}'' because the value contained an octothorpe (#) character that was not immediately followed by two valid hexadecimal digits."), 
    ERR_AUDIT_LOG_MESSAGE_LIST_CANNOT_PARSE_HEADER("Unable to create an audit log message from the provided log message line list because the first line in that list (''{0}'') cannot be parsed as a valid audit log message header:  {1}"), 
    ERR_AUDIT_LOG_MESSAGE_LIST_CONTAINS_EMPTY_LINE("Unable to create an audit log message from a log message line list that contains an empty line."), 
    ERR_AUDIT_LOG_MESSAGE_LIST_DOES_NOT_START_WITH_COMMENT("Unable to create an audit log message from a log message line list that does not start with a comment line that represents the log message header."), 
    ERR_AUDIT_LOG_MESSAGE_LIST_EMPTY("Unable to create an audit log message from a log message line list that is empty."), 
    ERR_AUDIT_LOG_MESSAGE_LIST_NULL("Unable to create an audit log message from a log message line list that is null."), 
    ERR_AUDIT_LOG_MESSAGE_UNEXPECTED_CHAR_AFTER_PROPERTY("Found unexpected character ''{0}'' after reading the value of property ''{1}''.  Only spaces and a semicolon were expected."), 
    ERR_AUDIT_LOG_READER_CANNOT_PARSE_CHANGE_RECORD("Unable to parse an LDIF change record out of the audit log message with lines {0}:  {1}"), 
    ERR_AUDIT_LOG_READER_UNSUPPORTED_CHANGE_RECORD("Unable to create an audit log message from the data contained in lines {0} because the parsed change log had an unsupported change type of ''{1}''."), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_DELETE("Unable to parse the provided lines as a delete audit log message because while the lines could be parsed as a valid LDIF change record, the change record had a change type of ''{0}'' instead of the expected change type of ''{1}''."), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_DELETED_ENTRY("Unable to create a list of revert change records for the delete of entry ''{0}'' because the audit log message did not include the contents of the entry that was deleted.  Make sure that the audit logger is configured to record changes in revertible form."), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD("Unable to parse the provided lines as a delete audit log message because they could not be parsed as a valid LDIF change record:  {0}"), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_NO_SOFT_DELETED_ENTRY_DN("Unable to create a list of revert change records for the soft-delete of entry ''{0}'' because the audit log message did not contain the DN of the resulting soft-deleted entry."), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_SUBTREE_DELETE_WITHOUT_ENTRY("Unable to create a list of revert change records for the subtree delete based at entry ''{0}'' because that audit log message in itself does not contain enough information to restore the entire subtree.  Further, even the base entry cannot be recreated because the delete audit log message is not in reversible form."), 
    ERR_DELETE_AUDIT_LOG_MESSAGE_SUBTREE_DELETE_WITH_ENTRY("Unable to create a list of revert change records for the subtree delete based at entry ''{0}'' because that audit log message in itself does not contain enough information to restore the entire subtree.  However, this record does have enough information to restore just the base entry, and the necessary revert change record can be manually obtained by creating an LDIF add change record from the deleted entry."), 
    ERR_LOG_MESSAGE_INVALID_ACCESS_MESSAGE_TYPE("The log message string does not include a recognized access log message type."), 
    ERR_LOG_MESSAGE_INVALID_ASSURANCE_COMPLETE_OPERATION_TYPE("The log message string appears to contain information about an operation for which replication assurance processing had been completed, but the message did not have a recognized operation type."), 
    ERR_LOG_MESSAGE_INVALID_ESCAPED_CHARACTER("The log message string appears to contain an invalid escaped character in token ''{0}'' because it contains a backslash not followed by two hexadecimal digits."), 
    ERR_LOG_MESSAGE_INVALID_FORWARD_FAILED_OPERATION_TYPE("The log message string appears to contain a failed forwarded operation but did not have a recognized operation type."), 
    ERR_LOG_MESSAGE_INVALID_FORWARD_OPERATION_TYPE("The log message string appears to contain n forwarded operation but did not have a recognized operation type."), 
    ERR_LOG_MESSAGE_INVALID_REQUEST_OPERATION_TYPE("The log message string appears to contain an operation request but did not have a recognized operation type."), 
    ERR_LOG_MESSAGE_INVALID_RESULT_OPERATION_TYPE("The log message string appears to contain an operation result but did not have a recognized operation type."), 
    ERR_LOG_MESSAGE_INVALID_TIMESTAMP("The log message string does not appear to start with a valid timestamp:  {0}"), 
    ERR_LOG_MESSAGE_NO_TIMESTAMP("The log message string does not appear to start with a timestamp."), 
    ERR_MODIFY_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_MODIFY("Unable to parse the provided lines as a modify audit log message because while the lines could be parsed as a valid LDIF change record, the change record had a change type of ''{0}'' instead of the expected change type of ''{1}''."), 
    ERR_MODIFY_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD("Unable to parse the provided lines as a modify audit log message because they could not be parsed as a valid LDIF change record:  {0}"), 
    ERR_MODIFY_AUDIT_LOG_MESSAGE_MOD_NOT_REVERTIBLE("Unable to create a list of revert change records for the modify of entry ''{0}'' because the modify audit log message included modification {1} that is not revertible.  Make sure that the audit logger is configured to record changes in reversible form."), 
    ERR_MODIFY_DN_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_MODIFY_DN("Unable to parse the provided lines as a modify DN audit log message because while the lines could be parsed as a valid LDIF change record, the change record had a change type of ''{0}'' instead of the expected change type of ''{1}''."), 
    ERR_MODIFY_DN_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD("Unable to parse the provided lines as a modify DN audit log message because they could not be parsed as a valid LDIF change record:  {0}"), 
    ERR_MODIFY_DN_CANNOT_GET_NEW_DN_WITHOUT_NEW_SUPERIOR("Unable to create a list of revert change records for the modify DN of entry ''{0}'' because either the original DN or new RDN (''{1}'') could not be parsed."), 
    ERR_MODIFY_DN_CANNOT_GET_NEW_DN_WITH_NEW_SUPERIOR("Unable to create a list of revert change records for the modify DN of entry ''{0}'' because at least one of the original DN, the new RDN (''{1}''), or the new superior DN (''{2}'') could not be parsed."), 
    ERR_MODIFY_DN_CANNOT_REVERT_NULL_DN("Unable to create a list of revert change records for a modify DN that targets the root DSE."), 
    ERR_MODIFY_DN_CANNOT_REVERT_WITHOUT_NECESSARY_MODS("Unable to create a list of revert change records for the modify DN of entry ''{0}'' because the log message did not include any attribute modifications, but attribute modifications are required to determine the correct deleteOldRDN value."), 
    ERR_MODIFY_DN_MOD_NOT_REVERTIBLE("Unable to create a list of revert change records for the modify DN of entry ''{0}'' because the modify DN audit log message included a non-revertible {1} modification for attribute ''{2}''.  Make sure that the audit logger is configured to record changes in reversible form."), 
    ERR_MODIFY_DN_NOT_REVERTIBLE("Unable to create a list of revert change records for the modify DN of entry ''{0}'' because the modify DN audit log message did not include a set of attribute modifications.  Make sure that the audit logger is configured to record changes in reversible form.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<LogMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<LogMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private LogMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = LogMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (LogMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = LogMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                LogMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (LogMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = LogMessages.MESSAGES.get(this);
        if (f == null) {
            if (LogMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(LogMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            LogMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (LogMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = LogMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (LogMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = LogMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                LogMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-log");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<LogMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<LogMessages, MessageFormat>(100);
    }
}
