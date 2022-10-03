package com.lowagie.text.pdf;

import java.security.cert.Certificate;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import java.security.MessageDigest;

public class PdfEncryption
{
    public static final int STANDARD_ENCRYPTION_40 = 2;
    public static final int STANDARD_ENCRYPTION_128 = 3;
    public static final int AES_128 = 4;
    private static final byte[] pad;
    private static final byte[] salt;
    private static final byte[] metadataPad;
    byte[] key;
    int keySize;
    byte[] mkey;
    byte[] extra;
    MessageDigest md5;
    byte[] ownerKey;
    byte[] userKey;
    protected PdfPublicKeySecurityHandler publicKeyHandler;
    int permissions;
    byte[] documentID;
    static long seq;
    private int revision;
    private ARCFOUREncryption arcfour;
    private int keyLength;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;
    private int cryptoMode;
    
    public PdfEncryption() {
        this.extra = new byte[5];
        this.ownerKey = new byte[32];
        this.userKey = new byte[32];
        this.publicKeyHandler = null;
        this.arcfour = new ARCFOUREncryption();
        try {
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        this.publicKeyHandler = new PdfPublicKeySecurityHandler();
    }
    
    public PdfEncryption(final PdfEncryption enc) {
        this();
        this.mkey = enc.mkey.clone();
        this.ownerKey = enc.ownerKey.clone();
        this.userKey = enc.userKey.clone();
        this.permissions = enc.permissions;
        if (enc.documentID != null) {
            this.documentID = enc.documentID.clone();
        }
        this.revision = enc.revision;
        this.keyLength = enc.keyLength;
        this.encryptMetadata = enc.encryptMetadata;
        this.embeddedFilesOnly = enc.embeddedFilesOnly;
        this.publicKeyHandler = enc.publicKeyHandler;
    }
    
    public void setCryptoMode(int mode, final int kl) {
        this.cryptoMode = mode;
        this.encryptMetadata = ((mode & 0x8) == 0x0);
        this.embeddedFilesOnly = ((mode & 0x18) != 0x0);
        mode &= 0x7;
        switch (mode) {
            case 0: {
                this.encryptMetadata = true;
                this.embeddedFilesOnly = false;
                this.keyLength = 40;
                this.revision = 2;
                break;
            }
            case 1: {
                this.embeddedFilesOnly = false;
                if (kl > 0) {
                    this.keyLength = kl;
                }
                else {
                    this.keyLength = 128;
                }
                this.revision = 3;
                break;
            }
            case 2: {
                this.keyLength = 128;
                this.revision = 4;
                break;
            }
            default: {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("no.valid.encryption.mode"));
            }
        }
    }
    
    public int getCryptoMode() {
        return this.cryptoMode;
    }
    
    public boolean isMetadataEncrypted() {
        return this.encryptMetadata;
    }
    
    public boolean isEmbeddedFilesOnly() {
        return this.embeddedFilesOnly;
    }
    
    private byte[] padPassword(final byte[] userPassword) {
        final byte[] userPad = new byte[32];
        if (userPassword == null) {
            System.arraycopy(PdfEncryption.pad, 0, userPad, 0, 32);
        }
        else {
            System.arraycopy(userPassword, 0, userPad, 0, Math.min(userPassword.length, 32));
            if (userPassword.length < 32) {
                System.arraycopy(PdfEncryption.pad, 0, userPad, userPassword.length, 32 - userPassword.length);
            }
        }
        return userPad;
    }
    
