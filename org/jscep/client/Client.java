package org.jscep.client;

import org.slf4j.LoggerFactory;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import org.jscep.message.PkcsPkiEnvelopeDecoder;
import org.jscep.message.PkcsPkiEnvelopeEncoder;
import org.jscep.asn1.IssuerAndSubject;
import org.jscep.transaction.TransactionId;
import org.bouncycastle.operator.RuntimeOperatorException;
import java.security.SignatureException;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.MessageDigest;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkiMessageEncoder;
import java.io.IOException;
import org.apache.commons.codec.binary.Hex;
import org.jscep.transaction.EnrollmentTransaction;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Collection;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.cert.CertStoreException;
import java.security.cert.CRLSelector;
import org.jscep.transaction.Transaction;
import org.jscep.transaction.TransactionException;
import org.jscep.transaction.NonEnrollmentTransaction;
import org.jscep.transaction.MessageType;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.jscep.util.X500Utils;
import org.jscep.transaction.OperationFailureException;
import java.security.cert.X509CRL;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.security.PrivateKey;
import org.jscep.transport.response.GetNextCaCertResponseHandler;
import org.jscep.transport.request.GetNextCaCertRequest;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import org.jscep.client.inspect.CertStoreInspector;
import org.jscep.transport.response.GetCaCertResponseHandler;
import org.jscep.transport.request.GetCaCertRequest;
import java.security.cert.CertStore;
import org.jscep.transport.Transport;
import org.jscep.transport.TransportException;
import org.jscep.transport.response.Capability;
import org.jscep.transport.response.ScepResponseHandler;
import org.jscep.transport.request.Request;
import org.jscep.transport.response.GetCaCapsResponseHandler;
import org.jscep.transport.request.GetCaCapsRequest;
import org.jscep.transport.response.Capabilities;
import org.jscep.client.verification.CertificateVerifier;
import org.jscep.transport.UrlConnectionTransportFactory;
import org.jscep.client.inspect.DefaultCertStoreInspectorFactory;
import org.jscep.transport.TransportFactory;
import org.jscep.client.inspect.CertStoreInspectorFactory;
import javax.security.auth.callback.CallbackHandler;
import java.net.URL;
import org.slf4j.Logger;

public final class Client
{
    private static final Logger LOGGER;
    private final URL url;
    private final CallbackHandler handler;
    private CertStoreInspectorFactory inspectorFactory;
    private TransportFactory transportFactory;
    
    public Client(final URL url, final CallbackHandler handler) {
        this.inspectorFactory = new DefaultCertStoreInspectorFactory();
        this.transportFactory = new UrlConnectionTransportFactory();
        this.url = url;
        this.handler = handler;
        this.validateInput();
    }
    
    public Client(final URL url, final CertificateVerifier verifier) {
        this.inspectorFactory = new DefaultCertStoreInspectorFactory();
        this.transportFactory = new UrlConnectionTransportFactory();
        this.url = url;
        this.handler = new DefaultCallbackHandler(verifier);
        this.validateInput();
    }
    
    private void validateInput() {
        if (this.url == null) {
            throw new NullPointerException("URL should not be null");
        }
        if (!this.url.getProtocol().matches("^https?$")) {
            throw new IllegalArgumentException("URL protocol should be HTTP or HTTPS");
        }
        if (this.url.getRef() != null) {
            throw new IllegalArgumentException("URL should contain no reference");
        }
        if (this.url.getQuery() != null) {
            throw new IllegalArgumentException("URL should contain no query string");
        }
        if (this.handler == null) {
            throw new NullPointerException("Callback handler should not be null");
        }
    }
    
    public Capabilities getCaCapabilities() {
        return this.getCaCapabilities(null);
    }
    
    public Capabilities getCaCapabilities(final String profile) {
        Client.LOGGER.debug("Determining capabilities of SCEP server");
        final GetCaCapsRequest req = new GetCaCapsRequest(profile);
        final Transport trans = this.transportFactory.forMethod(TransportFactory.Method.GET, this.url);
        try {
            return trans.sendRequest(req, (ScepResponseHandler<Capabilities>)new GetCaCapsResponseHandler());
        }
        catch (final TransportException e) {
            Client.LOGGER.warn("AbstractTransport problem when determining capabilities.  Using empty capabilities.");
            return new Capabilities(new Capability[0]);
        }
    }
    
