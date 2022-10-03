package com.unboundid.util.ssl;

import com.unboundid.util.ssl.cert.CertException;
import com.unboundid.util.ObjectPair;
import java.security.cert.CertificateException;
import java.io.InputStreamReader;
import java.util.Map;
import java.security.cert.Certificate;
import java.io.IOException;
import java.util.Iterator;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.unboundid.util.Debug;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.PrintStream;
import java.util.List;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PromptTrustManager implements X509TrustManager
{
    private static final X509Certificate[] NO_CERTIFICATES;
    private final boolean examineValidityDates;
    private final ConcurrentHashMap<String, Boolean> acceptedCerts;
    private final InputStream in;
    private final List<String> expectedAddresses;
    private final PrintStream out;
    private final String acceptedCertsFile;
    
    public PromptTrustManager() {
        this(null, true, null, null);
    }
    
    public PromptTrustManager(final String acceptedCertsFile) {
        this(acceptedCertsFile, true, null, null);
    }
    
    public PromptTrustManager(final String acceptedCertsFile, final boolean examineValidityDates, final InputStream in, final PrintStream out) {
        this(acceptedCertsFile, examineValidityDates, (Collection<String>)Collections.emptyList(), in, out);
    }
    
    public PromptTrustManager(final String acceptedCertsFile, final boolean examineValidityDates, final String expectedAddress, final InputStream in, final PrintStream out) {
        this(acceptedCertsFile, examineValidityDates, (expectedAddress == null) ? Collections.emptyList() : Collections.singletonList(expectedAddress), in, out);
    }
    
    public PromptTrustManager(final String acceptedCertsFile, final boolean examineValidityDates, final Collection<String> expectedAddresses, final InputStream in, final PrintStream out) {
        this.acceptedCertsFile = acceptedCertsFile;
        this.examineValidityDates = examineValidityDates;
        if (expectedAddresses == null) {
            this.expectedAddresses = Collections.emptyList();
        }
        else {
            this.expectedAddresses = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(expectedAddresses));
        }
        if (in == null) {
            this.in = System.in;
        }
        else {
            this.in = in;
        }
        if (out == null) {
            this.out = System.out;
        }
        else {
            this.out = out;
        }
        this.acceptedCerts = new ConcurrentHashMap<String, Boolean>(StaticUtils.computeMapCapacity(20));
        if (acceptedCertsFile != null) {
            BufferedReader r = null;
            try {
                final File f = new File(acceptedCertsFile);
                if (f.exists()) {
                    r = new BufferedReader(new FileReader(f));
                    while (true) {
                        final String line = r.readLine();
                        if (line == null) {
                            break;
                        }
                        this.acceptedCerts.put(line, false);
                    }
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
            }
            finally {
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
            }
        }
    }
    
    private void writeCacheFile() throws IOException {
        final File tempFile = new File(this.acceptedCertsFile + ".new");
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(tempFile));
            for (final String certBytes : this.acceptedCerts.keySet()) {
                w.write(certBytes);
                w.newLine();
            }
        }
        finally {
            if (w != null) {
                w.close();
            }
        }
        final File cacheFile = new File(this.acceptedCertsFile);
        if (cacheFile.exists()) {
            final File oldFile = new File(this.acceptedCertsFile + ".previous");
            if (oldFile.exists()) {
                Files.delete(oldFile.toPath());
            }
            Files.move(cacheFile.toPath(), oldFile.toPath(), new CopyOption[0]);
        }
        Files.move(tempFile.toPath(), cacheFile.toPath(), new CopyOption[0]);
    }
    
    public synchronized boolean wouldPrompt(final X509Certificate[] chain) {
        try {
            final String cacheKey = getCacheKey(chain[0]);
            return PromptTrustManagerProcessor.shouldPrompt(cacheKey, convertChain(chain), false, this.examineValidityDates, this.acceptedCerts, null).getFirst();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    private synchronized void checkCertificateChain(final X509Certificate[] chain, final boolean serverCert) throws CertificateException {
        final com.unboundid.util.ssl.cert.X509Certificate[] convertedChain = convertChain(chain);
        final String cacheKey = getCacheKey(chain[0]);
        final ObjectPair<Boolean, List<String>> shouldPromptResult = PromptTrustManagerProcessor.shouldPrompt(cacheKey, convertedChain, serverCert, this.examineValidityDates, this.acceptedCerts, this.expectedAddresses);
        if (!shouldPromptResult.getFirst()) {
            return;
        }
        if (serverCert) {
            this.out.println(SSLMessages.INFO_PROMPT_SERVER_HEADING.get());
        }
        else {
            this.out.println(SSLMessages.INFO_PROMPT_CLIENT_HEADING.get());
        }
        this.out.println();
        this.out.println("     " + SSLMessages.INFO_PROMPT_SUBJECT.get(convertedChain[0].getSubjectDN()));
        this.out.println("     " + SSLMessages.INFO_PROMPT_VALID_FROM.get(PromptTrustManagerProcessor.formatDate(convertedChain[0].getNotBeforeDate())));
        this.out.println("     " + SSLMessages.INFO_PROMPT_VALID_TO.get(PromptTrustManagerProcessor.formatDate(convertedChain[0].getNotAfterDate())));
        try {
            final byte[] sha1Fingerprint = convertedChain[0].getSHA1Fingerprint();
            final StringBuilder buffer = new StringBuilder();
            StaticUtils.toHex(sha1Fingerprint, ":", buffer);
            this.out.println("     " + SSLMessages.INFO_PROMPT_SHA1_FINGERPRINT.get(buffer));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            final byte[] sha256Fingerprint = convertedChain[0].getSHA256Fingerprint();
            final StringBuilder buffer = new StringBuilder();
            StaticUtils.toHex(sha256Fingerprint, ":", buffer);
            this.out.println("     " + SSLMessages.INFO_PROMPT_SHA256_FINGERPRINT.get(buffer));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        for (int i = 1; i < chain.length; ++i) {
            this.out.println("     -");
            this.out.println("     " + SSLMessages.INFO_PROMPT_ISSUER_SUBJECT.get(i, convertedChain[i].getSubjectDN()));
            this.out.println("     " + SSLMessages.INFO_PROMPT_VALID_FROM.get(PromptTrustManagerProcessor.formatDate(convertedChain[i].getNotBeforeDate())));
            this.out.println("     " + SSLMessages.INFO_PROMPT_VALID_TO.get(PromptTrustManagerProcessor.formatDate(convertedChain[i].getNotAfterDate())));
            try {
                final byte[] sha1Fingerprint2 = convertedChain[i].getSHA1Fingerprint();
                final StringBuilder buffer2 = new StringBuilder();
                StaticUtils.toHex(sha1Fingerprint2, ":", buffer2);
                this.out.println("     " + SSLMessages.INFO_PROMPT_SHA1_FINGERPRINT.get(buffer2));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
            try {
                final byte[] sha256Fingerprint2 = convertedChain[i].getSHA256Fingerprint();
                final StringBuilder buffer2 = new StringBuilder();
                StaticUtils.toHex(sha256Fingerprint2, ":", buffer2);
                this.out.println("     " + SSLMessages.INFO_PROMPT_SHA256_FINGERPRINT.get(buffer2));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
        for (final String warningMessage : shouldPromptResult.getSecond()) {
            this.out.println();
            for (final String line : StaticUtils.wrapLine(warningMessage, StaticUtils.TERMINAL_WIDTH_COLUMNS - 1)) {
                this.out.println(line);
            }
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));
        while (true) {
            try {
                while (true) {
                    this.out.println();
                    this.out.print(SSLMessages.INFO_PROMPT_MESSAGE.get() + ' ');
                    this.out.flush();
                    final String line2 = reader.readLine();
                    if (line2 == null) {
                        throw new CertificateException(SSLMessages.ERR_CERTIFICATE_REJECTED_BY_END_OF_STREAM.get(SSLUtil.certificateToString(chain[0])));
                    }
                    if (line2.equalsIgnoreCase("y") || line2.equalsIgnoreCase("yes")) {
                        break;
                    }
                    if (line2.equalsIgnoreCase("n") || line2.equalsIgnoreCase("no")) {
                        throw new CertificateException(SSLMessages.ERR_CERTIFICATE_REJECTED_BY_USER.get(SSLUtil.certificateToString(chain[0])));
                    }
                }
            }
            catch (final CertificateException ce) {
                throw ce;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                continue;
            }
            break;
        }
        boolean isOutsideValidityWindow = false;
        for (final com.unboundid.util.ssl.cert.X509Certificate c : convertedChain) {
            if (!c.isWithinValidityWindow()) {
                isOutsideValidityWindow = true;
                break;
            }
        }
        this.acceptedCerts.put(cacheKey, isOutsideValidityWindow);
        if (this.acceptedCertsFile != null) {
            try {
                this.writeCacheFile();
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
            }
        }
    }
    
    public boolean examineValidityDates() {
        return this.examineValidityDates;
    }
    
    public List<String> getExpectedAddresses() {
        return this.expectedAddresses;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkCertificateChain(chain, false);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkCertificateChain(chain, true);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return PromptTrustManager.NO_CERTIFICATES;
    }
    
    static String getCacheKey(final Certificate certificate) {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        return StaticUtils.toLowerCase(StaticUtils.toHex(x509Certificate.getSignature()));
    }
    
    static com.unboundid.util.ssl.cert.X509Certificate[] convertChain(final Certificate[] chain) throws CertificateException {
        final com.unboundid.util.ssl.cert.X509Certificate[] convertedChain = new com.unboundid.util.ssl.cert.X509Certificate[chain.length];
        for (int i = 0; i < chain.length; ++i) {
            try {
                convertedChain[i] = new com.unboundid.util.ssl.cert.X509Certificate(chain[i].getEncoded());
            }
            catch (final CertException ce) {
                Debug.debugException(ce);
                throw new CertificateException(ce.getMessage(), ce);
            }
        }
        return convertedChain;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
