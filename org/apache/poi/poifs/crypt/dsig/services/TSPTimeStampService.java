package org.apache.poi.poifs.crypt.dsig.services;

import org.apache.poi.util.POILogFactory;
import org.bouncycastle.cms.SignerInformationVerifier;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.tsp.TimeStampRequest;
import java.security.MessageDigest;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x500.X500Name;
import java.util.HashMap;
import org.bouncycastle.util.Selector;
import org.bouncycastle.tsp.TimeStampResponse;
import org.apache.poi.util.HexDump;
import java.io.OutputStream;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.net.HttpURLConnection;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.URL;
import java.net.Proxy;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.util.POILogger;

public class TSPTimeStampService implements TimeStampService
{
    private static final POILogger LOG;
    private SignatureConfig signatureConfig;
    
    public ASN1ObjectIdentifier mapDigestAlgoToOID(final HashAlgorithm digestAlgo) {
        switch (digestAlgo) {
            case sha1: {
                return X509ObjectIdentifiers.id_SHA1;
            }
            case sha256: {
                return NISTObjectIdentifiers.id_sha256;
            }
            case sha384: {
                return NISTObjectIdentifiers.id_sha384;
            }
            case sha512: {
                return NISTObjectIdentifiers.id_sha512;
            }
            default: {
                throw new IllegalArgumentException("unsupported digest algo: " + digestAlgo);
            }
        }
    }
    
