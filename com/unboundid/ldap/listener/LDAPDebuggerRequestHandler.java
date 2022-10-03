package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.protocol.IntermediateResponseProtocolOp;
import java.util.Arrays;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import java.util.Iterator;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collection;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import java.net.Socket;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import java.util.logging.Handler;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPDebuggerRequestHandler extends LDAPListenerRequestHandler implements IntermediateResponseTransformer, SearchEntryTransformer, SearchReferenceTransformer
{
    private static final ThreadLocal<StringBuilder> BUFFERS;
    private final Handler logHandler;
    private final LDAPListenerRequestHandler requestHandler;
    private final String headerString;
    
    public LDAPDebuggerRequestHandler(final Handler logHandler, final LDAPListenerRequestHandler requestHandler) {
        Validator.ensureNotNull(logHandler, requestHandler);
        this.logHandler = logHandler;
        this.requestHandler = requestHandler;
        this.headerString = null;
    }
    
    private LDAPDebuggerRequestHandler(final Handler logHandler, final LDAPListenerRequestHandler requestHandler, final String headerString) {
        Validator.ensureNotNull(logHandler, requestHandler);
        this.logHandler = logHandler;
        this.requestHandler = requestHandler;
        this.headerString = headerString;
    }
    
    @Override
    public LDAPDebuggerRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        final StringBuilder b = getBuffer();
        final Socket s = connection.getSocket();
        b.append("conn=");
        b.append(connection.getConnectionID());
        b.append(" from=\"");
        b.append(s.getInetAddress().getHostAddress());
        b.append(':');
        b.append(s.getPort());
        b.append("\" to=\"");
        b.append(s.getLocalAddress().getHostAddress());
        b.append(':');
        b.append(s.getLocalPort());
        b.append('\"');
        b.append(StaticUtils.EOL);
        final String header = b.toString();
        final LDAPDebuggerRequestHandler h = new LDAPDebuggerRequestHandler(this.logHandler, this.requestHandler.newInstance(connection), header);
        connection.addIntermediateResponseTransformer(h);
        connection.addSearchEntryTransformer(h);
        connection.addSearchReferenceTransformer(h);
        this.logHandler.publish(new LogRecord(Level.INFO, "CONNECT " + header));
        return h;
    }
    
    @Override
    public void closeInstance() {
        final StringBuilder b = getBuffer();
        b.append("DISCONNECT ");
        b.append(this.headerString);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.requestHandler.closeInstance();
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Abandon Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          ID to Abandon:  ").append(request.getIDToAbandon()).append(StaticUtils.EOL);
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.requestHandler.processAbandonRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Add Request Protocol Op:").append(StaticUtils.EOL);
        final Entry e = new Entry(request.getDN(), request.getAttributes());
        final String[] arr$;
        final String[] ldifLines = arr$ = e.toLDIF(80);
        for (final String line : arr$) {
            b.append("          ").append(line).append(StaticUtils.EOL);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processAddRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Add Response Protocol Op:").append(StaticUtils.EOL);
        final AddResponseProtocolOp protocolOp = responseMessage.getAddResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Bind Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          LDAP Version:  ").append(request.getVersion()).append(StaticUtils.EOL);
        b.append("          Bind DN:  ").append(request.getBindDN()).append(StaticUtils.EOL);
        switch (request.getCredentialsType()) {
            case Byte.MIN_VALUE: {
                b.append("          Credentials Type:  SIMPLE").append(StaticUtils.EOL);
                b.append("               Password:  ").append(request.getSimplePassword()).append(StaticUtils.EOL);
                break;
            }
            case -93: {
                b.append("          Credentials Type:  SASL").append(StaticUtils.EOL);
                b.append("               Mechanism:  ").append(request.getSASLMechanism()).append(StaticUtils.EOL);
                final ASN1OctetString saslCredentials = request.getSASLCredentials();
                if (saslCredentials != null) {
                    b.append("               Encoded Credentials:");
                    b.append(StaticUtils.EOL);
                    StaticUtils.toHexPlusASCII(saslCredentials.getValue(), 20, b);
                    break;
                }
                break;
            }
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processBindRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Bind Response Protocol Op:").append(StaticUtils.EOL);
        final BindResponseProtocolOp protocolOp = responseMessage.getBindResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        final ASN1OctetString serverSASLCredentials = protocolOp.getServerSASLCredentials();
        if (serverSASLCredentials != null) {
            b.append("               Encoded Server SASL Credentials:");
            b.append(StaticUtils.EOL);
            StaticUtils.toHexPlusASCII(serverSASLCredentials.getValue(), 20, b);
        }
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Compare Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          DN:  ").append(request.getDN()).append(StaticUtils.EOL);
        b.append("          Attribute Type:  ").append(request.getAttributeName()).append(StaticUtils.EOL);
        b.append("          Assertion Value:  ").append(request.getAssertionValue().stringValue()).append(StaticUtils.EOL);
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processCompareRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Compare Response Protocol Op:").append(StaticUtils.EOL);
        final CompareResponseProtocolOp protocolOp = responseMessage.getCompareResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Delete Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          DN:  ").append(request.getDN()).append(StaticUtils.EOL);
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processDeleteRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Delete Response Protocol Op:").append(StaticUtils.EOL);
        final DeleteResponseProtocolOp protocolOp = responseMessage.getDeleteResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Extended Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          Request OID:  ").append(request.getOID()).append(StaticUtils.EOL);
        final ASN1OctetString requestValue = request.getValue();
        if (requestValue != null) {
            b.append("          Encoded Request Value:");
            b.append(StaticUtils.EOL);
            StaticUtils.toHexPlusASCII(requestValue.getValue(), 15, b);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processExtendedRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Extended Response Protocol Op:").append(StaticUtils.EOL);
        final ExtendedResponseProtocolOp protocolOp = responseMessage.getExtendedResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        final String responseOID = protocolOp.getResponseOID();
        if (responseOID != null) {
            b.append("          Response OID:  ").append(responseOID).append(StaticUtils.EOL);
        }
        final ASN1OctetString responseValue = protocolOp.getResponseValue();
        if (responseValue != null) {
            b.append("          Encoded Response Value:");
            b.append(StaticUtils.EOL);
            StaticUtils.toHexPlusASCII(responseValue.getValue(), 15, b);
        }
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Modify Request Protocol Op:").append(StaticUtils.EOL);
        final LDIFModifyChangeRecord changeRecord = new LDIFModifyChangeRecord(request.getDN(), request.getModifications());
        final String[] arr$;
        final String[] ldifLines = arr$ = changeRecord.toLDIF(80);
        for (final String line : arr$) {
            b.append("          ").append(line).append(StaticUtils.EOL);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processModifyRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Modify Response Protocol Op:").append(StaticUtils.EOL);
        final ModifyResponseProtocolOp protocolOp = responseMessage.getModifyResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Modify DN Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          DN:  ").append(request.getDN()).append(StaticUtils.EOL);
        b.append("          New RDN:  ").append(request.getNewRDN()).append(StaticUtils.EOL);
        b.append("          Delete Old RDN:  ").append(request.deleteOldRDN()).append(StaticUtils.EOL);
        final String newSuperior = request.getNewSuperiorDN();
        if (newSuperior != null) {
            b.append("          New Superior DN:  ").append(newSuperior).append(StaticUtils.EOL);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processModifyDNRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Modify DN Response Protocol Op:").append(StaticUtils.EOL);
        final ModifyDNResponseProtocolOp protocolOp = responseMessage.getModifyDNResponseProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Search Request Protocol Op:").append(StaticUtils.EOL);
        b.append("          Base DN:  ").append(request.getBaseDN()).append(StaticUtils.EOL);
        b.append("          Scope:  ").append(request.getScope()).append(StaticUtils.EOL);
        b.append("          Dereference Policy:  ").append(request.getDerefPolicy()).append(StaticUtils.EOL);
        b.append("          Size Limit:  ").append(request.getSizeLimit()).append(StaticUtils.EOL);
        b.append("          Time Limit:  ").append(request.getSizeLimit()).append(StaticUtils.EOL);
        b.append("          Types Only:  ").append(request.typesOnly()).append(StaticUtils.EOL);
        b.append("          Filter:  ");
        request.getFilter().toString(b);
        b.append(StaticUtils.EOL);
        final List<String> attributes = request.getAttributes();
        if (!attributes.isEmpty()) {
            b.append("          Requested Attributes:").append(StaticUtils.EOL);
            for (final String attr : attributes) {
                b.append("               ").append(attr).append(StaticUtils.EOL);
            }
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        final LDAPMessage responseMessage = this.requestHandler.processSearchRequest(messageID, request, controls);
        b.setLength(0);
        this.appendHeader(b, responseMessage.getMessageID());
        b.append("     Search Result Done Protocol Op:").append(StaticUtils.EOL);
        final SearchResultDoneProtocolOp protocolOp = responseMessage.getSearchResultDoneProtocolOp();
        appendResponse(b, protocolOp.getResultCode(), protocolOp.getDiagnosticMessage(), protocolOp.getMatchedDN(), protocolOp.getReferralURLs());
        appendControls(b, responseMessage.getControls());
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return responseMessage;
    }
    
    @Override
    public void processUnbindRequest(final int messageID, final UnbindRequestProtocolOp request, final List<Control> controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Unbind Request Protocol Op:").append(StaticUtils.EOL);
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        this.requestHandler.processUnbindRequest(messageID, request, controls);
    }
    
    private static StringBuilder getBuffer() {
        StringBuilder b = LDAPDebuggerRequestHandler.BUFFERS.get();
        if (b == null) {
            b = new StringBuilder();
            LDAPDebuggerRequestHandler.BUFFERS.set(b);
        }
        else {
            b.setLength(0);
        }
        return b;
    }
    
    private void appendHeader(final StringBuilder b, final int messageID) {
        b.append(this.headerString);
        b.append("LDAP Message:").append(StaticUtils.EOL);
        b.append("     Message ID:  ").append(messageID).append(StaticUtils.EOL);
    }
    
    private static void appendResponse(final StringBuilder b, final int resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs) {
        b.append("          Result Code:  ").append(ResultCode.valueOf(resultCode)).append(StaticUtils.EOL);
        if (diagnosticMessage != null) {
            b.append("          Diagnostic Message:  ").append(diagnosticMessage).append(StaticUtils.EOL);
        }
        if (matchedDN != null) {
            b.append("          Matched DN:  ").append(matchedDN).append(StaticUtils.EOL);
        }
        if (!referralURLs.isEmpty()) {
            b.append("          Referral URLs:").append(StaticUtils.EOL);
            for (final String url : referralURLs) {
                b.append("               ").append(url).append(StaticUtils.EOL);
            }
        }
    }
    
    private static void appendControls(final StringBuilder b, final List<Control> controls) {
        if (!controls.isEmpty()) {
            b.append("     Controls:").append(StaticUtils.EOL);
            int index = 1;
            for (final Control c : controls) {
                b.append("          Control ");
                b.append(index++);
                b.append(StaticUtils.EOL);
                b.append("               OID:  ");
                b.append(c.getOID());
                b.append(StaticUtils.EOL);
                b.append("               Is Critical:  ");
                b.append(c.isCritical());
                b.append(StaticUtils.EOL);
                final ASN1OctetString value = c.getValue();
                if (value != null && value.getValueLength() > 0) {
                    b.append("               Encoded Value:");
                    b.append(StaticUtils.EOL);
                    StaticUtils.toHexPlusASCII(value.getValue(), 20, b);
                }
                if (!c.getClass().getName().equals(Control.class.getName())) {
                    b.append("               String Representation:  ");
                    c.toString(b);
                    b.append(StaticUtils.EOL);
                }
            }
        }
    }
    
    private static void appendControls(final StringBuilder b, final Control[] controls) {
        appendControls(b, Arrays.asList(controls));
    }
    
    @Override
    public ObjectPair<IntermediateResponseProtocolOp, Control[]> transformIntermediateResponse(final int messageID, final IntermediateResponseProtocolOp response, final Control[] controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Intermediate Response Protocol Op:").append(StaticUtils.EOL);
        final String oid = response.getOID();
        if (oid != null) {
            b.append("          OID:  ").append(oid).append(StaticUtils.EOL);
        }
        final ASN1OctetString value = response.getValue();
        if (value != null) {
            b.append("          Encoded Value:");
            b.append(StaticUtils.EOL);
            StaticUtils.toHexPlusASCII(value.getValue(), 15, b);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return new ObjectPair<IntermediateResponseProtocolOp, Control[]>(response, controls);
    }
    
    @Override
    public ObjectPair<SearchResultEntryProtocolOp, Control[]> transformEntry(final int messageID, final SearchResultEntryProtocolOp entry, final Control[] controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Search Result Entry Protocol Op:").append(StaticUtils.EOL);
        final Entry e = new Entry(entry.getDN(), entry.getAttributes());
        final String[] arr$;
        final String[] ldifLines = arr$ = e.toLDIF(80);
        for (final String line : arr$) {
            b.append("          ").append(line).append(StaticUtils.EOL);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return new ObjectPair<SearchResultEntryProtocolOp, Control[]>(entry, controls);
    }
    
    @Override
    public ObjectPair<SearchResultReferenceProtocolOp, Control[]> transformReference(final int messageID, final SearchResultReferenceProtocolOp reference, final Control[] controls) {
        final StringBuilder b = getBuffer();
        this.appendHeader(b, messageID);
        b.append("     Search Result Reference Protocol Op:").append(StaticUtils.EOL);
        b.append("          Referral URLs:").append(StaticUtils.EOL);
        for (final String url : reference.getReferralURLs()) {
            b.append("               ").append(url).append(StaticUtils.EOL);
        }
        appendControls(b, controls);
        this.logHandler.publish(new LogRecord(Level.INFO, b.toString()));
        return new ObjectPair<SearchResultReferenceProtocolOp, Control[]>(reference, controls);
    }
    
    static {
        BUFFERS = new ThreadLocal<StringBuilder>();
    }
}
