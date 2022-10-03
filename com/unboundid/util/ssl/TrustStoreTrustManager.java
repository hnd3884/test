package com.unboundid.util.ssl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import com.unboundid.util.StaticUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import com.unboundid.util.Debug;
import java.security.cert.CertificateException;
import java.util.Date;
import java.security.KeyStore;
import com.unboundid.util.Validator;
import java.io.File;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TrustStoreTrustManager implements X509TrustManager, Serializable
{
    private static final X509Certificate[] NO_CERTIFICATES;
    private static final long serialVersionUID = -4093869102727719415L;
    private final boolean examineValidityDates;
    private final char[] trustStorePIN;
    private final String trustStoreFile;
    private final String trustStoreFormat;
    
    public TrustStoreTrustManager(final File trustStoreFile) {
        this(trustStoreFile.getAbsolutePath(), null, null, true);
    }
    
    public TrustStoreTrustManager(final String trustStoreFile) {
        this(trustStoreFile, null, null, true);
    }
    
    public TrustStoreTrustManager(final File trustStoreFile, final char[] trustStorePIN, final String trustStoreFormat, final boolean examineValidityDates) {
        this(trustStoreFile.getAbsolutePath(), trustStorePIN, trustStoreFormat, examineValidityDates);
    }
    
    public TrustStoreTrustManager(final String trustStoreFile, final char[] trustStorePIN, final String trustStoreFormat, final boolean examineValidityDates) {
        Validator.ensureNotNull(trustStoreFile);
        this.trustStoreFile = trustStoreFile;
        this.trustStorePIN = trustStorePIN;
        this.examineValidityDates = examineValidityDates;
        if (trustStoreFormat == null) {
            this.trustStoreFormat = KeyStore.getDefaultType();
        }
        else {
            this.trustStoreFormat = trustStoreFormat;
        }
    }
    
    public String getTrustStoreFile() {
        return this.trustStoreFile;
    }
    
    public String getTrustStoreFormat() {
        return this.trustStoreFormat;
    }
    
    public boolean examineValidityDates() {
        return this.examineValidityDates;
    }
    
    private synchronized X509TrustManager[] getTrustManagers(final X509Certificate[] chain) throws CertificateException {
        if (this.examineValidityDates) {
            final Date d = new Date();
            for (final X509Certificate c : chain) {
                c.checkValidity(d);
            }
        }
        final File f = new File(this.trustStoreFile);
        if (!f.exists()) {
            throw new CertificateException(SSLMessages.ERR_TRUSTSTORE_NO_SUCH_FILE.get(this.trustStoreFile));
        }
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(this.trustStoreFormat);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertificateException(SSLMessages.ERR_TRUSTSTORE_UNSUPPORTED_FORMAT.get(this.trustStoreFormat), e);
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            ks.load(inputStream, this.trustStorePIN);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertificateException(SSLMessages.ERR_TRUSTSTORE_CANNOT_LOAD.get(this.trustStoreFile, this.trustStoreFormat, StaticUtils.getExceptionMessage(e2)), e2);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
        }
        try {
            final TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(ks);
            final TrustManager[] trustManagers = factory.getTrustManagers();
            final X509TrustManager[] x509TrustManagers = new X509TrustManager[trustManagers.length];
            for (int i = 0; i < trustManagers.length; ++i) {
                x509TrustManagers[i] = (X509TrustManager)trustManagers[i];
            }
            return x509TrustManagers;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertificateException(SSLMessages.ERR_TRUSTSTORE_CANNOT_GET_TRUST_MANAGERS.get(this.trustStoreFile, this.trustStoreFormat, StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public synchronized void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        for (final X509TrustManager m : this.getTrustManagers(chain)) {
            m.checkClientTrusted(chain, authType);
        }
    }
    
    @Override
    public synchronized void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        for (final X509TrustManager m : this.getTrustManagers(chain)) {
            m.checkServerTrusted(chain, authType);
        }
    }
    
    @Override
    public synchronized X509Certificate[] getAcceptedIssuers() {
        return TrustStoreTrustManager.NO_CERTIFICATES;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
