package org.jscep.server;

import org.slf4j.LoggerFactory;
import org.bouncycastle.util.encoders.Base64;
import org.apache.commons.io.IOUtils;
import java.security.PrivateKey;
import java.util.Iterator;
import java.util.Set;
import org.jscep.transport.response.Capability;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.cert.jcajce.JcaCRLStore;
import java.util.Collections;
import java.security.cert.X509CRL;
import java.security.GeneralSecurityException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSAbsentContent;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import java.io.IOException;
import java.util.List;
import java.math.BigInteger;
import org.jscep.transaction.TransactionId;
import java.util.Collection;
import org.bouncycastle.util.Store;
import org.jscep.message.MessageEncodingException;
import org.jscep.message.PkiMessage;
import org.jscep.message.PkiMessageEncoder;
import org.jscep.message.PkcsPkiEnvelopeEncoder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.jscep.asn1.IssuerAndSubject;
import org.jscep.transaction.OperationFailureException;
import org.jscep.message.CertRep;
import org.jscep.transaction.FailInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.message.MessageDecodingException;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkcsPkiEnvelopeDecoder;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import javax.servlet.ServletException;
import org.jscep.transport.request.Operation;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import javax.servlet.http.HttpServlet;

public abstract class ScepServlet extends HttpServlet
{
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String MSG_PARAM = "message";
    private static final String OP_PARAM = "operation";
    private static final Logger LOGGER;
    private static final long serialVersionUID = 1L;
    
