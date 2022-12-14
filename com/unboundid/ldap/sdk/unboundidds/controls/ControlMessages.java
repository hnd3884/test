package com.unboundid.ldap.sdk.unboundidds.controls;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ControlMessages
{
    ERR_ACCOUNT_USABLE_REQUEST_HAS_VALUE("The provided control cannot be decoded as an account usable request control because it has a value."), 
    ERR_ACCOUNT_USABLE_RESPONSE_GRACE_LOGINS_NOT_INT("Unable to decode the provided control as an account usable response control because the remainingGraceLogins element could not be decoded as an integer:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_INACTIVE_NOT_BOOLEAN("Unable to decode the provided control as an account usable response control because the isInactive element could not be decoded as a Boolean:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_INVALID_TYPE("Unable to decode the provided control as an account usable response control because the control value element had an invalid BER type of {0}."), 
    ERR_ACCOUNT_USABLE_RESPONSE_IS_EXP_NOT_BOOLEAN("Unable to decode the provided control as an account usable response control because the passwordIsExpired element could not be decoded as a Boolean:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_MORE_INFO_INVALID_TYPE("Unable to decode the provided control as an account usable control because the MORE_INFO sequence contained an element with an invalid BER type of {0}."), 
    ERR_ACCOUNT_USABLE_RESPONSE_MUST_CHANGE_NOT_BOOLEAN("Unable to decode the provided control as an account usable response control because the mustChangePassword element could not be decoded as a Boolean:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_NO_VALUE("Unable to decode the provided control as an account usable response control because the provided control does not have a value."), 
    ERR_ACCOUNT_USABLE_RESPONSE_STE_NOT_INT("Unable to decode the provided control as an account usable response control because the seconds until expiration element could not be decoded as an integer:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_STU_NOT_INT("Unable to decode the provided control as an account usable response control because the secondsUntilUnlock element could not be decoded as an integer:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_VALUE_NOT_ELEMENT("Unable to decode the provided control as an account usable response control because the control value could not be decoded as an ASN.1 element:  {0}"), 
    ERR_ACCOUNT_USABLE_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the provided control as an account usable response control because the control value element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_ACCT_UNUSABLE_INACTIVE("The account has been locked or deactivated."), 
    ERR_ACCT_UNUSABLE_MUST_CHANGE_PW("The password must be changed before any other operations will be allowed."), 
    ERR_ACCT_UNUSABLE_PW_EXPIRED("The password is expired."), 
    ERR_ACCT_UNUSABLE_REMAINING_GRACE_MULTIPLE("{0,number,0} grace logins are available."), 
    ERR_ACCT_UNUSABLE_REMAINING_GRACE_NONE("No remaining grace logins."), 
    ERR_ACCT_UNUSABLE_REMAINING_GRACE_ONE("1 grace login is available."), 
    ERR_ACCT_UNUSABLE_SECONDS_UNTIL_UNLOCK("The account will be automatically unlocked in {0,number,0} seconds."), 
    ERR_ASSURED_REPLICATION_REQUEST_ERROR_DECODING_VALUE("The provided control cannot be decoded as an assured replication request control because an unexpected error occurred while attempting to decode the value:  {0}"), 
    ERR_ASSURED_REPLICATION_REQUEST_INVALID_MAX_LOCAL_LEVEL("The provided control cannot be decoded as an assured replication request control because it included an unrecognized maximum local assurance level value of {0}."), 
    ERR_ASSURED_REPLICATION_REQUEST_INVALID_MAX_REMOTE_LEVEL("The provided control cannot be decoded as an assured replication request control because it included an unrecognized maximum remote assurance level value of {0}."), 
    ERR_ASSURED_REPLICATION_REQUEST_INVALID_MIN_LOCAL_LEVEL("The provided control cannot be decoded as an assured replication request control because it included an unrecognized minimum local assurance level value of {0}."), 
    ERR_ASSURED_REPLICATION_REQUEST_INVALID_MIN_REMOTE_LEVEL("The provided control cannot be decoded as an assured replication request control because it included an unrecognized minimum remote assurance level value of {0}."), 
    ERR_ASSURED_REPLICATION_REQUEST_NO_VALUE("The provided control cannot be decoded as an assured replication request control because it does not have a value."), 
    ERR_ASSURED_REPLICATION_REQUEST_UNEXPECTED_ELEMENT_TYPE("The provided control cannot be decoded as an assured replication request control because the value sequence had an element with an unrecognized BER type of {0}."), 
    ERR_ASSURED_REPLICATION_RESPONSE_ERROR_DECODING_SR("The provided control cannot be decoded as an assured replication response control because an error occurred while attempting to decode a server result element:  {0}"), 
    ERR_ASSURED_REPLICATION_RESPONSE_ERROR_DECODING_VALUE("The provided control cannot be decoded as an assured replication response control because an error occurred while attempting to decode the value:  {0}"), 
    ERR_ASSURED_REPLICATION_RESPONSE_INVALID_LOCAL_LEVEL("The provided control cannot be decoded as an assured replication response control because it included an unrecognized local level value of {0}."), 
    ERR_ASSURED_REPLICATION_RESPONSE_INVALID_LOCAL_RESULT("The provided control cannot be decoded as an assured replication response control because it included an unrecognized local result code value of {0}."), 
    ERR_ASSURED_REPLICATION_RESPONSE_INVALID_REMOTE_LEVEL("The provided control cannot be decoded as an assured replication response control because it included an unrecognized remote level value of {0}."), 
    ERR_ASSURED_REPLICATION_RESPONSE_INVALID_REMOTE_RESULT("The provided control cannot be decoded as an assured replication response control because it included an unrecognized remote result code value of {0}."), 
    ERR_ASSURED_REPLICATION_RESPONSE_NO_LOCAL_SATISFIED("The provided control cannot be decoded as an assured replication response control because it does not indicate whether the desired local level of assurance was satisfied."), 
    ERR_ASSURED_REPLICATION_RESPONSE_NO_REMOTE_SATISFIED("The provided control cannot be decoded as an assured replication response control because it does not indicate whether the desired remote level of assurance was satisfied."), 
    ERR_ASSURED_REPLICATION_RESPONSE_NO_VALUE("The provided control cannot be decoded as an assured replication response control because it does not have a value."), 
    ERR_ASSURED_REPLICATION_RESPONSE_UNEXPECTED_ELEMENT_TYPE("The provided control cannot be decoded as an assured replication response control because the value sequence included an element with an unexpected BER type of {0}."), 
    ERR_ASSURED_REPLICATION_SERVER_RESULT_CANNOT_DECODE("An error occurred while attempting to decode an assured replication server result:  {0}"), 
    ERR_ASSURED_REPLICATION_SERVER_RESULT_INVALID_RESULT_CODE("Unrecognized result code value {0} found in the replication server result."), 
    ERR_ASSURED_REPLICATION_SERVER_RESULT_NO_RESULT_CODE("The encoded assured replication server result did not include the required result code element."), 
    ERR_ASSURED_REPLICATION_SERVER_RESULT_NO_SERVER_ID("The encoded assured replication server result did not include the required replication server ID element."), 
    ERR_ASSURED_REPLICATION_SERVER_RESULT_UNEXPECTED_ELEMENT_TYPE("Unable to decode an assured replication server result because the element sequence included an element with an unexpected BER type of {0}."), 
    ERR_AUTH_FAILURE_REASON_CANNOT_DECODE("Unable to decode string ''{0}'' as an account usability error:  {1}"), 
    ERR_AUTH_FAILURE_REASON_NO_CODE("There was no ''code'' element containing the integer value for the error."), 
    ERR_AUTH_FAILURE_REASON_NO_NAME("There was no ''name'' element containing the name for the error."), 
    ERR_EXCLUDE_BRANCH_ERROR_PARSING_VALUE("An error occurred while attempting to decode the provided control as an exclude branch request control:  {0}"), 
    ERR_EXCLUDE_BRANCH_MISSING_VALUE("The provided control cannot be decoded as an exclude branch request control because it does not have a value."), 
    ERR_EXCLUDE_BRANCH_NO_BASE_DNS("The provided control cannot be decoded as an exclude branch request control because the value did not specify any base DNs to be excluded."), 
    ERR_EXCLUDE_BRANCH_VALUE_NOT_SEQUENCE("The provided control cannot be decoded as an exclude branch request control because the value cannot be parsed as an ASN.1 sequence:  {0}"), 
    ERR_EXTENDED_SCHEMA_INFO_REQUEST_HAS_VALUE("The provided control cannot be decoded as an extended schema info request control because it has a value."), 
    ERR_GENERATE_PASSWORD_REQUEST_HAS_VALUE("Unable to decode the provided control as a generate password request control because the provided control has a value."), 
    ERR_GENERATE_PASSWORD_RESPONSE_CANNOT_DECODE_VALUE("An error occurred while trying to decode the value of the provided control as the value of a generate password response control:  {0}"), 
    ERR_GENERATE_PASSWORD_RESPONSE_NO_VALUE("Unable to decode the provided control as a generate password response control because the provided control does not have a value."), 
    ERR_GER_REQUEST_CANNOT_DECODE("The provided control cannot be decoded as a get effective rights control because an error occurred while attempting to decode the target attributes:  {0}"), 
    ERR_GER_REQUEST_INVALID_ELEMENT_COUNT("The provided control cannot be decoded as a get effective rights control because the sequence had an invalid number of elements (expected 1 or 2, got {0,number,0})."), 
    ERR_GER_REQUEST_NO_VALUE("The provided control cannot be decoded as a get effective rights request control because it does not have a value."), 
    ERR_GER_REQUEST_VALUE_NOT_SEQUENCE("The provided control cannot be decoded as a get effective rights control because the value could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_GET_AUTHORIZATION_ENTRY_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as an authorization entry request control:  {0}"), 
    ERR_GET_AUTHORIZATION_ENTRY_REQUEST_INVALID_SEQUENCE_ELEMENT("Unable to decode the provided control as an authorization entry request control because the values sequence included an element with an invalid BER type {0}."), 
    ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_CANNOT_DECODE_VALUE("Unable to decode the provided control as an authorization entry response control:  {0}"), 
    ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_INVALID_ENTRY_TYPE("Unable to decode the provided control as an authorization entry response control because the control entry sequence had an element with an invalid BER type of {0}."), 
    ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_INVALID_VALUE_TYPE("Unable to decode the provided control as an authorization entry response control because the control value sequence had an element with an invalid BER type of {0}."), 
    ERR_GET_AUTHORIZATION_ENTRY_RESPONSE_NO_VALUE("Unable to decode the provided control as an authorization entry response control because it does not have a value."), 
    ERR_GET_BACKEND_SET_ID_REQUEST_HAS_VALUE("The provided control cannot be decoded as a get backend set ID request control because it has a value."), 
    ERR_GET_BACKEND_SET_ID_RESPONSE_CANNOT_DECODE("An unexpected problem was encountered while attempting to decode the provided value as a get backend set ID response control value:  {0}"), 
    ERR_GET_BACKEND_SET_ID_RESPONSE_MISSING_VALUE("The provided control cannot be decoded as a get backend set ID response control because it does not have a value."), 
    ERR_GET_PWP_STATE_ISSUES_REQUEST_HAS_VALUE("The provided control cannot be decoded as a get password policy state issues request control because it has a value."), 
    ERR_GET_PWP_STATE_ISSUES_RESPONSE_CANNOT_DECODE("The provided control cannot be decoded as a get password policy state issues response control:  {0}"), 
    ERR_GET_PWP_STATE_ISSUES_RESPONSE_NO_VALUE("The provided control cannot be decoded as a get password policy state issues response control because it does not have a value."), 
    ERR_GET_PWP_STATE_ISSUES_RESPONSE_UNEXPECTED_TYPE("The provided control cannot be decoded as a get password policy state issues response control because the value sequence includes an element with an unexpected type of {0}."), 
    ERR_GET_SERVER_ID_REQUEST_HAS_VALUE("The provided control cannot be decoded as a get server ID request control because it has a value."), 
    ERR_GET_SERVER_ID_RESPONSE_MISSING_VALUE("The provided control cannot be decoded as a get server ID response control because it does not have a value."), 
    ERR_GET_USER_RESOURCE_LIMITS_REQUEST_HAS_VALUE("The provided control cannot be decoded as a get user resource limits request control because it has a value."), 
    ERR_GET_USER_RESOURCE_LIMITS_RESPONSE_CANNOT_DECODE_VALUE("The provided control cannot be decoded as a get user resource limits control because an error was encountered while decoding its value:  {0}"), 
    ERR_GET_USER_RESOURCE_LIMITS_RESPONSE_MISSING_VALUE("The provided control cannot be decoded as a get user resource limits control because it does not have a value."), 
    ERR_HARD_DELETE_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as a hard delete request control because an error occurred while attempting to parse the value:  {0}"), 
    ERR_HARD_DELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE("Unable to decode the provided control as a hard delete request control because the value sequence had an unexpected element with type {0}."), 
    ERR_ICREQ_CANNOT_DECODE_DOWNSTREAM_REQUEST("Unable to decode the downstreamRequest element of the intermediate client request value:  {0}"), 
    ERR_ICREQ_CANNOT_DECODE_DOWNSTREAM_SECURE("Unable to decode the downstreamClientSecure element of the intermediate client request value:  {0}"), 
    ERR_ICREQ_CONTROL_NO_VALUE("Unable to decode the provided control as an intermediate client request control because it does not have a value."), 
    ERR_ICREQ_CONTROL_VALUE_NOT_SEQUENCE("Unable to decode the provided control as in intermediate client request control because the value cannot be decoded as an ASN.1 sequence:  {0}"), 
    ERR_ICREQ_INVALID_ELEMENT_TYPE("Unable to decode the intermediate client request value because the value sequence contained an element with invalid type {0}."), 
    ERR_ICRESP_CANNOT_DECODE_UPSTREAM_RESPONSE("Unable to decode the upstreamResponse element of the intermediate client response value:  {0}"), 
    ERR_ICRESP_CANNOT_DECODE_UPSTREAM_SECURE("Unable to decode the upstreamServerSecure element of the intermediate client response value:  {0}"), 
    ERR_ICRESP_CONTROL_NO_VALUE("Unable to decode the provided control as an intermediate client response control because it does not have a value."), 
    ERR_ICRESP_CONTROL_VALUE_NOT_SEQUENCE("Unable to decode the provided control as in intermediate client response control because the value cannot be decoded as an ASN.1 sequence:  {0}"), 
    ERR_ICRESP_INVALID_ELEMENT_TYPE("Unable to decode the intermediate client response value because the value sequence contained an element with invalid type {0}."), 
    ERR_IGNORENUM_REQUEST_HAS_VALUE("Unable to decode the provided control as an ignore NO-USER-MODIFICATION request control because the control had a value when none was expected."), 
    ERR_INT_TXN_REQUEST_ABORT_ON_FAILURE_NOT_BOOLEAN("Unable to decode the provided control as an interactive transaction specification request control because the abortOnFailure element of the value sequence could not be decoded as a Boolean:  {0}"), 
    ERR_INT_TXN_REQUEST_INVALID_ELEMENT_TYPE("Unable to decode the provided control as an interactive transaction specification request control because the value sequence contained an element with an invalid BER type of {0}."), 
    ERR_INT_TXN_REQUEST_NO_TXN_ID("Unable to decode the provided control as an interactive transaction specification request control because it did not include a transaction ID."), 
    ERR_INT_TXN_REQUEST_NO_VALUE("Unable to decode the provided control as an interactive transaction specification request control because it does not have a value."), 
    ERR_INT_TXN_REQUEST_VALUE_NOT_SEQUENCE("Unable to decode the provided control as an interactive transaction specification request control because the control value could not be decoded as a sequence:  {0}"), 
    ERR_INT_TXN_REQUEST_WRITE_LOCK_NOT_BOOLEAN("Unable to decode the provided control as an interactive transaction specification request control because the writeLock element of the value sequence could not be decoded as a Boolean:  {0}"), 
    ERR_INT_TXN_RESPONSE_BASE_DNS_NOT_SEQUENCE("Unable to decode the provided control as an interactive transaction specification response control because the baseDNs element could not be decoded as a sequence:  {0}"), 
    ERR_INT_TXN_RESPONSE_INVALID_ELEMENT_TYPE("Unable to decode the provided control as an interactive transaction specification response control because the value sequence contained an element with an invalid BER type of {0}."), 
    ERR_INT_TXN_RESPONSE_NO_TXN_VALID("Unable to decode the provided control as an interactive transaction specification response control because it did not include a transactionValid element."), 
    ERR_INT_TXN_RESPONSE_NO_VALUE("Unable to decode the provided control as an interactive transaction specification response control because it does not have a value."), 
    ERR_INT_TXN_RESPONSE_TXN_VALID_NOT_BOOLEAN("Unable to decode the provided control as an interactive transaction specification response control because the transactionValid element could not be decoded as a Boolean:  {0}"), 
    ERR_INT_TXN_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the provided control as an interactive transaction specification response control because the control value could not be decoded as a sequence:  {0}"), 
    ERR_JOINED_ENTRY_CANNOT_DECODE("Unable to decode an ASN.1 element as a joined entry:  {0}"), 
    ERR_JOIN_BASE_DECODE_INVALID_TYPE("The join base DN element could not be decoded because it has an unrecognized BER type of {0}."), 
    ERR_JOIN_REQUEST_CONTROL_NO_VALUE("The provided control cannot be decoded as a join request control because it does not have a value"), 
    ERR_JOIN_REQUEST_VALUE_CANNOT_DECODE("The join request value could not be decoded:  {0}"), 
    ERR_JOIN_REQUEST_VALUE_INVALID_ELEMENT_TYPE("The join request value sequence included an element with an invalid BER type of {0}."), 
    ERR_JOIN_RESULT_CANNOT_DECODE("Unable to decode a control as a join result control:  {0}"), 
    ERR_JOIN_RESULT_INVALID_ELEMENT_TYPE("The join result value sequence included an element with an invalid BER type of {0}."), 
    ERR_JOIN_RESULT_NO_VALUE("Unable to decode a join result control because it did not have a value."), 
    ERR_JOIN_RULE_CANNOT_DECODE("The join rule element could not be decoded:  {0}"), 
    ERR_JOIN_RULE_DECODE_INVALID_TYPE("The join rule element could not be decoded because it has an unrecognized BER type of {0}."), 
    ERR_MATCHING_ENTRY_COUNT_REQUEST_CANNOT_DECODE("An unexpected problem was encountered while attempting to decode the provided control as a matching entry count request control:  {0}"), 
    ERR_MATCHING_ENTRY_COUNT_REQUEST_INVALID_ELEMENT_TYPE("An error was encountered while attempting to decode the provided control as a matching entry count request control because the value sequence includes an element with an unrecognized BER type of ''{0}''."), 
    ERR_MATCHING_ENTRY_COUNT_REQUEST_INVALID_MAX("The matching entry count request control is invalid because it contains a negative value for the maximum number of candidate entries to examine."), 
    ERR_MATCHING_ENTRY_COUNT_REQUEST_MISSING_VALUE("The provided control cannot be decoded as a matching entry count request control because it does not have a value."), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_CANNOT_DECODE("An unexpected problem was encountered while attempting to decode the provided control as a matching entry count response control:  {0}"), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_INVALID_COUNT_TYPE("An error was encountered while attempting to decode a matching entry count response control value because it contained an invalid count type of {0}."), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_MISSING_VALUE("The provided control cannot be decoded as a matching entry count response control because it does not have a value."), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_NEGATIVE_EXACT_COUNT("An error was encountered while attempting to decode a matching entry count response control value because the exact count value is negative."), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_NON_POSITIVE_UPPER_BOUND("An error was encountered while attempting to decode a matching entry count response control value because the upper bound value is negative or zero."), 
    ERR_MATCHING_ENTRY_COUNT_RESPONSE_UNKNOWN_ELEMENT_TYPE("An error was encountered while attempting to decode a matching entry count response control because the value sequence contained an element with an unexpected BER type of {0}."), 
    ERR_NAME_WITH_ENTRY_UUID_REQUEST_HAS_VALUE("The provided control cannot be decoded as a name with entryUUID request control because it has a value."), 
    ERR_NOOP_REQUEST_HAS_VALUE("The provided control cannot be decoded as a no-op request control because it has a value."), 
    ERR_OP_PURPOSE_NO_VALUE("The provided control cannot be decoded as an operation purpose request control because it does not have a value."), 
    ERR_OP_PURPOSE_VALUE_NOT_SEQUENCE("The provided control cannot be decoded as an operation purpose request control because its value cannot be parsed as an ASN.1 sequence:  {0}"), 
    ERR_OP_PURPOSE_VALUE_SEQUENCE_EMPTY("The provided control cannot be decoded as an operation purpose request control because its value sequence did not contain any elements."), 
    ERR_OP_PURPOSE_VALUE_UNSUPPORTED_ELEMENT("The provided control cannot be decoded as an operation purpose request control because the value sequence contains an element with an unsupported BER type of ''{0}''."), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as an override search limits request control because an error occurred while parsing the request value:  {0}"), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_CONTROL_NO_PROPERTIES("Unable to decode the provided control as an override search limits request control because the control value did not include any properties."), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_DUPLICATE_PROPERTY_NAME("Unable to decode the provided control as an override search limits request control because the provided control contained multiple values for property ''{0}''."), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_EMPTY_PROPERTY_NAME("Unable to decode the provided control as an override search limits request control because the provided control contained a property with an empty name."), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_EMPTY_PROPERTY_VALUE("Unable to decode the provided control as an override search limits request control because the provided control contained property ''{0}''' with an empty value."), 
    ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_NO_VALUE("Unable to decode the provided control as an override search limits request control because the provided control does not have a value."), 
    ERR_PERMIT_UNINDEXED_SEARCH_REQUEST_HAS_VALUE("The provided control cannot be decoded as a permit unindexed search request control because it has a value."), 
    ERR_PURGE_PASSWORD_REQUEST_CONTROL_HAS_VALUE("The provided control cannot be decoded as a purge password request control because the provided control has a value."), 
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
    ERR_PW_REQ_VALIDATION_RESULT_CANNOT_DECODE("An error occurred while attempting to decode the provided ASN.1 element as a password quality requirement validation result:  {0}"), 
    ERR_PW_REQ_VALIDATION_RESULT_INVALID_ELEMENT_TYPE("Unable to decode the provided ASN.1 element as a password quality requirement validation result because the sequence included an element with an unrecognized BER type of {0}."), 
    ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_ERROR("An error occurred while attempting to decode the provided control as a password update behavior request control:  {0}"), 
    ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_NO_VALUE("Unable to decode the provided control as a password update behavior request control because the provided control does not have a value."), 
    ERR_PW_UPDATE_BEHAVIOR_REQ_DECODE_UNRECOGNIZED_ELEMENT_TYPE("Unable to decode the provided control as a password update behavior request control because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_PW_VALIDATION_REQUEST_HAS_VALUE("The provided control cannot be decoded as a password validation details request control because it has a value."), 
    ERR_PW_VALIDATION_RESPONSE_ERROR_PARSING_VALUE("Unable to decode the provided control information as a password validation details response control because an error was encountered while attempting to parse the value:  {0}"), 
    ERR_PW_VALIDATION_RESPONSE_INVALID_RESPONSE_TYPE("Unable to decode the provided control information as a password validation details response control because the value sequence includes an unrecognized response type of {0}."), 
    ERR_PW_VALIDATION_RESPONSE_NO_VALUE("Unable to decode the provided control information as a password validation details response control because there is no control value."), 
    ERR_REAL_ATTRS_ONLY_REQUEST_HAS_VALUE("The provided control cannot be decoded as a real attributes only request control because it has a value."), 
    ERR_REJECT_UNINDEXED_SEARCH_REQUEST_HAS_VALUE("The provided control cannot be decoded as a reject unindexed search request control because it has a value."), 
    ERR_REPLICATION_REPAIR_REQUEST_HAS_VALUE("The provided control cannot be decoded as a replication repair request control because it has a value."), 
    ERR_RETAIN_IDENTITY_REQUEST_HAS_VALUE("The provided control cannot be decoded as a retain identity request control because it has a value."), 
    ERR_RETIRE_PASSWORD_REQUEST_CONTROL_HAS_VALUE("The provided control cannot be decoded as a retire password request control because the provided control has a value."), 
    ERR_RETURN_CONFLICT_ENTRIES_REQUEST_HAS_VALUE("The provided control cannot be decoded as a return conflict entries request control because it has a value."), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_ABSOLUTE_SET_EMPTY("Unable to decode the provided control as a valid route to backend set request control because it included an absolute routing type but an empty collection of backend sets."), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_CANNOT_DECODE("An unexpected problem was encountered while attempting to decode the control value as a route to backend set request control value:  {0}"), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_HINT_FALLBACK_SET_EMPTY("Unable to decode the provided control as a valid route to backend set request control because it included a routing hint routing type but an empty collection of backend sets as the fallback set."), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_HINT_FIRST_SET_EMPTY("Unable to decode the provided control as a valid route to backend set request control because it included a routing hint routing type but an empty collection of backend sets as the first guess."), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_MISSING_VALUE("The provided control cannot be decoded as a route to backend set request control because it does not have a value."), 
    ERR_ROUTE_TO_BACKEND_SET_REQUEST_UNKNOWN_ROUTING_TYPE("Unable to decode the provided control as a route to backend set request control because the value sequence has an unrecognized routing type of {0}."), 
    ERR_ROUTE_TO_SERVER_REQUEST_ERROR_PARSING_VALUE("An error occurred while attempting to decode the provided control as a route to server request control:  {0}"), 
    ERR_ROUTE_TO_SERVER_REQUEST_INVALID_VALUE_TYPE("The provided control cannot be decoded as a route to server request control because the value sequence included an element with an unexpected type of {0}."), 
    ERR_ROUTE_TO_SERVER_REQUEST_MISSING_VALUE("The provided control cannot be decoded as a route to server request control because it does not have a value."), 
    ERR_ROUTE_TO_SERVER_REQUEST_VALUE_NOT_SEQUENCE("The provided control cannot be decoded as a route to server request control because the value cannot be parsed as an ASN.1 sequence:  {0}"), 
    ERR_SOFT_DELETED_ACCESS_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as a soft-deleted entry access request control because an error occurred while attempting to parse the value:  {0}"), 
    ERR_SOFT_DELETED_ACCESS_REQUEST_UNSUPPORTED_ELEMENT_TYPE("Unable to decode the provided control as a soft-deleted entry access request control because the value sequence had an unexpected element with type {0}."), 
    ERR_SOFT_DELETE_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as a soft delete request control because an error occurred while attempting to parse the value:  {0}"), 
    ERR_SOFT_DELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE("Unable to decode the provided control as a soft delete request control because the value sequence had an unexpected element with type {0}."), 
    ERR_SOFT_DELETE_RESPONSE_NO_VALUE("Unable to decode the provided control as a soft delete response control because it did not have a value."), 
    ERR_SOFT_DELETE_RESPONSE_VALUE_NOT_DN("Unable to decode the provided control as a soft delete response control because the value could not be parsed as a valid DN."), 
    ERR_SUPPRESS_OP_ATTR_UNRECOGNIZED_SUPPRESS_TYPE("Unable to decode the value of the provided control for use as a suppress operational attribute request control because it included an unrecognized suppress type value of {0}."), 
    ERR_SUPPRESS_OP_ATTR_UPDATE_REQUEST_CANNOT_DECODE("An error occurred while attempting to decode the value of the provided control as appropriate for a suppress operational attribute update request control:  {0}"), 
    ERR_SUPPRESS_OP_ATTR_UPDATE_REQUEST_MISSING_VALUE("The provided control cannot be decoded as a suppress operational attribute update request control because it does not have a value."), 
    ERR_SUPPRESS_REFINT_REQUEST_CONTROL_HAS_VALUE("The provided control cannot be decoded as a suppress referential integrity updates request control because the provided control has a value."), 
    ERR_TXN_REQUEST_CONTROL_NO_VALUE("The provided control cannot be decoded as a transaction specification request control because it does not have a value."), 
    ERR_TXN_SETTINGS_REQUEST_ERROR_DECODING_VALUE("The provided control cannot be decoded as a transaction settings request control because an error was encountered while attempting to decode the value:  {0}"), 
    ERR_TXN_SETTINGS_REQUEST_INVALID_BACKEND_LOCK_TIMEOUT("Unable to decode a transaction settings request control because it specified an invalid value of {0,number,0} for the backend lock timeout."), 
    ERR_TXN_SETTINGS_REQUEST_INVALID_MAX_TXN_LOCK_TIMEOUT("Unable to decode a transaction settings request control because it specified a value of {0,number,0} for the maximum transaction lock timeout, which is less than the specified minimum transaction lock timeout value of {1,number,0}."), 
    ERR_TXN_SETTINGS_REQUEST_INVALID_MIN_TXN_LOCK_TIMEOUT("Unable to decode a transaction settings request control because it specified an invalid value of {0,number,0} for the minimum transaction lock timeout."), 
    ERR_TXN_SETTINGS_REQUEST_INVALID_RETRY_ATTEMPTS("Unable to decode a transaction settings request control because it specified an invalid value of {0,number,0} for the number of retry attempts."), 
    ERR_TXN_SETTINGS_REQUEST_MISSING_VALUE("The provided control cannot be decoded as a transaction settings request control because it does not have a value."), 
    ERR_TXN_SETTINGS_REQUEST_UNKNOWN_DURABILITY("Unable to decode a transaction settings request control because it indicated an unrecognized commit durability value of {0,number,0}."), 
    ERR_TXN_SETTINGS_REQUEST_UNKNOWN_LOCK_BEHAVIOR("Unable to decode a transaction settings request control because it indicated an unrecognized backend lock behavior value of {0,number,0}."), 
    ERR_TXN_SETTINGS_REQUEST_UNRECOGNIZED_ELEMENT_TYPE("Unable to decode a transaction settings request control because the value sequence included an unrecognized element of type {0}."), 
    ERR_TXN_SETTINGS_RESPONSE_ERROR_DECODING_VALUE("The provided control cannot be decoded as a transaction settings response control because an error was encountered while attempting to decode the control value:  {0}"), 
    ERR_TXN_SETTINGS_RESPONSE_NO_VALUE("The provided control cannot be decoded as a transaction settings response control because it does not have a value."), 
    ERR_UNDELETE_REQUEST_CANNOT_DECODE_VALUE("Unable to decode the provided control as an undelete request control because an error occurred while attempting to parse the value:  {0}"), 
    ERR_UNDELETE_REQUEST_UNSUPPORTED_VALUE_ELEMENT_TYPE("Unable to decode the provided control as an undelete request control because the value sequence had an unexpected element with type {0}."), 
    ERR_UNIQUENESS_REQ_DECODE_ERROR_DECODING_VALUE("Unable to decode the provided control as a uniqueness request control because an error occurred while trying to decode the value sequence:  {0}"), 
    ERR_UNIQUENESS_REQ_DECODE_NO_VALUE("Unable to decode the provided control as a uniqueness request control because the control does not have a value."), 
    ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_ELEMENT_TYPE("Unable to decode the provided control as a uniqueness request control because the value sequence had an element with an unrecognized BER type of {0}."), 
    ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_MULTIPLE_ATTR_BEHAVIOR("Unable to decode the provided control as a uniqueness request control because the value sequence had unrecognized integer value {0,number,0} as the value for the multiple attribute behavior enumerated element."), 
    ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_POST_COMMIT_LEVEL("Unable to decode the provided control as a uniqueness request control because the value sequence unrecognized integer value {0,number,0} as the value for the post-commit validation level."), 
    ERR_UNIQUENESS_REQ_DECODE_UNKNOWN_PRE_COMMIT_LEVEL("Unable to decode the provided control as a uniqueness request control because the value sequence unrecognized integer value {0,number,0} as the value for the pre-commit validation level."), 
    ERR_UNIQUENESS_REQ_MISSING_ATTR_OR_FILTER("Unable to decode the provided control as a uniqueness request control because the value sequence did not contain either a set of attribute types or a filter.  At least one of those elements must be present."), 
    ERR_UNIQUENESS_REQ_MISSING_UNIQUENESS_ID("Unable to decode the provided control as a uniqueness request control because the value sequence did not include the required uniqueness ID element."), 
    ERR_UNIQUENESS_REQ_NO_ATTRS_OR_FILTER("A uniqueness request control cannot be created with both an empty set of attribute types and a null filter."), 
    ERR_UNIQUENESS_RES_DECODE_ERROR("Unable to decode the provided control as a uniqueness response control because an unexpected error occurred while trying to decode the value sequence:  {0}"), 
    ERR_UNIQUENESS_RES_DECODE_NO_UNIQUENESS_ID("Unable to decode the provided control as a uniqueness response control because the value sequence does not contain the required uniqueness ID element."), 
    ERR_UNIQUENESS_RES_DECODE_NO_VALUE("Unable to decode the provided control as a uniqueness response control because the control does not have a value."), 
    ERR_UNIQUENESS_RES_DECODE_UNKNOWN_ELEMENT_TYPE("Unable to decode the provided control as a uniqueness response control because the value sequence has an element with an unrecognized BER type of {0}."), 
    ERR_UNIQUENESS_RES_GET_ID_CONFLICT("The LDAP result includes multiple uniqueness response controls with uniqueness ID ''{0}''."), 
    ERR_UNSOLICITED_CANCEL_RESPONSE_HAS_VALUE("Unable to decode the provided control as an unsolicited cancel response control because it had a value but no value is allowed for this control."), 
    ERR_VIRTUAL_ATTRS_ONLY_REQUEST_HAS_VALUE("The provided control cannot be decoded as a virtual attributes only request control because it has a value."), 
    INFO_CONTROL_NAME_ACCOUNT_USABLE_REQUEST("Account Usable Request Control"), 
    INFO_CONTROL_NAME_ACCOUNT_USABLE_RESPONSE("Account Usable Response Control"), 
    INFO_CONTROL_NAME_ADMINISTRATIVE_OPERATION_REQUEST("Administrative Operation Request Control"), 
    INFO_CONTROL_NAME_ASSURED_REPLICATION_REQUEST("Assured Replication Request Control"), 
    INFO_CONTROL_NAME_ASSURED_REPLICATION_RESPONSE("Assured Replication Response Control"), 
    INFO_CONTROL_NAME_BATCHED_TXN_REQUEST("Batched Transaction Specification Request Control"), 
    INFO_CONTROL_NAME_EXCLUDE_BRANCH("Exclude Branch Request Control"), 
    INFO_CONTROL_NAME_EXTENDED_SCHEMA_INFO("Extended Schema Info Request Control"), 
    INFO_CONTROL_NAME_GENERATE_PASSWORD_REQUEST("Generate Password Request Control"), 
    INFO_CONTROL_NAME_GENERATE_PASSWORD_RESPONSE("Generate Password Response Control"), 
    INFO_CONTROL_NAME_GET_AUTHORIZATION_ENTRY_REQUEST("Get Authorization Entry Request Control"), 
    INFO_CONTROL_NAME_GET_AUTHORIZATION_ENTRY_RESPONSE("Get Authorization Entry Response Control"), 
    INFO_CONTROL_NAME_GET_BACKEND_SET_ID_REQUEST("Get Backend Set ID Request Control"), 
    INFO_CONTROL_NAME_GET_BACKEND_SET_ID_RESPONSE("Get Backend Set ID Response Control"), 
    INFO_CONTROL_NAME_GET_EFFECTIVE_RIGHTS_REQUEST("Get Effective Rights Request Control"), 
    INFO_CONTROL_NAME_GET_PWP_STATE_ISSUES_REQUEST("Get Password Policy State Issues Request Control"), 
    INFO_CONTROL_NAME_GET_PWP_STATE_ISSUES_RESPONSE("Get Password Policy State Issues Response Control"), 
    INFO_CONTROL_NAME_GET_SERVER_ID_REQUEST("Get Server ID Request Control"), 
    INFO_CONTROL_NAME_GET_SERVER_ID_RESPONSE("Get Server ID Response Control"), 
    INFO_CONTROL_NAME_GET_USER_RESOURCE_LIMITS_REQUEST("Get User Resource Limits Request Control"), 
    INFO_CONTROL_NAME_GET_USER_RESOURCE_LIMITS_RESPONSE("Get User Resource Limits Response Control"), 
    INFO_CONTROL_NAME_HARD_DELETE_REQUEST("Hard Delete Request Control"), 
    INFO_CONTROL_NAME_IGNORE_NO_USER_MODIFICATION_REQUEST("Ignore NO-USER-MODIFICATION Request Control"), 
    INFO_CONTROL_NAME_INTERACTIVE_TXN_REQUEST("Interactive Transaction Specification Request Control"), 
    INFO_CONTROL_NAME_INTERACTIVE_TXN_RESPONSE("Interactive Transaction Specification Response Control"), 
    INFO_CONTROL_NAME_INTERMEDIATE_CLIENT_REQUEST("Intermediate Client Request Control"), 
    INFO_CONTROL_NAME_INTERMEDIATE_CLIENT_RESPONSE("Intermediate Client Response Control"), 
    INFO_CONTROL_NAME_JOIN_REQUEST("Join Request Control"), 
    INFO_CONTROL_NAME_JOIN_RESULT("Join Result Control"), 
    INFO_CONTROL_NAME_MATCHING_ENTRY_COUNT_REQUEST("Matching Entry Count Request Control"), 
    INFO_CONTROL_NAME_MATCHING_ENTRY_COUNT_RESPONSE("Matching Entry Count Response Control"), 
    INFO_CONTROL_NAME_NOOP_REQUEST("No-Op Request Control"), 
    INFO_CONTROL_NAME_OP_PURPOSE("Operation Purpose Request Control"), 
    INFO_CONTROL_NAME_PERMIT_UNINDEXED_SEARCH_REQUEST("Permit Unindexed Search Request Control"), 
    INFO_CONTROL_NAME_PURGE_PASSWORD_REQUEST("Purge Password Request Control"), 
    INFO_CONTROL_NAME_PW_POLICY_REQUEST("Password Policy Request Control"), 
    INFO_CONTROL_NAME_PW_POLICY_RESPONSE("Password Policy Response Control"), 
    INFO_CONTROL_NAME_PW_VALIDATION_REQUEST("Password Validation Details Request Control"), 
    INFO_CONTROL_NAME_PW_VALIDATION_RESPONSE("Password Validation Details Response Control"), 
    INFO_CONTROL_NAME_REAL_ATTRS_ONLY_REQUEST("Real Attributes Only Request Control"), 
    INFO_CONTROL_NAME_REJECT_UNINDEXED_SEARCH_REQUEST("Reject Unindexed Search Request Control"), 
    INFO_CONTROL_NAME_REPLICATION_REPAIR_REQUEST("Replication Repair Request Control"), 
    INFO_CONTROL_NAME_RETAIN_IDENTITY_REQUEST("Retain Identity Request Control"), 
    INFO_CONTROL_NAME_RETIRE_PASSWORD_REQUEST("Retire Password Request Control"), 
    INFO_CONTROL_NAME_RETURN_CONFLICT_ENTRIES_REQUEST("Return Conflict Entries Request Control"), 
    INFO_CONTROL_NAME_ROUTE_TO_BACKEND_SET_REQUEST("Route To Backend Set Request Control"), 
    INFO_CONTROL_NAME_ROUTE_TO_SERVER_REQUEST("Route to Server Request Control"), 
    INFO_CONTROL_NAME_SOFT_DELETED_ACCESS_REQUEST("Soft-Deleted Entry Access Request Control"), 
    INFO_CONTROL_NAME_SOFT_DELETE_REQUEST("Soft Delete Request Control"), 
    INFO_CONTROL_NAME_SOFT_DELETE_RESPONSE("Soft Delete Response Control"), 
    INFO_CONTROL_NAME_SUPPRESS_OP_ATTR_UPDATE_REQUEST("Suppress Operational Attribute Update Request Control"), 
    INFO_CONTROL_NAME_SUPPRESS_REFINT_REQUEST("Suppress Referential Integrity Updates Request Control"), 
    INFO_CONTROL_NAME_TXN_SETTINGS_REQUEST("Transaction Settings Request Control"), 
    INFO_CONTROL_NAME_TXN_SETTINGS_RESPONSE("Transaction Settings Response Control"), 
    INFO_CONTROL_NAME_UNDELETE_REQUEST("Undelete Request Control"), 
    INFO_CONTROL_NAME_UNSOLICITED_CANCEL_RESPONSE("Unsolicited Cancel Response Control"), 
    INFO_CONTROL_NAME_VIRTUAL_ATTRS_ONLY_REQUEST("Virtual Attributes Only Request Control"), 
    INFO_CONTROL_NAME_WITH_ENTRY_UUID_REQUEST("Name with entryUUID Request Control"), 
    INFO_OVERRIDE_SEARCH_LIMITS_REQUEST_CONTROL_NAME("Override Search Limits Request Control"), 
    INFO_PW_UPDATE_BEHAVIOR_REQ_CONTROL_NAME("Password Update Behavior Request Control"), 
    INFO_UNIQUENESS_REQ_CONTROL_NAME("Uniqueness Request Control"), 
    INFO_UNIQUENESS_RES_CONTROL_NAME("Uniqueness Response Control");
    
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
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-unboundid-controls");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ControlMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ControlMessages, MessageFormat>(100);
    }
}
