package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.protocol.IntermediateResponseProtocolOp;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.util.Debug;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.HashMap;
import com.unboundid.util.StaticUtils;
import java.util.List;
import java.util.Map;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.listener.SearchReferenceTransformer;
import com.unboundid.ldap.listener.SearchEntryTransformer;
import com.unboundid.ldap.listener.IntermediateResponseTransformer;
import com.unboundid.ldap.listener.LDAPListenerRequestHandler;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryOperationInterceptorRequestHandler extends LDAPListenerRequestHandler implements IntermediateResponseTransformer, SearchEntryTransformer, SearchReferenceTransformer
{
    private final InMemoryOperationInterceptor[] interceptors;
    private final LDAPListenerClientConnection connection;
    private final LDAPListenerRequestHandler wrappedHandler;
    private final Map<Integer, InterceptedOperation> activeOperations;
    
    public InMemoryOperationInterceptorRequestHandler(final List<InMemoryOperationInterceptor> interceptors, final LDAPListenerRequestHandler wrappedHandler) {
        this.wrappedHandler = wrappedHandler;
        interceptors.toArray(this.interceptors = new InMemoryOperationInterceptor[interceptors.size()]);
        this.connection = null;
        this.activeOperations = new HashMap<Integer, InterceptedOperation>(StaticUtils.computeMapCapacity(5));
    }
    
    private InMemoryOperationInterceptorRequestHandler(final InMemoryOperationInterceptor[] interceptors, final LDAPListenerRequestHandler wrappedHandler, final LDAPListenerClientConnection connection) {
        this.interceptors = interceptors;
        this.wrappedHandler = wrappedHandler;
        this.connection = connection;
        this.activeOperations = new HashMap<Integer, InterceptedOperation>(StaticUtils.computeMapCapacity(5));
    }
    
    @Override
    public InMemoryOperationInterceptorRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        final InMemoryOperationInterceptorRequestHandler handler = new InMemoryOperationInterceptorRequestHandler(this.interceptors, this.wrappedHandler.newInstance(connection), connection);
        connection.addSearchEntryTransformer(handler);
        connection.addSearchReferenceTransformer(handler);
        connection.addIntermediateResponseTransformer(handler);
        return handler;
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        final InterceptedAddOperation op = new InterceptedAddOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processAddRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processAddRequest(messageID, new AddRequestProtocolOp((AddRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getAddResponseProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processAddResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new AddResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        if (request.getCredentialsType() == -128) {
            final InterceptedSimpleBindOperation op = new InterceptedSimpleBindOperation(this.connection, messageID, request, toArray(controls));
            this.activeOperations.put(messageID, op);
            try {
                for (final InMemoryOperationInterceptor i : this.interceptors) {
                    try {
                        i.processSimpleBindRequest(op);
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null, null), new Control[0]);
                    }
                }
                final LDAPMessage resultMessage = this.wrappedHandler.processBindRequest(messageID, new BindRequestProtocolOp(op.getRequest()), op.getRequest().getControlList());
                op.setResult(resultMessage.getBindResponseProtocolOp().toBindResult(toArray(resultMessage.getControls())));
                for (final InMemoryOperationInterceptor j : this.interceptors) {
                    try {
                        j.processSimpleBindResult(op);
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null, null), new Control[0]);
                    }
                }
                return new LDAPMessage(messageID, new BindResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
            }
            finally {
                this.activeOperations.remove(messageID);
            }
        }
        final InterceptedSASLBindOperation op2 = new InterceptedSASLBindOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op2);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processSASLBindRequest(op2);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op2), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null, null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processBindRequest(messageID, new BindRequestProtocolOp(op2.getRequest()), op2.getRequest().getControlList());
            op2.setResult(resultMessage.getBindResponseProtocolOp().toBindResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processSASLBindResult(op2);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op2), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null, null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new BindResponseProtocolOp(op2.getResult()), op2.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        final InterceptedCompareOperation op = new InterceptedCompareOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processCompareRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new CompareResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new CompareResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processCompareRequest(messageID, new CompareRequestProtocolOp((CompareRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getCompareResponseProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processCompareResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new CompareResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new CompareResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        final InterceptedDeleteOperation op = new InterceptedDeleteOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processDeleteRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new DeleteResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new DeleteResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processDeleteRequest(messageID, new DeleteRequestProtocolOp((DeleteRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getDeleteResponseProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processDeleteResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new DeleteResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new DeleteResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        final InterceptedExtendedOperation op = new InterceptedExtendedOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processExtendedRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null, null, null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processExtendedRequest(messageID, new ExtendedRequestProtocolOp(op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getExtendedResponseProtocolOp().toExtendedResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processExtendedResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null, null, null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        final InterceptedModifyOperation op = new InterceptedModifyOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processModifyRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processModifyRequest(messageID, new ModifyRequestProtocolOp((ModifyRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getModifyResponseProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processModifyResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new ModifyResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        final InterceptedModifyDNOperation op = new InterceptedModifyDNOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processModifyDNRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processModifyDNRequest(messageID, new ModifyDNRequestProtocolOp((ModifyDNRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getModifyDNResponseProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processModifyDNResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        final InterceptedSearchOperation op = new InterceptedSearchOperation(this.connection, messageID, request, toArray(controls));
        this.activeOperations.put(messageID, op);
        try {
            for (final InMemoryOperationInterceptor i : this.interceptors) {
                try {
                    i.processSearchRequest(op);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le.toLDAPResult()), le.getResponseControls());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_REQUEST_ERROR.get(String.valueOf(op), i.getClass().getName(), StaticUtils.getExceptionMessage(e)), null), new Control[0]);
                }
            }
            final LDAPMessage resultMessage = this.wrappedHandler.processSearchRequest(messageID, new SearchRequestProtocolOp((SearchRequest)op.getRequest()), op.getRequest().getControlList());
            op.setResult(resultMessage.getSearchResultDoneProtocolOp().toLDAPResult(toArray(resultMessage.getControls())));
            for (final InMemoryOperationInterceptor j : this.interceptors) {
                try {
                    j.processSearchResult(op);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(80, null, InterceptorMessages.ERR_DS_INTERCEPTOR_RESULT_ERROR.get(String.valueOf(op), j.getClass().getName(), StaticUtils.getExceptionMessage(e2)), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(op.getResult()), op.getResult().getResponseControls());
        }
        finally {
            this.activeOperations.remove(messageID);
        }
    }
    
    @Override
    public ObjectPair<SearchResultEntryProtocolOp, Control[]> transformEntry(final int messageID, final SearchResultEntryProtocolOp entry, final Control[] controls) {
        final InterceptedSearchOperation op = this.activeOperations.get(messageID);
        if (op == null) {
            return new ObjectPair<SearchResultEntryProtocolOp, Control[]>(entry, controls);
        }
        final InterceptedSearchEntry e = new InterceptedSearchEntry(op, entry, controls);
        for (final InMemoryOperationInterceptor i : this.interceptors) {
            try {
                i.processSearchEntry(e);
                if (e.getSearchEntry() == null) {
                    return null;
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                return null;
            }
        }
        return new ObjectPair<SearchResultEntryProtocolOp, Control[]>(new SearchResultEntryProtocolOp(e.getSearchEntry()), e.getSearchEntry().getControls());
    }
    
    @Override
    public ObjectPair<SearchResultReferenceProtocolOp, Control[]> transformReference(final int messageID, final SearchResultReferenceProtocolOp reference, final Control[] controls) {
        final InterceptedSearchOperation op = this.activeOperations.get(messageID);
        if (op == null) {
            return new ObjectPair<SearchResultReferenceProtocolOp, Control[]>(reference, controls);
        }
        final InterceptedSearchReference r = new InterceptedSearchReference(op, reference, controls);
        for (final InMemoryOperationInterceptor i : this.interceptors) {
            try {
                i.processSearchReference(r);
                if (r.getSearchReference() == null) {
                    return null;
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                return null;
            }
        }
        return new ObjectPair<SearchResultReferenceProtocolOp, Control[]>(new SearchResultReferenceProtocolOp(r.getSearchReference()), r.getSearchReference().getControls());
    }
    
    @Override
    public ObjectPair<IntermediateResponseProtocolOp, Control[]> transformIntermediateResponse(final int messageID, final IntermediateResponseProtocolOp response, final Control[] controls) {
        final InterceptedOperation op = this.activeOperations.get(messageID);
        if (op == null) {
            return new ObjectPair<IntermediateResponseProtocolOp, Control[]>(response, controls);
        }
        final InterceptedIntermediateResponse r = new InterceptedIntermediateResponse(op, response, controls);
        for (final InMemoryOperationInterceptor i : this.interceptors) {
            try {
                i.processIntermediateResponse(r);
                if (r.getIntermediateResponse() == null) {
                    return null;
                }
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
                return null;
            }
        }
        return new ObjectPair<IntermediateResponseProtocolOp, Control[]>(new IntermediateResponseProtocolOp(r.getIntermediateResponse()), r.getIntermediateResponse().getControls());
    }
    
    private static Control[] toArray(final List<Control> controls) {
        if (controls == null || controls.isEmpty()) {
            return StaticUtils.NO_CONTROLS;
        }
        final Control[] controlArray = new Control[controls.size()];
        return controls.toArray(controlArray);
    }
}