    public CertStore getCaCertificate() throws ClientException {
        return this.getCaCertificate(null);
    }
    
    public CertStore getCaCertificate(final String profile) throws ClientException {
        Client.LOGGER.debug("Retrieving current CA certificate");
        final GetCaCertRequest req = new GetCaCertRequest(profile);
        final Transport trans = this.transportFactory.forMethod(TransportFactory.Method.GET, this.url);
        CertStore store;
        try {
            store = trans.sendRequest(req, (ScepResponseHandler<CertStore>)new GetCaCertResponseHandler());
        }
        catch (final TransportException e) {
            throw new ClientException(e);
        }
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        this.verifyCA(certs.getIssuer());
        this.verifyRA(certs.getIssuer(), certs.getRecipient());
        this.verifyRA(certs.getIssuer(), certs.getSigner());
        return store;
    }
    
    private void verifyRA(final X509Certificate ca, final X509Certificate ra) throws ClientException {
        Client.LOGGER.debug("Verifying signature of RA certificate");
        if (ca.equals(ra)) {
            Client.LOGGER.debug("RA and CA are identical");
            return;
        }
        try {
            final JcaX509CertificateHolder raHolder = new JcaX509CertificateHolder(ra);
            final ContentVerifierProvider verifierProvider = new JcaContentVerifierProviderBuilder().build(ca);
            if (!raHolder.isSignatureValid(verifierProvider)) {
                Client.LOGGER.debug("Signature verification failed for RA.");
                throw new ClientException("RA not issued by CA");
            }
            Client.LOGGER.debug("Signature verification passed for RA.");
        }
        catch (final CertException e) {
            throw new ClientException((Throwable)e);
        }
        catch (final CertificateEncodingException e2) {
            throw new ClientException(e2);
        }
        catch (final OperatorCreationException e3) {
            throw new ClientException((Throwable)e3);
        }
    }
    
    public CertStore getRolloverCertificate() throws ClientException {
        return this.getRolloverCertificate(null);
    }
    
    public CertStore getRolloverCertificate(final String profile) throws ClientException {
        Client.LOGGER.debug("Retriving next CA certificate from CA");
        if (!this.getCaCapabilities(profile).isRolloverSupported()) {
            throw new UnsupportedOperationException();
        }
        final CertStore store = this.getCaCertificate(profile);
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        final X509Certificate signer = certs.getSigner();
        final Transport trans = this.transportFactory.forMethod(TransportFactory.Method.GET, this.url);
        final GetNextCaCertRequest req = new GetNextCaCertRequest(profile);
        try {
            return trans.sendRequest(req, (ScepResponseHandler<CertStore>)new GetNextCaCertResponseHandler(signer));
        }
        catch (final TransportException e) {
            throw new ClientException(e);
        }
    }
    
    public X509CRL getRevocationList(final X509Certificate identity, final PrivateKey key, final X500Principal issuer, final BigInteger serial) throws ClientException, OperationFailureException {
        return this.getRevocationList(identity, key, issuer, serial, null);
    }
    
    public X509CRL getRevocationList(final X509Certificate identity, final PrivateKey key, final X500Principal issuer, final BigInteger serial, final String profile) throws ClientException, OperationFailureException {
        Client.LOGGER.debug("Retriving CRL from CA");
        this.checkDistributionPoints(profile);
        final X500Name name = X500Utils.toX500Name(issuer);
        final IssuerAndSerialNumber iasn = new IssuerAndSerialNumber(name, serial);
        final Transport transport = this.createTransport(profile);
        final Transaction t = new NonEnrollmentTransaction(transport, this.getEncoder(identity, key, profile), this.getDecoder(identity, key, profile), iasn, MessageType.GET_CRL);
        Transaction.State state;
        try {
            state = t.send();
        }
        catch (final TransactionException e) {
            throw new ClientException(e);
        }
        if (state == Transaction.State.CERT_ISSUED) {
            try {
                final Collection<X509CRL> crls = (Collection<X509CRL>)t.getCertStore().getCRLs(null);
                if (crls.size() == 0) {
                    return null;
                }
                return crls.iterator().next();
            }
            catch (final CertStoreException e2) {
                throw new RuntimeException(e2);
            }
        }
        if (state == Transaction.State.CERT_REQ_PENDING) {
            throw new IllegalStateException();
        }
        throw new OperationFailureException(t.getFailInfo());
    }
    