    public final void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        final byte[] body = this.getMessageBytes(req);
        Operation op;
        try {
            op = this.getOperation(req);
            if (op == null) {
                res.sendError(400, "Missing \"operation\" parameter.");
                return;
            }
        }
        catch (final IllegalArgumentException e) {
            res.sendError(400, "Invalid \"operation\" parameter.");
            return;
        }
        ScepServlet.LOGGER.debug("Incoming Operation: " + op);
        final String reqMethod = req.getMethod();
        if (op == Operation.PKI_OPERATION) {
            if (!reqMethod.equals("POST") && !reqMethod.equals("GET")) {
                res.setStatus(405);
                res.addHeader("Allow", "GET, POST");
                return;
            }
        }
        else if (!reqMethod.equals("GET")) {
            res.setStatus(405);
            res.addHeader("Allow", "GET");
            return;
        }
        ScepServlet.LOGGER.debug("Method " + reqMethod + " Allowed for Operation: " + op);
        if (op == Operation.GET_CA_CAPS) {
            try {
                ScepServlet.LOGGER.debug("Invoking doGetCaCaps");
                this.doGetCaCaps(req, res);
                return;
            }
            catch (final Exception e2) {
                throw new ServletException((Throwable)e2);
            }
        }
        if (op == Operation.GET_CA_CERT) {
            try {
                ScepServlet.LOGGER.debug("Invoking doGetCaCert");
                this.doGetCaCert(req, res);
                return;
            }
            catch (final Exception e2) {
                throw new ServletException((Throwable)e2);
            }
        }
        if (op == Operation.GET_NEXT_CA_CERT) {
            try {
                ScepServlet.LOGGER.debug("Invoking doGetNextCaCert");
                this.doGetNextCaCert(req, res);
                return;
            }
            catch (final Exception e2) {
                throw new ServletException((Throwable)e2);
            }
        }
        if (op == Operation.PKI_OPERATION) {
            res.setHeader("Content-Type", "application/x-pki-message");
            CMSSignedData sd;
            try {
                sd = new CMSSignedData(body);
            }
            catch (final CMSException e3) {
                throw new ServletException((Throwable)e3);
            }
            final Store reqStore = sd.getCertificates();
            final Collection<X509CertificateHolder> reqCerts = reqStore.getMatches((Selector)null);
            CertificateFactory factory;
            try {
                factory = CertificateFactory.getInstance("X.509");
            }
            catch (final CertificateException e4) {
                throw new ServletException((Throwable)e4);
            }
            final X509CertificateHolder holder = reqCerts.iterator().next();
            final ByteArrayInputStream bais = new ByteArrayInputStream(holder.getEncoded());
            X509Certificate reqCert;
            try {
                reqCert = (X509Certificate)factory.generateCertificate(bais);
            }
            catch (final CertificateException e5) {
                throw new ServletException((Throwable)e5);
            }
            PkiMessage<?> msg;
            try {
                final PkcsPkiEnvelopeDecoder envDecoder = new PkcsPkiEnvelopeDecoder(this.getRecipient(), this.getRecipientKey());
                final PkiMessageDecoder decoder = new PkiMessageDecoder(reqCert, envDecoder);
                msg = decoder.decode(sd);
            }
            catch (final MessageDecodingException e6) {
                ScepServlet.LOGGER.error("Error decoding request", (Throwable)e6);
                throw new ServletException((Throwable)e6);
            }
            ScepServlet.LOGGER.debug("Processing message {}", (Object)msg);
            final MessageType msgType = msg.getMessageType();
            final Object msgData = msg.getMessageData();
            final Nonce senderNonce = Nonce.nextNonce();
            final TransactionId transId = msg.getTransactionId();
            final Nonce recipientNonce = msg.getSenderNonce();
            CertRep certRep;
            if (msgType == MessageType.GET_CERT) {
                final IssuerAndSerialNumber iasn = (IssuerAndSerialNumber)msgData;
                final X500Name principal = iasn.getName();
                final BigInteger serial = iasn.getSerialNumber().getValue();
                try {
                    final List<X509Certificate> issued = this.doGetCert(principal, serial);
                    if (issued.size() == 0) {
                        certRep = new CertRep(transId, senderNonce, recipientNonce, FailInfo.badCertId);
                    }
                    else {
                        final CMSSignedData messageData = this.getMessageData(issued);
                        certRep = new CertRep(transId, senderNonce, recipientNonce, messageData);
                    }
                }
                catch (final OperationFailureException e7) {
                    certRep = new CertRep(transId, senderNonce, recipientNonce, e7.getFailInfo());
                }
                catch (final Exception e8) {
                    throw new ServletException((Throwable)e8);
                }
            }
            else if (msgType == MessageType.GET_CERT_INITIAL) {
                final IssuerAndSubject ias = (IssuerAndSubject)msgData;
                final X500Name issuer = X500Name.getInstance((Object)ias.getIssuer());
                final X500Name subject = X500Name.getInstance((Object)ias.getSubject());
                try {
                    final List<X509Certificate> issued = this.doGetCertInitial(issuer, subject, transId);
                    if (issued.size() == 0) {
                        certRep = new CertRep(transId, senderNonce, recipientNonce);
                    }
                    else {
                        final CMSSignedData messageData = this.getMessageData(issued);
                        certRep = new CertRep(transId, senderNonce, recipientNonce, messageData);
                    }
                }
                catch (final OperationFailureException e7) {
                    certRep = new CertRep(transId, senderNonce, recipientNonce, e7.getFailInfo());
                }
                catch (final Exception e8) {
                    throw new ServletException((Throwable)e8);
                }
            }
            else if (msgType == MessageType.GET_CRL) {
                final IssuerAndSerialNumber iasn = (IssuerAndSerialNumber)msgData;
                final X500Name issuer = iasn.getName();
                final BigInteger serialNumber = iasn.getSerialNumber().getValue();
                try {
                    ScepServlet.LOGGER.debug("Invoking doGetCrl");
                    final CMSSignedData messageData2 = this.getMessageData(this.doGetCrl(issuer, serialNumber));
                    certRep = new CertRep(transId, senderNonce, recipientNonce, messageData2);
                }
                catch (final OperationFailureException e7) {
                    ScepServlet.LOGGER.error("Error executing GetCRL request", (Throwable)e7);
                    certRep = new CertRep(transId, senderNonce, recipientNonce, e7.getFailInfo());
                }
                catch (final Exception e8) {
                    ScepServlet.LOGGER.error("Error executing GetCRL request", (Throwable)e8);
                    throw new ServletException((Throwable)e8);
                }
            }
            else {
                if (msgType != MessageType.PKCS_REQ) {
                    throw new ServletException("Unknown Message for Operation");
                }
                final PKCS10CertificationRequest certReq = (PKCS10CertificationRequest)msgData;
                try {
                    ScepServlet.LOGGER.debug("Invoking doEnrol");
                    final List<X509Certificate> issued2 = this.doEnrol(certReq, reqCert, transId);
                    if (issued2.size() == 0) {
                        certRep = new CertRep(transId, senderNonce, recipientNonce);
                    }
                    else {
                        final CMSSignedData messageData3 = this.getMessageData(issued2);
                        certRep = new CertRep(transId, senderNonce, recipientNonce, messageData3);
                    }
                }
                catch (final OperationFailureException e9) {
                    certRep = new CertRep(transId, senderNonce, recipientNonce, e9.getFailInfo());
                }
                catch (final Exception e10) {
                    throw new ServletException((Throwable)e10);
                }
            }
            final PkcsPkiEnvelopeEncoder envEncoder = new PkcsPkiEnvelopeEncoder(reqCert, "DESede");
            final PkiMessageEncoder encoder = new PkiMessageEncoder(this.getSignerKey(), this.getSigner(), this.getSignerCertificateChain(), envEncoder);
            CMSSignedData signedData;
            try {
                signedData = encoder.encode(certRep);
            }
            catch (final MessageEncodingException e11) {
                ScepServlet.LOGGER.error("Error decoding response", (Throwable)e11);
                throw new ServletException((Throwable)e11);
            }
            res.getOutputStream().write(signedData.getEncoded());
            res.getOutputStream().close();
        }
        else {
            res.sendError(400, "Unknown Operation");
        }
    }
    
    private CMSSignedData getMessageData(final List<X509Certificate> certs) throws IOException, CMSException, GeneralSecurityException {
        final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        JcaCertStore store;
        try {
            store = new JcaCertStore((Collection)certs);
        }
        catch (final CertificateEncodingException e) {
            final IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
        generator.addCertificates((Store)store);
        return generator.generate((CMSTypedData)new CMSAbsentContent());
    }
    
    private CMSSignedData getMessageData(final X509CRL crl) throws IOException, CMSException, GeneralSecurityException {
        final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        JcaCRLStore store;
        if (crl == null) {
            store = new JcaCRLStore((Collection)Collections.emptyList());
        }
        else {
            store = new JcaCRLStore((Collection)Collections.singleton(crl));
        }
        generator.addCRLs((Store)store);
        return generator.generate((CMSTypedData)new CMSAbsentContent());
    }
    
    private void doGetNextCaCert(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        res.setHeader("Content-Type", "application/x-x509-next-ca-cert");
        final List<X509Certificate> certs = this.getNextCaCertificate(req.getParameter("message"));
        if (certs.size() == 0) {
            res.sendError(501, "GetNextCACert Not Supported");
        }
        else {
            final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            JcaCertStore store;
            try {
                store = new JcaCertStore((Collection)certs);
            }
            catch (final CertificateEncodingException e) {
                final IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
            generator.addCertificates((Store)store);
            final DigestCalculatorProvider digestProvider = new JcaDigestCalculatorProviderBuilder().build();
            final SignerInfoGeneratorBuilder infoGenBuilder = new SignerInfoGeneratorBuilder(digestProvider);
            final X509CertificateHolder certHolder = new X509CertificateHolder(this.getRecipient().getEncoded());
            final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").build(this.getRecipientKey());
            final SignerInfoGenerator infoGen = infoGenBuilder.build(contentSigner, certHolder);
            generator.addSignerInfoGenerator(infoGen);
            final CMSSignedData degenerateSd = generator.generate((CMSTypedData)new CMSAbsentContent());
            final byte[] bytes = degenerateSd.getEncoded();
            res.getOutputStream().write(bytes);
            res.getOutputStream().close();
        }
    }
    
    private void doGetCaCert(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        final List<X509Certificate> certs = this.doGetCaCertificate(req.getParameter("message"));
        byte[] bytes;
        if (certs.size() == 0) {
            res.sendError(500, "GetCaCert failed to obtain CA from store");
            bytes = new byte[0];
        }
        else if (certs.size() == 1) {
            res.setHeader("Content-Type", "application/x-x509-ca-cert");
            bytes = certs.get(0).getEncoded();
        }
        else {
            res.setHeader("Content-Type", "application/x-x509-ca-ra-cert");
            final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            JcaCertStore store;
            try {
                store = new JcaCertStore((Collection)certs);
            }
            catch (final CertificateEncodingException e) {
                final IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
            generator.addCertificates((Store)store);
            final CMSSignedData degenerateSd = generator.generate((CMSTypedData)new CMSAbsentContent());
            bytes = degenerateSd.getEncoded();
        }
        res.getOutputStream().write(bytes);
        res.getOutputStream().close();
    }
    
    private Operation getOperation(final HttpServletRequest req) {
        final String op = req.getParameter("operation");
        if (op == null) {
            return null;
        }
        return Operation.forName(req.getParameter("operation"));
    }
    
    private void doGetCaCaps(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        res.setHeader("Content-Type", "text/plain");
        final Set<Capability> caps = this.doCapabilities(req.getParameter("message"));
        for (final Capability cap : caps) {
            res.getWriter().write(cap.toString());
            res.getWriter().write(10);
        }
        res.getWriter().close();
    }
    
    protected abstract Set<Capability> doCapabilities(final String p0) throws Exception;
    
    protected abstract List<X509Certificate> doGetCaCertificate(final String p0) throws Exception;
    
    protected abstract List<X509Certificate> getNextCaCertificate(final String p0) throws Exception;
    
    protected abstract List<X509Certificate> doGetCert(final X500Name p0, final BigInteger p1) throws Exception;
    
    protected abstract List<X509Certificate> doGetCertInitial(final X500Name p0, final X500Name p1, final TransactionId p2) throws Exception;
    
    protected abstract X509CRL doGetCrl(final X500Name p0, final BigInteger p1) throws Exception;
    
    protected abstract List<X509Certificate> doEnrol(final PKCS10CertificationRequest p0, final X509Certificate p1, final TransactionId p2) throws Exception;
    
    protected abstract PrivateKey getRecipientKey();
    
    protected abstract X509Certificate getRecipient();
    
    protected abstract PrivateKey getSignerKey();
    
    protected abstract X509Certificate getSigner();
    
    protected abstract X509Certificate[] getSignerCertificateChain();
    
    private byte[] getMessageBytes(final HttpServletRequest req) throws IOException {
        if (req.getMethod().equals("POST")) {
            return IOUtils.toByteArray((InputStream)req.getInputStream());
        }
        Operation op;
        try {
            op = this.getOperation(req);
        }
        catch (final IllegalArgumentException e) {
            return new byte[0];
        }
        if (op != Operation.PKI_OPERATION) {
            return new byte[0];
        }
        final String msg = req.getParameter("message");
        if (msg.length() == 0) {
            return new byte[0];
        }
        if (ScepServlet.LOGGER.isDebugEnabled()) {
            ScepServlet.LOGGER.debug("Decoding {}", (Object)msg);
        }
        return Base64.decode(this.fixBrokenBase64(msg));
    }
    
    private String fixBrokenBase64(final String base64) {
        return base64.replace(' ', '+');
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ScepServlet.class);
    }
}
