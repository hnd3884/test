package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ExtOpMessages
{
    ERR_ALL_ATTRS_CHANGE_SELECTION_CRITERIA_DECODE_ERROR("An error was encountered while attempting to decode the all attributes get changelog batch change selection criteria element:  {0}"), 
    ERR_ANY_ATTRS_CHANGE_SELECTION_CRITERIA_DECODE_ERROR("An error was encountered while attempting to decode the any attributes get changelog batch change selection criteria element:  {0}"), 
    ERR_ASSURED_REPLICATION_EQ_POLL_CRITERIA_CANNOT_DECODE("An error occurred while attempting to decode an exact match assured replication poll criteria:  {0}"), 
    ERR_ASSURED_REPLICATION_GE_POLL_CRITERIA_CANNOT_DECODE("An error occurred while attempting to decode greater-or-equal assured replication poll criteria:  {0}"), 
    ERR_ASSURED_REPLICATION_POLL_CRITERIA_UNEXPECTED_TYPE("Unable to decode an assured replication poll criteria element because it has an unexpected BER type of {0}."), 
    ERR_ASSURED_REPLICATION_POLL_REQUEST_ERROR_DECODING_VALUE("An error occurred while attempting to decode the value of the provided extended request for use as an assured replication poll request:  {0}"), 
    ERR_ASSURED_REPLICATION_POLL_REQUEST_NO_VALUE("Unable to decode the provided extended request as an assured replication poll request because it does not have a value."), 
    ERR_BEGINNING_OF_CHANGELOG_STARTING_POINT_HAS_VALUE("Unable to decode an ASN.1 element as a beginning of changelog starting point because it has a nonzero-length value."), 
    ERR_CHANGELOG_ENTRY_IR_ERROR_PARSING_VALUE("An error occurred while attempting to parse the value of the changelog entry intermediate response:  {0}"), 
    ERR_CHANGELOG_ENTRY_IR_INVALID_VALUE_COUNT("Unable to decode the provided intermediate response as a changelog batch entry intermediate response because the value sequence had an unexpected number of elements (expected 4, got {0})."), 
    ERR_CHANGELOG_ENTRY_IR_NO_VALUE("Unable to decode the provided intermediate response as a changelog batch entry intermediate response because the provided response did not have a value."), 
    ERR_CHANGELOG_ENTRY_IR_VALUE_NOT_SEQUENCE("Unable to decode the provided intermediate response as a changelog batch entry intermediate response because the value could not be parsed as an ASN.1 sequence:  {0}"), 
    ERR_CHANGE_TIME_STARTING_POINT_MALFORMED_VALUE("Unable to decode an ASN.1 element as a change time staring point because the value could not be parsed as a generalized time string:  {0}"), 
    ERR_CLBATCH_CHANGE_SELECTION_CRITERIA_DECODE_INNER_FAILURE("Unable to decode the get changelog batch change selection criteria because the change selection criteria element value could not be decoded as an ASN.1 element:  {0}"), 
    ERR_CLBATCH_CHANGE_SELECTION_CRITERIA_UNKNOWN_TYPE("Unable to decode the get changelog batch change selection criteria element because it had an unknown BER type of {0}."), 
    ERR_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a clear missed notification changes alarm request because the request does not have a value."), 
    ERR_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a clear missed notification changes alarm request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_CONSUME_SINGLE_USE_TOKEN_REQUEST_CANNOT_DECODE("Unable to decode the provided extended request as a consume single-use token request:  {0}"), 
    ERR_CONSUME_SINGLE_USE_TOKEN_REQUEST_NO_VALUE("Unable to decode the provided extended request as a consume single-use token request because the request does not have a value."), 
    ERR_DELIVER_OTP_REQ_ERROR_PARSING_VALUE("Unable to decode the provided extended request as a deliver one-time password request because an error occurred while parsing the request value:  {0}"), 
    ERR_DELIVER_OTP_REQ_NO_AUTHN_ID("Unable to decode the provided extended request as a deliver one-time password request because the value sequence did not include an authentication ID."), 
    ERR_DELIVER_OTP_REQ_NO_PW("Unable to decode the provided extended request as a deliver one-time password request because the value sequence did not include a static password."), 
    ERR_DELIVER_OTP_REQ_NO_VALUE("Unable to decode the provided extended request as a deliver one-time password request because it does not have a value."), 
    ERR_DELIVER_OTP_REQ_UNEXPECTED_ELEMENT_TYPE("Unable to decode the provided extended request as a deliver one-time password request because the value sequence includes an element with an unexpected BER type of {0}."), 
    ERR_DELIVER_OTP_RES_ERROR_PARSING_VALUE("Unable to decode the provided extended result as a deliver one-time password result because an error occurred while parsing the result value:  {0}"), 
    ERR_DELIVER_OTP_RES_NO_MECH("Unable to decode the provided extended result as a deliver one-time password result because the value sequence did not include a delivery mechanism."), 
    ERR_DELIVER_OTP_RES_NO_RECIPIENT_DN("Unable to decode the provided extended result as a deliver one-time password result because the value sequence did not include a recipient DN."), 
    ERR_DELIVER_OTP_RES_UNEXPECTED_ELEMENT_TYPE("Unable to decode the provided extended result as a deliver one-time password result because the value sequence includes an element with an unexpected BER type of {0}."), 
    ERR_DELIVER_PW_RESET_TOKEN_REQUEST_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a deliver password reset token request because an error was encountered while trying to parse the value:  {0}"), 
    ERR_DELIVER_PW_RESET_TOKEN_REQUEST_NO_VALUE("Unable to decode the provided extended request as a deliver password reset token request because the provided request did not have a value."), 
    ERR_DELIVER_PW_RESET_TOKEN_REQUEST_UNEXPECTED_TYPE("Unable to decode the provided extended request as a deliver password reset token request because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_DELIVER_PW_RESET_TOKEN_RESULT_ERROR_DECODING_VALUE("Unable to decode the provided extended result as a deliver password reset token result because an error was encountered while trying to parse the value:  {0}"), 
    ERR_DELIVER_PW_RESET_TOKEN_RESULT_UNEXPECTED_TYPE("Unable to decode the provided extended result as a deliver password reset token result because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_CANNOT_DECODE("Unable to decode the provided extended request as a deliver single-use token request:  {0}"), 
    ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_NO_VALUE("Unable to decode the provided extended request as a deliver single-use token request because the request does not have a value."), 
    ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_UNKNOWN_ELEMENT("Unable to decode the provided extended request as a deliver single-use token request because the request value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_DELIVER_SINGLE_USE_TOKEN_RESULT_ERROR_DECODING_VALUE("Unable to decode the provided extended result as a deliver single-use token result because an error was encountered while trying to parse the value:  {0}"), 
    ERR_DELIVER_SINGLE_USE_TOKEN_RESULT_UNEXPECTED_TYPE("Unable to decode the provided extended result as a deliver single-use token result because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_DEL_NOTIFICATION_DEST_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a delete notification destination request because the request does not have a value."), 
    ERR_DEL_NOTIFICATION_DEST_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a delete notification destination request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_DEL_NOTIFICATION_SUB_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a delete notification subscription request because the request does not have a value."), 
    ERR_DEL_NOTIFICATION_SUB_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a delete notification subscription request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a deregister YubiKey OTP device request because an error was encountered while attempting to decode the value:  {0}"), 
    ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_NO_VALUE("Unable to decode the provided extended request as a deregister YubiKey OTP device request because the provided request does not have a value."), 
    ERR_DEREGISTER_YUBIKEY_OTP_REQUEST_UNRECOGNIZED_TYPE("Unable to decode the provided extended request as a deregister YubiKey OTP device request because the value sequence included an element with an unrecognized BER type of ''{0}''."), 
    ERR_END_ADMIN_SESSION_REQUEST_HAS_VALUE("The end administrative session extended request had a value, but none was expected."), 
    ERR_END_INT_TXN_REQUEST_CANNOT_DECODE("The provided extended request cannot be decoded as an end interactive transaction request:  {0}"), 
    ERR_END_INT_TXN_REQUEST_INVALID_TYPE("The provided extended request cannot be decoded as an end interactive transaction request because the value contains an element with an invalid BER type of {0}."), 
    ERR_END_INT_TXN_REQUEST_NO_TXN_ID("The provided extended request cannot be decoded as an end interactive transaction request because the value sequence did not include a transaction ID."), 
    ERR_END_INT_TXN_REQUEST_NO_VALUE("The provided extended request cannot be decoded as an end interactive transaction request because it does not have a value."), 
    ERR_END_OF_CHANGELOG_STARTING_POINT_HAS_VALUE("Unable to decode an ASN.1 element as an end of changelog starting point because it has a nonzero-length value."), 
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
    ERR_GENERATED_PASSWORD_DECODING_ERROR("An unexpected error occurred while attempting to decode a generated password returned by the server:  {0}"), 
    ERR_GENERATE_PASSWORD_REQUEST_DECODING_ERROR("An unexpected error occurred while attempting to decode the value of the provided extended request as a generate password request value:  {0}"), 
    ERR_GENERATE_PASSWORD_REQUEST_INVALID_NUM_ATTEMPTS("Unable to decode the provided extended request as a generate password request because the value included invalid value {0,number,0} for the number of validation attempts to make.  The value must be greater than or equal to zero."), 
    ERR_GENERATE_PASSWORD_REQUEST_INVALID_NUM_PASSWORDS("Unable to decode the provided extended request as a generate password request because the value included invalid value {0,number,0} for the number of passwords to generate.  The value must be greater than or equal to one."), 
    ERR_GENERATE_PASSWORD_REQUEST_UNSUPPORTED_SELECTION_TYPE("Unable to decode the provided extended request as a generate password request because the value included an unrecognized password policy selection type of {0}."), 
    ERR_GENERATE_PASSWORD_RESULT_DECODE_NO_PASSWORDS("Unable to decode the provided extended result as a generate password result because the result code indicated that the operation was processed successfully, but the response value did not include any generated passwords."), 
    ERR_GENERATE_PASSWORD_RESULT_DECODING_ERROR("An unexpected error occurred while trying to decode the value of the provided extended operation as a generate password value:  {0}"), 
    ERR_GENERATE_PASSWORD_RESULT_NON_SUCCESS_WITH_VALUE("Unable to decode the provided extended result as a generate password result because the result code indicated that the operation was not processed successfully, but the response included a value."), 
    ERR_GENERATE_PASSWORD_RESULT_SUCCESS_MISSING_VALUE("Unable to decode the provided extended result as a generate password result because the result code indicated that the operation was processed successfully, but the response did not have a value."), 
    ERR_GEN_TOTP_SECRET_REQUEST_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a generate TOTP shared secret request because an unexpected error was encountered while attempting to decode the value:  {0}"), 
    ERR_GEN_TOTP_SECRET_REQUEST_NEITHER_AUTHN_ID_NOR_PW("Unable to decode the provided extended request as a generate TOTP shared secret request because the provided request had neither an authentication ID nor a static password.  At least one of these elements must be present."), 
    ERR_GEN_TOTP_SECRET_REQUEST_NO_VALUE("Unable to decode the provided extended request as a generate TOTP shared secret request because the provided request does not have a value."), 
    ERR_GEN_TOTP_SECRET_REQUEST_UNRECOGNIZED_TYPE("Unable to decode the provided extended request as a generate TOTP shared secret request because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_GEN_TOTP_SECRET_RESULT_ERROR_DECODING_VALUE("Unable to decode the provided extended result as a generate TOTP shared secret result because an unexpected error was encountered while attempting to decode the value:  {0}"), 
    ERR_GET_BACKUP_COMPAT_REQUEST_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended request as a get backup compatibility descriptor request value:  {0}"), 
    ERR_GET_BACKUP_COMPAT_REQUEST_NO_VALUE("Unable to decode the provided extended request as a get backup compatibility descriptor request because the provided request does not have a value."), 
    ERR_GET_BACKUP_COMPAT_RESULT_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended result as a get backup compatibility descriptor result value:  {0}"), 
    ERR_GET_CHANGELOG_BATCH_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a get changelog batch request because an error occurred while trying to decode the value sequence:  {0}"), 
    ERR_GET_CHANGELOG_BATCH_REQ_IR_LISTENER_NOT_ALLOWED("The get changelog batch extended request is not allowed to have a general-purpose intermediate response listener.  If an intermediate response listener is required, then a ChangelogBatchEntryListener should be used."), 
    ERR_GET_CHANGELOG_BATCH_REQ_NO_VALUE("Unable to decode the provided extended request as a get changelog batch request because it does not have a value."), 
    ERR_GET_CHANGELOG_BATCH_REQ_TOO_FEW_ELEMENTS("Unable to decode the provided extended request as a get changelog batch request because the value sequence had too few elements to form a valid request."), 
    ERR_GET_CHANGELOG_BATCH_REQ_VALUE_NOT_SEQUENCE("Unable to decode the provided extended request as a get changelog batch request because the value could not be parsed as an ASN.1 sequence:  {0}"), 
    ERR_GET_CHANGELOG_BATCH_REQ_VALUE_UNRECOGNIZED_CT("Unable to decode the provided extended request as a get changelog batch request because the value sequence included an unrecognized change type of {0}."), 
    ERR_GET_CHANGELOG_BATCH_REQ_VALUE_UNRECOGNIZED_TYPE("Unable to decode the provided extended request as a get changelog batch request because the value sequence included an element with an unrecognized type of {0}."), 
    ERR_GET_CHANGELOG_BATCH_RES_ERROR_PARSING_VALUE("An error occurred while attempting to decode the provided extended result as a get changelog batch extended result:  {0}"), 
    ERR_GET_CHANGELOG_BATCH_RES_MISSING_MORE("Unable to decode the provided extended result as a get changelog batch extended result because the value sequence did not include an element indicating whether there may be more changes which are immediately available."), 
    ERR_GET_CHANGELOG_BATCH_RES_UNEXPECTED_VALUE_ELEMENT("Unable to decode the provided extended result as a get changelog batch extended result because the value sequence contained an ASN.1 element with an unexpected type of {0}."), 
    ERR_GET_CHANGELOG_BATCH_RES_VALUE_NOT_SEQUENCE("Unable to decode the provided extended result as a get changelog batch extended result because the value could not be parsed as an ASN.1 sequence:  {0}"), 
    ERR_GET_CONFIG_REQUEST_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended request as a get configuration request value:  {0}"), 
    ERR_GET_CONFIG_REQUEST_NO_ARCHIVED_FILE_NAME("The encoded request value does not include the required archived configuration file name element."), 
    ERR_GET_CONFIG_REQUEST_NO_CONFIG_TYPE("The encoded request value does not include the required configuration type element."), 
    ERR_GET_CONFIG_REQUEST_NO_VALUE("Unable to decode the provided extended request as a get configuration request because the provided request does not have a value."), 
    ERR_GET_CONFIG_REQUEST_UNEXPECTED_CONFIG_TYPE("Unable to decode the provided extended request as a get configuration request because the value sequence includes an element with an unexpected configuration type BER value of {0}."), 
    ERR_GET_CONFIG_RESULT_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended result as a get configuration result value:  {0}"), 
    ERR_GET_CONFIG_RESULT_INVALID_CONFIG_TYPE("Unable to decode the provided extended result as a get configuration result because the value sequence referenced an unrecognized configuration type value of {0}."), 
    ERR_GET_CONN_ID_REQUEST_HAS_VALUE("The provided extended cannot request be decoded as a get connection ID request because it has a value."), 
    ERR_GET_CONN_ID_RESPONSE_VALUE_NOT_INT("Unable to decode the provided extended result as a get connection ID result because the value could not be decoded as an integer."), 
    ERR_GET_PW_QUALITY_REQS_REQUEST_CANNOT_DECODE("An error was encountered while attempting to decode an extended request as a get password quality requirements request:  {0}"), 
    ERR_GET_PW_QUALITY_REQS_REQUEST_NO_VALUE("Unable to decoded the provided extended request as a get password quality request because the extended request does not have a value."), 
    ERR_GET_PW_QUALITY_REQS_REQUEST_UNKNOWN_TARGET_TYPE("Unable to decode the provided extended request as a get password quality request because the value sequence included an unrecognized target type of {0}."), 
    ERR_GET_PW_QUALITY_REQS_RESULT_CANNOT_DECODE("Unable to decode the provided extended result as a get password quality requirements result because an error occurred while attempting to decode the value  {0}"), 
    ERR_GET_SUBTREE_ACCESSIBILITY_REQUEST_HAS_VALUE("Unable to decode the provided extended request as a get subtree accessibility request because it has a value."), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_DECODE_ERROR("Unable to decode the provided extended result as a get subtree accessibility result:  {0}"), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_BASE("Unable to decode the provided extended result as a get subtree accessibility result because an accessibility restriction definition was missing the required subtree base DN."), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_STATE("Unable to decode the provided extended result as a get subtree accessibility result because an accessibility restriction definition was missing the required accessibility state."), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_MISSING_TIME("Unable to decode the provided extended result as a get subtree accessibility result because an accessibility restriction definition was missing the required effective time."), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_UNEXPECTED_STATE("Unable to decode the provided extended result as a get subtree accessibility result because a restriction element included an unrecognized accessibility state value of {0}."), 
    ERR_GET_SUBTREE_ACCESSIBILITY_RESULT_UNEXPECTED_TYPE("Unable to decode the provided extended result as a get subtree accessibility result because the value sequence contained an element with an unexpected BER type of {0}."), 
    ERR_GET_SUPPORTED_OTP_MECH_REQUEST_CANNOT_DECODE("Unable to decode the provided extended request as a get supported OTP delivery mechanisms request:  {0}"), 
    ERR_GET_SUPPORTED_OTP_MECH_REQUEST_NO_VALUE("Cannot decode the provided extended request as a get supported OTP delivery mechanisms request because the request does not have a value."), 
    ERR_GET_SUPPORTED_OTP_MECH_RESULT_CANNOT_DECODE("Unable to decode the provided extended result as a get supported OTP delivery mechanisms result:  {0}"), 
    ERR_GET_SUPPORTED_OTP_MECH_RESULT_UNKNOWN_ELEMENT("Unable to decode the provided extended result as a get supported OTP delivery mechanisms result because a value sequence included an element with unexpected BER type ''{0}''."), 
    ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_REQUEST_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended request as an identify backup compatibility problems request value:  {0}"), 
    ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_REQUEST_NO_VALUE("Unable to decode the provided extended request as an identify backup compatibility problems request because the provided request does not have a value."), 
    ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_RESULT_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended result as an identify backup compatibility problems result value:  {0}"), 
    ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_RESULT_UNEXPECTED_TYPE("Unable to decode the provided extended result as an identify backup compatibility problems result because the value sequence includes an element with an unexpected BER type of {0}."), 
    ERR_IGNORE_ATTRS_CHANGE_SELECTION_CRITERIA_DECODE_ERROR("An error was encountered while attempting to decode the ignore attributes get changelog batch change selection criteria element:  {0}"), 
    ERR_LIST_CONFIGS_REQUEST_HAS_VALUE("Unable to decode the provided extended request as a list configurations request because the provided request includes a value but none was expected."), 
    ERR_LIST_CONFIGS_RESULT_ERROR_PARSING_VALUE("An unexpected problem was encountered while trying to parse the value of the provided extended result as a list configurations result value:  {0}"), 
    ERR_LIST_CONFIGS_RESULT_NO_ACTIVE_CONFIG("The encoded result value does not include the required active configuration file name element."), 
    ERR_LIST_CONFIGS_RESULT_UNEXPECTED_ELEMENT_TYPE("Unable to decode the provided extended result as a list configurations result because the value sequence includes an element with an unexpected BER type of {0}."), 
    ERR_LIST_NOTIFICATION_SUBS_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a list notification subscriptions request because the request does not have a value."), 
    ERR_LIST_NOTIFICATION_SUBS_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a list notification subscriptions request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_LIST_NOTIFICATION_SUBS_RESULT_CANNOT_DECODE_VALUE("Unable to decode the provided extended result as a list notification subscriptions result because an error occurred while attempting to decode the result value:  {0}"), 
    ERR_MISSING_CHANGELOG_ENTRIES_IR_UNEXPECTED_VALUE_TYPE("Unable to decode the provided intermediate response as a missing changelog batch entries intermediate response because the value sequence included an element with an unexpected type of {0}."), 
    ERR_MISSING_CHANGELOG_ENTRIES_IR_VALUE_NOT_SEQUENCE("Unable to decode the provided intermediate response as a missing changelog batch entries intermediate response because the value could not be parsed as an ASN.1 sequence:  {0}"), 
    ERR_MULTI_UPDATE_REQUEST_CANNOT_DECODE_VALUE("An error occurred while attempting to decode a generic extended request as a multi-update request:  {0}"), 
    ERR_MULTI_UPDATE_REQUEST_INVALID_ERROR_BEHAVIOR("Unable to decode a generic extended request as a multi-update request because it has an invalid error behavior value of {0}."), 
    ERR_MULTI_UPDATE_REQUEST_INVALID_OP_TYPE("Unable to decode a generic extended request as a multi-update request because the set of operation requests included an invalid operation type of {0}."), 
    ERR_MULTI_UPDATE_REQUEST_INVALID_REQUEST_TYPE("Unable to create a multi-update extended request with an operation of type {0}."), 
    ERR_MULTI_UPDATE_REQUEST_NO_VALUE("Unable to decode a generic extended request as a multi-update request because the extended request did not have a value."), 
    ERR_MULTI_UPDATE_RESULT_CANNOT_DECODE_VALUE("Unable to decode the provided extended result as a multi-update extended result because an error occurred while attempting to decode the value:  {0}"), 
    ERR_MULTI_UPDATE_RESULT_DECODE_INVALID_OP_TYPE("Unable to decode the provided extended result as a multi-update extended result because the value included result information for unexpected operation type {0}."), 
    ERR_MULTI_UPDATE_RESULT_INVALID_CHANGES_APPLIED("Unable to decode the provided extended result as a multi-update extended result because it had an invalid changesApplied value of {0}."), 
    ERR_MULTI_UPDATE_RESULT_INVALID_OP_TYPE("Multi-update extended responses are not allowed to include results for operations of type {0}."), 
    ERR_NOT_DEST_CHANGE_SELECTION_CRITERIA_DECODE_ERROR("An error was encountered while attempting to decode the notification destination get changelog batch change selection criteria element:  {0}"), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_CANNOT_DECODE("Unable to decode string ''{0}'' as an account usability error:  {1}"), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_NO_CODE("There was no ''code'' element containing the integer value for the error."), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_ERROR_NO_NAME("There was no ''name'' element containing the name for the error."), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_CANNOT_DECODE("Unable to decode string ''{0}'' as an account usability notice:  {1}"), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_NO_CODE("There was no ''code'' element containing the integer value for the notice."), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_NOTICE_NO_NAME("There was no ''name'' element containing the name for the notice."), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_CANNOT_DECODE("Unable to decode string ''{0}'' as an account usability warning:  {1}"), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_NO_CODE("There was no ''code'' element containing the integer value for the warning."), 
    ERR_PWP_STATE_ACCOUNT_USABILITY_WARNING_NO_NAME("There was no ''name'' element containing the name for the warning."), 
    ERR_PWP_STATE_CANNOT_DECODE_VALUES("Unable to decode the provided ASN.1 element as a password policy state operation because an error occurred while decoding the set of values:  {0}"), 
    ERR_PWP_STATE_ELEMENT_NOT_SEQUENCE("Unable to decode the provided ASN.1 element as a password policy state operation because it could not be decoded as a sequence:  {0}"), 
    ERR_PWP_STATE_INVALID_BOOLEAN_VALUE_COUNT("The password policy state operation had an invalid number of values for a boolean (expected 1, got {0,number,0})."), 
    ERR_PWP_STATE_INVALID_ELEMENT_COUNT("Unable to decode the provided ASN.1 element as a password policy state operation because the value sequence had an invalid number of elements (expected 1 or 2, got {0,number,0})."), 
    ERR_PWP_STATE_NO_VALUES("The password policy state operation did not have any values."), 
    ERR_PWP_STATE_OP_TYPE_NOT_INTEGER("Unable to decode the provided ASN.1 element as a password policy state operation because the op type could not be decoded as an integer:  {0}"), 
    ERR_PWP_STATE_REQUEST_CANNOT_DECODE_OPS("Unable to decode the provided extended request as a password policy state request because an error occurred while attempting to decode the operations:  {0}"), 
    ERR_PWP_STATE_REQUEST_INVALID_ELEMENT_COUNT("Unable to decode the provided extended request as a password policy state request because the value sequence contained an invalid number of elements (expected 1 or 2, but found {0,number,0})."), 
    ERR_PWP_STATE_REQUEST_NO_VALUE("Unable to decode the provided extended request as a password policy state request because it does not have a value."), 
    ERR_PWP_STATE_REQUEST_VALUE_NOT_SEQUENCE("Unable to decode the provided extended request as a password policy state request because the value element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_PWP_STATE_RESPONSE_CANNOT_DECODE_OPS("Unable to decode the provided extended response as a password policy state response because an error occurred while attempting to decode the operations:  {0}"), 
    ERR_PWP_STATE_RESPONSE_INVALID_ELEMENT_COUNT("Unable to decode the provided extended response as a password policy state response because the value sequence contained an invalid number of elements (expected 1 or 2, but found {0,number,0})."), 
    ERR_PWP_STATE_RESPONSE_NO_SUCH_OPERATION("The specified password policy operation was not included in the response."), 
    ERR_PWP_STATE_RESPONSE_VALUE_NOT_SEQUENCE("Unable to decode the provided extended response as a password policy state response because the value element could not be decoded as an ASN.1 sequence:  {0}"), 
    ERR_PWP_STATE_VALUE_NOT_BOOLEAN("The value ''{0}'' could not be decoded as a boolean value."), 
    ERR_PW_QUALITY_REQ_DECODE_ERROR("An error occurred while attempting to decode the provided ASN.1 element as a password quality requirement:  {0}"), 
    ERR_PW_QUALITY_REQ_INVALID_CSV_ELEMENT_TYPE("Unable to decode the provided ASN.1 element as a password quality requirement object because the client-side validation info sequence included an element with an unexpected BER type of {0}."), 
    ERR_PW_QUALITY_REQ_INVALID_REQ_ELEMENT_TYPE("Unable to decode the provided ASN.1 element as a password quality requirement object because the requirement sequence included an element with an unexpected BER type of {0}."), 
    ERR_REGISTER_YUBIKEY_OTP_REQUEST_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a register YubiKey OTP device request because an error was encountered while attempting to decode the value:  {0}"), 
    ERR_REGISTER_YUBIKEY_OTP_REQUEST_MISSING_OTP("Unable to decode the provided extended request as a register YubiKey OTP device request because the value sequence was missing the required YubiKey-generated one-time password."), 
    ERR_REGISTER_YUBIKEY_OTP_REQUEST_NO_VALUE("Unable to decode the provided extended request as a register YubiKey OTP device request because the provided request does not have a value."), 
    ERR_REGISTER_YUBIKEY_OTP_REQUEST_UNRECOGNIZED_TYPE("Unable to decode the provided extended request as a register YubiKey OTP device request because the value sequence included an element with an unrecognized BER type of ''{0}''."), 
    ERR_REVOKE_TOTP_SECRET_REQUEST_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a revoke TOTP shared secret request because an unexpected error was encountered while attempting to decode the value:  {0}"), 
    ERR_REVOKE_TOTP_SECRET_REQUEST_NO_AUTHN_ID_OR_PW_OR_SECRET("Unable to decode the provided extended request as a revoke TOTP shared secret request because the value sequence did not include any of the authentication ID, static password, or TOTP shared secret elements.  At least one of these elements must be present in the request (and if only the authentication ID is provided, then the underlying connection must be authenticated as a user with the password-reset privilege)."), 
    ERR_REVOKE_TOTP_SECRET_REQUEST_NO_VALUE("Unable to decode the provided extended request as a revoke TOTP shared secret request because the provided request does not have a value."), 
    ERR_REVOKE_TOTP_SECRET_REQUEST_UNRECOGNIZED_TYPE("Unable to decode the provided extended request as a revoke TOTP shared secret request because the value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_SET_NOTIFICATION_DEST_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a set notification destination request because the request does not have a value."), 
    ERR_SET_NOTIFICATION_DEST_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a set notification destination request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_SET_NOTIFICATION_DEST_REQ_INVALID_CT("Unable to decode the provided extended request as a set notification destination request because the destination details change type element had an invalid integer value of {0}."), 
    ERR_SET_NOTIFICATION_DEST_REQ_INVALID_ELEMENT_TYPE("Unable to decode the provided extended request as a set notification destination request because the value sequence had an element with an unrecognized BER type of ''{0}''."), 
    ERR_SET_NOTIFICATION_SUB_REQ_DECODE_NO_VALUE("Unable to decode the provided extended request as a set notification subscription request because the request does not have a value."), 
    ERR_SET_NOTIFICATION_SUB_REQ_ERROR_DECODING_VALUE("Unable to decode the provided extended request as a set notification subscription request because an error occurred while attempting to decode the request value:  {0}"), 
    ERR_SET_SUBTREE_ACCESSIBILITY_CANNOT_DECODE("An error occurred while trying to decode the value of provided extended request for a set subtree accessibility request:  {0}"), 
    ERR_SET_SUBTREE_ACCESSIBILITY_INVALID_ACCESSIBILITY_STATE("Unrecognized subtree accessibility state value {0} contained in the set subtree accessibility extended request."), 
    ERR_SET_SUBTREE_ACCESSIBILITY_INVALID_ELEMENT_TYPE("Unable to decode the provided extended request as a set subtree accessibility request because the value sequence included an element with an unexpected type of {0}."), 
    ERR_SET_SUBTREE_ACCESSIBILITY_MISSING_BYPASS_DN("The set subtree accessibility request was missing a required bypass user DN element for accessibility state {0}."), 
    ERR_SET_SUBTREE_ACCESSIBILITY_NO_VALUE("Unable to decode the provided extended request as a set subtree accessibility request because there was no extended request value."), 
    ERR_SET_SUBTREE_ACCESSIBILITY_UNEXPECTED_BYPASS_DN("The set subtree accessibility request included a bypass user DN element when none was allowed for accessibility state {0}."), 
    ERR_START_ADMIN_SESSION_REQUEST_ERROR_DECODING_VALUE("An error occurred while attempting to decode the value of the start administrative session extended request:  {0}"), 
    ERR_START_ADMIN_SESSION_REQUEST_NO_VALUE("The start administrative session extended request did not include a value."), 
    ERR_START_ADMIN_SESSION_REQUEST_UNKNOWN_VALUE_ELEMENT_TYPE("The start administrative session extended request value sequence included an element with an unrecognized BER type of {0}."), 
    ERR_START_INT_TXN_REQUEST_INVALID_ELEMENT("The provided extended request cannot be decoded as a start interactive transaction request because the value sequence contained an element with an invalid BER type of {0}."), 
    ERR_START_INT_TXN_REQUEST_VALUE_NOT_SEQUENCE("The provided extended request cannot be decoded as a start interactive transaction request because the request value could not be parsed as a sequence:  {0}"), 
    ERR_START_INT_TXN_RESULT_BASE_DNS_NOT_SEQUENCE("The provided extended result cannot be decoded as a start interactive transaction result because the baseDNs element of the result value could not be parsed as a sequence:  {0}"), 
    ERR_START_INT_TXN_RESULT_INVALID_ELEMENT("The provided extended result cannot be decoded as a start interactive transaction result because the value sequence contained an element with an invalid BER type of {0}."), 
    ERR_START_INT_TXN_RESULT_NO_TXN_ID("The provided extended result cannot be decoded as a start interactive transaction result because the value did not include a transaction ID."), 
    ERR_START_INT_TXN_RESULT_VALUE_NOT_SEQUENCE("The provided extended result cannot be decoded as a start interactive transaction result because the result value could not be parsed as a sequence:  {0}"), 
    ERR_START_TXN_REQUEST_HAS_VALUE("The provided extended cannot request be decoded as a start transaction request because it has a value."), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_CANNOT_DECODE("An error occurred while attempting to decode a stream directory values extended request:  {0}"), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_INVALID_INCLUDE_DNS_TYPE("Unable to decode the provided extended request as a stream directory values request because the includeDNs sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_INVALID_SCOPE("Unable to decode the provided extended request as a stream directory values request because it included an invalid entry DN scope value of {0}."), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_INVALID_SEQUENCE_TYPE("Unable to decode the provided extended request as a stream directory values request because the request sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_NO_BASE_DN("Unable to decode the provided extended request as a stream directory values request because the value sequence did not include a base DN element."), 
    ERR_STREAM_DIRECTORY_VALUES_REQUEST_NO_VALUE("The stream directory values extended request cannot be parsed because it does not have a value."), 
    ERR_STREAM_DIRECTORY_VALUES_RESPONSE_CANNOT_DECODE("An error occurred while attempting to decode a stream directory values intermediate response:  {0}"), 
    ERR_STREAM_DIRECTORY_VALUES_RESPONSE_INVALID_RESULT("Unable to decode the provided intermediate response as a stream directory values intermediate response because the response sequence had an invalid value {0} for the result element."), 
    ERR_STREAM_DIRECTORY_VALUES_RESPONSE_INVALID_SEQUENCE_TYPE("Unable to decode the provided intermediate response as a stream directory values intermediate response because the response sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_DIRECTORY_VALUES_RESPONSE_NO_RESULT("Unable to decode the provided intermediate response as a stream directory values intermediate response because the response sequence did not include a result element."), 
    ERR_STREAM_DIRECTORY_VALUES_RESPONSE_NO_VALUE("The stream directory values intermediate response cannot be parsed because it does not have a value."), 
    ERR_STREAM_PROXY_VALUES_BACKEND_SET_CANNOT_DECODE("Unable to decode a backend set config element from the stream proxy values extended request:  {0}"), 
    ERR_STREAM_PROXY_VALUES_BACKEND_SET_VALUE_CANNOT_DECODE("Unable to decode a backend set value element from the stream proxy values intermediate response:  {0}"), 
    ERR_STREAM_PROXY_VALUES_REQUEST_CANNOT_DECODE("An error occurred while attempting to decode a stream proxy values extended request:  {0}"), 
    ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_INCLUDE_DNS_TYPE("Unable to decode the provided extended request as a stream proxy values request because the includeDNs sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_SCOPE("Unable to decode the provided extended request as a stream proxy values request because it included an invalid entry DN scope value of {0}."), 
    ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_SEQUENCE_TYPE("Unable to decode the provided extended request as a stream proxy values request because the request sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_PROXY_VALUES_REQUEST_NO_BASE_DN("Unable to decode the provided extended request as a stream proxy values request because the value sequence did not include a base DN element."), 
    ERR_STREAM_PROXY_VALUES_REQUEST_NO_VALUE("The stream proxy values extended request cannot be parsed because it does not have a value."), 
    ERR_STREAM_PROXY_VALUES_RESPONSE_CANNOT_DECODE("An error occurred while attempting to decode a stream proxy values intermediate response:  {0}"), 
    ERR_STREAM_PROXY_VALUES_RESPONSE_INVALID_RESULT("Unable to decode the provided intermediate response as a stream proxy values intermediate response because the response sequence had an invalid value {0} for the result element."), 
    ERR_STREAM_PROXY_VALUES_RESPONSE_INVALID_SEQUENCE_TYPE("Unable to decode the provided intermediate response as a stream proxy values intermediate response because the response sequence included element with an invalid BER type of {0}."), 
    ERR_STREAM_PROXY_VALUES_RESPONSE_NO_RESULT("Unable to decode the provided intermediate response as a stream proxy values intermediate response because the response sequence did not include a result element."), 
    ERR_STREAM_PROXY_VALUES_RESPONSE_NO_VALUE("The stream proxy values intermediate response cannot be parsed because it does not have a value."), 
    ERR_UNKNOWN_CHANGELOG_BATCH_STARTING_POINT_TYPE("Unable to decode an ASN.1 element as a changelog batch starting point because it has an unrecognized type of {0}."), 
    ERR_VALIDATE_TOTP_REQUEST_MALFORMED_VALUE("Unable to decode an extended request as a validate TOTP request because the request had a malformed value:  {0}"), 
    ERR_VALIDATE_TOTP_REQUEST_MISSING_VALUE("Unable to decode an extended request as a validate TOTP request because the provided request did not have a value."), 
    INFO_CHANGELOG_ENTRY_IR_NAME("Changelog Batch Entry Intermediate Response"), 
    INFO_DELIVER_OTP_REQ_NAME("Deliver One-Time Password Extended Request"), 
    INFO_DELIVER_OTP_RES_NAME("Deliver One-Time Password Extended Result"), 
    INFO_DEREGISTER_YUBIKEY_OTP_REQUEST_NAME("Deregister YubiKey OTP Device Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_ASSURED_REPLICATION_POLL("Assured Replication Poll Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM("Clear Missed Notification Changes Alarm Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_CONSUME_SINGLE_USE_TOKEN("Consume Single-Use Token Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_DELIVER_PW_RESET_TOKEN("Deliver Password Reset Token Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_DELIVER_SINGLE_USE_TOKEN("Deliver Single-Use Token Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_DEL_NOTIFICATION_DEST("Delete Notification Destination Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_DEL_NOTIFICATION_SUB("Delete Notification Subscription Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_END_ADMIN_SESSION("End Administrative Session Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_END_BATCHED_TXN("End Batched Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_END_INTERACTIVE_TXN("End Interactive Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_GET_BACKUP_COMPAT("Get Backup Compatibility Descriptor Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_GET_CONFIG("Get Configuration Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_GET_CONNECTION_ID("Get Connection ID Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_GET_PW_QUALITY_REQS("Get Password Quality Requirements Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_GET_SUBTREE_ACCESSIBILITY("Get Subtree Accessibility Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_IDENTIFY_BACKUP_COMPAT_PROBLEMS("Identify Backup Compatibility Problems Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_LIST_CONFIGS("List Configurations Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_LIST_NOTIFICATION_SUBS("List Notification Subscriptions Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_MULTI_UPDATE("Multi-Update Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_PW_POLICY_STATE("Password Policy State Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_SET_NOTIFICATION_DEST("Set Notification Destination Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_SET_NOTIFICATION_SUB("Set Notification Subscription Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_SET_SUBTREE_ACCESSIBILITY("Set Subtree Accessibility Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_START_ADMIN_SESSION("Start Administrative Session Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_START_BATCHED_TXN("Start Batched Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_START_INTERACTIVE_TXN("Start Interactive Transaction Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_STREAM_DIRECTORY_VALUES("Stream Directory Values Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_STREAM_PROXY_VALUES("Stream Proxy Values Extended Request"), 
    INFO_EXTENDED_REQUEST_NAME_VALIDATE_TOTP("Validate TOTP Password Extended Request"), 
    INFO_EXTENDED_RESULT_NAME_DELIVER_PW_RESET_TOKEN("Deliver Password Reset Token Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_DELIVER_SINGLE_USE_TOKEN("Deliver Single-Use Token Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_END_BATCHED_TXN("End Batched Transaction Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_GET_BACKUP_COMPAT("Get Backup Compatibility Descriptor Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_GET_CONFIG("Get Configuration Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_GET_CONNECTION_ID("Get Connection ID Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_GET_PW_QUALITY_REQS("Get Password Quality Requirements Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_GET_SUBTREE_ACCESSIBILITY("Get Subtree Accessibility Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_IDENTIFY_BACKUP_COMPAT_PROBLEMS("Identify Backup Compatibility Problems Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_INTERACTIVE_TXN_ABORTED("Interactive Transaction Aborted Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_LIST_CONFIGS("List Configurations Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_LIST_NOTIFICATION_SUBS("List Notification Subscriptions Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_MULTI_UPDATE("Multi-Update Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_PW_POLICY_STATE("Password Policy State Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_START_BATCHED_TXN("Start Batched Transaction Extended Result"), 
    INFO_EXTENDED_RESULT_NAME_START_INTERACTIVE_TXN("Start Interactive Transaction Extended Result"), 
    INFO_GENERATE_PASSWORD_REQUEST_NAME("Generate Password Extended Request"), 
    INFO_GENERATE_PASSWORD_RESULT_NAME("Generate Password Extended Result"), 
    INFO_GEN_TOTP_SECRET_REQUEST_NAME("Generate TOTP Shared Secret Extended Request"), 
    INFO_GEN_TOTP_SECRET_RESULT_NAME("Generate TOTP Shared Secret Extended Result"), 
    INFO_GET_CHANGELOG_BATCH_REQ_NAME("Get Changelog Batch Request"), 
    INFO_GET_CHANGELOG_BATCH_RES_NAME("Get Changelog Batch Result"), 
    INFO_GET_SUPPORTED_OTP_MECH_REQ_NAME("Get Supported OTP Delivery Mechanisms Extended Request"), 
    INFO_GET_SUPPORTED_OTP_MECH_RES_NAME("Get Supported OTP Delivery Mechanisms Extended Result"), 
    INFO_INTERMEDIATE_RESPONSE_NAME_STREAM_DIRECTORY_VALUES("Stream Directory Values Intermediate Response"), 
    INFO_INTERMEDIATE_RESPONSE_NAME_STREAM_PROXY_VALUES("Stream Proxy Values Intermediate Response"), 
    INFO_MISSING_CHANGELOG_ENTRIES_IR_NAME("Missing Changelog Batch Entries Intermediate Response"), 
    INFO_REGISTER_YUBIKEY_OTP_REQUEST_NAME("Register YubiKey OTP Device Extended Request"), 
    INFO_REVOKE_TOTP_SECRET_REQUEST_NAME("Revoke TOTP Shared Secret Extended Request");
    
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
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-unboundid-extop");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ExtOpMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ExtOpMessages, MessageFormat>(100);
    }
}
