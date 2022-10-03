package sun.security.pkcs11;

import sun.nio.ch.DirectBuffer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import java.security.DigestException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.util.MessageDigestSpi2;
import java.security.MessageDigestSpi;

final class P11Digest extends MessageDigestSpi implements Cloneable, MessageDigestSpi2
{
    private static final int S_BLANK = 1;
    private static final int S_BUFFERED = 2;
    private static final int S_INIT = 3;
    private static final int BUFFER_SIZE = 96;
    private final Token token;
    private final String algorithm;
    private final CK_MECHANISM mechanism;
    private final int digestLength;
    private Session session;
    private int state;
    private byte[] buffer;
    private int bufOfs;
    
    P11Digest(final Token token, final String algorithm, final long n) {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = new CK_MECHANISM(n);
        switch ((int)n) {
            case 512:
            case 528: {
                this.digestLength = 16;
                break;
            }
            case 544: {
                this.digestLength = 20;
                break;
            }
            case 72:
            case 597: {
                this.digestLength = 28;
                break;
            }
            case 76:
            case 592: {
                this.digestLength = 32;
                break;
            }
            case 608: {
                this.digestLength = 48;
                break;
            }
            case 624: {
                this.digestLength = 64;
                break;
            }
            default: {
                throw new ProviderException("Unknown mechanism: " + n);
            }
        }
        this.buffer = new byte[96];
        this.state = 1;
    }
    
    @Override
    protected int engineGetDigestLength() {
        return this.digestLength;
    }
    
    private void fetchSession() {
        this.token.ensureValid();
        if (this.state == 1) {
            try {
                this.session = this.token.getOpSession();
                this.state = 2;
            }
            catch (final PKCS11Exception ex) {
                throw new ProviderException("No more session available", ex);
            }
        }
    }
    
    @Override
    protected void engineReset() {
        this.token.ensureValid();
        if (this.session != null) {
            if (this.state == 3 && this.token.explicitCancel && !this.session.hasObjects()) {
                this.session = this.token.killSession(this.session);
            }
            else {
                this.session = this.token.releaseSession(this.session);
            }
        }
        this.state = 1;
        this.bufOfs = 0;
    }
    
    @Override
    protected byte[] engineDigest() {
        try {
            final byte[] array = new byte[this.digestLength];
            this.engineDigest(array, 0, this.digestLength);
            return array;
        }
        catch (final DigestException ex) {
            throw new ProviderException("internal error", ex);
        }
    }
    
    @Override
    protected int engineDigest(final byte[] array, final int n, final int n2) throws DigestException {
        if (n2 < this.digestLength) {
            throw new DigestException("Length must be at least " + this.digestLength);
        }
        this.fetchSession();
        try {
            int n3;
            if (this.state == 2) {
                n3 = this.token.p11.C_DigestSingle(this.session.id(), this.mechanism, this.buffer, 0, this.bufOfs, array, n, n2);
                this.bufOfs = 0;
            }
            else {
                if (this.bufOfs != 0) {
                    this.token.p11.C_DigestUpdate(this.session.id(), 0L, this.buffer, 0, this.bufOfs);
                    this.bufOfs = 0;
                }
                n3 = this.token.p11.C_DigestFinal(this.session.id(), array, n, n2);
            }
            if (n3 != this.digestLength) {
                throw new ProviderException("internal digest length error");
            }
            return n3;
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("digest() failed", ex);
        }
        finally {
            this.engineReset();
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.engineUpdate(new byte[] { b }, 0, 1);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        if (n2 <= 0) {
            return;
        }
        this.fetchSession();
        try {
            if (this.state == 2) {
                this.token.p11.C_DigestInit(this.session.id(), this.mechanism);
                this.state = 3;
            }
            if (this.bufOfs != 0 && this.bufOfs + n2 > this.buffer.length) {
                this.token.p11.C_DigestUpdate(this.session.id(), 0L, this.buffer, 0, this.bufOfs);
                this.bufOfs = 0;
            }
            if (this.bufOfs + n2 > this.buffer.length) {
                this.token.p11.C_DigestUpdate(this.session.id(), 0L, array, n, n2);
            }
            else {
                System.arraycopy(array, n, this.buffer, this.bufOfs, n2);
                this.bufOfs += n2;
            }
        }
        catch (final PKCS11Exception ex) {
            this.engineReset();
            throw new ProviderException("update() failed", ex);
        }
    }
    
    public void engineUpdate(final SecretKey secretKey) throws InvalidKeyException {
        if (!(secretKey instanceof P11Key)) {
            throw new InvalidKeyException("Not a P11Key: " + secretKey);
        }
        final P11Key p11Key = (P11Key)secretKey;
        if (p11Key.token != this.token) {
            throw new InvalidKeyException("Not a P11Key of this provider: " + secretKey);
        }
        this.fetchSession();
        final long keyID = p11Key.getKeyID();
        try {
            if (this.state == 2) {
                this.token.p11.C_DigestInit(this.session.id(), this.mechanism);
                this.state = 3;
            }
            if (this.bufOfs != 0) {
                this.token.p11.C_DigestUpdate(this.session.id(), 0L, this.buffer, 0, this.bufOfs);
                this.bufOfs = 0;
            }
            this.token.p11.C_DigestKey(this.session.id(), keyID);
        }
        catch (final PKCS11Exception ex) {
            this.engineReset();
            throw new ProviderException("update(SecretKey) failed", ex);
        }
        finally {
            p11Key.releaseKeyID();
        }
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        final int remaining = byteBuffer.remaining();
        if (remaining <= 0) {
            return;
        }
        if (!(byteBuffer instanceof DirectBuffer)) {
            super.engineUpdate(byteBuffer);
            return;
        }
        this.fetchSession();
        final long address = ((DirectBuffer)byteBuffer).address();
        final int position = byteBuffer.position();
        try {
            if (this.state == 2) {
                this.token.p11.C_DigestInit(this.session.id(), this.mechanism);
                this.state = 3;
            }
            if (this.bufOfs != 0) {
                this.token.p11.C_DigestUpdate(this.session.id(), 0L, this.buffer, 0, this.bufOfs);
                this.bufOfs = 0;
            }
            this.token.p11.C_DigestUpdate(this.session.id(), address + position, null, 0, remaining);
            byteBuffer.position(position + remaining);
        }
        catch (final PKCS11Exception ex) {
            this.engineReset();
            throw new ProviderException("update() failed", ex);
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final P11Digest p11Digest = (P11Digest)super.clone();
        p11Digest.buffer = this.buffer.clone();
        try {
            if (this.session != null) {
                p11Digest.session = p11Digest.token.getOpSession();
            }
            if (this.state == 3) {
                this.token.p11.C_SetOperationState(p11Digest.session.id(), this.token.p11.C_GetOperationState(this.session.id()), 0L, 0L);
            }
        }
        catch (final PKCS11Exception ex) {
            throw (CloneNotSupportedException)new CloneNotSupportedException(this.algorithm).initCause(ex);
        }
        return p11Digest;
    }
}
