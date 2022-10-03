package com.unboundid.ldap.sdk.unboundidds;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum UnboundIDDSMessages
{
    ERR_CHANGELOG_EXCEEDED_AFTER_VALUE_COUNT("Attribute ''{0}'' in entry ''{1}'' had more values ({2,number,0}) after the change was processed than are allowed to be included in a changelog entry."), 
    ERR_CHANGELOG_EXCEEDED_BEFORE_VALUE_COUNT("Attribute ''{0}'' in entry ''{1}'' had more values ({2,number,0}) before the change was processed than are allowed to be included in a changelog entry."), 
    ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MALFORMED_COUNT("The ds-changelog-attr-exceeded-max-values-count attribute contains value ''{0}'' with a malformed value for the ''{1}'' token."), 
    ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MALFORMED_TOKEN("The ds-changelog-attr-exceeded-max-values-count attribute contains value ''{0}'' with a malformed token ''{1}''."), 
    ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MISSING_TOKEN("The ds-changelog-attr-exceeded-max-values-count attribute contains value ''{0}'' that does not include the ''{1}'' token."), 
    ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_REPEATED_TOKEN("The ds-changelog-attr-exceeded-max-values-count attribute contains value ''{0}'' with multiple instances of the ''{1}'' token."), 
    ERR_CHANGELOG_EXCEEDED_VIRTUAL_AFTER_VALUE_COUNT("Attribute ''{0}'' in entry ''{1}'' had more virtual values ({2,number,0}) after the change was processed than are allowed to be included in a changelog entry."), 
    ERR_CHANGELOG_EXCEEDED_VIRTUAL_BEFORE_VALUE_COUNT("Attribute ''{0}'' in entry ''{1}'' had more virtual values ({2,number,0}) before the change was processed than are allowed to be included in a changelog entry."), 
    ERR_DELIVER_OTP_CANNOT_GET_CONNECTION("Unable to connect to the server:  {0}"), 
    ERR_DELIVER_OTP_CANNOT_READ_BIND_PW("Unable to read the bind password:  {0}"), 
    ERR_DELIVER_OTP_ERROR_PROCESSING_EXTOP("An error was encountered while attempting to send the deliver one-time password extended request or reading the corresponding response:  {0}"), 
    ERR_DELIVER_OTP_ERROR_RESULT("Unable to deliver the one-time password.  The server returned a result code of {0} and an error message of ''{1}''."), 
    ERR_DELIVER_OTP_ERROR_RESULT_NO_MESSAGE("Unable to deliver the one-time password.  The server returned a result code of {0} but did not provide any additional information about the reason for the failure."), 
    ERR_DELIVER_PW_RESET_TOKEN_CANNOT_GET_CONNECTION("Unable to connect to the server:  {0}"), 
    ERR_DELIVER_PW_RESET_TOKEN_ERROR_PROCESSING_EXTOP("An error was encountered while attempting to send the deliver password reset token extended request or reading the corresponding response:  {0}"), 
    ERR_DELIVER_PW_RESET_TOKEN_ERROR_RESULT("Unable to deliver the password reset token.  The server returned a result code of {0} and an error message of ''{1}''."), 
    ERR_DELIVER_PW_RESET_TOKEN_ERROR_RESULT_NO_MESSAGE("Unable to deliver the password reset token.  The server returned a result code of {0} but did not provide any additional information about the reason for the failure."), 
    ERR_DOTP_DECODE_ERROR("Unable to decode the provided SASL credentials for use with an UNBOUNDID-DELIVERED-OTP bind request because an unexpected error was encountered:  {0}"), 
    ERR_DOTP_DECODE_INVALID_ELEMENT_TYPE("Unable to decode the provided SASL credentials for use with an UNBOUNDID-DELIVERED-OTP bind request because the credentials sequence had an element with an unexpected BER type of {0}."), 
    ERR_DOTP_DECODE_MISSING_AUTHN_ID("Unable to decode the provided SASL credentials for use with an UNBOUNDID-DELIVERED-OTP bind request because the credentials did not include an authentication ID."), 
    ERR_DOTP_DECODE_MISSING_OTP("Unable to decode the provided SASL credentials for use with an UNBOUNDID-DELIVERED-OTP bind request because the credentials did not include a one-time password."), 
    ERR_EXTERNALLY_PROCESSED_AUTH_CANNOT_DECODE_CREDS("An error occurred while attempting to decode the provided SASL credentials to create an {0} bind request:  {1}"), 
    ERR_EXTERNALLY_PROCESSED_AUTH_NO_AUTH_ID("The encoded {0} credentials did not include the required authenticationID element."), 
    ERR_EXTERNALLY_PROCESSED_AUTH_NO_MECH("The encoded {0} credentials did not include the required externalMechanismName element."), 
    ERR_EXTERNALLY_PROCESSED_AUTH_NO_WAS_SUCCESSFUL("The encoded {0} credentials did not include the required externalAuthenticationWasSuccessful element."), 
    ERR_HOTP_ERROR_GENERATING_PW("An error occurred while attempting to generate an HMAC-based one-time password:  {0}"), 
    ERR_HOTP_INVALID_NUM_DIGITS("The number of digits ({0,number,0}) is invalid for use with an HMAC-based one-time password.  The number of digits must be greater than or equal to 6 and less than or equal to 8."), 
    ERR_LAUNCHER_CLASS_NOT_COMMAND_LINE_TOOL("Unable to create a command-line tool instance from class {0} because that class is not a subclass of {1}."), 
    ERR_LAUNCHER_ERROR_INVOKING_CONSTRUCTOR("Unable to create a command-line tool instance from class {0} because an error occurred while attempting to invoke its two-argument constructor with the provided streams for standard output and standard error:  {1}"), 
    ERR_LAUNCHER_TOOL_CLASS_MISSING_EXPECTED_CONSTRUCTOR("Unable to create a command-line tool instance from class {0} because that class does not offer a two-argument constructor in which the arguments are OutputStream objects to use for standard output and standard error, respectively."), 
    ERR_MOVE_ENTRY_CANNOT_COMMIT_SOURCE_TXN("An error occurred while attempting to commit the interactive transaction in the source server:  {0}"), 
    ERR_MOVE_ENTRY_CANNOT_COMMIT_TARGET_TXN("An error occurred while attempting to commit the interactive transaction in the target server:  {0}"), 
    ERR_MOVE_ENTRY_CANNOT_DECODE_DELETE_TXN_CONTROL("An error occurred while attempting to decode the interactive transaction specification response control in the delete result for entry ''{0}'':  {1}"), 
    ERR_MOVE_ENTRY_CANNOT_DECODE_SEARCH_TXN_CONTROL("An error occurred while attempting to decode the interactive transaction specification response control in the search result:  {0}"), 
    ERR_MOVE_ENTRY_CANNOT_START_SOURCE_TXN("An error occurred while attempting to start an interactive transaction in the source server:  {0}"), 
    ERR_MOVE_ENTRY_CANNOT_START_TARGET_TXN("An error occurred while attempting to start an interactive transaction in the source server:  {0}"), 
    ERR_MOVE_ENTRY_DELETE_TXN_NO_LONGER_VALID("The interactive transaction specification response control included in the delete result for entry ''{0}'' indicated that the transaction is no longer valid for performing operations in the source server."), 
    ERR_MOVE_ENTRY_EXISTS_IN_BOTH_SERVERS("The transaction used to add entry ''{0}'' to the target server was committed successfully, but the transaction attempting to delete that entry from the source server could not be committed.  As a result, the entry now exists in both the source and target servers and must be manually deleted from the source server in order to complete the move (or deleted from the target server to revert the move)."), 
    ERR_MOVE_ENTRY_SEARCH_TXN_NO_LONGER_VALID("The interactive transaction specification response control included in the search result indicated that that the transaction is no longer valid for performing operations in the source server."), 
    ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE("The attempt to abort the interactive transaction in the source server failed:  {0}"), 
    ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE_ADMIN_ACTION("As a result of the source transaction abort failure, you should manually ensure that entry ''{0}'' still exist in the source server."), 
    ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE("The attempt to abort the interactive transaction in the target server failed:  {0}"), 
    ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE_ADMIN_ACTION("As a result of the target transaction abort failure, you should manually ensure that entry ''{0}'' does not exist in the target server."), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_ADD_FAILURE("An error occurred while attempting to add entry ''{0}'' to the target server:  {1}"), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_CANNOT_PARSE_DN("Unable to parse search result entry DN ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_POST_ADD_FAILURE("The move subtree listener encountered an error during post-add processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_PRE_ADD_DN_ALTERED("The move subtree listener returned an entry with DN ''{0}'', which is different from the source entry DN of ''{1}''."), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_PRE_ADD_FAILURE("The move subtree listener encountered an error during pre-add processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_ACC_LISTENER_REFERENCE_RETURNED("A search reference was included in the set of results returned by the source server with referral URLs {0}.  Search references are not allowed in move subtree processing."), 
    ERR_MOVE_SUBTREE_ADMIN_ACTION("Some administrative action is required in order to either complete the move or revert the servers back to their original states:  {0}"), 
    ERR_MOVE_SUBTREE_BASE_DN_FILE_EMPTY("The specified base DN file ''{0}'' did not actually include any base DNs for subtrees to move."), 
    ERR_MOVE_SUBTREE_CANNOT_CONNECT_TO_SOURCE("An error occurred while attempting to connect or authenticate to the source directory server:  {0}"), 
    ERR_MOVE_SUBTREE_CANNOT_CONNECT_TO_TARGET("An error occurred while attempting to connect or authenticate to the target directory server:  {0}"), 
    ERR_MOVE_SUBTREE_CANNOT_GET_ACCESSIBILITY_STATE("An error occurred while attempting to get the accessibility state for subtree ''{0}'' from the {1} server:  {2}"), 
    ERR_MOVE_SUBTREE_CANNOT_IDENTIFY_CONNECTED_USER("Unable to identify the user authenticated on the {0} connection."), 
    ERR_MOVE_SUBTREE_CANNOT_PARSE_BASE_DN("Unable to parse base DN string ''{0}'' as a valid DN:  {1}"), 
    ERR_MOVE_SUBTREE_CANNOT_PARSE_RESTRICTION_BASE_DN("Unable to parse ''{0}'' as a valid DN in {1} server accessibility restriction ''{2}'':  {3}"), 
    ERR_MOVE_SUBTREE_CONTAINS_UNACCESSIBLE_TREE("Subtree ''{0}'' in the {1} server is contains subtree ''{2}'' that is already considered ''{3}''."), 
    ERR_MOVE_SUBTREE_DELETE_FAILURE("An error occurred while attempting to delete entry ''{0}'' from the source server:  {1}"), 
    ERR_MOVE_SUBTREE_ERROR_INVOKING_WHO_AM_I("An error occurred while invoking the 'Who Am I?' extended request in order to identify the authorization identity on the {0} connection:  {1}"), 
    ERR_MOVE_SUBTREE_ERROR_MESSAGE("The error(s) resulting from move subtree processing are:  {0}"), 
    ERR_MOVE_SUBTREE_ERROR_READING_BASE_DN_FILE("An error occurred while attempting to read the contents of specified base DN file ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_ERROR_SETTING_ACCESSIBILITY("An error occurred while attempting to apply accessibility state ''{0}'' to subtree ''{1}'' in the {2} server:  {3}"), 
    ERR_MOVE_SUBTREE_NOT_ACCESSIBLE("Subtree ''{0}'' in the {1} server has an accessibility state of ''{2}'', which may indicate that a previous move-subtree operation failed, or that some other administrative action is in progress for that server."), 
    ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED("Subtree ''{0}'' is not fully accessible on source server {1}:{2,number,0} and/or target server {3}:{4,number,0} as required by the move-subtree tool.  {5}  This may be the result of a previous move-subtree attempt that failed or was interrupted before processing could complete, or it may indicate some other administrative action is already in progress on one or both servers.  Contact support for assistance if you have any questions or wish to conduct a further investigation."), 
    ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_ADDS("Subtree ''{0}'' is currently read-only in source server {1}:{2,number,0} and hidden in target server {3}:{4,number,0}.  This suggests that a previous move-subtree invocation failed or was interrupted during the process of copying entries from the source server into the target server."), 
    ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_ADDS_ADMIN_MSG("In order to restore the servers to their former states, delete all entries from that subtree in the target server, and make that subtree accessible on both servers (e.g., using the subtree-accessibility tool) before attempting to use move-subtree again on that subtree.  Contact support for assistance if you have any questions or wish to conduct a further investigation."), 
    ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_DELETES("Subtree ''{0}'' is currently hidden in source server {1}:{2,number,0} and accessible in target server {3}:{4,number,0}.  This suggests that a previous move-subtree invocation failed or was interrupted during the process of deleting entries from the source server after they had been copied to the target server."), 
    ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_DELETES_ADMIN_MSG("In order to complete the move, manually delete all entries from the subtree on the source server and make that subtree accessible (e.g., using the subtree-accessibility tool) on the source server.  Contact support for assistance if you have any questions or wish to conduct a further investigation."), 
    ERR_MOVE_SUBTREE_POST_DELETE_FAILURE("The move subtree tool encountered an error during post-delete processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_PRE_DELETE_FAILURE("The move subtree tool encountered an error during pre-delete processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_RESULT_UNSUCCESSFUL("Move subtree processing is complete, but one or more errors were encountered in the process."), 
    ERR_MOVE_SUBTREE_SAME_SOURCE_AND_TARGET_SERVERS("The move-subtree tool cannot be used if the source and target servers are the same."), 
    ERR_MOVE_SUBTREE_SEARCH_FAILED("An error occurred while performing a search in the source server to find entries below ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_SOURCE_LEFT_INACCESSIBLE("The source server was left with an accessibility state of ''{0}'' for subtree ''{1}''.  The accessibility state should be manually made re-accessible after any necessary cleanup work has been done."), 
    ERR_MOVE_SUBTREE_SOURCE_NOT_DELETED_ADMIN_ACTION("One or more entries in subtree ''{0}'' could not be deleted from the source server.  The contents of that subtree must be manually deleted to complete the move."), 
    ERR_MOVE_SUBTREE_TARGET_LEFT_INACCESSIBLE("The target server was left with an accessibility state of ''{0}'' for subtree ''{1}''.  The accessibility state should be manually made re-accessible after any necessary cleanup work has been done."), 
    ERR_MOVE_SUBTREE_TARGET_NOT_DELETED_ADMIN_ACTION("One or more entries in subtree ''{0}'' on the target server but could not be removed after a subsequent failed add attempt.  The contents of that subtree must be manually deleted to complete the cleanup process."), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_ADD_FAILURE("An error occurred while attempting to add entry ''{0}'' to the target server:  {1}"), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_CANNOT_DECODE_TXN_CONTROL("An error occurred while attempting to decode the interactive transaction specification response control in the add result for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_CANNOT_PARSE_DN("Unable to parse search result entry DN ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_POST_ADD_FAILURE("The move subtree listener encountered an error during post-add processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_PRE_ADD_DN_ALTERED("The move subtree listener returned an entry with DN ''{0}'', which is different from the source entry DN of ''{1}''."), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_PRE_ADD_FAILURE("The move subtree listener encountered an error during pre-add processing for entry ''{0}'':  {1}"), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_REFERENCE_RETURNED("A search reference was included in the set of results returned by the source server with referral URLs {0}.  Search references are not allowed in move subtree processing."), 
    ERR_MOVE_SUBTREE_TXN_LISTENER_TXN_NO_LONGER_VALID("The interactive transaction specification response control included in the add result for entry ''{0}'' indicated that the transaction is no longer valid for performing operations in the target server."), 
    ERR_MOVE_SUBTREE_TXN_SIZE_LIMIT_EXCEEDS_MAXIMUM("The requested size limit of {0} entries is not allowed when performing the move using interactive transactions.  A maximum size limit of {1} is required when using interactive transactions."), 
    ERR_MOVE_SUBTREE_UNSUPPORTED_ACCESSIBILITY_STATE("Undefined accessibility state ''{0}'' cannot be applied to subtree ''{1}'' in the {2} server."), 
    ERR_MOVE_SUBTREE_WITHIN_UNACCESSIBLE_TREE("Subtree ''{0}'' in the {1} server is within subtree ''{2}'' that is already considered ''{3}''."), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_CONNECT("An error occurred while attempting to establish a connection to the server:  {0}"), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_GET_AUTHZID("The tool was able to successfully authenticate to the server, but was unable to obtain the authorization identity response control from the bind response as a means of identifying the target user.  You will need to provide the {0} argument to identify the target user."), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_CANNOT_READ_PW("An error occurred while trying to read the static password:  {0}"), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_FAILED("An error occurred while attempting to deregister one or more YubiKey OTP devices for user {0}:  {1}"), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_NO_OTP_TO_REGISTER("When registering a YubiKey OTP device with the server, the {0} argument must be used to provide a one-time password generated by the device to be registered."), 
    ERR_REGISTER_YUBIKEY_OTP_DEVICE_REGISTER_FAILED("An error occurred while attempting to register the specified YubiKey OTP device for user {0}:  {1}"), 
    ERR_SASL_MISSING_REQUIRED_OPTION("SASL option ''{0}'' is required with the {1} mechanism but was not provided."), 
    ERR_SASL_OPTION_MECH_DOESNT_ACCEPT_PASSWORD("A password was provided, but SASL mechanism {0} does not accept a password."), 
    ERR_SINGLE_USE_TOTP_DECODE_ERROR("An error occurred while attempting to decode a TOTP bind request:  {0}"), 
    ERR_SINGLE_USE_TOTP_DECODE_INVALID_ELEMENT_TYPE("The credentials sequence included with an invalid BER type of {0}."), 
    ERR_SINGLE_USE_TOTP_DECODE_MISSING_AUTHN_ID("The encoded credentials did not include an authentication ID element."), 
    ERR_SINGLE_USE_TOTP_DECODE_MISSING_TOTP_PW("The encoded credentials did not include a TOTP password element."), 
    ERR_SOFT_DELETED_ENTRY_MISSING_FROM_DN("Unable to parse entry ''{0}'' as a soft-deleted entry because it does not include attribute ds-soft-delete-from-dn to specify the original DN of the entry at the time of the soft delete operation."), 
    ERR_SOFT_DELETED_ENTRY_MISSING_OC("Unable to parse entry ''{0}'' as a soft-deleted entry because it does not include the ds-soft-delete-entry object class."), 
    ERR_TOTP_ERROR_GENERATING_PW("An error occurred while attempting to generate a time-based one-time password:  {0}"), 
    ERR_TOTP_INVALID_NUM_DIGITS("The number of digits ({0,number,0}) is invalid for use with a time-based one-time password.  The number of digits must be greater than or equal to 6 and less than or equal to 8."), 
    ERR_YUBIKEY_OTP_DECODE_ERROR("An error occurred while attempting to decode the {0} bind request:  {1}"), 
    ERR_YUBIKEY_OTP_DECODE_NO_AUTH_ID("Unable to decode an {0} bind request because the SASL credentials did not include an authentication ID."), 
    ERR_YUBIKEY_OTP_DECODE_UNRECOGNIZED_CRED_ELEMENT("Unable to decode an {0} bind request because the SASL credentials sequence included an element with an unrecognized BER type of {1}."), 
    ERR_YUBIKEY_OTP_NO_OTP("Unable to decode an {0} bind request because the SASL credentials did not include a YubiKey OTP."), 
    INFO_DELIVER_OTP_DESCRIPTION_BIND_DN("The DN for the user to whom the one-time password should be delivered.  Either the bind DN or username must be provided."), 
    INFO_DELIVER_OTP_DESCRIPTION_BIND_PW("The static password for the user to whom the one-time password should be delivered."), 
    INFO_DELIVER_OTP_DESCRIPTION_BIND_PW_FILE("The path to a file containing the static password for the user to whom the one-time password should be delivered."), 
    INFO_DELIVER_OTP_DESCRIPTION_BIND_PW_PROMPT("Indicates that the tool should interactively prompt the user for the bind password."), 
    INFO_DELIVER_OTP_DESCRIPTION_COMPACT_AFTER("The text to include immediately after the one-time password in the message delivered to the end user via a mechanism that imposes a significant constraint on message size."), 
    INFO_DELIVER_OTP_DESCRIPTION_COMPACT_BEFORE("The text to include immediately before the one-time password in the message delivered to the end user via a mechanism that imposes a significant constraint on message size."), 
    INFO_DELIVER_OTP_DESCRIPTION_FULL_AFTER("The text to include immediately after the one-time password in the message delivered to the end user via a mechanism that does not impose a significant constraint on message size."), 
    INFO_DELIVER_OTP_DESCRIPTION_FULL_BEFORE("The text to include immediately before the one-time password in the message delivered to the end user via a mechanism that does not impose a significant constraint on message size."), 
    INFO_DELIVER_OTP_DESCRIPTION_MECH("The name of the mechanism that should be used to deliver the one-time password to the user.  If multiple values are specified (using multiple instances of this argument) then they will be tried in the order in which they are given until one of them is able to deliver the one-time password.  If this is not specified, then the server configuration will dictate which delivery mechanisms should be attempted."), 
    INFO_DELIVER_OTP_DESCRIPTION_SUBJECT("The subject to use for the message containing the one-time password.  This will be ignored for delivery mechanisms that do not require a subject."), 
    INFO_DELIVER_OTP_DESCRIPTION_USERNAME("The username for the user to whom the one-time password should be delivered.  Either the bind DN or username must be provided."), 
    INFO_DELIVER_OTP_ENTER_PW("Enter the static password for the user:"), 
    INFO_DELIVER_OTP_EXAMPLE_1("Generate and deliver a one-time password to the user with DN 'uid=test.user,ou=People,dc=example,dc=com' via the default mechanisms configured in the server."), 
    INFO_DELIVER_OTP_EXAMPLE_2("Generate and deliver a one-time password to the user with username 'test.user' via SMS if possible, or e-mail as a second choice."), 
    INFO_DELIVER_OTP_GROUP_DELIVERY_MECH("Delivery Mechanism Arguments"), 
    INFO_DELIVER_OTP_GROUP_ID_AND_AUTH("Identification and Authentication Arguments"), 
    INFO_DELIVER_OTP_PLACEHOLDER_COMPACT_AFTER("{text}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_COMPACT_BEFORE("{text}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_DN("{dn}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_FULL_AFTER("{text}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_FULL_BEFORE("{text}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_NAME("{name}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_PASSWORD("{password}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_PATH("{path}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_SUBJECT("{subject}"), 
    INFO_DELIVER_OTP_PLACEHOLDER_USERNAME("{username}"), 
    INFO_DELIVER_OTP_SUCCESS_MESSAGE("Additional delivery message:  {0}"), 
    INFO_DELIVER_OTP_SUCCESS_RESULT_WITHOUT_ID("Successfully delivered a one-time password via mechanism ''{0}''"), 
    INFO_DELIVER_OTP_SUCCESS_RESULT_WITH_ID("Successfully delivered a one-time password via mechanism ''{0}'' to ''{1}''"), 
    INFO_DELIVER_OTP_TOOL_DESCRIPTION("Generate and deliver a one-time password to a user through some out-of-band mechanism.  That password can then be used to authenticate via the UNBOUNDID-DELIVERED-OTP SASL mechanism."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_COMPACT_AFTER("The text to include immediately after the password reset token in the message delivered to the end user via a mechanism that imposes a significant constraint on message size."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_COMPACT_BEFORE("The text to include immediately before the password reset token in the message delivered to the end user via a mechanism that imposes a significant constraint on message size."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_FULL_AFTER("The text to include immediately after the password reset token in the message delivered to the end user via a mechanism that does not impose a significant constraint on message size."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_FULL_BEFORE("The text to include immediately before the password reset token in the message delivered to the end user via a mechanism that does not impose a significant constraint on message size."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_MECH("The name of the mechanism that should be used to deliver the password reset token to the user.  If multiple values are specified (using multiple instances of this argument) then they will bbe tried in the order in which they are given until one of them is able to deliver the token.  If this is not specified, then the server configuration will dictate which delivery mechanisms should be attempted."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_SUBJECT("The subject to use for the message containing the password reset token.  This will be ignored for delivery mechanisms that do not require a subject."), 
    INFO_DELIVER_PW_RESET_TOKEN_DESCRIPTION_USER_DN("The DN of the user to whom the generated password reset token should be delivered."), 
    INFO_DELIVER_PW_RESET_TOKEN_EXAMPLE("Generate and deliver a password reset token to the user with DN 'uid=test.user,ou=People,dc=example,dc=com' via SMS if possible, or e-mail as a second choice."), 
    INFO_DELIVER_PW_RESET_TOKEN_GROUP_DELIVERY_MECH("Delivery Mechanism Arguments"), 
    INFO_DELIVER_PW_RESET_TOKEN_GROUP_ID("Identification Arguments"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_COMPACT_AFTER("{text}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_COMPACT_BEFORE("{text}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_DN("{dn}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_FULL_AFTER("{text}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_FULL_BEFORE("{text}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_NAME("{name}"), 
    INFO_DELIVER_PW_RESET_TOKEN_PLACEHOLDER_SUBJECT("{subject}"), 
    INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_MESSAGE("Additional delivery message:  {0}"), 
    INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_RESULT_WITHOUT_ID("Successfully delivered a password reset token via mechanism ''{0}''."), 
    INFO_DELIVER_PW_RESET_TOKEN_SUCCESS_RESULT_WITH_ID("Successfully delivered a password reset token via mechanism ''{0}'' to ''{1}''."), 
    INFO_DELIVER_PW_RESET_TOKEN_TOOL_DESCRIPTION("Generate and deliver a single-use token to a user through some out-of-band mechanism.  The user can provide that token to the password modify extended request in lieu of the user's current password in order to select a new password."), 
    INFO_MOVE_ENTRY_SOURCE_ABORT_SUCCEEDED("Successfully aborted the interactive transaction in the source server.  No changes should have been made to the data in the source server."), 
    INFO_MOVE_ENTRY_TARGET_ABORT_SUCCEEDED("Successfully aborted the interactive transaction in the target server.  No changes should have been made to the data in the target server."), 
    INFO_MOVE_SUBTREE_ADD_SUCCESSFUL("Added entry ''{0}'' to target server."), 
    INFO_MOVE_SUBTREE_ARG_BASE_DN_DESCRIPTION("The base DN of the subtree to move."), 
    INFO_MOVE_SUBTREE_ARG_BASE_DN_FILE_DESCRIPTION("The path to a file containing the base DNs of the subtrees to move.  Each subtree base DN should be provided on a separate line."), 
    INFO_MOVE_SUBTREE_ARG_BASE_DN_FILE_PLACEHOLDER("{path}"), 
    INFO_MOVE_SUBTREE_ARG_BASE_DN_PLACEHOLDER("{dn}"), 
    INFO_MOVE_SUBTREE_ARG_PURPOSE_DESCRIPTION("The reason that the subtree is to be moved from the source server to the target server."), 
    INFO_MOVE_SUBTREE_ARG_PURPOSE_PLACEHOLDER("{purpose}"), 
    INFO_MOVE_SUBTREE_ARG_SIZE_LIMIT_DESCRIPTION("The maximum number of entries to allow for the move."), 
    INFO_MOVE_SUBTREE_ARG_SIZE_LIMIT_PLACEHOLDER("{num}"), 
    INFO_MOVE_SUBTREE_ARG_VERBOSE_DESCRIPTION("Indicates that the tool should operate in verbose mode in which it will output detailed information as entries are added to the target server and removed from the source server."), 
    INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE("source"), 
    INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET("target"), 
    INFO_MOVE_SUBTREE_DELETE_SUCCESSFUL("Deleted entry ''{0}'' from source server."), 
    INFO_MOVE_SUBTREE_EXAMPLE_DESCRIPTION("Migrate all entries at or below 'cn=small subtree,dc=example,dc=com' from source server 'ds1.example.com' to target server 'ds2.example.com'."), 
    INFO_MOVE_SUBTREE_RESULT_SUCCESSFUL("Successfully moved all {0} entries in subtree ''{1}'' from the source server to the target server."), 
    INFO_MOVE_SUBTREE_TOOL_DESCRIPTION("Move all entries in a specified subtree from one server to another."), 
    INFO_MOVE_SUBTREE_UNSOLICITED_NOTIFICATION("Received an unsolicited notification of type ''{0}'' from {1} server with result code {2} and message ''{3}''."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_SUCCESS_ALL("Successfully deregistered all YubiKey OTP devices that had been registered for user {0}."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DEREGISTER_SUCCESS_ONE("Successfully deregistered the specified YubiKey OTP device that had been registered for user {0}."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_AUTHID("An authentication ID that identifies the user for whom the device is to be registered or deregistered.  If this is provided, then it should either be in the form 'dn:' followed by the DN of the target user's entry, or in the form 'u:' followed by the username for the target user.  If this is not provided, then it will target the user as whom the tool is authenticated."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_DEREGISTER("Indicates that the tool should deregister one or more YubiKey OTP devices for the target user rather than registering a new device.  If the {0} argument is provided, then only the device used to generate that one-time password will be deregistered.  If the {0} argument is not provided, then it will deregister all YubiKey OTP devices that have been registered for the target user."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_OTP("A one-time password generated by the YubiKey OTP device to be registered or deregistered."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_PROMPT_FOR_USER_PW("Indicates that the tool should interactively prompt for the static password for the user targeted by the {0} argument."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_USER_PW("The static password for the user targeted by the {0} argument."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_DESCRIPTION_USER_PW_FILE("The path to a file containing the static password for the user targeted by the {0} argument."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_ENTER_PW("Enter the static password for user {0}:"), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_EXAMPLE_DEREGISTER("Deregisters all YubiKey OTP devices for the user with DN 'uid=test.user,ou=People,dc=example,dc=com'."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_EXAMPLE_REGISTER("Registers the YubiKey device that generated the provided one-time password so that the account with username 'test.user' can use that device to authenticate."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_AUTHID("{authID}"), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_OTP("{otp}"), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_PLACEHOLDER_USER_PW("{password}"), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_REGISTER_SUCCESS("Successfully registered the specified YubiKey OTP device for user {0}."), 
    INFO_REGISTER_YUBIKEY_OTP_DEVICE_TOOL_DESCRIPTION("Registers a YubiKey OTP device with the Directory Server for a specified user so that the device may be used to authenticate that user in conjunction with the {0} SASL mechanism.  Alternately, it may be used to deregister one or more YubiKey OTP devices for a user so that they may no longer be used to authenticate that user."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_ENTRIES_ADDED_TO_TARGET("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  The subtree has been made hidden on target server {1}:{2,number,0} and read-only on source server {3}:{4,number,0}.  In addition, one more more entries from that subtree may have been added to the target server.  No entries have been removed from the source server.  In order to restore the servers to their former states, delete all entries from that subtree in the target server, and make that subtree accessible on both servers (e.g., using the subtree-accessibility tool)."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_SOURCE_HIDDEN("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  All entries in the subtree have been successfully copied from source server {1}:{2,number,0} to target server {3}:{4,number,0}, but that subtree is hidden on the source server and read-only on the target.  To complete the move, delete all entries from the subtree in the source server and make the subtree accessible (e.g., using the subtree-accessibility tool) on both the source and target servers."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_SOURCE_READ_ONLY("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  The subtree has been made hidden on target server {1}:{2,number,0} and read-only on source server {3}:{4,number,0}.  These subtrees should be made re-accessible (e.g., using the subtree-accessibility tool) to restore the servers to their former states."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_ACCESSIBLE("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  All entries in the subtree have been successfully copied from source server {1}:{2,number,0} to target server {3}:{4,number,0}.  The subtree is fully accessible on the target server, but is currently hidden on the source server and may contain one or more of the original entries.  To complete the move, delete all entries from the subtree in the source server and make the subtree accessible (e.g., using the subtree-accessibility tool) on that server."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_HIDDEN("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  The subtree has been made hidden on target server {1}:{2,number,0} and should be made re-accessible (e.g., using the subtree-accessibility tool) to restore that server to its former state."), 
    WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_READ_ONLY("The move-subtree tool was interrupted before it could finish processing for subtree ''{0}''.  All entries in the subtree have been successfully copied from source server {1}:{2,number,0} to target server {3}:{4,number,0}, but that subtree is still read-only on both servers.  To complete the move, make the subtree hidden on the source server (e.g., using the subtree-accessibility tool), make the subtree accessible on the target server, delete all entries from the subtree in the source server, and make the subtree accessible on the source server.  To revert the move, use the same process but swap the source and target servers.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<UnboundIDDSMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<UnboundIDDSMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private UnboundIDDSMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = UnboundIDDSMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (UnboundIDDSMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = UnboundIDDSMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                UnboundIDDSMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (UnboundIDDSMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = UnboundIDDSMessages.MESSAGES.get(this);
        if (f == null) {
            if (UnboundIDDSMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(UnboundIDDSMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            UnboundIDDSMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (UnboundIDDSMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = UnboundIDDSMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (UnboundIDDSMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = UnboundIDDSMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                UnboundIDDSMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-unboundidds");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<UnboundIDDSMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<UnboundIDDSMessages, MessageFormat>(100);
    }
}
