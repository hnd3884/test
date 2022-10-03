package com.lowagie.text.pdf;

import org.bouncycastle.cert.ocsp.SingleResp;
import com.lowagie.text.ExceptionConverter;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;
import java.io.InputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.security.Provider;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import java.util.Random;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.cert.ocsp.OCSPReq;
import java.math.BigInteger;
import java.security.cert.X509Certificate;

public class OcspClientBouncyCastle implements OcspClient
{
    private final X509Certificate rootCert;
    private final X509Certificate checkCert;
    private final String url;
    
    public OcspClientBouncyCastle(final X509Certificate checkCert, final X509Certificate rootCert, final String url) {
        this.checkCert = checkCert;
        this.rootCert = rootCert;
        this.url = url;
    }
    
    private static OCSPReq generateOCSPRequest(final X509Certificate issuerCert, final BigInteger serialNumber) throws OCSPException, IOException, OperatorCreationException, CertificateEncodingException {
        final Provider prov = (Provider)new BouncyCastleProvider();
        Security.addProvider(prov);
        final DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider(prov).build();
        final CertificateID id = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), (X509CertificateHolder)new JcaX509CertificateHolder(issuerCert), serialNumber);
        final OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(id);
        final ExtensionsGenerator extGen = new ExtensionsGenerator();
        final byte[] nonce = new byte[16];
        final Random rand = new Random();
        rand.nextBytes(nonce);
        extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, (ASN1Encodable)new DEROctetString(nonce));
        gen.setRequestExtensions(extGen.generate());
        return gen.build();
    }
    
    @Override
    public byte[] getEncoded() {
        try {
            final OCSPReq request = generateOCSPRequest(this.rootCert, this.checkCert.getSerialNumber());
            final byte[] array = request.getEncoded();
            final URL urlt = new URL(this.url);
            final HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
            con.setRequestProperty("Content-Type", "application/ocsp-request");
            con.setRequestProperty("Accept", "application/ocsp-response");
            con.setDoOutput(true);
            final OutputStream out = con.getOutputStream();
            final DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.write(array);
            dataOut.flush();
            dataOut.close();
            if (con.getResponseCode() / 100 != 2) {
                throw new IOException(MessageLocalization.getComposedMessage("invalid.http.response.1", con.getResponseCode()));
            }
            final InputStream in = (InputStream)con.getContent();
            final OCSPResp ocspResponse = new OCSPResp(in);
            if (ocspResponse.getStatus() != 0) {
                throw new IOException(MessageLocalization.getComposedMessage("invalid.status.1", ocspResponse.getStatus()));
            }
            final BasicOCSPResp basicResponse = (BasicOCSPResp)ocspResponse.getResponseObject();
            if (basicResponse != null) {
                final SingleResp[] responses = basicResponse.getResponses();
                if (responses.length == 1) {
                    final SingleResp resp = responses[0];
                    final Object status = resp.getCertStatus();
                    if (status == null) {
                        return basicResponse.getEncoded();
                    }
                    if (status instanceof RevokedStatus) {
                        throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.revoked"));
                    }
                    throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.unknown"));
                }
            }
        }
        catch (final Exception ex) {
            throw new ExceptionConverter(ex);
        }
        return null;
    }
}
