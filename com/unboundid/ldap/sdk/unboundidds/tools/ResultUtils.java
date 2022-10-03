package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.unboundidds.controls.UniquenessResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.TransactionSettingsResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeleteResponseControl;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordQualityRequirement;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordQualityRequirementValidationResult;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordValidationDetailsResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordPolicyWarningType;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordPolicyErrorType;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordPolicyResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.MatchingEntryCountResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinedEntry;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinResultControl;
import com.unboundid.ldap.sdk.unboundidds.controls.IntermediateClientResponseValue;
import com.unboundid.ldap.sdk.unboundidds.controls.IntermediateClientResponseControl;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.unboundidds.controls.GetUserResourceLimitsResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetServerIDResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AuthenticationFailureReason;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityNotice;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityWarning;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityError;
import com.unboundid.ldap.sdk.unboundidds.controls.GetPasswordPolicyStateIssuesResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetBackendSetIDResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetAuthorizationEntryResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GeneratePasswordResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationServerResultCode;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRemoteLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationLocalLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationServerResult;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationResponseControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AccountUsableResponseControl;
import com.unboundid.ldap.sdk.controls.VirtualListViewResponseControl;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ServerSideSortResponseControl;
import com.unboundid.ldap.sdk.controls.PreReadResponseControl;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.controls.PostReadResponseControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiringControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiredControl;
import com.unboundid.ldap.sdk.controls.PersistentSearchChangeType;
import com.unboundid.ldap.sdk.controls.EntryChangeNotificationControl;
import com.unboundid.ldap.sdk.controls.ContentSyncStateControl;
import com.unboundid.ldap.sdk.controls.ContentSyncDoneControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.extensions.AbortedTransactionExtendedResult;
import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Collection;
import java.util.Arrays;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateChangesApplied;
import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedResult;
import com.unboundid.ldap.sdk.Control;
import java.util.Map;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedResult;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResultUtils
{
    private ResultUtils() {
    }
    
    public static List<String> formatResult(final LDAPResult result, final boolean comment, final int indent, final int maxWidth) {
        final ArrayList<String> lines = new ArrayList<String>(10);
        formatResult(lines, result, comment, false, indent, maxWidth);
        return lines;
    }
    
    public static List<String> formatResult(final LDAPException ldapException, final boolean comment, final int indent, final int maxWidth) {
        return formatResult(ldapException.toLDAPResult(), comment, indent, maxWidth);
    }
    
    public static void formatResult(final List<String> lines, final LDAPResult result, final boolean comment, final boolean inTxn, final int indent, final int maxWidth) {
        formatResult(lines, result, inTxn, createPrefix(comment, indent), maxWidth);
    }
    
    private static void formatResult(final List<String> lines, final LDAPResult result, final boolean inTxn, final String prefix, final int maxWidth) {
        final ResultCode resultCode = result.getResultCode();
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESULT_CODE.get(String.valueOf(resultCode)), prefix, maxWidth);
        if (inTxn && resultCode == ResultCode.SUCCESS) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_SUCCESS_WITH_TXN.get(), prefix, maxWidth);
        }
        final String diagnosticMessage = result.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_DIAGNOSTIC_MESSAGE.get(diagnosticMessage), prefix, maxWidth);
        }
        final String matchedDN = result.getMatchedDN();
        if (matchedDN != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHED_DN.get(matchedDN), prefix, maxWidth);
        }
        final String[] referralURLs = result.getReferralURLs();
        if (referralURLs != null) {
            for (final String referralURL : referralURLs) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_REFERRAL_URL.get(referralURL), prefix, maxWidth);
            }
        }
        if (result instanceof SearchResult) {
            final SearchResult searchResult = (SearchResult)result;
            final int numEntries = searchResult.getEntryCount();
            if (numEntries >= 0) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_NUM_SEARCH_ENTRIES.get(numEntries), prefix, maxWidth);
            }
            final int numReferences = searchResult.getReferenceCount();
            if (numReferences > 0) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_NUM_SEARCH_REFERENCES.get(numReferences), prefix, maxWidth);
            }
        }
        else if (result instanceof StartTransactionExtendedResult) {
            final StartTransactionExtendedResult startTxnResult = (StartTransactionExtendedResult)result;
            final ASN1OctetString txnID = startTxnResult.getTransactionID();
            if (txnID != null) {
                if (StaticUtils.isPrintableString(txnID.getValue())) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_START_TXN_RESULT_TXN_ID.get(txnID.stringValue()), prefix, maxWidth);
                }
                else {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_START_TXN_RESULT_TXN_ID.get("0x" + StaticUtils.toHex(txnID.getValue())), prefix, maxWidth);
                }
            }
        }
        else if (result instanceof EndTransactionExtendedResult) {
            final EndTransactionExtendedResult endTxnResult = (EndTransactionExtendedResult)result;
            final int failedOpMessageID = endTxnResult.getFailedOpMessageID();
            if (failedOpMessageID > 0) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_END_TXN_RESULT_FAILED_MSG_ID.get(failedOpMessageID), prefix, maxWidth);
            }
            final Map<Integer, Control[]> controls = endTxnResult.getOperationResponseControls();
            if (controls != null) {
                for (final Map.Entry<Integer, Control[]> e : controls.entrySet()) {
                    for (final Control c : e.getValue()) {
                        wrap(lines, ToolMessages.INFO_RESULT_UTILS_END_TXN_RESULT_OP_CONTROL.get(e.getKey()), prefix, maxWidth);
                        formatResponseControl(lines, c, prefix + "     ", maxWidth);
                    }
                }
            }
        }
        else if (result instanceof MultiUpdateExtendedResult) {
            final MultiUpdateExtendedResult multiUpdateResult = (MultiUpdateExtendedResult)result;
            final MultiUpdateChangesApplied changesApplied = multiUpdateResult.getChangesApplied();
            if (changesApplied != null) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MULTI_UPDATE_CHANGES_APPLIED.get(changesApplied.name()), prefix, maxWidth);
            }
            final List<ObjectPair<OperationType, LDAPResult>> multiUpdateResults = multiUpdateResult.getResults();
            if (multiUpdateResults != null) {
                for (final ObjectPair<OperationType, LDAPResult> p : multiUpdateResults) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_MULTI_UPDATE_RESULT_HEADER.get(p.getFirst().name()), prefix, maxWidth);
                    formatResult(lines, p.getSecond(), false, prefix + "     ", maxWidth);
                }
            }
        }
        else if (result instanceof PasswordModifyExtendedResult) {
            final PasswordModifyExtendedResult passwordModifyResult = (PasswordModifyExtendedResult)result;
            final String generatedPassword = passwordModifyResult.getGeneratedPassword();
            if (generatedPassword != null) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PASSWORD_MODIFY_RESULT_GENERATED_PW.get(generatedPassword), prefix, maxWidth);
            }
        }
        else if (result instanceof ExtendedResult) {
            final ExtendedResult extendedResult = (ExtendedResult)result;
            final String oid = ((ExtendedResult)result).getOID();
            if (oid != null) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_OID.get(oid), prefix, maxWidth);
            }
            final ASN1OctetString value = extendedResult.getValue();
            if (value != null && value.getValueLength() > 0) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_RAW_VALUE_HEADER.get(), prefix, maxWidth);
                for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(value.getValue(), 0))) {
                    lines.add(prefix + "     " + line);
                }
            }
        }
        final Control[] controls2 = result.getResponseControls();
        if (controls2 != null) {
            for (final Control c2 : controls2) {
                formatResponseControl(lines, c2, prefix, maxWidth);
            }
        }
    }
    
    public static void formatSearchResultEntry(final List<String> lines, final SearchResultEntry entry, final int maxWidth) {
        for (final Control c : entry.getControls()) {
            formatResponseControl(lines, c, true, 0, maxWidth);
        }
        lines.addAll(Arrays.asList(entry.toLDIF(maxWidth)));
    }
    
    public static void formatSearchResultReference(final List<String> lines, final SearchResultReference reference, final int maxWidth) {
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_SEARCH_REFERENCE_HEADER.get(), "# ", maxWidth);
        for (final String url : reference.getReferralURLs()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_REFERRAL_URL.get(url), "#      ", maxWidth);
        }
        for (final Control c : reference.getControls()) {
            formatResponseControl(lines, c, "#      ", maxWidth);
        }
    }
    
    public static void formatUnsolicitedNotification(final List<String> lines, final ExtendedResult notification, final boolean comment, final int indent, final int maxWidth) {
        final String prefix = createPrefix(comment, indent);
        final String indentPrefix = prefix + "     ";
        boolean includeRawValue = true;
        final String oid = notification.getOID();
        if (oid != null) {
            if (oid.equals("1.3.6.1.4.1.1466.20036")) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_NOTICE_OF_DISCONNECTION_HEADER.get(), prefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_OID.get(oid), indentPrefix, maxWidth);
            }
            else if (oid.equals("1.3.6.1.1.21.4")) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_ABORTED_TXN_HEADER.get(), prefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_OID.get(oid), indentPrefix, maxWidth);
                try {
                    final AbortedTransactionExtendedResult r = new AbortedTransactionExtendedResult(notification);
                    String txnID;
                    if (StaticUtils.isPrintableString(r.getTransactionID().getValue())) {
                        txnID = r.getTransactionID().stringValue();
                    }
                    else {
                        txnID = "0x" + StaticUtils.toHex(r.getTransactionID().getValue());
                    }
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_TXN_ID_HEADER.get(txnID), indentPrefix, maxWidth);
                    includeRawValue = false;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
            else {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNSOLICITED_NOTIFICATION_HEADER.get(), prefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_OID.get(oid), indentPrefix, maxWidth);
            }
        }
        else {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNSOLICITED_NOTIFICATION_HEADER.get(), prefix, maxWidth);
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESULT_CODE.get(String.valueOf(notification.getResultCode())), indentPrefix, maxWidth);
        final String diagnosticMessage = notification.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_DIAGNOSTIC_MESSAGE.get(diagnosticMessage), indentPrefix, maxWidth);
        }
        final String matchedDN = notification.getMatchedDN();
        if (matchedDN != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHED_DN.get(matchedDN), indentPrefix, maxWidth);
        }
        final String[] referralURLs = notification.getReferralURLs();
        if (referralURLs != null) {
            for (final String referralURL : referralURLs) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_REFERRAL_URL.get(referralURL), indentPrefix, maxWidth);
            }
        }
        if (includeRawValue) {
            final ASN1OctetString value = notification.getValue();
            if (value != null && value.getValueLength() > 0) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_EXTOP_RAW_VALUE_HEADER.get(), indentPrefix, maxWidth);
                for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(value.getValue(), 0))) {
                    lines.add(prefix + "          " + line);
                }
            }
        }
        final Control[] controls = notification.getResponseControls();
        if (controls != null) {
            for (final Control c : controls) {
                formatResponseControl(lines, c, comment, indent + 5, maxWidth);
            }
        }
    }
    
    public static void formatResponseControl(final List<String> lines, final Control c, final boolean comment, final int indent, final int maxWidth) {
        final StringBuilder buffer = new StringBuilder(indent + 2);
        if (comment) {
            buffer.append("# ");
        }
        for (int i = 0; i < indent; ++i) {
            buffer.append(' ');
        }
        final String prefix = buffer.toString();
        formatResponseControl(lines, c, prefix, maxWidth);
    }
    
    private static void formatResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        final String oid = c.getOID();
        if (oid.equals("2.16.840.1.113730.3.4.15")) {
            addAuthorizationIdentityResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.4203.1.9.1.3")) {
            addContentSyncDoneControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.4203.1.9.1.2")) {
            addContentSyncStateControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("2.16.840.1.113730.3.4.7")) {
            addEntryChangeNotificationControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("2.16.840.1.113730.3.4.4")) {
            addPasswordExpiredControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("2.16.840.1.113730.3.4.5")) {
            addPasswordExpiringControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.1.13.2")) {
            addPostReadResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.1.13.1")) {
            addPreReadResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.2.840.113556.1.4.474")) {
            addServerSideSortResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.2.840.113556.1.4.319")) {
            addSimplePagedResultsControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("2.16.840.1.113730.3.4.10")) {
            addVirtualListViewResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.42.2.27.9.5.8")) {
            addAccountUsableResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.29")) {
            addAssuredReplicationResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.59")) {
            addGeneratePasswordResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.6")) {
            addGetAuthorizationEntryResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.34")) {
            addGetBackendSetIDResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.47")) {
            addGetPasswordPolicyStateIssuesResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.15")) {
            addGetServerIDResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.26")) {
            addGetUserResourceLimitsResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.2")) {
            addIntermediateClientResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.9")) {
            addJoinResultControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.37")) {
            addMatchingEntryCountResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.42.2.27.8.5.1")) {
            addPasswordPolicyResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.41")) {
            addPasswordValidationDetailsResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.21")) {
            addSoftDeleteResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.39")) {
            addTransactionSettingsResponseControl(lines, c, prefix, maxWidth);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.5.53")) {
            addUniquenessResponseControl(lines, c, prefix, maxWidth);
        }
        else {
            addGenericResponseControl(lines, c, prefix, maxWidth);
        }
    }
    
    private static void addGenericResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GENERIC_RESPONSE_CONTROL_HEADER.get(), prefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), prefix + "     ", maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_IS_CRITICAL.get(c.isCritical()), prefix + "     ", maxWidth);
        final ASN1OctetString value = c.getValue();
        if (value != null && value.getValue().length > 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_RAW_VALUE_HEADER.get(), prefix + "     ", maxWidth);
            for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(value.getValue(), 0))) {
                lines.add(prefix + "          " + line);
            }
        }
    }
    
    private static void addAuthorizationIdentityResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        AuthorizationIdentityResponseControl decoded;
        try {
            decoded = new AuthorizationIdentityResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_AUTHZ_ID_RESPONSE_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_AUTHZ_ID_RESPONSE_ID.get(decoded.getAuthorizationID()), indentPrefix, maxWidth);
    }
    
    private static void addContentSyncDoneControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        ContentSyncDoneControl decoded;
        try {
            decoded = new ContentSyncDoneControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_DONE_RESPONSE_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_DONE_REFRESH_DELETES.get(decoded.refreshDeletes()), indentPrefix, maxWidth);
        final ASN1OctetString cookie = decoded.getCookie();
        if (cookie != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_DONE_COOKIE_HEADER.get(), indentPrefix, maxWidth);
            for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(cookie.getValue(), 0))) {
                lines.add(indentPrefix + "     " + line);
            }
        }
    }
    
    private static void addContentSyncStateControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        ContentSyncStateControl decoded;
        try {
            decoded = new ContentSyncStateControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_STATE_RESPONSE_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_STATE_ENTRY_UUID.get(decoded.getEntryUUID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_STATE_NAME.get(decoded.getState().name()), indentPrefix, maxWidth);
        final ASN1OctetString cookie = decoded.getCookie();
        if (cookie != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_CONTENT_SYNC_STATE_COOKIE_HEADER.get(), indentPrefix, maxWidth);
            for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(cookie.getValue(), 0))) {
                lines.add(indentPrefix + "     " + line);
            }
        }
    }
    
    private static void addEntryChangeNotificationControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        EntryChangeNotificationControl decoded;
        try {
            decoded = new EntryChangeNotificationControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ECN_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final PersistentSearchChangeType changeType = decoded.getChangeType();
        if (changeType != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ECN_CHANGE_TYPE.get(changeType.getName()), indentPrefix, maxWidth);
        }
        final long changeNumber = decoded.getChangeNumber();
        if (changeNumber >= 0L) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ECN_CHANGE_NUMBER.get(changeNumber), indentPrefix, maxWidth);
        }
        final String previousDN = decoded.getPreviousDN();
        if (previousDN != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ECN_PREVIOUS_DN.get(previousDN), indentPrefix, maxWidth);
        }
    }
    
    private static void addPasswordExpiredControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PasswordExpiredControl decoded;
        try {
            decoded = new PasswordExpiredControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PASSWORD_EXPIRED_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(decoded.getOID()), indentPrefix, maxWidth);
    }
    
    private static void addPasswordExpiringControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PasswordExpiringControl decoded;
        try {
            decoded = new PasswordExpiringControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PASSWORD_EXPIRING_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final int secondsUntilExpiration = decoded.getSecondsUntilExpiration();
        if (secondsUntilExpiration >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PASSWORD_EXPIRING_SECONDS_UNTIL_EXPIRATION.get(secondsUntilExpiration), indentPrefix, maxWidth);
        }
    }
    
    private static void addPostReadResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PostReadResponseControl decoded;
        try {
            decoded = new PostReadResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_POST_READ_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_POST_READ_ENTRY_HEADER.get(c.getOID()), indentPrefix, maxWidth);
        addLDIF(lines, decoded.getEntry(), true, indentPrefix + "     ", maxWidth);
    }
    
    private static void addPreReadResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PreReadResponseControl decoded;
        try {
            decoded = new PreReadResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PRE_READ_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PRE_READ_ENTRY_HEADER.get(c.getOID()), indentPrefix, maxWidth);
        addLDIF(lines, decoded.getEntry(), true, indentPrefix + "     ", maxWidth);
    }
    
    private static void addServerSideSortResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        ServerSideSortResponseControl decoded;
        try {
            decoded = new ServerSideSortResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_SORT_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final ResultCode resultCode = decoded.getResultCode();
        if (resultCode != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_SORT_RESULT_CODE.get(String.valueOf(resultCode)), indentPrefix, maxWidth);
        }
        final String attributeName = decoded.getAttributeName();
        if (attributeName != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_SORT_ATTRIBUTE_NAME.get(attributeName), indentPrefix, maxWidth);
        }
    }
    
    private static void addSimplePagedResultsControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        SimplePagedResultsControl decoded;
        try {
            decoded = new SimplePagedResultsControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PAGED_RESULTS_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final int estimatedCount = decoded.getSize();
        if (estimatedCount >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PAGED_RESULTS_COUNT.get(estimatedCount), indentPrefix, maxWidth);
        }
        final ASN1OctetString cookie = decoded.getCookie();
        if (cookie != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PAGED_RESULTS_COOKIE_HEADER.get(), indentPrefix, maxWidth);
            for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(cookie.getValue(), 0))) {
                lines.add(indentPrefix + "     " + line);
            }
        }
    }
    
    private static void addVirtualListViewResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        VirtualListViewResponseControl decoded;
        try {
            decoded = new VirtualListViewResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_VLV_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final ResultCode resultCode = decoded.getResultCode();
        if (resultCode != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_VLV_RESULT_CODE.get(String.valueOf(resultCode)), indentPrefix, maxWidth);
        }
        final int contentCount = decoded.getContentCount();
        if (contentCount >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_VLV_CONTENT_COUNT.get(contentCount), indentPrefix, maxWidth);
        }
        final int targetPosition = decoded.getTargetPosition();
        if (targetPosition >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_VLV_TARGET_POSITION.get(targetPosition), indentPrefix, maxWidth);
        }
        final ASN1OctetString contextID = decoded.getContextID();
        if (contextID != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_VLV_CONTEXT_ID_HEADER.get(), indentPrefix, maxWidth);
            for (final String line : StaticUtils.stringToLines(StaticUtils.toHexPlusASCII(contextID.getValue(), 0))) {
                lines.add(indentPrefix + "     " + line);
            }
        }
    }
    
    private static void addAccountUsableResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        AccountUsableResponseControl decoded;
        try {
            decoded = new AccountUsableResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_IS_USABLE.get(decoded.isUsable()), indentPrefix, maxWidth);
        final List<String> unusableReasons = decoded.getUnusableReasons();
        if (unusableReasons != null && !unusableReasons.isEmpty()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_UNUSABLE_REASONS_HEADER.get(), indentPrefix, maxWidth);
            for (final String reason : unusableReasons) {
                wrap(lines, reason, indentPrefix + "     ", maxWidth);
            }
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_PW_EXPIRED.get(decoded.passwordIsExpired()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_MUST_CHANGE_PW.get(decoded.mustChangePassword()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_IS_INACTIVE.get(decoded.isInactive()), indentPrefix, maxWidth);
        final int remainingGraceLogins = decoded.getRemainingGraceLogins();
        if (remainingGraceLogins >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_REMAINING_GRACE.get(remainingGraceLogins), indentPrefix, maxWidth);
        }
        final int secondsUntilExpiration = decoded.getSecondsUntilExpiration();
        if (secondsUntilExpiration >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_SECONDS_UNTIL_EXPIRATION.get(secondsUntilExpiration), indentPrefix, maxWidth);
        }
        final int secondsUntilUnlock = decoded.getSecondsUntilUnlock();
        if (secondsUntilUnlock >= 0) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ACCOUNT_USABLE_SECONDS_UNTIL_UNLOCK.get(secondsUntilUnlock), indentPrefix, maxWidth);
        }
    }
    
    private static void addAssuredReplicationResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        AssuredReplicationResponseControl decoded;
        try {
            decoded = new AssuredReplicationResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final String csn = decoded.getCSN();
        if (csn != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_CSN.get(csn), indentPrefix, maxWidth);
        }
        final AssuredReplicationLocalLevel localLevel = decoded.getLocalLevel();
        if (localLevel != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_LOCAL_LEVEL.get(localLevel.name()), indentPrefix, maxWidth);
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_LOCAL_SATISFIED.get(decoded.localAssuranceSatisfied()), indentPrefix, maxWidth);
        final String localMessage = decoded.getLocalAssuranceMessage();
        if (localMessage != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_LOCAL_MESSAGE.get(localMessage), indentPrefix, maxWidth);
        }
        final AssuredReplicationRemoteLevel remoteLevel = decoded.getRemoteLevel();
        if (remoteLevel != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_REMOTE_LEVEL.get(remoteLevel.name()), indentPrefix, maxWidth);
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_REMOTE_SATISFIED.get(decoded.remoteAssuranceSatisfied()), indentPrefix, maxWidth);
        final String remoteMessage = decoded.getRemoteAssuranceMessage();
        if (remoteMessage != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_REMOTE_MESSAGE.get(remoteMessage), indentPrefix, maxWidth);
        }
        final List<AssuredReplicationServerResult> serverResults = decoded.getServerResults();
        if (serverResults != null) {
            for (final AssuredReplicationServerResult r : serverResults) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_SERVER_RESULT_HEADER.get(), indentPrefix, maxWidth);
                final AssuredReplicationServerResultCode rc = r.getResultCode();
                if (rc != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_SERVER_RESULT_CODE.get(rc.name()), indentPrefix + "     ", maxWidth);
                }
                final Short replicationServerID = r.getReplicationServerID();
                if (replicationServerID != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_SERVER_RESULT_REPL_SERVER_ID.get(replicationServerID), indentPrefix + "     ", maxWidth);
                }
                final Short replicaID = r.getReplicaID();
                if (replicaID != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_ASSURED_REPL_SERVER_RESULT_REPL_ID.get(replicaID), indentPrefix + "     ", maxWidth);
                }
            }
        }
    }
    
    private static void addGeneratePasswordResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GeneratePasswordResponseControl decoded;
        try {
            decoded = new GeneratePasswordResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GENERATE_PW_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GENERATE_PW_PASSWORD.get(decoded.getGeneratedPasswordString()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GENERATE_PW_MUST_CHANGE.get(String.valueOf(decoded.mustChangePassword())), indentPrefix, maxWidth);
        if (decoded.getSecondsUntilExpiration() != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GENERATE_PW_SECONDS_UNTIL_EXPIRATION.get(decoded.getSecondsUntilExpiration()), indentPrefix, maxWidth);
        }
    }
    
    private static void addGetAuthorizationEntryResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GetAuthorizationEntryResponseControl decoded;
        try {
            decoded = new GetAuthorizationEntryResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_IS_AUTHENTICATED.get(decoded.isAuthenticated()), indentPrefix, maxWidth);
        if (!decoded.isAuthenticated()) {
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_IDS_MATCH.get(decoded.identitiesMatch()), indentPrefix, maxWidth);
        final String authNID = decoded.getAuthNID();
        if (authNID != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_AUTHN_ID.get(authNID), indentPrefix, maxWidth);
        }
        final Entry authNEntry = decoded.getAuthNEntry();
        if (authNEntry != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_AUTHN_ENTRY_HEADER.get(), indentPrefix, maxWidth);
            addLDIF(lines, authNEntry, true, indentPrefix + "     ", maxWidth);
        }
        if (decoded.identitiesMatch()) {
            return;
        }
        final String authZID = decoded.getAuthZID();
        if (authZID != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_AUTHZ_ID.get(authZID), indentPrefix, maxWidth);
        }
        final Entry authZEntry = decoded.getAuthZEntry();
        if (authZEntry != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_AUTHZ_ENTRY_AUTHZ_ENTRY_HEADER.get(), indentPrefix, maxWidth);
            addLDIF(lines, authZEntry, true, indentPrefix + "     ", maxWidth);
        }
    }
    
    private static void addGetBackendSetIDResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GetBackendSetIDResponseControl decoded;
        try {
            decoded = new GetBackendSetIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_BACKEND_SET_ID_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_BACKEND_SET_ID_EB_RP_ID.get(decoded.getEntryBalancingRequestProcessorID()), indentPrefix, maxWidth);
        for (final String id : decoded.getBackendSetIDs()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_BACKEND_SET_ID.get(id), indentPrefix, maxWidth);
        }
    }
    
    private static void addGetPasswordPolicyStateIssuesResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GetPasswordPolicyStateIssuesResponseControl decoded;
        try {
            decoded = new GetPasswordPolicyStateIssuesResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final String doubleIndentPrefix = indentPrefix + "     ";
        final AuthenticationFailureReason authFailureReason = decoded.getAuthenticationFailureReason();
        if (authFailureReason != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_FAILURE_REASON_HEADER.get(), indentPrefix, maxWidth);
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_FAILURE_TYPE.get(authFailureReason.getName()), doubleIndentPrefix, maxWidth);
            final String message = authFailureReason.getMessage();
            if (message != null) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_FAILURE_MESSAGE.get(message), doubleIndentPrefix, maxWidth);
            }
        }
        final List<PasswordPolicyStateAccountUsabilityError> errors = decoded.getErrors();
        if (errors != null) {
            for (final PasswordPolicyStateAccountUsabilityError e2 : errors) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_ERROR_HEADER.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_ERROR_NAME.get(e2.getName()), doubleIndentPrefix, maxWidth);
                final String message2 = e2.getMessage();
                if (message2 != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_ERROR_MESSAGE.get(message2), doubleIndentPrefix, maxWidth);
                }
            }
        }
        final List<PasswordPolicyStateAccountUsabilityWarning> warnings = decoded.getWarnings();
        if (warnings != null) {
            for (final PasswordPolicyStateAccountUsabilityWarning w : warnings) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_WARNING_HEADER.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_WARNING_NAME.get(w.getName()), doubleIndentPrefix, maxWidth);
                final String message3 = w.getMessage();
                if (message3 != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_WARNING_MESSAGE.get(message3), doubleIndentPrefix, maxWidth);
                }
            }
        }
        final List<PasswordPolicyStateAccountUsabilityNotice> notices = decoded.getNotices();
        if (notices != null) {
            for (final PasswordPolicyStateAccountUsabilityNotice n : notices) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_NOTICE_HEADER.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_NOTICE_NAME.get(n.getName()), doubleIndentPrefix, maxWidth);
                final String message4 = n.getMessage();
                if (message4 != null) {
                    wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_PW_STATE_ISSUES_NOTICE_MESSAGE.get(message4), doubleIndentPrefix, maxWidth);
                }
            }
        }
    }
    
    private static void addGetServerIDResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GetServerIDResponseControl decoded;
        try {
            decoded = new GetServerIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_SERVER_ID_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_SERVER_ID.get(decoded.getServerID()), indentPrefix, maxWidth);
    }
    
    private static void addGetUserResourceLimitsResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        GetUserResourceLimitsResponseControl decoded;
        try {
            decoded = new GetUserResourceLimitsResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final Long sizeLimit = decoded.getSizeLimit();
        if (sizeLimit != null) {
            String value;
            if (sizeLimit > 0L) {
                value = String.valueOf(sizeLimit);
            }
            else {
                value = ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_VALUE_UNLIMITED.get();
            }
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_SIZE_LIMIT.get(value), indentPrefix, maxWidth);
        }
        final Long timeLimit = decoded.getTimeLimitSeconds();
        if (timeLimit != null) {
            String value2;
            if (timeLimit > 0L) {
                value2 = timeLimit + " " + ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_UNIT_SECONDS.get();
            }
            else {
                value2 = ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_VALUE_UNLIMITED.get();
            }
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_TIME_LIMIT.get(value2), indentPrefix, maxWidth);
        }
        final Long idleTimeLimit = decoded.getIdleTimeLimitSeconds();
        if (idleTimeLimit != null) {
            String value3;
            if (idleTimeLimit > 0L) {
                value3 = idleTimeLimit + " " + ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_UNIT_SECONDS.get();
            }
            else {
                value3 = ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_VALUE_UNLIMITED.get();
            }
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_IDLE_TIME_LIMIT.get(value3), indentPrefix, maxWidth);
        }
        final Long lookthroughLimit = decoded.getLookthroughLimit();
        if (lookthroughLimit != null) {
            String value4;
            if (lookthroughLimit > 0L) {
                value4 = String.valueOf(lookthroughLimit);
            }
            else {
                value4 = ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_VALUE_UNLIMITED.get();
            }
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_LOOKTHROUGH_LIMIT.get(value4), indentPrefix, maxWidth);
        }
        final String equivalentUserDN = decoded.getEquivalentAuthzUserDN();
        if (equivalentUserDN != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_EQUIVALENT_AUTHZ_USER_DN.get(equivalentUserDN), indentPrefix, maxWidth);
        }
        final String ccpName = decoded.getClientConnectionPolicyName();
        if (ccpName != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_CCP_NAME.get(ccpName), indentPrefix, maxWidth);
        }
        final String doubleIndentPrefix = indentPrefix + "     ";
        final List<String> groupDNs = decoded.getGroupDNs();
        if (groupDNs != null && !groupDNs.isEmpty()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_GROUP_DNS_HEADER.get(), indentPrefix, maxWidth);
            for (final String groupDN : groupDNs) {
                wrap(lines, groupDN, doubleIndentPrefix, maxWidth);
            }
        }
        final List<String> privilegeNames = decoded.getPrivilegeNames();
        if (privilegeNames != null && !privilegeNames.isEmpty()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_PRIVILEGES_HEADER.get(), indentPrefix, maxWidth);
            for (final String privilegeName : privilegeNames) {
                wrap(lines, privilegeName, doubleIndentPrefix, maxWidth);
            }
        }
        final List<Attribute> otherAttrs = decoded.getOtherAttributes();
        if (otherAttrs != null && !otherAttrs.isEmpty()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_GET_USER_RLIM_OTHER_ATTRIBUTES_HEADER.get(), indentPrefix, maxWidth);
            addLDIF(lines, new Entry("", otherAttrs), false, doubleIndentPrefix, maxWidth);
        }
    }
    
    private static void addIntermediateClientResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        IntermediateClientResponseControl decoded;
        try {
            decoded = new IntermediateClientResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        addIntermediateResponseValue(lines, decoded.getResponseValue(), indentPrefix, maxWidth);
    }
    
    private static void addIntermediateResponseValue(final List<String> lines, final IntermediateClientResponseValue v, final String prefix, final int maxWidth) {
        final String address = v.getUpstreamServerAddress();
        if (address != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_UPSTREAM_ADDRESS.get(address), prefix, maxWidth);
        }
        final Boolean secure = v.upstreamServerSecure();
        if (secure != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_UPSTREAM_SECURE.get(String.valueOf(secure)), prefix, maxWidth);
        }
        final String serverName = v.getServerName();
        if (serverName != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_SERVER_NAME.get(serverName), prefix, maxWidth);
        }
        final String sessionID = v.getServerSessionID();
        if (sessionID != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_SESSION_ID.get(sessionID), prefix, maxWidth);
        }
        final String responseID = v.getServerResponseID();
        if (responseID != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_RESPONSE_ID.get(responseID), prefix, maxWidth);
        }
        final IntermediateClientResponseValue upstreamResponse = v.getUpstreamResponse();
        if (upstreamResponse != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_INTERMEDIATE_CLIENT_UPSTREAM_RESPONSE_HEADER.get(), prefix, maxWidth);
            addIntermediateResponseValue(lines, upstreamResponse, prefix + "     ", maxWidth);
        }
    }
    
    private static void addJoinResultControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        JoinResultControl decoded;
        try {
            decoded = new JoinResultControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOIN_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final ResultCode resultCode = decoded.getResultCode();
        if (resultCode != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOIN_RESULT_CODE.get(String.valueOf(resultCode)), indentPrefix, maxWidth);
        }
        final String diagnosticMessage = decoded.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOIN_DIAGNOSTIC_MESSAGE.get(diagnosticMessage), indentPrefix, maxWidth);
        }
        final String matchedDN = decoded.getMatchedDN();
        if (matchedDN != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOIN_MATCHED_DN.get(matchedDN), indentPrefix, maxWidth);
        }
        final List<String> referralURLs = decoded.getReferralURLs();
        if (referralURLs != null) {
            for (final String referralURL : referralURLs) {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOIN_REFERRAL_URL.get(referralURL), indentPrefix, maxWidth);
            }
        }
        final List<JoinedEntry> joinedEntries = decoded.getJoinResults();
        if (joinedEntries != null) {
            for (final JoinedEntry e2 : joinedEntries) {
                addJoinedEntry(lines, e2, indentPrefix, maxWidth);
            }
        }
    }
    
    private static void addJoinedEntry(final List<String> lines, final JoinedEntry joinedEntry, final String prefix, final int maxWidth) {
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_JOINED_WITH_ENTRY_HEADER.get(), prefix, maxWidth);
        addLDIF(lines, joinedEntry, true, prefix + "     ", maxWidth);
        final List<JoinedEntry> nestedJoinResults = joinedEntry.getNestedJoinResults();
        if (nestedJoinResults != null) {
            for (final JoinedEntry e : nestedJoinResults) {
                addJoinedEntry(lines, e, prefix + "          ", maxWidth);
            }
        }
    }
    
    private static void addMatchingEntryCountResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        MatchingEntryCountResponseControl decoded;
        try {
            decoded = new MatchingEntryCountResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        switch (decoded.getCountType()) {
            case EXAMINED_COUNT: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_TYPE_EXAMINED.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_VALUE.get(decoded.getCountValue()), indentPrefix, maxWidth);
                break;
            }
            case UNEXAMINED_COUNT: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_TYPE_UNEXAMINED.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_VALUE.get(decoded.getCountValue()), indentPrefix, maxWidth);
                break;
            }
            case UPPER_BOUND: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_TYPE_UPPER_BOUND.get(), indentPrefix, maxWidth);
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_VALUE.get(decoded.getCountValue()), indentPrefix, maxWidth);
                break;
            }
            default: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_TYPE_UNKNOWN.get(), indentPrefix, maxWidth);
                break;
            }
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_INDEXED.get(decoded.searchIndexed()), indentPrefix, maxWidth);
        final List<String> debugInfo = decoded.getDebugInfo();
        if (debugInfo != null && !debugInfo.isEmpty()) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_MATCHING_ENTRY_COUNT_DEBUG_HEADER.get(), indentPrefix, maxWidth);
            for (final String s : debugInfo) {
                wrap(lines, s, indentPrefix + "     ", maxWidth);
            }
        }
    }
    
    private static void addPasswordPolicyResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PasswordPolicyResponseControl decoded;
        try {
            decoded = new PasswordPolicyResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final PasswordPolicyErrorType errorType = decoded.getErrorType();
        if (errorType == null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_ERROR_TYPE_NONE.get(), indentPrefix, maxWidth);
        }
        else {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_ERROR_TYPE.get(errorType.getName()), indentPrefix, maxWidth);
        }
        final PasswordPolicyWarningType warningType = decoded.getWarningType();
        if (warningType == null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_WARNING_TYPE_NONE.get(), indentPrefix, maxWidth);
        }
        else {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_WARNING_TYPE.get(warningType.getName()), indentPrefix, maxWidth);
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_POLICY_WARNING_VALUE.get(decoded.getWarningValue()), indentPrefix, maxWidth);
        }
    }
    
    private static void addPasswordValidationDetailsResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        PasswordValidationDetailsResponseControl decoded;
        try {
            decoded = new PasswordValidationDetailsResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        switch (decoded.getResponseType()) {
            case VALIDATION_DETAILS: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_RESULT_TYPE_RESULT.get(), indentPrefix, maxWidth);
                final List<PasswordQualityRequirementValidationResult> results = decoded.getValidationResults();
                if (results != null) {
                    for (final PasswordQualityRequirementValidationResult r : results) {
                        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_HEADER.get(), indentPrefix + "     ", maxWidth);
                        final String tripleIndentPrefix = indentPrefix + "          ";
                        final PasswordQualityRequirement pqr = r.getPasswordRequirement();
                        final String description = pqr.getDescription();
                        if (description != null) {
                            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_DESC.get(description), tripleIndentPrefix, maxWidth);
                        }
                        final String clientSideType = pqr.getClientSideValidationType();
                        if (clientSideType != null) {
                            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_TYPE.get(clientSideType), tripleIndentPrefix, maxWidth);
                        }
                        final Map<String, String> properties = pqr.getClientSideValidationProperties();
                        if (properties != null) {
                            for (final Map.Entry<String, String> e2 : properties.entrySet()) {
                                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_PROP.get(e2.getKey(), e2.getValue()), tripleIndentPrefix, maxWidth);
                            }
                        }
                        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_SATISFIED.get(r.requirementSatisfied()), tripleIndentPrefix, maxWidth);
                        final String additionalInfo = r.getAdditionalInfo();
                        if (additionalInfo != null) {
                            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_PQR_INFO.get(additionalInfo), tripleIndentPrefix, maxWidth);
                        }
                    }
                    break;
                }
                break;
            }
            case NO_PASSWORD_PROVIDED: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_RESULT_TYPE_NO_PW.get(), indentPrefix, maxWidth);
                break;
            }
            case MULTIPLE_PASSWORDS_PROVIDED: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_RESULT_TYPE_MULTIPLE_PW.get(), indentPrefix, maxWidth);
                break;
            }
            case NO_VALIDATION_ATTEMPTED: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_RESULT_TYPE_NO_VALIDATION.get(), indentPrefix, maxWidth);
                break;
            }
            default: {
                wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_RESULT_TYPE_DEFAULT.get(decoded.getResponseType().name()), indentPrefix, maxWidth);
                break;
            }
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_MISSING_CURRENT.get(decoded.missingCurrentPassword()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_MUST_CHANGE.get(decoded.mustChangePassword()), indentPrefix, maxWidth);
        final Integer secondsUntilExpiration = decoded.getSecondsUntilExpiration();
        if (secondsUntilExpiration != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_PW_VALIDATION_DETAILS_SECONDS_TO_EXP.get(secondsUntilExpiration), indentPrefix, maxWidth);
        }
    }
    
    private static void addSoftDeleteResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        SoftDeleteResponseControl decoded;
        try {
            decoded = new SoftDeleteResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_SOFT_DELETE_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        final String dn = decoded.getSoftDeletedEntryDN();
        if (dn != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_SOFT_DELETED_DN.get(dn), indentPrefix, maxWidth);
        }
    }
    
    private static void addTransactionSettingsResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        TransactionSettingsResponseControl decoded;
        try {
            decoded = new TransactionSettingsResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_TXN_SETTINGS_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_TXN_SETTINGS_NUM_CONFLICTS.get(decoded.getNumLockConflicts()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_TXN_SETTINGS_BACKEND_LOCK_ACQUIRED.get(decoded.backendLockAcquired()), indentPrefix, maxWidth);
    }
    
    private static void addUniquenessResponseControl(final List<String> lines, final Control c, final String prefix, final int maxWidth) {
        UniquenessResponseControl decoded;
        try {
            decoded = new UniquenessResponseControl(c.getOID(), c.isCritical(), c.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addGenericResponseControl(lines, c, prefix, maxWidth);
            return;
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_HEADER.get(), prefix, maxWidth);
        final String indentPrefix = prefix + "     ";
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_RESPONSE_CONTROL_OID.get(c.getOID()), indentPrefix, maxWidth);
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_ID.get(decoded.getUniquenessID()), indentPrefix, maxWidth);
        String preCommitStatus;
        if (decoded.getPreCommitValidationPassed() == null) {
            preCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_NOT_ATTEMPTED.get();
        }
        else if (decoded.getPreCommitValidationPassed() == Boolean.TRUE) {
            preCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_PASSED.get();
        }
        else {
            preCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_FAILED.get();
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_PRE_COMMIT_STATUS.get(preCommitStatus), indentPrefix, maxWidth);
        String postCommitStatus;
        if (decoded.getPostCommitValidationPassed() == null) {
            postCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_NOT_ATTEMPTED.get();
        }
        else if (decoded.getPostCommitValidationPassed() == Boolean.TRUE) {
            postCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_PASSED.get();
        }
        else {
            postCommitStatus = ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_STATUS_VALUE_FAILED.get();
        }
        wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_POST_COMMIT_STATUS.get(postCommitStatus), indentPrefix, maxWidth);
        final String message = decoded.getValidationMessage();
        if (message != null) {
            wrap(lines, ToolMessages.INFO_RESULT_UTILS_UNIQUENESS_MESSAGE.get(message), indentPrefix, maxWidth);
        }
    }
    
    private static String createPrefix(final boolean comment, final int indent) {
        final StringBuilder buffer = new StringBuilder(indent + 2);
        if (comment) {
            buffer.append("# ");
        }
        for (int i = 0; i < indent; ++i) {
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    private static void wrap(final List<String> lines, final String s, final String prefix, final int maxWidth) {
        final int minimumMaxWidth = prefix.length() + 20;
        final int effectiveMaxWidth = Math.max(minimumMaxWidth, maxWidth);
        if (prefix.length() + s.length() <= effectiveMaxWidth) {
            lines.add(prefix + s);
            return;
        }
        final List<String> wrappedLines = StaticUtils.wrapLine(s, maxWidth - prefix.length(), maxWidth - prefix.length() - 5);
        for (int i = 0; i < wrappedLines.size(); ++i) {
            if (i > 0) {
                lines.add(prefix + "     " + wrappedLines.get(i));
            }
            else {
                lines.add(prefix + wrappedLines.get(i));
            }
        }
    }
    
    private static void addLDIF(final List<String> lines, final Entry entry, final boolean includeDN, final String prefix, final int maxWidth) {
        final int wrapColumn = Math.max(maxWidth - prefix.length(), 20);
        if (includeDN) {
            for (final String s : entry.toLDIF(wrapColumn)) {
                lines.add(prefix + s);
            }
        }
        else {
            String[] ldifLinesWithDN;
            if (entry.getDN().length() > 10) {
                final Entry dup = entry.duplicate();
                dup.setDN("");
                ldifLinesWithDN = dup.toLDIF(wrapColumn);
            }
            else {
                ldifLinesWithDN = entry.toLDIF(wrapColumn);
            }
            for (int i = 1; i < ldifLinesWithDN.length; ++i) {
                lines.add(prefix + ldifLinesWithDN[i]);
            }
        }
    }
}
