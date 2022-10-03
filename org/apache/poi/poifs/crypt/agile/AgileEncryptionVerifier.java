package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.common.Duplicatable;
import java.util.function.Consumer;
import java.util.function.Function;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import java.util.Iterator;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.EncryptedDocumentException;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import java.util.ArrayList;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import java.util.List;
import org.apache.poi.poifs.crypt.EncryptionVerifier;

public class AgileEncryptionVerifier extends EncryptionVerifier
{
    private final List<AgileCertificateEntry> certList;
    private int keyBits;
    private int blockSize;
    
    public AgileEncryptionVerifier(final String descriptor) {
        this(AgileEncryptionInfoBuilder.parseDescriptor(descriptor));
    }
    
    protected AgileEncryptionVerifier(final EncryptionDocument ed) {
        this.certList = new ArrayList<AgileCertificateEntry>();
        this.keyBits = -1;
        this.blockSize = -1;
        final Iterator<CTKeyEncryptor> encList = ed.getEncryption().getKeyEncryptors().getKeyEncryptorList().iterator();
        CTPasswordKeyEncryptor keyData;
        try {
            keyData = encList.next().getEncryptedPasswordKey();
            if (keyData == null) {
                throw new NullPointerException("encryptedKey not set");
            }
        }
        catch (final Exception e) {
            throw new EncryptedDocumentException("Unable to parse keyData", (Throwable)e);
        }
        final int kb = (int)keyData.getKeyBits();
        final CipherAlgorithm ca = CipherAlgorithm.fromXmlId(keyData.getCipherAlgorithm().toString(), kb);
        this.setCipherAlgorithm(ca);
        this.setKeySize(kb);
        final int blockSize = keyData.getBlockSize();
        this.setBlockSize(blockSize);
        final int hashSize = keyData.getHashSize();
        final HashAlgorithm ha = HashAlgorithm.fromEcmaId(keyData.getHashAlgorithm().toString());
        this.setHashAlgorithm(ha);
        if (this.getHashAlgorithm().hashSize != hashSize) {
            throw new EncryptedDocumentException("Unsupported hash algorithm: " + keyData.getHashAlgorithm() + " @ " + hashSize + " bytes");
        }
        this.setSpinCount(keyData.getSpinCount());
        this.setEncryptedVerifier(keyData.getEncryptedVerifierHashInput());
        this.setSalt(keyData.getSaltValue());
        this.setEncryptedKey(keyData.getEncryptedKeyValue());
        this.setEncryptedVerifierHash(keyData.getEncryptedVerifierHashValue());
        final int saltSize = keyData.getSaltSize();
        if (saltSize != this.getSalt().length) {
            throw new EncryptedDocumentException("Invalid salt size");
        }
        switch (keyData.getCipherChaining().intValue()) {
            case 1: {
                this.setChainingMode(ChainingMode.cbc);
                break;
            }
            case 2: {
                this.setChainingMode(ChainingMode.cfb);
                break;
            }
            default: {
                throw new EncryptedDocumentException("Unsupported chaining mode - " + keyData.getCipherChaining());
            }
        }
        if (!encList.hasNext()) {
            return;
        }
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            while (encList.hasNext()) {
                final CTCertificateKeyEncryptor certKey = encList.next().getEncryptedCertificateKey();
                final AgileCertificateEntry ace = new AgileCertificateEntry();
                ace.certVerifier = certKey.getCertVerifier();
                ace.encryptedKey = certKey.getEncryptedKeyValue();
                ace.x509 = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(certKey.getX509Certificate()));
                this.certList.add(ace);
            }
        }
        catch (final GeneralSecurityException e2) {
            throw new EncryptedDocumentException("can't parse X509 certificate", (Throwable)e2);
        }
    }
    
    public AgileEncryptionVerifier(final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.certList = new ArrayList<AgileCertificateEntry>();
        this.keyBits = -1;
        this.blockSize = -1;
        this.setCipherAlgorithm(cipherAlgorithm);
        this.setHashAlgorithm(hashAlgorithm);
        this.setChainingMode(chainingMode);
        this.setKeySize(keyBits);
        this.setBlockSize(blockSize);
        this.setSpinCount(100000);
    }
    
    public AgileEncryptionVerifier(final AgileEncryptionVerifier other) {
        super((EncryptionVerifier)other);
        this.certList = new ArrayList<AgileCertificateEntry>();
        this.keyBits = -1;
        this.blockSize = -1;
        this.keyBits = other.keyBits;
        this.blockSize = other.blockSize;
        other.certList.stream().map((Function<? super Object, ?>)AgileCertificateEntry::new).forEach(this.certList::add);
    }
    
    protected void setSalt(final byte[] salt) {
        if (salt == null || salt.length != this.getCipherAlgorithm().blockSize) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
        super.setSalt(salt);
    }
    
    protected void setEncryptedVerifier(final byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }
    
    protected void setEncryptedVerifierHash(final byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }
    
    protected void setEncryptedKey(final byte[] encryptedKey) {
        super.setEncryptedKey(encryptedKey);
    }
    
    public void addCertificate(final X509Certificate x509) {
        final AgileCertificateEntry ace = new AgileCertificateEntry();
        ace.x509 = x509;
        this.certList.add(ace);
    }
    
    public List<AgileCertificateEntry> getCertificates() {
        return this.certList;
    }
    
    public AgileEncryptionVerifier copy() {
        return new AgileEncryptionVerifier(this);
    }
    
    public int getKeySize() {
        return this.keyBits;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    protected void setKeySize(final int keyBits) {
        this.keyBits = keyBits;
        for (final int allowedBits : this.getCipherAlgorithm().allowedKeySize) {
            if (allowedBits == keyBits) {
                return;
            }
        }
        throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for cipher " + this.getCipherAlgorithm());
    }
    
    protected void setBlockSize(final int blockSize) {
        this.blockSize = blockSize;
    }
    
    protected final void setCipherAlgorithm(final CipherAlgorithm cipherAlgorithm) {
        super.setCipherAlgorithm(cipherAlgorithm);
        if (cipherAlgorithm.allowedKeySize.length == 1) {
            this.setKeySize(cipherAlgorithm.defaultKeySize);
        }
    }
    
    public static class AgileCertificateEntry
    {
        X509Certificate x509;
        byte[] encryptedKey;
        byte[] certVerifier;
        
        public AgileCertificateEntry() {
        }
        
        public AgileCertificateEntry(final AgileCertificateEntry other) {
            this.x509 = other.x509;
            this.encryptedKey = (byte[])((other.encryptedKey == null) ? null : ((byte[])other.encryptedKey.clone()));
            this.certVerifier = (byte[])((other.certVerifier == null) ? null : ((byte[])other.certVerifier.clone()));
        }
    }
}
