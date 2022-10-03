package org.jscep.transaction;

import org.jscep.util.SignedDataUtils;
import org.jscep.message.CertRep;
import org.jscep.message.MessageEncodingException;
import org.jscep.message.MessageDecodingException;
import org.jscep.message.PkiMessage;
import org.jscep.transport.TransportException;
import org.jscep.transport.response.ScepResponseHandler;
import org.bouncycastle.cms.CMSSignedData;
import org.jscep.transport.request.Request;
import org.jscep.transport.response.PkiOperationResponseHandler;
import java.security.cert.CertStore;
import org.jscep.transport.Transport;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkiMessageEncoder;

public abstract class Transaction
{
    private final PkiMessageEncoder encoder;
    private final PkiMessageDecoder decoder;
    private final Transport transport;
    private State state;
    private FailInfo failInfo;
    private CertStore certStore;
    
    public Transaction(final Transport transport, final PkiMessageEncoder encoder, final PkiMessageDecoder decoder) {
        this.transport = transport;
        this.encoder = encoder;
        this.decoder = decoder;
    }
    
    public final FailInfo getFailInfo() {
        if (this.state != State.CERT_NON_EXISTANT) {
            throw new IllegalStateException("No failure has been received.  Check state!");
        }
        return this.failInfo;
    }
    
    public final CertStore getCertStore() {
        if (this.state != State.CERT_ISSUED) {
            throw new IllegalStateException("No certstore has been received.  Check state!");
        }
        return this.certStore;
    }
    
    public abstract State send() throws TransactionException;
    
    public abstract TransactionId getId();
    
    final CMSSignedData send(final PkiOperationResponseHandler handler, final Request req) throws TransactionException {
        try {
            return this.transport.sendRequest(req, (ScepResponseHandler<CMSSignedData>)handler);
        }
        catch (final TransportException e) {
            throw new TransactionException(e);
        }
    }
    
    final PkiMessage<?> decode(final CMSSignedData res) throws MessageDecodingException {
        return this.decoder.decode(res);
    }
    
    final CMSSignedData encode(final PkiMessage<?> message) throws MessageEncodingException {
        return this.encoder.encode(message);
    }
    
    final State pending() {
        return this.state = State.CERT_REQ_PENDING;
    }
    
    final State failure(final FailInfo failInfo) {
        this.failInfo = failInfo;
        return this.state = State.CERT_NON_EXISTANT;
    }
    
    final State success(final CertStore certStore) {
        this.certStore = certStore;
        return this.state = State.CERT_ISSUED;
    }
    
    final CertStore extractCertStore(final CertRep response) {
        final CMSSignedData signedData = response.getMessageData();
        return SignedDataUtils.fromSignedData(signedData);
    }
    
    public enum State
    {
        CERT_REQ_PENDING, 
        CERT_NON_EXISTANT, 
        CERT_ISSUED;
    }
}
