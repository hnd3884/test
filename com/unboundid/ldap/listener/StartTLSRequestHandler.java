package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import java.io.OutputStream;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class StartTLSRequestHandler extends LDAPListenerRequestHandler
{
    private final LDAPListenerClientConnection connection;
    private final LDAPListenerRequestHandler requestHandler;
    private final SSLSocketFactory sslSocketFactory;
    
    public StartTLSRequestHandler(final SSLSocketFactory sslSocketFactory, final LDAPListenerRequestHandler requestHandler) {
        this.sslSocketFactory = sslSocketFactory;
        this.requestHandler = requestHandler;
        this.connection = null;
    }
    
    private StartTLSRequestHandler(final SSLSocketFactory sslSocketFactory, final LDAPListenerRequestHandler requestHandler, final LDAPListenerClientConnection connection) {
        this.sslSocketFactory = sslSocketFactory;
        this.requestHandler = requestHandler;
        this.connection = connection;
    }
    
    @Override
    public StartTLSRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new StartTLSRequestHandler(this.sslSocketFactory, this.requestHandler.newInstance(connection), connection);
    }
    
    @Override
    public void closeInstance() {
        this.requestHandler.closeInstance();
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        this.requestHandler.processAbandonRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processAddRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processBindRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processCompareRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processDeleteRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        if (request.getOID().equals("1.3.6.1.4.1.1466.20037")) {
            try {
                final StartTLSExtendedRequest startTLSRequest = new StartTLSExtendedRequest(new ExtendedRequest(request.getOID(), request.getValue()));
                final OutputStream clearOutputStream = this.connection.convertToTLS(this.sslSocketFactory);
                final LDAPMessage responseMessage = new LDAPMessage(messageID, new ExtendedResponseProtocolOp(0, null, null, null, null, null), new Control[0]);
                final ASN1Buffer buffer = new ASN1Buffer();
                responseMessage.writeTo(buffer);
                try {
                    buffer.writeTo(clearOutputStream);
                    clearOutputStream.flush();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    final LDAPException le = new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_START_TLS_REQUEST_HANDLER_WRITE_RESPONSE_FAILURE.get(StaticUtils.getExceptionMessage(e)), e);
                    this.connection.close(le);
                    throw le;
                }
                return responseMessage;
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(le2.getResultCode().intValue(), le2.getMatchedDN(), le2.getDiagnosticMessage(), StaticUtils.toList(le2.getReferralURLs()), null, null), le2.getResponseControls());
            }
        }
        return this.requestHandler.processExtendedRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processModifyRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processModifyDNRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        return this.requestHandler.processSearchRequest(messageID, request, controls);
    }
    
    @Override
    public void processUnbindRequest(final int messageID, final UnbindRequestProtocolOp request, final List<Control> controls) {
        this.requestHandler.processUnbindRequest(messageID, request, controls);
    }
}
