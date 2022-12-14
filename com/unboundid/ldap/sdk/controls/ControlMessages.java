package com.unboundid.ldap.sdk.controls;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ControlMessages
{
    ERR_ASSERT_CANNOT_DECODE("The provided control cannot be decoded as an assertion request control because an error occurred while attempting to decode the assertion filter from the value:  {0}"), 
    ERR_ASSERT_NO_VALUE("The provided control cannot be decoded as an assertion request control because it does not have a value."), 
    ERR_AUTHZID_REQUEST_HAS_VALUE("The provided control cannot be decoded as an authorization identity request control because it has a value."), 
    ERR_AUTHZID_RESPONSE_NO_VALUE("The provided control cannot be decoded as an authorization identity response control because it does not have a value."), 
    ERR_DONT_USE_COPY_HAS_VALUE("The provided control cannot be decoded as a don't use copy request control because it has a value."), 
    ERR_ECN_CANNOT_DECODE_CHANGE_NUMBER("Unable to decode the change number from the entry change notification control sequence:  {0}"), 
    ERR_ECN_FIRST_NOT_ENUMERATED("Unable to decode the first element of the entry change notification control sequence as a change type enumerated element:  {0}"), 
    ERR_ECN_INVALID_CHANGE_TYPE("Invalid change type value for the entry change notification control:  {0}."), 
    ERR_ECN_INVALID_ELEMENT_COUNT("Invalid number of elements in the entry change notification control value sequence (expected between 1 and 3, got {0,number,0})."), 
    ERR_ECN_INVALID_ELEMENT_TYPE("Invalid element type in the entry change notification value sequence:  {0}."), 
    ERR_ECN_NO_VALUE("The provided control cannot be decoded as an entry change notification control because it does not have a value."), 
    ERR_ECN_VALUE_NOT_SEQUENCE("Unable to decode the value as an entry change notification control sequence:  {0}"), 
    ERR_MANAGE_DSA_IT_HAS_VALUE("The provided control cannot be decoded as a ManageDsaIT request control because it has a value."), 
    ERR_MV_FILTER_AND_NOT_SUPPORTED("AND search filters cannot be represented as matched values filters."), 
    ERR_MV_FILTER_CANNOT_DECODE_SUBSTRING("Unable to decode the provided matched values filter element as a substring filter sequence:  {0}"), 
    ERR_MV_FILTER_DNATTRS_NOT_SUPPORTED("Extensible match search filters with the DN attributes flag cannot be represented as matched values filters."), 
    ERR_MV_FILTER_EXT_INVALID_TYPE("Unable to decode the provided matched values filter element as an extensible match filter sequence because it contained an element with an invalid BER type of {0}."), 
    ERR_MV_FILTER_EXT_MULTIPLE_AT("Unable to decode the provided matched values filter element as an extensible match filter sequence because it contained multiple attribute type elements."), 
    ERR_MV_FILTER_EXT_MULTIPLE_VALUE("Unable to decode the provided matched values filter element as an extensible match filter sequence because it contained multiple match value elements."), 
    ERR_MV_FILTER_EXT_NOT_SEQUENCE("Unable to decode the provided matched values filter element as an extensible match filter sequence:  {0}"), 
    ERR_MV_FILTER_EXT_NO_VALUE("Unable to decode the provided matched values filter element as an extensible match filter because it did not contain a match value."), 
    ERR_MV_FILTER_INVALID_FILTER_TYPE("The provided search filter had an invalid filter type of {0} that cannot be converted to a matched values filter."), 
    ERR_MV_FILTER_INVALID_SUB_TYPE("Unable to decode the provided matched values filter element as a substring filter sequence because one of the substring components had an invalid BER type of {0}."), 
    ERR_MV_FILTER_INVALID_TYPE("Unable to decode the provided matched values filter element because it had an invalid BER type of {0}."), 
    ERR_MV_FILTER_MULTIPLE_MRID("Unable to decode the provided matched values filter element as an extensible match filter sequence because it contained multiple matching rule ID elements."), 
    ERR_MV_FILTER_MULTIPLE_SUBFINAL("Unable to decode the provided matched values filter element as a substring filter sequence because it contained multiple subFinal components."), 
    ERR_MV_FILTER_MULTIPLE_SUBINITIAL("Unable to decode the provided matched values filter element as a substring filter sequence because it contained multiple subInitial components."), 
    ERR_MV_FILTER_NOT_AVA("Unable to decode the provided matched values filter element as an attribute value assertion:  {0}"), 
    ERR_MV_FILTER_NOT_NOT_SUPPORTED("NOT search filters cannot be represented as matched values filters."), 
    ERR_MV_FILTER_NO_ATTR_OR_MRID("Unable to decode the provided matched values filter element as an extensible match filter because it did not contain either a match attribute or matching rule ID."), 
    ERR_MV_FILTER_NO_SUBSTRING_ELEMENTS("Unable to decode the provided matched values filter element as a substring filter because it did not contain any substring filter components."), 
    ERR_MV_FILTER_OR_NOT_SUPPORTED("OR search filters cannot be represented as matched values filters."), 
    ERR_MV_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a matched values request control because an error occurred while attempting to decode the value:  {0}"), 
    ERR_MV_REQUEST_NO_VALUE("The provided control cannot be decoded as a matched values request control because it does not have a value."), 
    ERR_PAGED_RESULTS_FIRST_NOT_INTEGER("Unable to decode the first element of the simple paged results value sequence as an integer:  {0}"), 
    ERR_PAGED_RESULTS_INVALID_ELEMENT_COUNT("Unexpected number of elements in the simple paged results value sequence (expected 2, got {0,number,0})."), 
    ERR_PAGED_RESULTS_NO_VALUE("No control value was provided, so it could not be decoded as a simple paged results response control."), 
    ERR_PAGED_RESULTS_VALUE_NOT_SEQUENCE("Unable to decode the value of the provided control as an paged results control sequence:  {0}"), 
    ERR_PERMISSIVE_MODIFY_HAS_VALUE("The provided control cannot be decoded as a permissive modify request control because it has a value."), 
    ERR_POST_READ_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a post-read request control because an error occurred while attempting to decode the value:  {0}"), 
    ERR_POST_READ_REQUEST_NO_VALUE("The provided control cannot be decoded as a post-read request control because it does not have a value."), 
    ERR_POST_READ_RESPONSE_ATTRIBUTES_NOT_SEQUENCE("The attributes element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_POST_READ_RESPONSE_ATTR_NOT_SEQUENCE("An attribute element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_POST_READ_RESPONSE_INVALID_ELEMENT_COUNT("The post-read entry sequence had an invalid number of elements (expected 2, got {0,number,0})."), 
    ERR_POST_READ_RESPONSE_NO_VALUE("The provided control cannot be decoded as a post-read response control because it does not have a value."), 
    ERR_POST_READ_RESPONSE_VALUE_NOT_SEQUENCE("The control value could not be decoded as a post-read response sequence:  {0}"), 
    ERR_PRE_READ_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a pre-read request control because an error occurred while attempting to decode the value:  {0}"), 
    ERR_PRE_READ_REQUEST_NO_VALUE("The provided control cannot be decoded as a pre-read request control because it does not have a value."), 
    ERR_PRE_READ_RESPONSE_ATTRIBUTES_NOT_SEQUENCE("The attributes element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_PRE_READ_RESPONSE_ATTR_NOT_SEQUENCE("An attribute element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_PRE_READ_RESPONSE_INVALID_ELEMENT_COUNT("The pre-read entry sequence had an invalid number of elements (expected 2, got {0,number,0})."), 
    ERR_PRE_READ_RESPONSE_NO_VALUE("The provided control cannot be decoded as a pre-read response control because it does not have a value."), 
    ERR_PRE_READ_RESPONSE_VALUE_NOT_SEQUENCE("The control value could not be decoded as pre-read response sequence:  {0}"), 
    ERR_PROXYV1_DECODE_ERROR("The provided control cannot be decoded as a proxied authorization v1 request control because an error occurred while attempting to decode the control value:  {0}"), 
    ERR_PROXY_V1_NO_VALUE("The provided control cannot be decoded as a proxied authorization v1 request control because it does not have a value."), 
    ERR_PROXY_V2_NO_VALUE("The provided control cannot be decoded as a proxied authorization v2 request control because it does not have a value."), 
    ERR_PSEARCH_CANNOT_DECODE("The provided control cannot be decoded as a persistent search request control because an error occurred while attempting to decode the control value:  {0}"), 
    ERR_PSEARCH_NO_VALUE("The provided control cannot be decoded as a persistent search request control because it does not have a value."), 
    ERR_PW_EXPIRED_NO_VALUE("The provided control cannot be decoded as a password expired response control because it does not have a value."), 
    ERR_PW_EXPIRED_VALUE_NOT_INTEGER("The provided control cannot be decoded as a password expired response control because the control value could not be parsed as an integer."), 
    ERR_PW_EXPIRING_NO_VALUE("The provided control cannot be decoded as a password expiring response control because it does not have a value."), 
    ERR_PW_EXPIRING_VALUE_NOT_INTEGER("The provided control cannot be decoded as a password expiring response control because the control value could not be parsed as an integer."), 
    ERR_SORT_KEY_ELEMENT_INVALID_TYPE("The provided ASN.1 element cannot be decoded as a sort key because the element sequence contained an element with an invalid BER type of {0}."), 
    ERR_SORT_KEY_INVALID_ELEMENT_COUNT("The provided ASN.1 element cannot be decoded as a sort key because the element sequence has an invalid number of elements (expected between 1 and 3, got {0,number,0})."), 
    ERR_SORT_KEY_NOT_SEQUENCE("The provided ASN.1 element cannot be decoded as a sort key because it could not be parsed as an ASN.1 sequence:  {0}"), 
    ERR_SORT_KEY_REVERSE_NOT_BOOLEAN("The provided ASN.1 element cannot be decoded as a sort key because the reverseOrder element could not be parsed as a Boolean:  {0}"), 
    ERR_SORT_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a server-side sort request control because an error occurred while attempting to decode the control value:  {0}"), 
    ERR_SORT_REQUEST_NO_VALUE("The provided control cannot be decoded as a server-side sort request control because it does not have a value."), 
    ERR_SORT_RESPONSE_FIRST_NOT_ENUM("Unable to decode the first element of the provided control as a result code enumerated element:  {0}"), 
    ERR_SORT_RESPONSE_INVALID_ELEMENT_COUNT("The sort response control value sequence had an invalid number of elements (expected 1 or 2, got {0,number,0})."), 
    ERR_SORT_RESPONSE_NO_VALUE("No control value was provided, so it could not be decoded as a server-side sort response control."), 
    ERR_SORT_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the value of the provided control as a sort response sequence:  {0}"), 
    ERR_SUBENTRIES_HAS_VALUE("The provided control cannot be decoded as a subentries request control because it has a value."), 
    ERR_SUBTREE_DELETE_HAS_VALUE("The provided control cannot be decoded as a subtree delete request control because it has a value."), 
    ERR_SYNC_DONE_NO_VALUE("The provided control cannot be decoded as a content synchronization done control because it does not have a value."), 
    ERR_SYNC_DONE_VALUE_CANNOT_DECODE("The provided control cannot be decoded as a content synchronization done control because an error occurred while trying to decode the control value:  {0}"), 
    ERR_SYNC_DONE_VALUE_INVALID_ELEMENT_TYPE("The provided control cannot be decoded as a content synchronization done control because the value sequence had an element with an invalid type of {0}."), 
    ERR_SYNC_DONE_VALUE_MULTIPLE_COOKIES("The provided control cannot be decoded as a content synchronization done control because the value sequence included multiple state cookies."), 
    ERR_SYNC_DONE_VALUE_MULTIPLE_REFRESH_DELETE("The provided control cannot be decoded as a content synchronization done control because the value sequence included multiple refreshDelete elements."), 
    ERR_SYNC_INFO_IR_INVALID_UUID("Unable to decode the provided intermediate response as a sync info response of type {0} because the value sequence included a malformed UUID:  {1}"), 
    ERR_SYNC_INFO_IR_NO_UUID_SET("Unable to decode the provided intermediate response as a sync info response of type {0} because the value sequence did not contain an entryUUID set."), 
    ERR_SYNC_INFO_IR_NO_VALUE("Unable to decode the provided intermediate response as a sync info response because it does not have a value."), 
    ERR_SYNC_INFO_IR_VALUE_DECODING_ERROR("Unable to decode the provided intermediate response as a sync info response because an error occurred while attempting to parse the value:  {0}"), 
    ERR_SYNC_INFO_IR_VALUE_INVALID_SEQUENCE_TYPE("Unable to decode the provided intermediate response as a sync info response of type {0} because the value sequence contained an element with an unexpected type of {1}."), 
    ERR_SYNC_INFO_IR_VALUE_NOT_ELEMENT("Unable to decode the provided intermediate response as a sync info response because the value could not be decoded as an ASN.1 element:  {0}"), 
    ERR_SYNC_INFO_IR_VALUE_UNRECOGNIZED_TYPE("Unable to decode the provided intermediate response as a sync info response because the value element had an unrecognized type of {0}."), 
    ERR_SYNC_REQUEST_NO_VALUE("The provided control cannot be decoded as a content synchronization request control because it does not have a value."), 
    ERR_SYNC_REQUEST_VALUE_CANNOT_DECODE("The provided control cannot be decoded as a content synchronization request control because an error occurred while trying to decode the control value:  {0}"), 
    ERR_SYNC_REQUEST_VALUE_INVALID_ELEMENT_TYPE("The provided control cannot be decoded as a content synchronization request control because the value sequence had an element with an invalid type of {0}."), 
    ERR_SYNC_REQUEST_VALUE_INVALID_MODE("The provided control cannot be decoded as a content synchronization request control because the value sequence included an invalid request mode of {0}."), 
    ERR_SYNC_REQUEST_VALUE_MULTIPLE_COOKIES("The provided control cannot be decoded as a content synchronization request control because the value sequence included multiple state cookies."), 
    ERR_SYNC_REQUEST_VALUE_MULTIPLE_HINTS("The provided control cannot be decoded as a content synchronization request control because the value sequence included multiple reload hint elements."), 
    ERR_SYNC_REQUEST_VALUE_MULTIPLE_MODES("The provided control cannot be decoded as a content synchronization request control because the value sequence included multiple request modes."), 
    ERR_SYNC_REQUEST_VALUE_NO_MODE("The provided control cannot be decoded as a content synchronization request control because the value sequence does not include a request mode."), 
    ERR_SYNC_STATE_NO_VALUE("The provided control cannot be decoded as a content synchronization state control because it does not have a value."), 
    ERR_SYNC_STATE_VALUE_CANNOT_DECODE("The provided control cannot be decoded as a content synchronization state control because an error occurred while trying to decode the control value:  {0}"), 
    ERR_SYNC_STATE_VALUE_INVALID_ELEMENT_TYPE("The provided control cannot be decoded as a content synchronization state control because the value sequence had an element with an invalid type of {0}."), 
    ERR_SYNC_STATE_VALUE_INVALID_STATE("The provided control cannot be decoded as a content synchronization state control because the value sequence included an invalid state value of {0}."), 
    ERR_SYNC_STATE_VALUE_MALFORMED_UUID("The provided control cannot be decoded as a content synchronization state control because the value sequence had a malformed entryUUID:  {0}"), 
    ERR_TXN_REQUEST_CONTROL_NO_VALUE("The provided control cannot be decoded as a transaction specification request control because it does not have a value."), 
    ERR_VLV_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a virtual list view request control because an error occurred while attempting to decode the control value:  {0}"), 
    ERR_VLV_REQUEST_INVALID_ELEMENT_TYPE("The provided control cannot be decoded as a virtual list view request control because the target element had an invalid BER type of {0}."), 
    ERR_VLV_REQUEST_NO_VALUE("The provided control cannot be decoded as a virtual list view request control because it does not have a value."), 
    ERR_VLV_RESPONSE_FIRST_NOT_INTEGER("Unable to decode the first element of the provided control as an integer element:  {0}"), 
    ERR_VLV_RESPONSE_INVALID_ELEMENT_COUNT("The control value sequence had an invalid number of elements (expected 3 or 4, got {0,number,0})."), 
    ERR_VLV_RESPONSE_NO_VALUE("No control value was provided, so it could not be decoded as a virtual list view response control."), 
    ERR_VLV_RESPONSE_SECOND_NOT_INTEGER("Unable to decode the second element of the provided control as an integer element:  {0}"), 
    ERR_VLV_RESPONSE_THIRD_NOT_ENUM("Unable to decode the third element of the provided control as an enumerated element:  {0}"), 
    ERR_VLV_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the value of the provided control as an ASN.1 sequence:  {0}"), 
    INFO_CONTROL_NAME_ASSERTION_REQUEST("Assertion Request Control"), 
    INFO_CONTROL_NAME_AUTHZID_REQUEST("Authorization Identity Request Control"), 
    INFO_CONTROL_NAME_AUTHZID_RESPONSE("Authorization Identity Response Control"), 
    INFO_CONTROL_NAME_CONTENT_SYNC_DONE("Content Synchronization Done Control"), 
    INFO_CONTROL_NAME_CONTENT_SYNC_REQUEST("Content Synchronization Request Control"), 
    INFO_CONTROL_NAME_CONTENT_SYNC_STATE("Content Synchronization State Control"), 
    INFO_CONTROL_NAME_DONT_USE_COPY("Don't Use Copy Request Control"), 
    INFO_CONTROL_NAME_ENTRY_CHANGE_NOTIFICATION("Entry Change Notification Control"), 
    INFO_CONTROL_NAME_MANAGE_DSAIT_REQUEST("ManageDsaIT Request Control"), 
    INFO_CONTROL_NAME_MATCHED_VALUES_REQUEST("Matched Values Request Control"), 
    INFO_CONTROL_NAME_PAGED_RESULTS("Simple Paged Results Control"), 
    INFO_CONTROL_NAME_PERMISSIVE_MODIFY_REQUEST("Permissive Modify Request Control"), 
    INFO_CONTROL_NAME_POST_READ_REQUEST("Post-Read Request Control"), 
    INFO_CONTROL_NAME_POST_READ_RESPONSE("Post-Read Response Control"), 
    INFO_CONTROL_NAME_PRE_READ_REQUEST("Pre-Read Request Control"), 
    INFO_CONTROL_NAME_PRE_READ_RESPONSE("Pre-Read Response Control"), 
    INFO_CONTROL_NAME_PROXIED_AUTHZ_V1_REQUEST("Proxied Authorization v1 Request Control"), 
    INFO_CONTROL_NAME_PROXIED_AUTHZ_V2_REQUEST("Proxied Authorization v2 Request Control"), 
    INFO_CONTROL_NAME_PSEARCH_REQUEST("Persistent Search Request Control"), 
    INFO_CONTROL_NAME_PW_EXPIRED("Password Expired Control"), 
    INFO_CONTROL_NAME_PW_EXPIRING("Password Expiring Control"), 
    INFO_CONTROL_NAME_SORT_REQUEST("Server-Side Sort Request Control"), 
    INFO_CONTROL_NAME_SORT_RESPONSE("Server-Side Sort Response Control"), 
    INFO_CONTROL_NAME_SUBENTRIES_REQUEST("LDAP Subentries Request Control"), 
    INFO_CONTROL_NAME_SUBTREE_DELETE_REQUEST("Subtree Delete Request Control"), 
    INFO_CONTROL_NAME_TXN_SPECIFICATION_REQUEST("Transaction Specification Request Control"), 
    INFO_CONTROL_NAME_VLV_REQUEST("Virtual List View Request Control"), 
    INFO_CONTROL_NAME_VLV_RESPONSE("Virtual List View Response Control"), 
    INFO_INTERMEDIATE_RESPONSE_NAME_SYNC_INFO("Content Synchronization Info Intermediate Response");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ControlMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ControlMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ControlMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ControlMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ControlMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ControlMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ControlMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ControlMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ControlMessages.MESSAGES.get(this);
        if (f == null) {
            if (ControlMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ControlMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ControlMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ControlMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ControlMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ControlMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ControlMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ControlMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-controls");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ControlMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ControlMessages, MessageFormat>(100);
    }
}
