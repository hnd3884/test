package org.bouncycastle.est;

import java.util.Collections;
import java.util.HashSet;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Iterator;
import java.util.HashMap;
import org.bouncycastle.util.encoders.Hex;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import java.io.InputStream;
import org.bouncycastle.util.Strings;
import java.util.Set;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.security.SecureRandom;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class HttpAuth implements ESTAuth
{
    private static final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder;
    private final String realm;
    private final String username;
    private final char[] password;
    private final SecureRandom nonceGenerator;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private static final Set<String> validParts;
    
    public HttpAuth(final String s, final char[] array) {
        this(null, s, array, null, null);
    }
    
    public HttpAuth(final String s, final String s2, final char[] array) {
        this(s, s2, array, null, null);
    }
    
    public HttpAuth(final String s, final char[] array, final SecureRandom secureRandom, final DigestCalculatorProvider digestCalculatorProvider) {
        this(null, s, array, secureRandom, digestCalculatorProvider);
    }
    
    public HttpAuth(final String realm, final String username, final char[] password, final SecureRandom nonceGenerator, final DigestCalculatorProvider digestCalculatorProvider) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.nonceGenerator = nonceGenerator;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }
    
    public void applyAuth(final ESTRequestBuilder estRequestBuilder) {
        estRequestBuilder.withHijacker(new ESTHijacker() {
            public ESTResponse hijack(final ESTRequest estRequest, final Source source) throws IOException {
                final ESTResponse estResponse = new ESTResponse(estRequest, source);
                if (estResponse.getStatusCode() != 401) {
                    return estResponse;
                }
                final String header = estResponse.getHeader("WWW-Authenticate");
                if (header == null) {
                    throw new ESTException("Status of 401 but no WWW-Authenticate header");
                }
                final String lowerCase = Strings.toLowerCase(header);
                ESTResponse estResponse2;
                if (lowerCase.startsWith("digest")) {
                    estResponse2 = HttpAuth.this.doDigestFunction(estResponse);
                }
                else {
                    if (!lowerCase.startsWith("basic")) {
                        throw new ESTException("Unknown auth mode: " + lowerCase);
                    }
                    estResponse.close();
                    final Map<String, String> splitCSL = HttpUtil.splitCSL("Basic", estResponse.getHeader("WWW-Authenticate"));
                    if (HttpAuth.this.realm != null && !HttpAuth.this.realm.equals(splitCSL.get("realm"))) {
                        throw new ESTException("Supplied realm '" + HttpAuth.this.realm + "' does not match server realm '" + splitCSL.get("realm") + "'", null, 401, null);
                    }
                    final ESTRequestBuilder withHijacker = new ESTRequestBuilder(estRequest).withHijacker(null);
                    if (HttpAuth.this.realm != null && HttpAuth.this.realm.length() > 0) {
                        withHijacker.setHeader("WWW-Authenticate", "Basic realm=\"" + HttpAuth.this.realm + "\"");
                    }
                    if (HttpAuth.this.username.contains(":")) {
                        throw new IllegalArgumentException("User must not contain a ':'");
                    }
                    final char[] array = new char[HttpAuth.this.username.length() + 1 + HttpAuth.this.password.length];
                    System.arraycopy(HttpAuth.this.username.toCharArray(), 0, array, 0, HttpAuth.this.username.length());
                    array[HttpAuth.this.username.length()] = ':';
                    System.arraycopy(HttpAuth.this.password, 0, array, HttpAuth.this.username.length() + 1, HttpAuth.this.password.length);
                    withHijacker.setHeader("Authorization", "Basic " + Base64.toBase64String(Strings.toByteArray(array)));
                    estResponse2 = estRequest.getClient().doRequest(withHijacker.build());
                    Arrays.fill(array, '\0');
                }
                return estResponse2;
            }
        });
    }
    
    private ESTResponse doDigestFunction(final ESTResponse estResponse) throws IOException {
        estResponse.close();
        final ESTRequest originalRequest = estResponse.getOriginalRequest();
        Map<String, String> splitCSL;
        try {
            splitCSL = HttpUtil.splitCSL("Digest", estResponse.getHeader("WWW-Authenticate"));
        }
        catch (final Throwable t) {
            throw new ESTException("Parsing WWW-Authentication header: " + t.getMessage(), t, estResponse.getStatusCode(), new ByteArrayInputStream(estResponse.getHeader("WWW-Authenticate").getBytes()));
        }
        String path;
        try {
            path = originalRequest.getURL().toURI().getPath();
        }
        catch (final Exception ex) {
            throw new IOException("unable to process URL in request: " + ex.getMessage());
        }
        for (final String next : splitCSL.keySet()) {
            if (!HttpAuth.validParts.contains(next)) {
                throw new ESTException("Unrecognised entry in WWW-Authenticate header: '" + (Object)next + "'");
            }
        }
        final String method = originalRequest.getMethod();
        final String s = splitCSL.get("realm");
        final String s2 = splitCSL.get("nonce");
        final String s3 = splitCSL.get("opaque");
        String s4 = splitCSL.get("algorithm");
        final String s5 = splitCSL.get("qop");
        final ArrayList list = new ArrayList();
        if (this.realm != null && !this.realm.equals(s)) {
            throw new ESTException("Supplied realm '" + this.realm + "' does not match server realm '" + s + "'", null, 401, null);
        }
        if (s4 == null) {
            s4 = "MD5";
        }
        if (s4.length() == 0) {
            throw new ESTException("WWW-Authenticate no algorithm defined.");
        }
        final String upperCase = Strings.toUpperCase(s4);
        if (s5 == null) {
            throw new ESTException("Qop is not defined in WWW-Authenticate header.");
        }
        if (s5.length() == 0) {
            throw new ESTException("QoP value is empty.");
        }
        final String[] split = Strings.toLowerCase(s5).split(",");
        for (int i = 0; i != split.length; ++i) {
            if (!split[i].equals("auth") && !split[i].equals("auth-int")) {
                throw new ESTException("QoP value unknown: '" + i + "'");
            }
            final String trim = split[i].trim();
            if (!list.contains(trim)) {
                list.add(trim);
            }
        }
        final AlgorithmIdentifier lookupDigest = this.lookupDigest(upperCase);
        if (lookupDigest == null || lookupDigest.getAlgorithm() == null) {
            throw new IOException("auth digest algorithm unknown: " + upperCase);
        }
        final DigestCalculator digestCalculator = this.getDigestCalculator(upperCase, lookupDigest);
        final OutputStream outputStream = digestCalculator.getOutputStream();
        final String nonce = this.makeNonce(10);
        this.update(outputStream, this.username);
        this.update(outputStream, ":");
        this.update(outputStream, s);
        this.update(outputStream, ":");
        this.update(outputStream, this.password);
        outputStream.close();
        byte[] array = digestCalculator.getDigest();
        if (upperCase.endsWith("-SESS")) {
            final DigestCalculator digestCalculator2 = this.getDigestCalculator(upperCase, lookupDigest);
            final OutputStream outputStream2 = digestCalculator2.getOutputStream();
            this.update(outputStream2, Hex.toHexString(array));
            this.update(outputStream2, ":");
            this.update(outputStream2, s2);
            this.update(outputStream2, ":");
            this.update(outputStream2, nonce);
            outputStream2.close();
            array = digestCalculator2.getDigest();
        }
        final String hexString = Hex.toHexString(array);
        final DigestCalculator digestCalculator3 = this.getDigestCalculator(upperCase, lookupDigest);
        final OutputStream outputStream3 = digestCalculator3.getOutputStream();
        if (((String)list.get(0)).equals("auth-int")) {
            final DigestCalculator digestCalculator4 = this.getDigestCalculator(upperCase, lookupDigest);
            final OutputStream outputStream4 = digestCalculator4.getOutputStream();
            originalRequest.writeData(outputStream4);
            outputStream4.close();
            final byte[] digest = digestCalculator4.getDigest();
            this.update(outputStream3, method);
            this.update(outputStream3, ":");
            this.update(outputStream3, path);
            this.update(outputStream3, ":");
            this.update(outputStream3, Hex.toHexString(digest));
        }
        else if (((String)list.get(0)).equals("auth")) {
            this.update(outputStream3, method);
            this.update(outputStream3, ":");
            this.update(outputStream3, path);
        }
        outputStream3.close();
        final String hexString2 = Hex.toHexString(digestCalculator3.getDigest());
        final DigestCalculator digestCalculator5 = this.getDigestCalculator(upperCase, lookupDigest);
        final OutputStream outputStream5 = digestCalculator5.getOutputStream();
        if (list.contains("missing")) {
            this.update(outputStream5, hexString);
            this.update(outputStream5, ":");
            this.update(outputStream5, s2);
            this.update(outputStream5, ":");
            this.update(outputStream5, hexString2);
        }
        else {
            this.update(outputStream5, hexString);
            this.update(outputStream5, ":");
            this.update(outputStream5, s2);
            this.update(outputStream5, ":");
            this.update(outputStream5, "00000001");
            this.update(outputStream5, ":");
            this.update(outputStream5, nonce);
            this.update(outputStream5, ":");
            if (((String)list.get(0)).equals("auth-int")) {
                this.update(outputStream5, "auth-int");
            }
            else {
                this.update(outputStream5, "auth");
            }
            this.update(outputStream5, ":");
            this.update(outputStream5, hexString2);
        }
        outputStream5.close();
        final String hexString3 = Hex.toHexString(digestCalculator5.getDigest());
        final HashMap hashMap = new HashMap();
        hashMap.put("username", this.username);
        hashMap.put("realm", s);
        hashMap.put("nonce", s2);
        hashMap.put("uri", path);
        hashMap.put("response", hexString3);
        if (((String)list.get(0)).equals("auth-int")) {
            hashMap.put("qop", "auth-int");
            hashMap.put("nc", "00000001");
            hashMap.put("cnonce", nonce);
        }
        else if (((String)list.get(0)).equals("auth")) {
            hashMap.put("qop", "auth");
            hashMap.put("nc", "00000001");
            hashMap.put("cnonce", nonce);
        }
        hashMap.put("algorithm", upperCase);
        if (s3 == null || s3.length() == 0) {
            hashMap.put("opaque", this.makeNonce(20));
        }
        final ESTRequestBuilder withHijacker = new ESTRequestBuilder(originalRequest).withHijacker(null);
        withHijacker.setHeader("Authorization", HttpUtil.mergeCSL("Digest", hashMap));
        return originalRequest.getClient().doRequest(withHijacker.build());
    }
    
    private DigestCalculator getDigestCalculator(final String s, final AlgorithmIdentifier algorithmIdentifier) throws IOException {
        DigestCalculator value;
        try {
            value = this.digestCalculatorProvider.get(algorithmIdentifier);
        }
        catch (final OperatorCreationException ex) {
            throw new IOException("cannot create digest calculator for " + s + ": " + ex.getMessage());
        }
        return value;
    }
    
    private AlgorithmIdentifier lookupDigest(String substring) {
        if (substring.endsWith("-SESS")) {
            substring = substring.substring(0, substring.length() - "-SESS".length());
        }
        if (substring.equals("SHA-512-256")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE);
        }
        return HttpAuth.digestAlgorithmIdentifierFinder.find(substring);
    }
    
    private void update(final OutputStream outputStream, final char[] array) throws IOException {
        outputStream.write(Strings.toUTF8ByteArray(array));
    }
    
    private void update(final OutputStream outputStream, final String s) throws IOException {
        outputStream.write(Strings.toUTF8ByteArray(s));
    }
    
    private String makeNonce(final int n) {
        final byte[] array = new byte[n];
        this.nonceGenerator.nextBytes(array);
        return Hex.toHexString(array);
    }
    
    static {
        digestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
        final HashSet set = new HashSet();
        set.add("realm");
        set.add("nonce");
        set.add("opaque");
        set.add("algorithm");
        set.add("qop");
        validParts = Collections.unmodifiableSet((Set<?>)set);
    }
}
