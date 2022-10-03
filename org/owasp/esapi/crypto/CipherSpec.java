package org.owasp.esapi.crypto;

import java.io.UnsupportedEncodingException;
import org.owasp.esapi.util.NullSafe;
import org.owasp.esapi.StringUtilities;
import javax.crypto.Cipher;
import org.owasp.esapi.ESAPI;
import java.io.Serializable;

public final class CipherSpec implements Serializable
{
    private static final long serialVersionUID = 20090822L;
    private String cipher_xform_;
    private int keySize_;
    private int blockSize_;
    private byte[] iv_;
    private boolean blockSizeExplicitlySet;
    
    public CipherSpec(final String cipherXform, final int keySize, final int blockSize, final byte[] iv) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setCipherTransformation(cipherXform);
        this.setKeySize(keySize);
        this.setBlockSize(blockSize);
        this.setIV(iv);
    }
    
    public CipherSpec(final String cipherXform, final int keySize, final int blockSize) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setCipherTransformation(cipherXform);
        this.setKeySize(keySize);
        this.setBlockSize(blockSize);
    }
    
    public CipherSpec(final String cipherXform, final int keySize) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setCipherTransformation(cipherXform);
        this.setKeySize(keySize);
    }
    
    public CipherSpec(final String cipherXform, final int keySize, final byte[] iv) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setCipherTransformation(cipherXform);
        this.setKeySize(keySize);
        this.setIV(iv);
    }
    
    public CipherSpec(final Cipher cipher) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setCipherTransformation(cipher.getAlgorithm(), true);
        this.setBlockSize(cipher.getBlockSize());
        if (cipher.getIV() != null) {
            this.setIV(cipher.getIV());
        }
    }
    
    public CipherSpec(final Cipher cipher, final int keySize) {
        this(cipher);
        this.setKeySize(keySize);
    }
    
    public CipherSpec(final byte[] iv) {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
        this.setIV(iv);
    }
    
    public CipherSpec() {
        this.cipher_xform_ = ESAPI.securityConfiguration().getCipherTransformation();
        this.keySize_ = ESAPI.securityConfiguration().getEncryptionKeyLength();
        this.blockSize_ = 16;
        this.iv_ = null;
        this.blockSizeExplicitlySet = false;
    }
    
    public CipherSpec setCipherTransformation(final String cipherXform) {
        this.setCipherTransformation(cipherXform, false);
        return this;
    }
    
    private CipherSpec setCipherTransformation(String cipherXform, final boolean fromCipher) {
        if (!StringUtilities.notNullOrEmpty(cipherXform, true)) {
            throw new IllegalArgumentException("Cipher transformation may not be null or empty string (after trimming whitespace).");
        }
        final int parts = cipherXform.split("/").length;
        assert parts == 3 : "Malformed cipherXform (" + cipherXform + "); must have form: \"alg/mode/paddingscheme\"";
        if (fromCipher && parts != 3) {
            if (parts == 1) {
                cipherXform += "/ECB/NoPadding";
            }
            else if (parts == 2) {
                cipherXform += "/NoPadding";
            }
            else if (parts != 3) {
                throw new IllegalArgumentException("Cipher transformation '" + cipherXform + "' must have form \"alg/mode/paddingscheme\"");
            }
        }
        else if (!fromCipher && parts != 3) {
            throw new IllegalArgumentException("Malformed cipherXform (" + cipherXform + "); must have form: \"alg/mode/paddingscheme\"");
        }
        assert cipherXform.split("/").length == 3 : "Implementation error setCipherTransformation()";
        this.cipher_xform_ = cipherXform;
        return this;
    }
    
    public String getCipherTransformation() {
        return this.cipher_xform_;
    }
    
    public CipherSpec setKeySize(final int keySize) {
        if (keySize <= 0) {
            throw new IllegalArgumentException("keySize must be > 0; keySize=" + keySize);
        }
        this.keySize_ = keySize;
        return this;
    }
    
    public int getKeySize() {
        return this.keySize_;
    }
    
    public CipherSpec setBlockSize(final int blockSize) {
        if (blockSize <= 0) {
            throw new IllegalArgumentException("blockSize must be > 0; blockSize=" + blockSize);
        }
        this.blockSize_ = blockSize;
        this.blockSizeExplicitlySet = true;
        return this;
    }
    
    public int getBlockSize() {
        return this.blockSize_;
    }
    
    public String getCipherAlgorithm() {
        return this.getFromCipherXform(CipherTransformationComponent.ALG);
    }
    
    public String getCipherMode() {
        return this.getFromCipherXform(CipherTransformationComponent.MODE);
    }
    
    public String getPaddingScheme() {
        return this.getFromCipherXform(CipherTransformationComponent.PADDING);
    }
    
    public byte[] getIV() {
        return this.iv_;
    }
    
    public CipherSpec setIV(final byte[] iv) {
        if (!this.requiresIV() || iv == null || iv.length == 0) {
            throw new IllegalArgumentException("Required IV cannot be null or 0 length.");
        }
        if (iv != null) {
            CryptoHelper.copyByteArray(iv, this.iv_ = new byte[iv.length]);
        }
        return this;
    }
    
    public boolean requiresIV() {
        final String cm = this.getCipherMode();
        return !"ECB".equalsIgnoreCase(cm);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CipherSpec: ");
        sb.append(this.getCipherTransformation()).append("; keysize= ").append(this.getKeySize());
        sb.append(" bits; blocksize= ").append(this.getBlockSize()).append(" bytes");
        final byte[] iv = this.getIV();
        String ivLen = null;
        if (iv != null) {
            ivLen = "" + iv.length;
        }
        else {
            ivLen = "[No IV present (not set or not required)]";
        }
        sb.append("; IV length = ").append(ivLen).append(" bytes.");
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object other) {
        boolean result = false;
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof CipherSpec) {
            final CipherSpec that = (CipherSpec)other;
            result = (that.canEqual(this) && NullSafe.equals(this.cipher_xform_, that.cipher_xform_) && this.keySize_ == that.keySize_ && this.blockSize_ == that.blockSize_ && CryptoHelper.arrayCompare(this.iv_, that.iv_));
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getCipherTransformation());
        sb.append("" + this.getKeySize());
        sb.append("" + this.getBlockSize());
        final byte[] iv = this.getIV();
        if (iv != null && iv.length > 0) {
            String ivStr = null;
            try {
                ivStr = new String(iv, "UTF-8");
            }
            catch (final UnsupportedEncodingException ex) {
                ivStr = new String(iv);
            }
            sb.append(ivStr);
        }
        return sb.toString().hashCode();
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof CipherSpec;
    }
    
    private String getFromCipherXform(final CipherTransformationComponent component) {
        final int part = component.ordinal();
        final String[] parts = this.getCipherTransformation().split("/");
        assert parts.length == 3 : "Invalid cipher transformation: " + this.getCipherTransformation();
        return parts[part];
    }
    
    private enum CipherTransformationComponent
    {
        ALG, 
        MODE, 
        PADDING;
    }
}