    @Override
    public byte[] timeStamp(final byte[] data, final RevocationData revocationData) throws Exception {
        final MessageDigest messageDigest = CryptoFunctions.getMessageDigest(this.signatureConfig.getTspDigestAlgo());
        final byte[] digest = messageDigest.digest(data);
        final BigInteger nonce = new BigInteger(128, new SecureRandom());
        final TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
        requestGenerator.setCertReq(true);
        final String requestPolicy = this.signatureConfig.getTspRequestPolicy();
        if (requestPolicy != null) {
            requestGenerator.setReqPolicy(new ASN1ObjectIdentifier(requestPolicy));
        }
        final ASN1ObjectIdentifier digestAlgoOid = this.mapDigestAlgoToOID(this.signatureConfig.getTspDigestAlgo());
        final TimeStampRequest request = requestGenerator.generate(digestAlgoOid, digest, nonce);
        final byte[] encodedRequest = request.getEncoded();
        Proxy proxy = Proxy.NO_PROXY;
        if (this.signatureConfig.getProxyUrl() != null) {
            final URL proxyUrl = new URL(this.signatureConfig.getProxyUrl());
            final String host = proxyUrl.getHost();
            final int port = proxyUrl.getPort();
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(host), (port == -1) ? 80 : port));
        }
        final HttpURLConnection huc = (HttpURLConnection)new URL(this.signatureConfig.getTspUrl()).openConnection(proxy);
        String contentType;
        ByteArrayOutputStream bos;
        try {
            if (this.signatureConfig.getTspUser() != null) {
                final String userPassword = this.signatureConfig.getTspUser() + ":" + this.signatureConfig.getTspPass();
                final String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.ISO_8859_1));
                huc.setRequestProperty("Authorization", "Basic " + encoding);
            }
            huc.setRequestMethod("POST");
            huc.setConnectTimeout(20000);
            huc.setReadTimeout(20000);
            huc.setDoOutput(true);
            huc.setRequestProperty("User-Agent", this.signatureConfig.getUserAgent());
            huc.setRequestProperty("Content-Type", this.signatureConfig.isTspOldProtocol() ? "application/timestamp-request" : "application/timestamp-query");
            final OutputStream hucOut = huc.getOutputStream();
            hucOut.write(encodedRequest);
            huc.connect();
            final int statusCode = huc.getResponseCode();
            if (statusCode != 200) {
                TSPTimeStampService.LOG.log(7, new Object[] { "Error contacting TSP server ", this.signatureConfig.getTspUrl() + ", had status code " + statusCode + "/" + huc.getResponseMessage() });
                throw new IOException("Error contacting TSP server " + this.signatureConfig.getTspUrl() + ", had status code " + statusCode + "/" + huc.getResponseMessage());
            }
            contentType = huc.getHeaderField("Content-Type");
            if (null == contentType) {
                throw new RuntimeException("missing Content-Type header");
            }
            bos = new ByteArrayOutputStream();
            IOUtils.copy(huc.getInputStream(), (OutputStream)bos);
            TSPTimeStampService.LOG.log(1, new Object[] { "response content: ", HexDump.dump(bos.toByteArray(), 0L, 0) });
        }
        finally {
            huc.disconnect();
        }
        if (!contentType.startsWith(this.signatureConfig.isTspOldProtocol() ? "application/timestamp-response" : "application/timestamp-reply")) {
            throw new RuntimeException("invalid Content-Type: " + contentType + ": " + HexDump.dump(bos.toByteArray(), 0L, 0, 200));
        }
        if (bos.size() == 0) {
            throw new RuntimeException("Content-Length is zero");
        }
        final TimeStampResponse timeStampResponse = new TimeStampResponse(bos.toByteArray());
        timeStampResponse.validate(request);
        if (0 != timeStampResponse.getStatus()) {
            TSPTimeStampService.LOG.log(1, new Object[] { "status: " + timeStampResponse.getStatus() });
            TSPTimeStampService.LOG.log(1, new Object[] { "status string: " + timeStampResponse.getStatusString() });
            final PKIFailureInfo failInfo = timeStampResponse.getFailInfo();
            if (null != failInfo) {
                TSPTimeStampService.LOG.log(1, new Object[] { "fail info int value: " + failInfo.intValue() });
                if (256 == failInfo.intValue()) {
                    TSPTimeStampService.LOG.log(1, new Object[] { "unaccepted policy" });
                }
            }
            throw new RuntimeException("timestamp response status != 0: " + timeStampResponse.getStatus());
        }
        final TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
        final SignerId signerId = timeStampToken.getSID();
        final BigInteger signerCertSerialNumber = signerId.getSerialNumber();
        final X500Name signerCertIssuer = signerId.getIssuer();
        TSPTimeStampService.LOG.log(1, new Object[] { "signer cert serial number: " + signerCertSerialNumber });
        TSPTimeStampService.LOG.log(1, new Object[] { "signer cert issuer: " + signerCertIssuer });
        final Collection<X509CertificateHolder> certificates = timeStampToken.getCertificates().getMatches((Selector)null);
        X509CertificateHolder signerCert = null;
        final Map<X500Name, X509CertificateHolder> certificateMap = new HashMap<X500Name, X509CertificateHolder>();
        for (final X509CertificateHolder certificate : certificates) {
            if (signerCertIssuer.equals((Object)certificate.getIssuer()) && signerCertSerialNumber.equals(certificate.getSerialNumber())) {
                signerCert = certificate;
            }
            certificateMap.put(certificate.getSubject(), certificate);
        }
        if (signerCert == null) {
            throw new RuntimeException("TSP response token has no signer certificate");
        }
        final List<X509Certificate> tspCertificateChain = new ArrayList<X509Certificate>();
        final JcaX509CertificateConverter x509converter = new JcaX509CertificateConverter();
        x509converter.setProvider("BC");
        X509CertificateHolder certificate2 = signerCert;
        do {
            TSPTimeStampService.LOG.log(1, new Object[] { "adding to certificate chain: " + certificate2.getSubject() });
            tspCertificateChain.add(x509converter.getCertificate(certificate2));
            if (certificate2.getSubject().equals((Object)certificate2.getIssuer())) {
                break;
            }
            certificate2 = certificateMap.get(certificate2.getIssuer());
        } while (null != certificate2);
        final X509CertificateHolder holder = new X509CertificateHolder(tspCertificateChain.get(0).getEncoded());
        final DefaultCMSSignatureAlgorithmNameGenerator nameGen = new DefaultCMSSignatureAlgorithmNameGenerator();
        final DefaultSignatureAlgorithmIdentifierFinder sigAlgoFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        final DefaultDigestAlgorithmIdentifierFinder hashAlgoFinder = new DefaultDigestAlgorithmIdentifierFinder();
        final BcDigestCalculatorProvider calculator = new BcDigestCalculatorProvider();
        final BcRSASignerInfoVerifierBuilder verifierBuilder = new BcRSASignerInfoVerifierBuilder((CMSSignatureAlgorithmNameGenerator)nameGen, (SignatureAlgorithmIdentifierFinder)sigAlgoFinder, (DigestAlgorithmIdentifierFinder)hashAlgoFinder, (DigestCalculatorProvider)calculator);
        final SignerInformationVerifier verifier = verifierBuilder.build(holder);
        timeStampToken.validate(verifier);
        if (this.signatureConfig.getTspValidator() != null) {
            this.signatureConfig.getTspValidator().validate(tspCertificateChain, revocationData);
        }
        TSPTimeStampService.LOG.log(1, new Object[] { "time-stamp token time: " + timeStampToken.getTimeStampInfo().getGenTime() });
        return timeStampToken.getEncoded();
    }
    
    @Override
    public void setSignatureConfig(final SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)TSPTimeStampService.class);
    }
}
