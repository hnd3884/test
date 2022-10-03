package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.unboundidds.controls.InteractiveTransactionSpecificationResponseControl;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.unboundidds.controls.IgnoreNoUserModificationRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.InteractiveTransactionSpecificationRequestControl;
import com.unboundid.ldap.sdk.DN;
import java.util.TreeSet;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class MoveSubtreeTxnSearchListener implements SearchResultListener
{
    private static final long serialVersionUID = 5725895630679439468L;
    private final AtomicBoolean targetTxnValid;
    private final AtomicInteger entriesAddedToTarget;
    private final AtomicInteger entriesReadFromSource;
    private final AtomicReference<ResultCode> resultCode;
    private final Control[] addControls;
    private final LDAPConnection targetConnection;
    private final MoveSubtreeListener moveListener;
    private final StringBuilder errorMessage;
    private final TreeSet<DN> sourceEntryDNs;
    
    MoveSubtreeTxnSearchListener(final LDAPConnection targetConnection, final AtomicReference<ResultCode> resultCode, final StringBuilder errorMessage, final AtomicInteger entriesReadFromSource, final AtomicInteger entriesAddedToTarget, final TreeSet<DN> sourceEntryDNs, final InteractiveTransactionSpecificationRequestControl targetTxnControl, final OperationPurposeRequestControl opPurposeControl, final MoveSubtreeListener moveListener) {
        this.targetConnection = targetConnection;
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
        this.entriesReadFromSource = entriesReadFromSource;
        this.entriesAddedToTarget = entriesAddedToTarget;
        this.sourceEntryDNs = sourceEntryDNs;
        this.moveListener = moveListener;
        this.targetTxnValid = new AtomicBoolean(true);
        if (opPurposeControl == null) {
            this.addControls = new Control[] { targetTxnControl, new IgnoreNoUserModificationRequestControl() };
        }
        else {
            this.addControls = new Control[] { targetTxnControl, new IgnoreNoUserModificationRequestControl(), opPurposeControl };
        }
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        this.entriesReadFromSource.incrementAndGet();
        try {
            this.sourceEntryDNs.add(searchEntry.getParsedDN());
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.resultCode.compareAndSet(null, le.getResultCode());
            MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_CANNOT_PARSE_DN.get(searchEntry.getDN(), StaticUtils.getExceptionMessage(le)), this.errorMessage);
            return;
        }
        if (this.errorMessage.length() > 0) {
            return;
        }
        ReadOnlyEntry entry;
        if (this.moveListener == null) {
            entry = searchEntry;
        }
        else {
            try {
                entry = this.moveListener.doPreAddProcessing(searchEntry);
                if (entry == null) {
                    return;
                }
                if (!DN.equals(entry.getDN(), searchEntry.getDN())) {
                    this.resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                    MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_PRE_ADD_DN_ALTERED.get(entry.getDN(), searchEntry.getDN()), this.errorMessage);
                    return;
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_PRE_ADD_FAILURE.get(searchEntry.getDN(), StaticUtils.getExceptionMessage(e)), this.errorMessage);
                return;
            }
        }
        LDAPResult addResult;
        try {
            addResult = this.targetConnection.add(new AddRequest(entry, this.addControls));
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            addResult = le2.toLDAPResult();
        }
        if (addResult.getResultCode() == ResultCode.SUCCESS) {
            this.entriesAddedToTarget.incrementAndGet();
            try {
                final InteractiveTransactionSpecificationResponseControl txnResult = InteractiveTransactionSpecificationResponseControl.get(addResult);
                if (txnResult != null && !txnResult.transactionValid()) {
                    this.targetTxnValid.set(false);
                    this.resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                    MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_TXN_NO_LONGER_VALID.get(searchEntry.getDN()), this.errorMessage);
                    return;
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.resultCode.compareAndSet(null, le2.getResultCode());
                MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_CANNOT_DECODE_TXN_CONTROL.get(searchEntry.getDN(), StaticUtils.getExceptionMessage(le2)), this.errorMessage);
                return;
            }
            if (this.moveListener != null) {
                try {
                    this.moveListener.doPostAddProcessing(entry);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.resultCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
                    MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_POST_ADD_FAILURE.get(searchEntry.getDN(), StaticUtils.getExceptionMessage(e2)), this.errorMessage);
                }
            }
            return;
        }
        this.resultCode.compareAndSet(null, addResult.getResultCode());
        MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_ADD_FAILURE.get(searchEntry.getDN(), addResult.getDiagnosticMessage()), this.errorMessage);
        try {
            final InteractiveTransactionSpecificationResponseControl txnResult = InteractiveTransactionSpecificationResponseControl.get(addResult);
            if (txnResult != null && !txnResult.transactionValid()) {
                this.targetTxnValid.set(false);
            }
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        if (this.errorMessage.length() > 0) {
            return;
        }
        MoveSubtree.append(UnboundIDDSMessages.ERR_MOVE_SUBTREE_TXN_LISTENER_REFERENCE_RETURNED.get(StaticUtils.concatenateStrings(searchReference.getReferralURLs())), this.errorMessage);
    }
    
    boolean targetTransactionValid() {
        return this.targetTxnValid.get();
    }
}