    private byte[] computeOwnerKey(final byte[] userPad, final byte[] ownerPad) {
        final byte[] ownerKey = new byte[32];
        final byte[] digest = this.md5.digest(ownerPad);
        if (this.revision == 3 || this.revision == 4) {
            final byte[] mkey = new byte[this.keyLength / 8];
            for (int k = 0; k < 50; ++k) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, mkey.length);
            }
            System.arraycopy(userPad, 0, ownerKey, 0, 32);
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < mkey.length; ++j) {
                    mkey[j] = (byte)(digest[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(mkey);
                this.arcfour.encryptARCFOUR(ownerKey);
            }
        }
        else {
            this.arcfour.prepareARCFOURKey(digest, 0, 5);
            this.arcfour.encryptARCFOUR(userPad, ownerKey);
        }
        return ownerKey;
    }
    
    private void setupGlobalEncryptionKey(final byte[] documentID, final byte[] userPad, final byte[] ownerKey, final int permissions) {
        this.documentID = documentID;
        this.ownerKey = ownerKey;
        this.permissions = permissions;
        this.mkey = new byte[this.keyLength / 8];
        this.md5.reset();
        this.md5.update(userPad);
        this.md5.update(ownerKey);
        final byte[] ext = { (byte)permissions, (byte)(permissions >> 8), (byte)(permissions >> 16), (byte)(permissions >> 24) };
        this.md5.update(ext, 0, 4);
        if (documentID != null) {
            this.md5.update(documentID);
        }
        if (!this.encryptMetadata) {
            this.md5.update(PdfEncryption.metadataPad);
        }
        final byte[] digest = new byte[this.mkey.length];
        System.arraycopy(this.md5.digest(), 0, digest, 0, this.mkey.length);
        if (this.revision == 3 || this.revision == 4) {
            for (int k = 0; k < 50; ++k) {
                System.arraycopy(this.md5.digest(digest), 0, digest, 0, this.mkey.length);
            }
        }
        System.arraycopy(digest, 0, this.mkey, 0, this.mkey.length);
    }
    
    private void setupUserKey() {
        if (this.revision == 3 || this.revision == 4) {
            this.md5.update(PdfEncryption.pad);
            final byte[] digest = this.md5.digest(this.documentID);
            System.arraycopy(digest, 0, this.userKey, 0, 16);
            for (int k = 16; k < 32; ++k) {
                this.userKey[k] = 0;
            }
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < this.mkey.length; ++j) {
                    digest[j] = (byte)(this.mkey[j] ^ i);
                }
                this.arcfour.prepareARCFOURKey(digest, 0, this.mkey.length);
                this.arcfour.encryptARCFOUR(this.userKey, 0, 16);
            }
        }
        else {
            this.arcfour.prepareARCFOURKey(this.mkey);
            this.arcfour.encryptARCFOUR(PdfEncryption.pad, this.userKey);
        }
    }
    
    public void setupAllKeys(final byte[] userPassword, byte[] ownerPassword, int permissions) {
        if (ownerPassword == null || ownerPassword.length == 0) {
            ownerPassword = this.md5.digest(createDocumentId());
        }
        permissions |= ((this.revision == 3 || this.revision == 4) ? -3904 : -64);
        permissions &= 0xFFFFFFFC;
        final byte[] userPad = this.padPassword(userPassword);
        final byte[] ownerPad = this.padPassword(ownerPassword);
        this.ownerKey = this.computeOwnerKey(userPad, ownerPad);
        this.setupByUserPad(this.documentID = createDocumentId(), userPad, this.ownerKey, permissions);
    }
    
    public static byte[] createDocumentId() {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        final long time = System.currentTimeMillis();
        final long mem = Runtime.getRuntime().freeMemory();
        final String s = time + "+" + mem + "+" + PdfEncryption.seq++;
        return md5.digest(s.getBytes());
    }
    
    public void setupByUserPassword(final byte[] documentID, final byte[] userPassword, final byte[] ownerKey, final int permissions) {
        this.setupByUserPad(documentID, this.padPassword(userPassword), ownerKey, permissions);
    }
    
    private void setupByUserPad(final byte[] documentID, final byte[] userPad, final byte[] ownerKey, final int permissions) {
        this.setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        this.setupUserKey();
    }
    
    public void setupByOwnerPassword(final byte[] documentID, final byte[] ownerPassword, final byte[] userKey, final byte[] ownerKey, final int permissions) {
        this.setupByOwnerPad(documentID, this.padPassword(ownerPassword), userKey, ownerKey, permissions);
    }
    
    private void setupByOwnerPad(final byte[] documentID, final byte[] ownerPad, final byte[] userKey, final byte[] ownerKey, final int permissions) {
        final byte[] userPad = this.computeOwnerKey(ownerKey, ownerPad);
        this.setupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions);
        this.setupUserKey();
    }
    
    public void setupByEncryptionKey(final byte[] key, final int keylength) {
        System.arraycopy(key, 0, this.mkey = new byte[keylength / 8], 0, this.mkey.length);
    }
    
    public void setHashKey(final int number, final int generation) {
        this.md5.reset();
        this.extra[0] = (byte)number;
        this.extra[1] = (byte)(number >> 8);
        this.extra[2] = (byte)(number >> 16);
        this.extra[3] = (byte)generation;
        this.extra[4] = (byte)(generation >> 8);
        this.md5.update(this.mkey);
        this.md5.update(this.extra);
        if (this.revision == 4) {
            this.md5.update(PdfEncryption.salt);
        }
        this.key = this.md5.digest();
        this.keySize = this.mkey.length + 5;
        if (this.keySize > 16) {
            this.keySize = 16;
        }
    }
    
    public static PdfObject createInfoId(byte[] id) {
        final ByteBuffer buf = new ByteBuffer(90);
        buf.append('[').append('<');
        for (int k = 0; k < 16; ++k) {
            buf.appendHex(id[k]);
        }
        buf.append('>').append('<');
        id = createDocumentId();
        for (int k = 0; k < 16; ++k) {
            buf.appendHex(id[k]);
        }
        buf.append('>').append(']');
        return new PdfLiteral(buf.toByteArray());
    }
    
    public PdfDictionary getEncryptionDictionary() {
        final PdfDictionary dic = new PdfDictionary();
        if (this.publicKeyHandler.getRecipientsSize() > 0) {
            PdfArray recipients = null;
            dic.put(PdfName.FILTER, PdfName.PUBSEC);
            dic.put(PdfName.R, new PdfNumber(this.revision));
            try {
                recipients = this.publicKeyHandler.getEncodedRecipients();
            }
            catch (final Exception f) {
                throw new ExceptionConverter(f);
            }
            if (this.revision == 2) {
                dic.put(PdfName.V, new PdfNumber(1));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                dic.put(PdfName.RECIPIENTS, recipients);
            }
            else if (this.revision == 3 && this.encryptMetadata) {
                dic.put(PdfName.V, new PdfNumber(2));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S4);
                dic.put(PdfName.RECIPIENTS, recipients);
            }
            else {
                dic.put(PdfName.R, new PdfNumber(4));
                dic.put(PdfName.V, new PdfNumber(4));
                dic.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_S5);
                final PdfDictionary stdcf = new PdfDictionary();
                stdcf.put(PdfName.RECIPIENTS, recipients);
                if (!this.encryptMetadata) {
                    stdcf.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                if (this.revision == 4) {
                    stdcf.put(PdfName.CFM, PdfName.AESV2);
                }
                else {
                    stdcf.put(PdfName.CFM, PdfName.V2);
                }
                final PdfDictionary cf = new PdfDictionary();
                cf.put(PdfName.DEFAULTCRYPTFILTER, stdcf);
                dic.put(PdfName.CF, cf);
                if (this.embeddedFilesOnly) {
                    dic.put(PdfName.EFF, PdfName.DEFAULTCRYPTFILTER);
                    dic.put(PdfName.STRF, PdfName.IDENTITY);
                    dic.put(PdfName.STMF, PdfName.IDENTITY);
                }
                else {
                    dic.put(PdfName.STRF, PdfName.DEFAULTCRYPTFILTER);
                    dic.put(PdfName.STMF, PdfName.DEFAULTCRYPTFILTER);
                }
            }
            MessageDigest md = null;
            byte[] encodedRecipient = null;
            try {
                md = MessageDigest.getInstance("SHA-1");
                md.update(this.publicKeyHandler.getSeed());
                for (int i = 0; i < this.publicKeyHandler.getRecipientsSize(); ++i) {
                    encodedRecipient = this.publicKeyHandler.getEncodedRecipient(i);
                    md.update(encodedRecipient);
                }
                if (!this.encryptMetadata) {
                    md.update(new byte[] { -1, -1, -1, -1 });
                }
            }
            catch (final Exception f2) {
                throw new ExceptionConverter(f2);
            }
            final byte[] mdResult = md.digest();
            this.setupByEncryptionKey(mdResult, this.keyLength);
        }
        else {
            dic.put(PdfName.FILTER, PdfName.STANDARD);
            dic.put(PdfName.O, new PdfLiteral(PdfContentByte.escapeString(this.ownerKey)));
            dic.put(PdfName.U, new PdfLiteral(PdfContentByte.escapeString(this.userKey)));
            dic.put(PdfName.P, new PdfNumber(this.permissions));
            dic.put(PdfName.R, new PdfNumber(this.revision));
            if (this.revision == 2) {
                dic.put(PdfName.V, new PdfNumber(1));
            }
            else if (this.revision == 3 && this.encryptMetadata) {
                dic.put(PdfName.V, new PdfNumber(2));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
            }
            else {
                if (!this.encryptMetadata) {
                    dic.put(PdfName.ENCRYPTMETADATA, PdfBoolean.PDFFALSE);
                }
                dic.put(PdfName.R, new PdfNumber(4));
                dic.put(PdfName.V, new PdfNumber(4));
                dic.put(PdfName.LENGTH, new PdfNumber(128));
                final PdfDictionary stdcf2 = new PdfDictionary();
                stdcf2.put(PdfName.LENGTH, new PdfNumber(16));
                if (this.embeddedFilesOnly) {
                    stdcf2.put(PdfName.AUTHEVENT, PdfName.EFOPEN);
                    dic.put(PdfName.EFF, PdfName.STDCF);
                    dic.put(PdfName.STRF, PdfName.IDENTITY);
                    dic.put(PdfName.STMF, PdfName.IDENTITY);
                }
                else {
                    stdcf2.put(PdfName.AUTHEVENT, PdfName.DOCOPEN);
                    dic.put(PdfName.STRF, PdfName.STDCF);
                    dic.put(PdfName.STMF, PdfName.STDCF);
                }
                if (this.revision == 4) {
                    stdcf2.put(PdfName.CFM, PdfName.AESV2);
                }
                else {
                    stdcf2.put(PdfName.CFM, PdfName.V2);
                }
                final PdfDictionary cf2 = new PdfDictionary();
                cf2.put(PdfName.STDCF, stdcf2);
                dic.put(PdfName.CF, cf2);
            }
        }
        return dic;
    }
    
    public PdfObject getFileID() {
        return createInfoId(this.documentID);
    }
    
    public OutputStreamEncryption getEncryptionStream(final OutputStream os) {
        return new OutputStreamEncryption(os, this.key, 0, this.keySize, this.revision);
    }
    
    public int calculateStreamSize(final int n) {
        if (this.revision == 4) {
            return (n & 0x7FFFFFF0) + 32;
        }
        return n;
    }
    
    public byte[] encryptByteArray(final byte[] b) {
        try {
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            final OutputStreamEncryption os2 = this.getEncryptionStream(ba);
            os2.write(b);
            os2.finish();
            return ba.toByteArray();
        }
        catch (final IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }
    
    public StandardDecryption getDecryptor() {
        return new StandardDecryption(this.key, 0, this.keySize, this.revision);
    }
    
    public byte[] decryptByteArray(final byte[] b) {
        try {
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            final StandardDecryption dec = this.getDecryptor();
            byte[] b2 = dec.update(b, 0, b.length);
            if (b2 != null) {
                ba.write(b2);
            }
            b2 = dec.finish();
            if (b2 != null) {
                ba.write(b2);
            }
            return ba.toByteArray();
        }
        catch (final IOException ex) {
            throw new ExceptionConverter(ex);
        }
    }
    
    public void addRecipient(final Certificate cert, final int permission) {
        this.documentID = createDocumentId();
        this.publicKeyHandler.addRecipient(new PdfPublicKeyRecipient(cert, permission));
    }
    
    public byte[] computeUserPassword(final byte[] ownerPassword) {
        final byte[] userPad = this.computeOwnerKey(this.ownerKey, this.padPassword(ownerPassword));
        for (int i = 0; i < userPad.length; ++i) {
            boolean match = true;
            for (int j = 0; j < userPad.length - i; ++j) {
                if (userPad[i + j] != PdfEncryption.pad[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                final byte[] userPassword = new byte[i];
                System.arraycopy(userPad, 0, userPassword, 0, i);
                return userPassword;
            }
        }
        return userPad;
    }
    
    static {
        pad = new byte[] { 40, -65, 78, 94, 78, 117, -118, 65, 100, 0, 78, 86, -1, -6, 1, 8, 46, 46, 0, -74, -48, 104, 62, -128, 47, 12, -87, -2, 100, 83, 105, 122 };
        salt = new byte[] { 115, 65, 108, 84 };
        metadataPad = new byte[] { -1, -1, -1, -1 };
        PdfEncryption.seq = System.currentTimeMillis();
    }
}