    private void checkDistributionPoints(final String profile) throws ClientException {
        final CertStore store = this.getCaCertificate(profile);
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        final X509Certificate ca = certs.getIssuer();
        if (ca.getExtensionValue(Extension.cRLDistributionPoints.getId()) != null) {
            Client.LOGGER.warn("CA supports distribution points");
        }
    }
    
    public CertStore getCertificate(final X509Certificate identity, final PrivateKey key, final BigInteger serial) throws ClientException, OperationFailureException {
        return this.getCertificate(identity, key, serial, null);
    }
    
    public CertStore getCertificate(final X509Certificate identity, final PrivateKey key, final BigInteger serial, final String profile) throws OperationFailureException, ClientException {
        Client.LOGGER.debug("Retriving certificate from CA");
        final CertStore store = this.getCaCertificate(profile);
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        final X509Certificate ca = certs.getIssuer();
        final X500Name name = X500Utils.toX500Name(ca.getSubjectX500Principal());
        final IssuerAndSerialNumber iasn = new IssuerAndSerialNumber(name, serial);
        final Transport transport = this.createTransport(profile);
        final Transaction t = new NonEnrollmentTransaction(transport, this.getEncoder(identity, key, profile), this.getDecoder(identity, key, profile), iasn, MessageType.GET_CERT);
        Transaction.State state;
        try {
            state = t.send();
        }
        catch (final TransactionException e) {
            throw new ClientException(e);
        }
        if (state == Transaction.State.CERT_ISSUED) {
            return t.getCertStore();
        }
        if (state == Transaction.State.CERT_REQ_PENDING) {
            throw new IllegalStateException();
        }
        throw new OperationFailureException(t.getFailInfo());
    }
    
    public EnrollmentResponse enrol(final X509Certificate identity, final PrivateKey key, final PKCS10CertificationRequest csr) throws ClientException, TransactionException {
        return this.enrol(identity, key, csr, null);
    }
    
    public EnrollmentResponse enrol(final X509Certificate identity, final PrivateKey key, final PKCS10CertificationRequest csr, final String profile) throws ClientException, TransactionException {
        Client.LOGGER.debug("Enrolling certificate with CA");
        if (this.isSelfSigned(identity)) {
            Client.LOGGER.debug("Certificate is self-signed");
            final X500Name csrSubject = csr.getSubject();
            final X500Name idSubject = X500Utils.toX500Name(identity.getSubjectX500Principal());
            if (!csrSubject.equals((Object)idSubject)) {
                Client.LOGGER.error("The self-signed certificate MUST use the same subject name as in the PKCS#10 request.");
            }
        }
        final Transport transport = this.createTransport(profile);
        final PkiMessageEncoder encoder = this.getEncoder(identity, key, profile);
        final PkiMessageDecoder decoder = this.getDecoder(identity, key, profile);
        final EnrollmentTransaction trans = new EnrollmentTransaction(transport, encoder, decoder, csr);
        try {
            final MessageDigest digest = this.getCaCapabilities(profile).getStrongestMessageDigest();
            final byte[] hash = digest.digest(csr.getEncoded());
            Client.LOGGER.debug("{} PKCS#10 Fingerprint: [{}]", (Object)digest.getAlgorithm(), (Object)new String(Hex.encodeHex(hash)));
        }
        catch (final IOException e) {
            Client.LOGGER.error("Error getting encoded CSR", (Throwable)e);
        }
        return this.send(trans);
    }
    
    private boolean isSelfSigned(final X509Certificate cert) throws ClientException {
        try {
            final JcaX509CertificateHolder holder = new JcaX509CertificateHolder(cert);
            final ContentVerifierProvider verifierProvider = new JcaContentVerifierProviderBuilder().build((X509CertificateHolder)holder);
            return holder.isSignatureValid(verifierProvider);
        }
        catch (final RuntimeOperatorException e) {
            if (e.getCause() instanceof SignatureException) {
                Client.LOGGER.warn("SignatureException detected so we consider that the certificate is not self signed");
                return false;
            }
            throw new ClientException((Throwable)e);
        }
        catch (final Exception e2) {
            throw new ClientException(e2);
        }
    }
    
