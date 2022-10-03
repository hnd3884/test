package com.unboundid.ldap.sdk.unboundidds;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.unboundidds.extensions.SetSubtreeAccessibilityExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.SubtreeAccessibilityRestriction;
import com.unboundid.ldap.sdk.unboundidds.extensions.GetSubtreeAccessibilityExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.GetSubtreeAccessibilityExtendedResult;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.unboundidds.extensions.SubtreeAccessibilityState;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.unboundidds.extensions.EndInteractiveTransactionExtendedRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressReferentialIntegrityUpdatesRequestControl;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.unboundidds.controls.InteractiveTransactionSpecificationResponseControl;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.unboundidds.controls.RealAttributesOnlyRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeletedEntryAccessRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ReturnConflictEntriesRequestControl;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.InteractiveTransactionSpecificationRequestControl;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartInteractiveTransactionExtendedResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartInteractiveTransactionExtendedRequest;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Comparator;
import java.util.TreeSet;
import com.unboundid.util.ReverseComparator;
import com.unboundid.ldap.sdk.RootDSE;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.DN;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.MultiServerLDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class MoveSubtree extends MultiServerLDAPCommandLineTool implements UnsolicitedNotificationHandler, MoveSubtreeListener
{
    private static final String ATTR_STARTUP_UUID = "startupUUID";
    private BooleanArgument verbose;
    private DNArgument baseDN;
    private FileArgument baseDNFile;
    private IntegerArgument sizeLimit;
    private volatile String interruptMessage;
    private StringArgument purpose;
    
    public static void main(final String... args) {
        final ResultCode rc = main(args, System.out, System.err);
        if (rc != ResultCode.SUCCESS) {
            System.exit(Math.max(rc.intValue(), 255));
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream out, final OutputStream err) {
        final MoveSubtree moveSubtree = new MoveSubtree(out, err);
        return moveSubtree.runTool(args);
    }
    
    public MoveSubtree(final OutputStream out, final OutputStream err) {
        super(out, err, new String[] { "source", "target" }, null);
        this.verbose = null;
        this.baseDN = null;
        this.baseDNFile = null;
        this.sizeLimit = null;
        this.interruptMessage = null;
        this.purpose = null;
    }
    
    @Override
    public String getToolName() {
        return "move-subtree";
    }
    
    @Override
    public String getToolDescription() {
        return UnboundIDDSMessages.INFO_MOVE_SUBTREE_TOOL_DESCRIPTION.get();
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        (this.baseDN = new DNArgument('b', "baseDN", false, 0, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_BASE_DN_PLACEHOLDER.get(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_BASE_DN_DESCRIPTION.get())).addLongIdentifier("entryDN", true);
        parser.addArgument(this.baseDN);
        (this.baseDNFile = new FileArgument('f', "baseDNFile", false, 1, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_BASE_DN_FILE_PLACEHOLDER.get(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_BASE_DN_FILE_DESCRIPTION.get(), true, true, true, false)).addLongIdentifier("entryDNFile", true);
        parser.addArgument(this.baseDNFile);
        parser.addArgument(this.sizeLimit = new IntegerArgument('z', "sizeLimit", false, 1, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_SIZE_LIMIT_PLACEHOLDER.get(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_SIZE_LIMIT_DESCRIPTION.get(), 0, Integer.MAX_VALUE, 0));
        parser.addArgument(this.purpose = new StringArgument(null, "purpose", false, 1, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_PURPOSE_PLACEHOLDER.get(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_PURPOSE_DESCRIPTION.get()));
        parser.addArgument(this.verbose = new BooleanArgument('v', "verbose", 1, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ARG_VERBOSE_DESCRIPTION.get()));
        parser.addRequiredArgumentSet(this.baseDN, this.baseDNFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.baseDN, this.baseDNFile, new Argument[0]);
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUnsolicitedNotificationHandler(this);
        return options;
    }
    
    @Override
    protected boolean supportsOutputFile() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        List<String> baseDNs;
        if (this.baseDN.isPresent()) {
            final List<DN> dnList = this.baseDN.getValues();
            baseDNs = new ArrayList<String>(dnList.size());
            for (final DN dn : dnList) {
                baseDNs.add(dn.toString());
            }
        }
        else {
            try {
                baseDNs = this.baseDNFile.getNonBlankFileLines();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_ERROR_READING_BASE_DN_FILE.get(this.baseDNFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                return ResultCode.LOCAL_ERROR;
            }
            if (baseDNs.isEmpty()) {
                this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_BASE_DN_FILE_EMPTY.get(this.baseDNFile.getValue().getAbsolutePath()));
                return ResultCode.PARAM_ERROR;
            }
        }
        LDAPConnection sourceConnection = null;
        LDAPConnection targetConnection = null;
        try {
            try {
                sourceConnection = this.getConnection(0);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_CONNECT_TO_SOURCE.get(StaticUtils.getExceptionMessage(le)));
                return le.getResultCode();
            }
            try {
                targetConnection = this.getConnection(1);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_CONNECT_TO_TARGET.get(StaticUtils.getExceptionMessage(le)));
                return le.getResultCode();
            }
            sourceConnection.setConnectionName(UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get());
            targetConnection.setConnectionName(UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get());
            if (sourceConnection.getConnectedAddress().equals(targetConnection.getConnectedAddress()) && sourceConnection.getConnectedPort() == targetConnection.getConnectedPort()) {
                this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SAME_SOURCE_AND_TARGET_SERVERS.get());
                return ResultCode.PARAM_ERROR;
            }
            boolean suppressReferentialIntegrityUpdates = false;
            try {
                final RootDSE sourceRootDSE = sourceConnection.getRootDSE();
                final RootDSE targetRootDSE = targetConnection.getRootDSE();
                if (sourceRootDSE != null && targetRootDSE != null) {
                    final String sourceStartupUUID = sourceRootDSE.getAttributeValue("startupUUID");
                    final String targetStartupUUID = targetRootDSE.getAttributeValue("startupUUID");
                    if (sourceStartupUUID != null && sourceStartupUUID.equals(targetStartupUUID)) {
                        this.err(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SAME_SOURCE_AND_TARGET_SERVERS.get());
                        return ResultCode.PARAM_ERROR;
                    }
                }
                if (sourceRootDSE != null) {
                    suppressReferentialIntegrityUpdates = sourceRootDSE.supportsControl("1.3.6.1.4.1.30221.2.5.30");
                }
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
            boolean first = true;
            ResultCode resultCode = ResultCode.SUCCESS;
            for (final String dn2 : baseDNs) {
                if (first) {
                    first = false;
                }
                else {
                    this.out(new Object[0]);
                }
                OperationPurposeRequestControl operationPurpose;
                if (this.purpose.isPresent()) {
                    operationPurpose = new OperationPurposeRequestControl(this.getToolName(), this.getToolVersion(), 20, this.purpose.getValue());
                }
                else {
                    operationPurpose = null;
                }
                final MoveSubtreeResult result = moveSubtreeWithRestrictedAccessibility(this, sourceConnection, targetConnection, dn2, this.sizeLimit.getValue(), operationPurpose, suppressReferentialIntegrityUpdates, this.verbose.isPresent() ? this : null);
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    this.wrapOut(0, 79, UnboundIDDSMessages.INFO_MOVE_SUBTREE_RESULT_SUCCESSFUL.get(result.getEntriesAddedToTarget(), dn2));
                }
                else {
                    if (resultCode == ResultCode.SUCCESS) {
                        resultCode = result.getResultCode();
                    }
                    this.wrapErr(0, 79, UnboundIDDSMessages.ERR_MOVE_SUBTREE_RESULT_UNSUCCESSFUL.get());
                    if (result.getErrorMessage() != null) {
                        this.wrapErr(0, 79, UnboundIDDSMessages.ERR_MOVE_SUBTREE_ERROR_MESSAGE.get(result.getErrorMessage()));
                    }
                    if (result.getAdminActionRequired() == null) {
                        continue;
                    }
                    this.wrapErr(0, 79, UnboundIDDSMessages.ERR_MOVE_SUBTREE_ADMIN_ACTION.get(result.getAdminActionRequired()));
                }
            }
            return resultCode;
        }
        finally {
            if (sourceConnection != null) {
                sourceConnection.close();
            }
            if (targetConnection != null) {
                targetConnection.close();
            }
        }
    }
    
    public static MoveSubtreeResult moveEntryWithInteractiveTransaction(final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String entryDN, final OperationPurposeRequestControl opPurposeControl, final MoveSubtreeListener listener) {
        return moveEntryWithInteractiveTransaction(sourceConnection, targetConnection, entryDN, opPurposeControl, false, listener);
    }
    
    public static MoveSubtreeResult moveEntryWithInteractiveTransaction(final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String entryDN, final OperationPurposeRequestControl opPurposeControl, final boolean suppressRefInt, final MoveSubtreeListener listener) {
        final StringBuilder errorMsg = new StringBuilder();
        final StringBuilder adminMsg = new StringBuilder();
        final ReverseComparator<DN> reverseComparator = new ReverseComparator<DN>();
        final TreeSet<DN> sourceEntryDNs = new TreeSet<DN>(reverseComparator);
        final AtomicInteger entriesReadFromSource = new AtomicInteger(0);
        final AtomicInteger entriesAddedToTarget = new AtomicInteger(0);
        final AtomicInteger entriesDeletedFromSource = new AtomicInteger(0);
        final AtomicReference<ResultCode> resultCode = new AtomicReference<ResultCode>();
        ASN1OctetString sourceTxnID = null;
        ASN1OctetString targetTxnID = null;
        boolean sourceServerAltered = false;
        boolean targetServerAltered = false;
        try {
            InteractiveTransactionSpecificationRequestControl sourceTxnControl = null;
            try {
                StartInteractiveTransactionExtendedRequest startTxnRequest;
                if (opPurposeControl == null) {
                    startTxnRequest = new StartInteractiveTransactionExtendedRequest(entryDN);
                }
                else {
                    startTxnRequest = new StartInteractiveTransactionExtendedRequest(entryDN, new Control[] { opPurposeControl });
                }
                final StartInteractiveTransactionExtendedResult startTxnResult = (StartInteractiveTransactionExtendedResult)sourceConnection.processExtendedOperation(startTxnRequest);
                if (startTxnResult.getResultCode() == ResultCode.SUCCESS) {
                    sourceTxnID = startTxnResult.getTransactionID();
                    sourceTxnControl = new InteractiveTransactionSpecificationRequestControl(sourceTxnID, true, true);
                }
                else {
                    resultCode.compareAndSet(null, startTxnResult.getResultCode());
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_START_SOURCE_TXN.get(startTxnResult.getDiagnosticMessage()), errorMsg);
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                resultCode.compareAndSet(null, le.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_START_SOURCE_TXN.get(StaticUtils.getExceptionMessage(le)), errorMsg);
            }
            InteractiveTransactionSpecificationRequestControl targetTxnControl = null;
            try {
                StartInteractiveTransactionExtendedRequest startTxnRequest2;
                if (opPurposeControl == null) {
                    startTxnRequest2 = new StartInteractiveTransactionExtendedRequest(entryDN);
                }
                else {
                    startTxnRequest2 = new StartInteractiveTransactionExtendedRequest(entryDN, new Control[] { opPurposeControl });
                }
                final StartInteractiveTransactionExtendedResult startTxnResult2 = (StartInteractiveTransactionExtendedResult)targetConnection.processExtendedOperation(startTxnRequest2);
                if (startTxnResult2.getResultCode() == ResultCode.SUCCESS) {
                    targetTxnID = startTxnResult2.getTransactionID();
                    targetTxnControl = new InteractiveTransactionSpecificationRequestControl(targetTxnID, true, true);
                }
                else {
                    resultCode.compareAndSet(null, startTxnResult2.getResultCode());
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_START_TARGET_TXN.get(startTxnResult2.getDiagnosticMessage()), errorMsg);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                resultCode.compareAndSet(null, le2.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_START_TARGET_TXN.get(StaticUtils.getExceptionMessage(le2)), errorMsg);
            }
            Control[] searchControls;
            if (opPurposeControl == null) {
                searchControls = new Control[] { sourceTxnControl, new ManageDsaITRequestControl(true), new SubentriesRequestControl(true), new ReturnConflictEntriesRequestControl(true), new SoftDeletedEntryAccessRequestControl(true, true, false), new RealAttributesOnlyRequestControl(true) };
            }
            else {
                searchControls = new Control[] { sourceTxnControl, new ManageDsaITRequestControl(true), new SubentriesRequestControl(true), new ReturnConflictEntriesRequestControl(true), new SoftDeletedEntryAccessRequestControl(true, true, false), new RealAttributesOnlyRequestControl(true), opPurposeControl };
            }
            final MoveSubtreeTxnSearchListener searchListener = new MoveSubtreeTxnSearchListener(targetConnection, resultCode, errorMsg, entriesReadFromSource, entriesAddedToTarget, sourceEntryDNs, targetTxnControl, opPurposeControl, listener);
            final SearchRequest searchRequest = new SearchRequest(searchListener, searchControls, entryDN, SearchScope.SUB, DereferencePolicy.NEVER, 1, 0, false, Filter.createPresenceFilter("objectClass"), new String[] { "*", "+" });
            SearchResult searchResult;
            try {
                searchResult = sourceConnection.search(searchRequest);
            }
            catch (final LDAPSearchException lse) {
                Debug.debugException(lse);
                searchResult = lse.getSearchResult();
            }
            Label_3372: {
                if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                    try {
                        final InteractiveTransactionSpecificationResponseControl txnResult = InteractiveTransactionSpecificationResponseControl.get(searchResult);
                        if (txnResult == null || !txnResult.transactionValid()) {
                            resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                            append(UnboundIDDSMessages.ERR_MOVE_ENTRY_SEARCH_TXN_NO_LONGER_VALID.get(), errorMsg);
                        }
                        break Label_3372;
                    }
                    catch (final LDAPException le3) {
                        Debug.debugException(le3);
                        resultCode.compareAndSet(null, le3.getResultCode());
                        append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_DECODE_SEARCH_TXN_CONTROL.get(StaticUtils.getExceptionMessage(le3)), errorMsg);
                    }
                }
                resultCode.compareAndSet(null, searchResult.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SEARCH_FAILED.get(entryDN, searchResult.getDiagnosticMessage()), errorMsg);
                try {
                    final InteractiveTransactionSpecificationResponseControl txnResult = InteractiveTransactionSpecificationResponseControl.get(searchResult);
                    if (txnResult != null && !txnResult.transactionValid()) {
                        sourceTxnID = null;
                    }
                }
                catch (final LDAPException le3) {
                    Debug.debugException(le3);
                }
                if (!searchListener.targetTransactionValid()) {
                    targetTxnID = null;
                }
            }
            if (resultCode.get() == null) {
                targetServerAltered = true;
            }
            final ArrayList<Control> deleteControlList = new ArrayList<Control>(4);
            deleteControlList.add(sourceTxnControl);
            deleteControlList.add(new ManageDsaITRequestControl(true));
            if (opPurposeControl != null) {
                deleteControlList.add(opPurposeControl);
            }
            if (suppressRefInt) {
                deleteControlList.add(new SuppressReferentialIntegrityUpdatesRequestControl(false));
            }
            final Control[] deleteControls = new Control[deleteControlList.size()];
            deleteControlList.toArray(deleteControls);
            for (final DN dn : sourceEntryDNs) {
                if (listener != null) {
                    try {
                        listener.doPreDeleteProcessing(dn);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                        append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_PRE_DELETE_FAILURE.get(dn.toString(), StaticUtils.getExceptionMessage(e)), errorMsg);
                    }
                }
                LDAPResult deleteResult;
                try {
                    deleteResult = sourceConnection.delete(new DeleteRequest(dn, deleteControls));
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                    deleteResult = le4.toLDAPResult();
                }
                Label_5611: {
                    if (deleteResult.getResultCode() == ResultCode.SUCCESS) {
                        sourceServerAltered = true;
                        entriesDeletedFromSource.incrementAndGet();
                        try {
                            final InteractiveTransactionSpecificationResponseControl txnResult2 = InteractiveTransactionSpecificationResponseControl.get(deleteResult);
                            if (txnResult2 == null || !txnResult2.transactionValid()) {
                                resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_DELETE_TXN_NO_LONGER_VALID.get(dn.toString()), errorMsg);
                            }
                            break Label_5611;
                        }
                        catch (final LDAPException le4) {
                            Debug.debugException(le4);
                            resultCode.compareAndSet(null, le4.getResultCode());
                            append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_DECODE_DELETE_TXN_CONTROL.get(dn.toString(), StaticUtils.getExceptionMessage(le4)), errorMsg);
                        }
                    }
                    resultCode.compareAndSet(null, deleteResult.getResultCode());
                    append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_DELETE_FAILURE.get(dn.toString(), deleteResult.getDiagnosticMessage()), errorMsg);
                    try {
                        final InteractiveTransactionSpecificationResponseControl txnResult2 = InteractiveTransactionSpecificationResponseControl.get(deleteResult);
                        if (txnResult2 != null && !txnResult2.transactionValid()) {
                            sourceTxnID = null;
                        }
                    }
                    catch (final LDAPException le4) {
                        Debug.debugException(le4);
                    }
                }
                if (listener != null) {
                    try {
                        listener.doPostDeleteProcessing(dn);
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                        append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_POST_DELETE_FAILURE.get(dn.toString(), StaticUtils.getExceptionMessage(e2)), errorMsg);
                    }
                }
            }
            try {
                EndInteractiveTransactionExtendedRequest commitRequest;
                if (opPurposeControl == null) {
                    commitRequest = new EndInteractiveTransactionExtendedRequest(targetTxnID, true);
                }
                else {
                    commitRequest = new EndInteractiveTransactionExtendedRequest(targetTxnID, true, new Control[] { opPurposeControl });
                }
                final ExtendedResult commitResult = targetConnection.processExtendedOperation(commitRequest);
                if (commitResult.getResultCode() == ResultCode.SUCCESS) {
                    targetTxnID = null;
                }
                else {
                    resultCode.compareAndSet(null, commitResult.getResultCode());
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_COMMIT_TARGET_TXN.get(commitResult.getDiagnosticMessage()), errorMsg);
                }
            }
            catch (final LDAPException le5) {
                Debug.debugException(le5);
                resultCode.compareAndSet(null, le5.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_COMMIT_TARGET_TXN.get(StaticUtils.getExceptionMessage(le5)), errorMsg);
            }
            try {
                EndInteractiveTransactionExtendedRequest commitRequest;
                if (opPurposeControl == null) {
                    commitRequest = new EndInteractiveTransactionExtendedRequest(sourceTxnID, true);
                }
                else {
                    commitRequest = new EndInteractiveTransactionExtendedRequest(sourceTxnID, true, new Control[] { opPurposeControl });
                }
                final ExtendedResult commitResult = sourceConnection.processExtendedOperation(commitRequest);
                if (commitResult.getResultCode() == ResultCode.SUCCESS) {
                    sourceTxnID = null;
                }
                else {
                    resultCode.compareAndSet(null, commitResult.getResultCode());
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_COMMIT_SOURCE_TXN.get(commitResult.getDiagnosticMessage()), errorMsg);
                }
            }
            catch (final LDAPException le5) {
                Debug.debugException(le5);
                resultCode.compareAndSet(null, le5.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_CANNOT_COMMIT_SOURCE_TXN.get(StaticUtils.getExceptionMessage(le5)), errorMsg);
                append(UnboundIDDSMessages.ERR_MOVE_ENTRY_EXISTS_IN_BOTH_SERVERS.get(entryDN), adminMsg);
            }
        }
        finally {
            if (targetTxnID != null) {
                try {
                    EndInteractiveTransactionExtendedRequest abortRequest;
                    if (opPurposeControl == null) {
                        abortRequest = new EndInteractiveTransactionExtendedRequest(targetTxnID, false);
                    }
                    else {
                        abortRequest = new EndInteractiveTransactionExtendedRequest(targetTxnID, false, new Control[] { opPurposeControl });
                    }
                    final ExtendedResult abortResult = targetConnection.processExtendedOperation(abortRequest);
                    if (abortResult.getResultCode() == ResultCode.INTERACTIVE_TRANSACTION_ABORTED) {
                        targetServerAltered = false;
                        entriesAddedToTarget.set(0);
                        append(UnboundIDDSMessages.INFO_MOVE_ENTRY_TARGET_ABORT_SUCCEEDED.get(), errorMsg);
                    }
                    else {
                        append(UnboundIDDSMessages.ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE.get(abortResult.getDiagnosticMessage()), errorMsg);
                        append(UnboundIDDSMessages.ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE_ADMIN_ACTION.get(entryDN), adminMsg);
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE.get(StaticUtils.getExceptionMessage(e3)), errorMsg);
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_TARGET_ABORT_FAILURE_ADMIN_ACTION.get(entryDN), adminMsg);
                }
            }
            if (sourceTxnID != null) {
                try {
                    EndInteractiveTransactionExtendedRequest abortRequest;
                    if (opPurposeControl == null) {
                        abortRequest = new EndInteractiveTransactionExtendedRequest(sourceTxnID, false);
                    }
                    else {
                        abortRequest = new EndInteractiveTransactionExtendedRequest(sourceTxnID, false, new Control[] { opPurposeControl });
                    }
                    final ExtendedResult abortResult = sourceConnection.processExtendedOperation(abortRequest);
                    if (abortResult.getResultCode() == ResultCode.INTERACTIVE_TRANSACTION_ABORTED) {
                        sourceServerAltered = false;
                        entriesDeletedFromSource.set(0);
                        append(UnboundIDDSMessages.INFO_MOVE_ENTRY_SOURCE_ABORT_SUCCEEDED.get(), errorMsg);
                    }
                    else {
                        append(UnboundIDDSMessages.ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE.get(abortResult.getDiagnosticMessage()), errorMsg);
                        append(UnboundIDDSMessages.ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE_ADMIN_ACTION.get(entryDN), adminMsg);
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE.get(StaticUtils.getExceptionMessage(e3)), errorMsg);
                    append(UnboundIDDSMessages.ERR_MOVE_ENTRY_SOURCE_ABORT_FAILURE_ADMIN_ACTION.get(entryDN), adminMsg);
                }
            }
        }
        Label_8529: {
            resultCode.compareAndSet(null, ResultCode.SUCCESS);
        }
        String errorMessage;
        if (errorMsg.length() > 0) {
            errorMessage = errorMsg.toString();
        }
        else {
            errorMessage = null;
        }
        String adminActionRequired;
        if (adminMsg.length() > 0) {
            adminActionRequired = adminMsg.toString();
        }
        else {
            adminActionRequired = null;
        }
        return new MoveSubtreeResult(resultCode.get(), errorMessage, adminActionRequired, sourceServerAltered, targetServerAltered, entriesReadFromSource.get(), entriesAddedToTarget.get(), entriesDeletedFromSource.get());
    }
    
    public static MoveSubtreeResult moveSubtreeWithRestrictedAccessibility(final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String baseDN, final int sizeLimit, final OperationPurposeRequestControl opPurposeControl, final MoveSubtreeListener listener) {
        return moveSubtreeWithRestrictedAccessibility(sourceConnection, targetConnection, baseDN, sizeLimit, opPurposeControl, false, listener);
    }
    
    public static MoveSubtreeResult moveSubtreeWithRestrictedAccessibility(final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String baseDN, final int sizeLimit, final OperationPurposeRequestControl opPurposeControl, final boolean suppressRefInt, final MoveSubtreeListener listener) {
        return moveSubtreeWithRestrictedAccessibility(null, sourceConnection, targetConnection, baseDN, sizeLimit, opPurposeControl, suppressRefInt, listener);
    }
    
    private static MoveSubtreeResult moveSubtreeWithRestrictedAccessibility(final MoveSubtree tool, final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String baseDN, final int sizeLimit, final OperationPurposeRequestControl opPurposeControl, final boolean suppressRefInt, final MoveSubtreeListener listener) {
        final MoveSubtreeResult initialAccessibilityResult = checkInitialAccessibility(sourceConnection, targetConnection, baseDN, opPurposeControl);
        if (initialAccessibilityResult != null) {
            return initialAccessibilityResult;
        }
        final StringBuilder errorMsg = new StringBuilder();
        final StringBuilder adminMsg = new StringBuilder();
        final ReverseComparator<DN> reverseComparator = new ReverseComparator<DN>();
        final TreeSet<DN> sourceEntryDNs = new TreeSet<DN>(reverseComparator);
        final AtomicInteger entriesReadFromSource = new AtomicInteger(0);
        final AtomicInteger entriesAddedToTarget = new AtomicInteger(0);
        final AtomicInteger entriesDeletedFromSource = new AtomicInteger(0);
        final AtomicReference<ResultCode> resultCode = new AtomicReference<ResultCode>();
        boolean sourceServerAltered = false;
        boolean targetServerAltered = false;
        SubtreeAccessibilityState currentSourceState = SubtreeAccessibilityState.ACCESSIBLE;
        SubtreeAccessibilityState currentTargetState = SubtreeAccessibilityState.ACCESSIBLE;
        Label_1261: {
            String sourceUserDN;
            String targetUserDN;
            try {
                sourceUserDN = getAuthenticatedUserDN(sourceConnection, true, opPurposeControl);
                targetUserDN = getAuthenticatedUserDN(targetConnection, false, opPurposeControl);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                resultCode.compareAndSet(null, le.getResultCode());
                append(le.getMessage(), errorMsg);
                break Label_1261;
            }
            try {
                setAccessibility(targetConnection, false, baseDN, SubtreeAccessibilityState.HIDDEN, targetUserDN, opPurposeControl);
                currentTargetState = SubtreeAccessibilityState.HIDDEN;
                setInterruptMessage(tool, UnboundIDDSMessages.WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_HIDDEN.get(baseDN, targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()));
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                resultCode.compareAndSet(null, le.getResultCode());
                append(le.getMessage(), errorMsg);
                break Label_1261;
            }
            try {
                setAccessibility(sourceConnection, true, baseDN, SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED, sourceUserDN, opPurposeControl);
                currentSourceState = SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED;
                setInterruptMessage(tool, UnboundIDDSMessages.WARN_MOVE_SUBTREE_INTERRUPT_MSG_SOURCE_READ_ONLY.get(baseDN, targetConnection.getConnectedAddress(), targetConnection.getConnectedPort(), sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort()));
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                resultCode.compareAndSet(null, le.getResultCode());
                append(le.getMessage(), errorMsg);
                break Label_1261;
            }
            Control[] searchControls;
            if (opPurposeControl == null) {
                searchControls = new Control[] { new ManageDsaITRequestControl(true), new SubentriesRequestControl(true), new ReturnConflictEntriesRequestControl(true), new SoftDeletedEntryAccessRequestControl(true, true, false), new RealAttributesOnlyRequestControl(true) };
            }
            else {
                searchControls = new Control[] { new ManageDsaITRequestControl(true), new SubentriesRequestControl(true), new ReturnConflictEntriesRequestControl(true), new SoftDeletedEntryAccessRequestControl(true, true, false), new RealAttributesOnlyRequestControl(true), opPurposeControl };
            }
            final MoveSubtreeAccessibilitySearchListener searchListener = new MoveSubtreeAccessibilitySearchListener(tool, baseDN, sourceConnection, targetConnection, resultCode, errorMsg, entriesReadFromSource, entriesAddedToTarget, sourceEntryDNs, opPurposeControl, listener);
            final SearchRequest searchRequest = new SearchRequest(searchListener, searchControls, baseDN, SearchScope.SUB, DereferencePolicy.NEVER, sizeLimit, 0, false, Filter.createPresenceFilter("objectClass"), new String[] { "*", "+" });
            SearchResult searchResult;
            try {
                searchResult = sourceConnection.search(searchRequest);
            }
            catch (final LDAPSearchException lse) {
                Debug.debugException(lse);
                searchResult = lse.getSearchResult();
            }
            if (entriesAddedToTarget.get() > 0) {
                targetServerAltered = true;
            }
            if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                resultCode.compareAndSet(null, searchResult.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SEARCH_FAILED.get(baseDN, searchResult.getDiagnosticMessage()), errorMsg);
                final AtomicInteger deleteCount = new AtomicInteger(0);
                if (targetServerAltered) {
                    deleteEntries(targetConnection, false, sourceEntryDNs, opPurposeControl, false, null, deleteCount, resultCode, errorMsg);
                    entriesAddedToTarget.addAndGet(0 - deleteCount.get());
                    if (entriesAddedToTarget.get() == 0) {
                        targetServerAltered = false;
                    }
                    else {
                        append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TARGET_NOT_DELETED_ADMIN_ACTION.get(baseDN), adminMsg);
                    }
                }
            }
            else if (resultCode.get() != null) {
                final AtomicInteger deleteCount = new AtomicInteger(0);
                if (targetServerAltered) {
                    deleteEntries(targetConnection, false, sourceEntryDNs, opPurposeControl, false, null, deleteCount, resultCode, errorMsg);
                    entriesAddedToTarget.addAndGet(0 - deleteCount.get());
                    if (entriesAddedToTarget.get() == 0) {
                        targetServerAltered = false;
                    }
                    else {
                        append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TARGET_NOT_DELETED_ADMIN_ACTION.get(baseDN), adminMsg);
                    }
                }
            }
            else {
                try {
                    setAccessibility(targetConnection, true, baseDN, SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED, targetUserDN, opPurposeControl);
                    currentTargetState = SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED;
                    setInterruptMessage(tool, UnboundIDDSMessages.WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_READ_ONLY.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()));
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    resultCode.compareAndSet(null, le2.getResultCode());
                    append(le2.getMessage(), errorMsg);
                    break Label_1261;
                }
                try {
                    setAccessibility(sourceConnection, true, baseDN, SubtreeAccessibilityState.HIDDEN, sourceUserDN, opPurposeControl);
                    currentSourceState = SubtreeAccessibilityState.HIDDEN;
                    setInterruptMessage(tool, UnboundIDDSMessages.WARN_MOVE_SUBTREE_INTERRUPT_MSG_SOURCE_HIDDEN.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()));
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    resultCode.compareAndSet(null, le2.getResultCode());
                    append(le2.getMessage(), errorMsg);
                    break Label_1261;
                }
                try {
                    setAccessibility(targetConnection, true, baseDN, SubtreeAccessibilityState.ACCESSIBLE, targetUserDN, opPurposeControl);
                    currentTargetState = SubtreeAccessibilityState.ACCESSIBLE;
                    setInterruptMessage(tool, UnboundIDDSMessages.WARN_MOVE_SUBTREE_INTERRUPT_MSG_TARGET_ACCESSIBLE.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()));
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    resultCode.compareAndSet(null, le2.getResultCode());
                    append(le2.getMessage(), errorMsg);
                    break Label_1261;
                }
                final boolean deleteSuccessful = deleteEntries(sourceConnection, true, sourceEntryDNs, opPurposeControl, suppressRefInt, listener, entriesDeletedFromSource, resultCode, errorMsg);
                sourceServerAltered = (entriesDeletedFromSource.get() != 0);
                if (!deleteSuccessful) {
                    append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SOURCE_NOT_DELETED_ADMIN_ACTION.get(baseDN), adminMsg);
                }
                else {
                    try {
                        setAccessibility(sourceConnection, true, baseDN, SubtreeAccessibilityState.ACCESSIBLE, sourceUserDN, opPurposeControl);
                        currentSourceState = SubtreeAccessibilityState.ACCESSIBLE;
                        setInterruptMessage(tool, null);
                    }
                    catch (final LDAPException le3) {
                        Debug.debugException(le3);
                        resultCode.compareAndSet(null, le3.getResultCode());
                        append(le3.getMessage(), errorMsg);
                    }
                }
            }
        }
        if (currentSourceState != SubtreeAccessibilityState.ACCESSIBLE) {
            if (!sourceServerAltered) {
                try {
                    setAccessibility(sourceConnection, true, baseDN, SubtreeAccessibilityState.ACCESSIBLE, null, opPurposeControl);
                    currentSourceState = SubtreeAccessibilityState.ACCESSIBLE;
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                }
            }
            if (currentSourceState != SubtreeAccessibilityState.ACCESSIBLE) {
                append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_SOURCE_LEFT_INACCESSIBLE.get(currentSourceState, baseDN), adminMsg);
            }
        }
        if (currentTargetState != SubtreeAccessibilityState.ACCESSIBLE) {
            if (!targetServerAltered) {
                try {
                    setAccessibility(targetConnection, false, baseDN, SubtreeAccessibilityState.ACCESSIBLE, null, opPurposeControl);
                    currentTargetState = SubtreeAccessibilityState.ACCESSIBLE;
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                }
            }
            if (currentTargetState != SubtreeAccessibilityState.ACCESSIBLE) {
                append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TARGET_LEFT_INACCESSIBLE.get(currentTargetState, baseDN), adminMsg);
            }
        }
        resultCode.compareAndSet(null, ResultCode.SUCCESS);
        String errorMessage;
        if (errorMsg.length() > 0) {
            errorMessage = errorMsg.toString();
        }
        else {
            errorMessage = null;
        }
        String adminActionRequired;
        if (adminMsg.length() > 0) {
            adminActionRequired = adminMsg.toString();
        }
        else {
            adminActionRequired = null;
        }
        return new MoveSubtreeResult(resultCode.get(), errorMessage, adminActionRequired, sourceServerAltered, targetServerAltered, entriesReadFromSource.get(), entriesAddedToTarget.get(), entriesDeletedFromSource.get());
    }
    
    private static String getAuthenticatedUserDN(final LDAPConnection connection, final boolean isSource, final OperationPurposeRequestControl opPurposeControl) throws LDAPException {
        final BindRequest bindRequest = InternalSDKHelper.getLastBindRequest(connection);
        if (bindRequest != null && bindRequest instanceof SimpleBindRequest) {
            final SimpleBindRequest r = (SimpleBindRequest)bindRequest;
            return r.getBindDN();
        }
        Control[] controls;
        if (opPurposeControl == null) {
            controls = StaticUtils.NO_CONTROLS;
        }
        else {
            controls = new Control[] { opPurposeControl };
        }
        final String connectionName = isSource ? UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get() : UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get();
        WhoAmIExtendedResult whoAmIResult;
        try {
            whoAmIResult = (WhoAmIExtendedResult)connection.processExtendedOperation(new WhoAmIExtendedRequest(controls));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(le.getResultCode(), UnboundIDDSMessages.ERR_MOVE_SUBTREE_ERROR_INVOKING_WHO_AM_I.get(connectionName, StaticUtils.getExceptionMessage(le)), le);
        }
        if (whoAmIResult.getResultCode() != ResultCode.SUCCESS) {
            throw new LDAPException(whoAmIResult.getResultCode(), UnboundIDDSMessages.ERR_MOVE_SUBTREE_ERROR_INVOKING_WHO_AM_I.get(connectionName, whoAmIResult.getDiagnosticMessage()));
        }
        final String authzID = whoAmIResult.getAuthorizationID();
        if (authzID != null && authzID.startsWith("dn:")) {
            return authzID.substring(3);
        }
        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_IDENTIFY_CONNECTED_USER.get(connectionName));
    }
    
    private static MoveSubtreeResult checkInitialAccessibility(final LDAPConnection sourceConnection, final LDAPConnection targetConnection, final String baseDN, final OperationPurposeRequestControl opPurposeControl) {
        DN parsedBaseDN;
        try {
            parsedBaseDN = new DN(baseDN);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new MoveSubtreeResult(ResultCode.INVALID_DN_SYNTAX, UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_PARSE_BASE_DN.get(baseDN, StaticUtils.getExceptionMessage(e)), null, false, false, 0, 0, 0);
        }
        Control[] controls;
        if (opPurposeControl == null) {
            controls = StaticUtils.NO_CONTROLS;
        }
        else {
            controls = new Control[] { opPurposeControl };
        }
        GetSubtreeAccessibilityExtendedResult sourceResult;
        try {
            sourceResult = (GetSubtreeAccessibilityExtendedResult)sourceConnection.processExtendedOperation(new GetSubtreeAccessibilityExtendedRequest(controls));
            if (sourceResult.getResultCode() != ResultCode.SUCCESS) {
                throw new LDAPException(sourceResult);
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return new MoveSubtreeResult(le.getResultCode(), UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_GET_ACCESSIBILITY_STATE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get(), le.getMessage()), null, false, false, 0, 0, 0);
        }
        boolean sourceMatch = false;
        String sourceMessage = null;
        SubtreeAccessibilityRestriction sourceRestriction = null;
        final List<SubtreeAccessibilityRestriction> sourceRestrictions = sourceResult.getAccessibilityRestrictions();
        if (sourceRestrictions != null) {
            for (final SubtreeAccessibilityRestriction r : sourceRestrictions) {
                if (r.getAccessibilityState() == SubtreeAccessibilityState.ACCESSIBLE) {
                    continue;
                }
                DN restrictionDN;
                try {
                    restrictionDN = new DN(r.getSubtreeBaseDN());
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new MoveSubtreeResult(ResultCode.INVALID_DN_SYNTAX, UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_PARSE_RESTRICTION_BASE_DN.get(r.getSubtreeBaseDN(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get(), r.toString(), StaticUtils.getExceptionMessage(e2)), null, false, false, 0, 0, 0);
                }
                if (restrictionDN.equals(parsedBaseDN)) {
                    sourceMatch = true;
                    sourceRestriction = r;
                    sourceMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_NOT_ACCESSIBLE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get(), r.getAccessibilityState().getStateName());
                    break;
                }
                if (restrictionDN.isAncestorOf(parsedBaseDN, false)) {
                    sourceRestriction = r;
                    sourceMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_WITHIN_UNACCESSIBLE_TREE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get(), r.getSubtreeBaseDN(), r.getAccessibilityState().getStateName());
                    break;
                }
                if (restrictionDN.isDescendantOf(parsedBaseDN, false)) {
                    sourceRestriction = r;
                    sourceMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_CONTAINS_UNACCESSIBLE_TREE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get(), r.getSubtreeBaseDN(), r.getAccessibilityState().getStateName());
                    break;
                }
            }
        }
        GetSubtreeAccessibilityExtendedResult targetResult;
        try {
            targetResult = (GetSubtreeAccessibilityExtendedResult)targetConnection.processExtendedOperation(new GetSubtreeAccessibilityExtendedRequest(controls));
            if (targetResult.getResultCode() != ResultCode.SUCCESS) {
                throw new LDAPException(targetResult);
            }
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            return new MoveSubtreeResult(le2.getResultCode(), UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_GET_ACCESSIBILITY_STATE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get(), le2.getMessage()), null, false, false, 0, 0, 0);
        }
        boolean targetMatch = false;
        String targetMessage = null;
        SubtreeAccessibilityRestriction targetRestriction = null;
        final List<SubtreeAccessibilityRestriction> targetRestrictions = targetResult.getAccessibilityRestrictions();
        if (targetRestrictions != null) {
            for (final SubtreeAccessibilityRestriction r2 : targetRestrictions) {
                if (r2.getAccessibilityState() == SubtreeAccessibilityState.ACCESSIBLE) {
                    continue;
                }
                DN restrictionDN2;
                try {
                    restrictionDN2 = new DN(r2.getSubtreeBaseDN());
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    return new MoveSubtreeResult(ResultCode.INVALID_DN_SYNTAX, UnboundIDDSMessages.ERR_MOVE_SUBTREE_CANNOT_PARSE_RESTRICTION_BASE_DN.get(r2.getSubtreeBaseDN(), UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get(), r2.toString(), StaticUtils.getExceptionMessage(e3)), null, false, false, 0, 0, 0);
                }
                if (restrictionDN2.equals(parsedBaseDN)) {
                    targetMatch = true;
                    targetRestriction = r2;
                    targetMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_NOT_ACCESSIBLE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get(), r2.getAccessibilityState().getStateName());
                    break;
                }
                if (restrictionDN2.isAncestorOf(parsedBaseDN, false)) {
                    targetRestriction = r2;
                    targetMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_WITHIN_UNACCESSIBLE_TREE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get(), r2.getSubtreeBaseDN(), r2.getAccessibilityState().getStateName());
                    break;
                }
                if (restrictionDN2.isDescendantOf(parsedBaseDN, false)) {
                    targetRestriction = r2;
                    targetMessage = UnboundIDDSMessages.ERR_MOVE_SUBTREE_CONTAINS_UNACCESSIBLE_TREE.get(baseDN, UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get(), r2.getSubtreeBaseDN(), r2.getAccessibilityState().getStateName());
                    break;
                }
            }
        }
        if (sourceRestriction == null && targetRestriction == null) {
            return null;
        }
        if (sourceMatch || targetMatch) {
            if (sourceRestriction != null && sourceRestriction.getAccessibilityState().isReadOnly() && targetRestriction != null && targetRestriction.getAccessibilityState().isHidden()) {
                return new MoveSubtreeResult(ResultCode.UNWILLING_TO_PERFORM, UnboundIDDSMessages.ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_ADDS.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()), UnboundIDDSMessages.ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_ADDS_ADMIN_MSG.get(), false, false, 0, 0, 0);
            }
            if (sourceRestriction != null && sourceRestriction.getAccessibilityState().isHidden() && targetRestriction == null) {
                return new MoveSubtreeResult(ResultCode.UNWILLING_TO_PERFORM, UnboundIDDSMessages.ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_DELETES.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort()), UnboundIDDSMessages.ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED_IN_DELETES_ADMIN_MSG.get(), false, false, 0, 0, 0);
            }
        }
        final StringBuilder details = new StringBuilder();
        if (sourceMessage != null) {
            details.append(sourceMessage);
        }
        if (targetMessage != null) {
            append(targetMessage, details);
        }
        return new MoveSubtreeResult(ResultCode.UNWILLING_TO_PERFORM, UnboundIDDSMessages.ERR_MOVE_SUBTREE_POSSIBLY_INTERRUPTED.get(baseDN, sourceConnection.getConnectedAddress(), sourceConnection.getConnectedPort(), targetConnection.getConnectedAddress(), targetConnection.getConnectedPort(), details.toString()), null, false, false, 0, 0, 0);
    }
    
    private static void setAccessibility(final LDAPConnection connection, final boolean isSource, final String baseDN, final SubtreeAccessibilityState state, final String bypassDN, final OperationPurposeRequestControl opPurposeControl) throws LDAPException {
        final String connectionName = isSource ? UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_SOURCE.get() : UnboundIDDSMessages.INFO_MOVE_SUBTREE_CONNECTION_NAME_TARGET.get();
        Control[] controls;
        if (opPurposeControl == null) {
            controls = StaticUtils.NO_CONTROLS;
        }
        else {
            controls = new Control[] { opPurposeControl };
        }
        SetSubtreeAccessibilityExtendedRequest request = null;
        switch (state) {
            case ACCESSIBLE: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetAccessibleRequest(baseDN, controls);
                break;
            }
            case READ_ONLY_BIND_ALLOWED: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetReadOnlyRequest(baseDN, true, bypassDN, controls);
                break;
            }
            case READ_ONLY_BIND_DENIED: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetReadOnlyRequest(baseDN, false, bypassDN, controls);
                break;
            }
            case HIDDEN: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetHiddenRequest(baseDN, bypassDN, controls);
                break;
            }
            default: {
                throw new LDAPException(ResultCode.PARAM_ERROR, UnboundIDDSMessages.ERR_MOVE_SUBTREE_UNSUPPORTED_ACCESSIBILITY_STATE.get(state.getStateName(), baseDN, connectionName));
            }
        }
        LDAPResult result;
        try {
            result = connection.processExtendedOperation(request);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            result = le.toLDAPResult();
        }
        if (result.getResultCode() != ResultCode.SUCCESS) {
            throw new LDAPException(result.getResultCode(), UnboundIDDSMessages.ERR_MOVE_SUBTREE_ERROR_SETTING_ACCESSIBILITY.get(state.getStateName(), baseDN, connectionName, result.getDiagnosticMessage()));
        }
    }
    
    static void setInterruptMessage(final MoveSubtree tool, final String message) {
        if (tool != null) {
            tool.interruptMessage = message;
        }
    }
    
    private static boolean deleteEntries(final LDAPConnection connection, final boolean isSource, final TreeSet<DN> entryDNs, final OperationPurposeRequestControl opPurposeControl, final boolean suppressRefInt, final MoveSubtreeListener listener, final AtomicInteger deleteCount, final AtomicReference<ResultCode> resultCode, final StringBuilder errorMsg) {
        final ArrayList<Control> deleteControlList = new ArrayList<Control>(3);
        deleteControlList.add(new ManageDsaITRequestControl(true));
        if (opPurposeControl != null) {
            deleteControlList.add(opPurposeControl);
        }
        if (suppressRefInt) {
            deleteControlList.add(new SuppressReferentialIntegrityUpdatesRequestControl(false));
        }
        final Control[] deleteControls = new Control[deleteControlList.size()];
        deleteControlList.toArray(deleteControls);
        boolean successful = true;
        for (final DN dn : entryDNs) {
            if (isSource && listener != null) {
                try {
                    listener.doPreDeleteProcessing(dn);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                    append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_PRE_DELETE_FAILURE.get(dn.toString(), StaticUtils.getExceptionMessage(e)), errorMsg);
                    successful = false;
                    continue;
                }
            }
            LDAPResult deleteResult;
            try {
                deleteResult = connection.delete(new DeleteRequest(dn, deleteControls));
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                deleteResult = le.toLDAPResult();
            }
            if (deleteResult.getResultCode() == ResultCode.SUCCESS) {
                deleteCount.incrementAndGet();
                if (!isSource || listener == null) {
                    continue;
                }
                try {
                    listener.doPostDeleteProcessing(dn);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                    append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_POST_DELETE_FAILURE.get(dn.toString(), StaticUtils.getExceptionMessage(e2)), errorMsg);
                    successful = false;
                }
            }
            else {
                resultCode.compareAndSet(null, deleteResult.getResultCode());
                append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_DELETE_FAILURE.get(dn.toString(), deleteResult.getDiagnosticMessage()), errorMsg);
                successful = false;
            }
        }
        return successful;
    }
    
    static void append(final String message, final StringBuilder buffer) {
        if (message != null) {
            if (buffer.length() > 0) {
                buffer.append("  ");
            }
            buffer.append(message);
        }
    }
    
    @Override
    public void handleUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        this.wrapOut(0, 79, UnboundIDDSMessages.INFO_MOVE_SUBTREE_UNSOLICITED_NOTIFICATION.get(notification.getOID(), connection.getConnectionName(), notification.getResultCode(), notification.getDiagnosticMessage()));
    }
    
    @Override
    public ReadOnlyEntry doPreAddProcessing(final ReadOnlyEntry entry) {
        return entry;
    }
    
    @Override
    public void doPostAddProcessing(final ReadOnlyEntry entry) {
        this.wrapOut(0, 79, UnboundIDDSMessages.INFO_MOVE_SUBTREE_ADD_SUCCESSFUL.get(entry.getDN()));
    }
    
    @Override
    public void doPreDeleteProcessing(final DN entryDN) {
    }
    
    @Override
    public void doPostDeleteProcessing(final DN entryDN) {
        this.wrapOut(0, 79, UnboundIDDSMessages.INFO_MOVE_SUBTREE_DELETE_SUCCESSFUL.get(entryDN.toString()));
    }
    
    @Override
    protected boolean registerShutdownHook() {
        return true;
    }
    
    @Override
    protected void doShutdownHookProcessing(final ResultCode resultCode) {
        if (resultCode != null) {
            return;
        }
        this.wrapErr(0, 79, this.interruptMessage);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--sourceHostname", "ds1.example.com", "--sourcePort", "389", "--sourceBindDN", "uid=admin,dc=example,dc=com", "--sourceBindPassword", "password", "--targetHostname", "ds2.example.com", "--targetPort", "389", "--targetBindDN", "uid=admin,dc=example,dc=com", "--targetBindPassword", "password", "--baseDN", "cn=small subtree,dc=example,dc=com", "--sizeLimit", "100", "--purpose", "Migrate a small subtree from ds1 to ds2" };
        exampleMap.put(args, UnboundIDDSMessages.INFO_MOVE_SUBTREE_EXAMPLE_DESCRIPTION.get());
        return exampleMap;
    }
}
