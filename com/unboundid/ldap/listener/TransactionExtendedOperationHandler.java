package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedRequest;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedResult;
import java.util.Map;
import com.unboundid.ldap.protocol.LDAPMessage;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.extensions.AbortedTransactionExtendedResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TransactionExtendedOperationHandler extends InMemoryExtendedOperationHandler
{
    private static final AtomicLong TXN_ID_COUNTER;
    static final String STATE_VARIABLE_TXN_INFO = "TXN-INFO";
    
    @Override
    public String getExtendedOperationHandlerName() {
        return "LDAP Transactions";
    }
    
    @Override
    public List<String> getSupportedExtendedRequestOIDs() {
        return Arrays.asList("1.3.6.1.1.21.1", "1.3.6.1.1.21.3");
    }
    
    @Override
    public ExtendedResult processExtendedOperation(final InMemoryRequestHandler handler, final int messageID, final ExtendedRequest request) {
        for (final Control c : request.getControls()) {
            if (c.isCritical()) {
                final ObjectPair<?, ?> existingTxnInfo = handler.getConnectionState().remove("TXN-INFO");
                if (existingTxnInfo != null) {
                    final ASN1OctetString txnID = (ASN1OctetString)existingTxnInfo.getFirst();
                    try {
                        handler.getClientConnection().sendUnsolicitedNotification(new AbortedTransactionExtendedResult(txnID, ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_TXN_EXTOP_ABORTED_BY_UNSUPPORTED_CONTROL.get(txnID.stringValue(), c.getOID()), null, null, null));
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        return new ExtendedResult(le);
                    }
                }
                return new ExtendedResult(messageID, ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_TXN_EXTOP_UNSUPPORTED_CONTROL.get(c.getOID()), null, null, null, null, null);
            }
        }
        final String oid = request.getOID();
        if (oid.equals("1.3.6.1.1.21.1")) {
            return handleStartTransaction(handler, messageID, request);
        }
        return handleEndTransaction(handler, messageID, request);
    }
    
    private static StartTransactionExtendedResult handleStartTransaction(final InMemoryRequestHandler handler, final int messageID, final ExtendedRequest request) {
        final Map<String, Object> connectionState = handler.getConnectionState();
        final ObjectPair<?, ?> existingTxnInfo = connectionState.remove("TXN-INFO");
        if (existingTxnInfo != null) {
            final ASN1OctetString txnID = (ASN1OctetString)existingTxnInfo.getFirst();
            try {
                handler.getClientConnection().sendUnsolicitedNotification(new AbortedTransactionExtendedResult(txnID, ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_TXN_EXTOP_TXN_ABORTED_BY_NEW_START_TXN.get(txnID.stringValue()), null, null, null));
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new StartTransactionExtendedResult(new ExtendedResult(le));
            }
        }
        try {
            new StartTransactionExtendedRequest(request);
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            return new StartTransactionExtendedResult(messageID, ResultCode.PROTOCOL_ERROR, le2.getMessage(), null, null, null, null);
        }
        final ASN1OctetString txnID = new ASN1OctetString(String.valueOf(TransactionExtendedOperationHandler.TXN_ID_COUNTER.getAndIncrement()));
        final List<LDAPMessage> requestList = new ArrayList<LDAPMessage>(10);
        final ObjectPair<ASN1OctetString, List<LDAPMessage>> txnInfo = new ObjectPair<ASN1OctetString, List<LDAPMessage>>(txnID, requestList);
        connectionState.put("TXN-INFO", txnInfo);
        return new StartTransactionExtendedResult(messageID, ResultCode.SUCCESS, ListenerMessages.INFO_TXN_EXTOP_CREATED_TXN.get(txnID.stringValue()), null, null, txnID, null);
    }
    
    private static EndTransactionExtendedResult handleEndTransaction(final InMemoryRequestHandler handler, final int messageID, final ExtendedRequest request) {
        final Map<String, Object> connectionState = handler.getConnectionState();
        final ObjectPair<?, ?> txnInfo = connectionState.remove("TXN-INFO");
        if (txnInfo == null) {
            return new EndTransactionExtendedResult(messageID, ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_TXN_EXTOP_END_NO_ACTIVE_TXN.get(), null, null, null, (Map<Integer, Control[]>)null, null);
        }
        final ASN1OctetString existingTxnID = (ASN1OctetString)txnInfo.getFirst();
        EndTransactionExtendedRequest endTxnRequest;
        try {
            endTxnRequest = new EndTransactionExtendedRequest(request);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            try {
                handler.getClientConnection().sendUnsolicitedNotification(new AbortedTransactionExtendedResult(existingTxnID, ResultCode.PROTOCOL_ERROR, ListenerMessages.ERR_TXN_EXTOP_ABORTED_BY_MALFORMED_END_TXN.get(existingTxnID.stringValue()), null, null, null));
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
            }
            return new EndTransactionExtendedResult(messageID, ResultCode.PROTOCOL_ERROR, le.getMessage(), null, null, null, (Map<Integer, Control[]>)null, null);
        }
        final ASN1OctetString targetTxnID = endTxnRequest.getTransactionID();
        if (!existingTxnID.stringValue().equals(targetTxnID.stringValue())) {
            try {
                handler.getClientConnection().sendUnsolicitedNotification(new AbortedTransactionExtendedResult(existingTxnID, ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_TXN_EXTOP_ABORTED_BY_WRONG_END_TXN.get(existingTxnID.stringValue(), targetTxnID.stringValue()), null, null, null));
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new EndTransactionExtendedResult(messageID, le3.getResultCode(), le3.getMessage(), le3.getMatchedDN(), le3.getReferralURLs(), null, (Map<Integer, Control[]>)null, le3.getResponseControls());
            }
            return new EndTransactionExtendedResult(messageID, ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_TXN_EXTOP_END_WRONG_TXN.get(targetTxnID.stringValue(), existingTxnID.stringValue()), null, null, null, (Map<Integer, Control[]>)null, null);
        }
        if (!endTxnRequest.commit()) {
            return new EndTransactionExtendedResult(messageID, ResultCode.SUCCESS, ListenerMessages.INFO_TXN_EXTOP_END_TXN_ABORTED.get(existingTxnID.stringValue()), null, null, null, (Map<Integer, Control[]>)null, null);
        }
        final InMemoryDirectoryServerSnapshot snapshot = handler.createSnapshot();
        boolean rollBack = true;
        try {
            final List<?> requestMessages = (List<?>)txnInfo.getSecond();
            final Map<Integer, Control[]> opResponseControls = new LinkedHashMap<Integer, Control[]>(StaticUtils.computeMapCapacity(requestMessages.size()));
            ResultCode resultCode = ResultCode.SUCCESS;
            String diagnosticMessage = null;
            String failedOpType = null;
            Integer failedOpMessageID = null;
        Label_1031:
            for (final Object o : requestMessages) {
                final LDAPMessage m = (LDAPMessage)o;
                switch (m.getProtocolOpType()) {
                    case 104: {
                        final LDAPMessage addResponseMessage = handler.processAddRequest(m.getMessageID(), m.getAddRequestProtocolOp(), m.getControls());
                        final AddResponseProtocolOp addResponseOp = addResponseMessage.getAddResponseProtocolOp();
                        final List<Control> addControls = addResponseMessage.getControls();
                        if (addControls != null && !addControls.isEmpty()) {
                            final Control[] controls = new Control[addControls.size()];
                            addControls.toArray(controls);
                            opResponseControls.put(m.getMessageID(), controls);
                        }
                        if (addResponseOp.getResultCode() != 0) {
                            resultCode = ResultCode.valueOf(addResponseOp.getResultCode());
                            diagnosticMessage = addResponseOp.getDiagnosticMessage();
                            failedOpType = ListenerMessages.INFO_TXN_EXTOP_OP_TYPE_ADD.get();
                            failedOpMessageID = m.getMessageID();
                            break Label_1031;
                        }
                        continue;
                    }
                    case 74: {
                        final LDAPMessage deleteResponseMessage = handler.processDeleteRequest(m.getMessageID(), m.getDeleteRequestProtocolOp(), m.getControls());
                        final DeleteResponseProtocolOp deleteResponseOp = deleteResponseMessage.getDeleteResponseProtocolOp();
                        final List<Control> deleteControls = deleteResponseMessage.getControls();
                        if (deleteControls != null && !deleteControls.isEmpty()) {
                            final Control[] controls2 = new Control[deleteControls.size()];
                            deleteControls.toArray(controls2);
                            opResponseControls.put(m.getMessageID(), controls2);
                        }
                        if (deleteResponseOp.getResultCode() != 0) {
                            resultCode = ResultCode.valueOf(deleteResponseOp.getResultCode());
                            diagnosticMessage = deleteResponseOp.getDiagnosticMessage();
                            failedOpType = ListenerMessages.INFO_TXN_EXTOP_OP_TYPE_DELETE.get();
                            failedOpMessageID = m.getMessageID();
                            break Label_1031;
                        }
                        continue;
                    }
                    case 102: {
                        final LDAPMessage modifyResponseMessage = handler.processModifyRequest(m.getMessageID(), m.getModifyRequestProtocolOp(), m.getControls());
                        final ModifyResponseProtocolOp modifyResponseOp = modifyResponseMessage.getModifyResponseProtocolOp();
                        final List<Control> modifyControls = modifyResponseMessage.getControls();
                        if (modifyControls != null && !modifyControls.isEmpty()) {
                            final Control[] controls3 = new Control[modifyControls.size()];
                            modifyControls.toArray(controls3);
                            opResponseControls.put(m.getMessageID(), controls3);
                        }
                        if (modifyResponseOp.getResultCode() != 0) {
                            resultCode = ResultCode.valueOf(modifyResponseOp.getResultCode());
                            diagnosticMessage = modifyResponseOp.getDiagnosticMessage();
                            failedOpType = ListenerMessages.INFO_TXN_EXTOP_OP_TYPE_MODIFY.get();
                            failedOpMessageID = m.getMessageID();
                            break Label_1031;
                        }
                        continue;
                    }
                    case 108: {
                        final LDAPMessage modifyDNResponseMessage = handler.processModifyDNRequest(m.getMessageID(), m.getModifyDNRequestProtocolOp(), m.getControls());
                        final ModifyDNResponseProtocolOp modifyDNResponseOp = modifyDNResponseMessage.getModifyDNResponseProtocolOp();
                        final List<Control> modifyDNControls = modifyDNResponseMessage.getControls();
                        if (modifyDNControls != null && !modifyDNControls.isEmpty()) {
                            final Control[] controls4 = new Control[modifyDNControls.size()];
                            modifyDNControls.toArray(controls4);
                            opResponseControls.put(m.getMessageID(), controls4);
                        }
                        if (modifyDNResponseOp.getResultCode() != 0) {
                            resultCode = ResultCode.valueOf(modifyDNResponseOp.getResultCode());
                            diagnosticMessage = modifyDNResponseOp.getDiagnosticMessage();
                            failedOpType = ListenerMessages.INFO_TXN_EXTOP_OP_TYPE_MODIFY_DN.get();
                            failedOpMessageID = m.getMessageID();
                            break Label_1031;
                        }
                        continue;
                    }
                }
            }
            if (resultCode == ResultCode.SUCCESS) {
                diagnosticMessage = ListenerMessages.INFO_TXN_EXTOP_COMMITTED.get(existingTxnID.stringValue());
                rollBack = false;
            }
            else {
                diagnosticMessage = ListenerMessages.ERR_TXN_EXTOP_COMMIT_FAILED.get(existingTxnID.stringValue(), failedOpType, failedOpMessageID, diagnosticMessage);
            }
            return new EndTransactionExtendedResult(messageID, resultCode, diagnosticMessage, null, null, failedOpMessageID, opResponseControls, null);
        }
        finally {
            if (rollBack) {
                handler.restoreSnapshot(snapshot);
            }
        }
    }
    
    static {
        TXN_ID_COUNTER = new AtomicLong(1L);
    }
}