    public EnrollmentResponse poll(final X509Certificate identity, final PrivateKey identityKey, final X500Principal subject, final TransactionId transId) throws ClientException, TransactionException {
        return this.poll(identity, identityKey, subject, transId, null);
    }
    
    public EnrollmentResponse poll(final X509Certificate identity, final PrivateKey identityKey, final X500Principal subject, final TransactionId transId, final String profile) throws ClientException, TransactionException {
        final Transport transport = this.createTransport(profile);
        final CertStore store = this.getCaCertificate(profile);
        final CertStoreInspector certStore = this.inspectorFactory.getInstance(store);
        final X509Certificate issuer = certStore.getIssuer();
        final PkiMessageEncoder encoder = this.getEncoder(identity, identityKey, profile);
        final PkiMessageDecoder decoder = this.getDecoder(identity, identityKey, profile);
        final IssuerAndSubject ias = new IssuerAndSubject(X500Utils.toX500Name(issuer.getSubjectX500Principal()), X500Utils.toX500Name(subject));
        final EnrollmentTransaction trans = new EnrollmentTransaction(transport, encoder, decoder, ias, transId);
        return this.send(trans);
    }
    
    private EnrollmentResponse send(final EnrollmentTransaction trans) throws TransactionException {
        final Transaction.State s = trans.send();
        if (s == Transaction.State.CERT_ISSUED) {
            return new EnrollmentResponse(trans.getId(), trans.getCertStore());
        }
        if (s == Transaction.State.CERT_REQ_PENDING) {
            return new EnrollmentResponse(trans.getId());
        }
        return new EnrollmentResponse(trans.getId(), trans.getFailInfo());
    }
    
    private PkiMessageEncoder getEncoder(final X509Certificate identity, final PrivateKey priKey, final String profile) throws ClientException {
        final CertStore store = this.getCaCertificate(profile);
        final Capabilities caps = this.getCaCapabilities(profile);
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        final X509Certificate recipientCertificate = certs.getRecipient();
        final PkcsPkiEnvelopeEncoder envEncoder = new PkcsPkiEnvelopeEncoder(recipientCertificate, caps.getStrongestCipher());
        final String sigAlg = caps.getStrongestSignatureAlgorithm();
        return new PkiMessageEncoder(priKey, identity, envEncoder, sigAlg);
    }
    
    private PkiMessageDecoder getDecoder(final X509Certificate identity, final PrivateKey key, final String profile) throws ClientException {
        final CertStore store = this.getCaCertificate(profile);
        final CertStoreInspector certs = this.inspectorFactory.getInstance(store);
        final X509Certificate signer = certs.getSigner();
        final PkcsPkiEnvelopeDecoder envDecoder = new PkcsPkiEnvelopeDecoder(identity, key);
        return new PkiMessageDecoder(signer, envDecoder);
    }
    
    private Transport createTransport(final String profile) {
        if (this.getCaCapabilities(profile).isPostSupported()) {
            return this.transportFactory.forMethod(TransportFactory.Method.POST, this.url);
        }
        return this.transportFactory.forMethod(TransportFactory.Method.GET, this.url);
    }
    
    private void verifyCA(final X509Certificate cert) throws ClientException {
        final CertificateVerificationCallback callback = new CertificateVerificationCallback(cert);
        try {
            Client.LOGGER.debug("Requesting certificate verification.");
            final Callback[] callbacks = { callback };
            this.handler.handle(callbacks);
        }
        catch (final UnsupportedCallbackException e) {
            Client.LOGGER.debug("Certificate verification failed.");
            throw new ClientException(e);
        }
        catch (final IOException e2) {
            throw new ClientException(e2);
        }
        if (!callback.isVerified()) {
            Client.LOGGER.debug("Certificate verification failed.");
            throw new ClientException("CA certificate fingerprint could not be verified.");
        }
        Client.LOGGER.debug("Certificate verification passed.");
    }
    
    public synchronized void setCertStoreInspectorFactory(final CertStoreInspectorFactory inspectorFactory) {
        this.inspectorFactory = inspectorFactory;
    }
    
    public synchronized void setTransportFactory(final TransportFactory transportFactory) {
        this.transportFactory = transportFactory;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Client.class);
    }
}
