package com.lowagie.text.pdf;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.io.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.net.URL;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.tsp.TimeStampRequest;
import com.lowagie.text.error_messages.MessageLocalization;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import java.math.BigInteger;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

public class TSAClientBouncyCastle implements TSAClient
{
    protected String tsaURL;
    protected String tsaUsername;
    protected String tsaPassword;
    protected int tokSzEstimate;
    
    public TSAClientBouncyCastle(final String url) {
        this(url, null, null, 4096);
    }
    
    public TSAClientBouncyCastle(final String url, final String username, final String password) {
        this(url, username, password, 4096);
    }
    
    public TSAClientBouncyCastle(final String url, final String username, final String password, final int tokSzEstimate) {
        this.tsaURL = url;
        this.tsaUsername = username;
        this.tsaPassword = password;
        this.tokSzEstimate = tokSzEstimate;
    }
    
    @Override
    public int getTokenSizeEstimate() {
        return this.tokSzEstimate;
    }
    
    @Override
    public byte[] getTimeStampToken(final PdfPKCS7 caller, final byte[] imprint) throws Exception {
        return this.getTimeStampToken(imprint);
    }
    
    protected byte[] getTimeStampToken(final byte[] imprint) throws Exception {
        byte[] respBytes = null;
        try {
            final TimeStampRequestGenerator tsqGenerator = new TimeStampRequestGenerator();
            tsqGenerator.setCertReq(true);
            final BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
            final TimeStampRequest request = tsqGenerator.generate(X509ObjectIdentifiers.id_SHA1.getId(), imprint, nonce);
            final byte[] requestBytes = request.getEncoded();
            respBytes = this.getTSAResponse(requestBytes);
            final TimeStampResponse response = new TimeStampResponse(respBytes);
            response.validate(request);
            final PKIFailureInfo failure = response.getFailInfo();
            final int value = (failure == null) ? 0 : failure.intValue();
            if (value != 0) {
                throw new Exception(MessageLocalization.getComposedMessage("invalid.tsa.1.response.code.2", this.tsaURL, String.valueOf(value)));
            }
            final TimeStampToken tsToken = response.getTimeStampToken();
            if (tsToken == null) {
                throw new Exception(MessageLocalization.getComposedMessage("tsa.1.failed.to.return.time.stamp.token.2", this.tsaURL, response.getStatusString()));
            }
            final TimeStampTokenInfo info = tsToken.getTimeStampInfo();
            final byte[] encoded = tsToken.getEncoded();
            final long stop = System.currentTimeMillis();
            this.tokSzEstimate = encoded.length + 32;
            return encoded;
        }
        catch (final Exception e) {
            throw e;
        }
        catch (final Throwable t) {
            throw new Exception(MessageLocalization.getComposedMessage("failed.to.get.tsa.response.from.1", this.tsaURL), t);
        }
    }
    
    protected byte[] getTSAResponse(final byte[] requestBytes) throws Exception {
        final URL url = new URL(this.tsaURL);
        final URLConnection tsaConnection = url.openConnection();
        tsaConnection.setDoInput(true);
        tsaConnection.setDoOutput(true);
        tsaConnection.setUseCaches(false);
        tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary");
        if (this.tsaUsername != null && !this.tsaUsername.equals("")) {
            final String userPassword = this.tsaUsername + ":" + this.tsaPassword;
            tsaConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(userPassword.getBytes())));
        }
        final OutputStream out = tsaConnection.getOutputStream();
        out.write(requestBytes);
        out.close();
        final InputStream inp = tsaConnection.getInputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inp.read(buffer, 0, buffer.length)) >= 0) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] respBytes = baos.toByteArray();
        final String encoding = tsaConnection.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("base64")) {
            respBytes = Base64.decodeBase64(respBytes);
        }
        return respBytes;
    }
}
