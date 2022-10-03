package sun.security.pkcs11;

import sun.nio.ch.DirectBuffer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import javax.crypto.MacSpi;

final class P11Mac extends MacSpi
{
    private final Token token;
    private final String algorithm;
    private final CK_MECHANISM ckMechanism;
    private final int macLength;
    private P11Key p11Key;
    private Session session;
    private boolean initialized;
    private byte[] oneByte;
    
    P11Mac(final Token token, final String algorithm, final long n) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        Long n2 = null;
        switch ((int)n) {
            case 529: {
                this.macLength = 16;
                break;
            }
            case 545: {
                this.macLength = 20;
                break;
            }
            case 73:
            case 598: {
                this.macLength = 28;
                break;
            }
            case 77:
            case 593: {
                this.macLength = 32;
                break;
            }
            case 609: {
                this.macLength = 48;
                break;
            }
            case 625: {
                this.macLength = 64;
                break;
            }
            case 896: {
                this.macLength = 16;
                n2 = 16L;
                break;
            }
            case 897: {
                this.macLength = 20;
                n2 = 20L;
                break;
            }
            default: {
                throw new ProviderException("Unknown mechanism: " + n);
            }
        }
        this.ckMechanism = new CK_MECHANISM(n, n2);
    }
    
    private void reset(final boolean b) {
        if (!this.initialized) {
            return;
        }
        this.initialized = false;
        try {
            if (this.session == null) {
                return;
            }
            if (b && this.token.explicitCancel) {
                this.cancelOperation();
            }
        }
        finally {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
        }
    }
    
    private void cancelOperation() {
        this.token.ensureValid();
        try {
            this.token.p11.C_SignFinal(this.session.id(), 0);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("Cancel failed", ex);
        }
    }
    
    private void ensureInitialized() throws PKCS11Exception {
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void initialize() throws PKCS11Exception {
        if (this.p11Key == null) {
            throw new ProviderException("Operation cannot be performed without calling engineInit first");
        }
        this.token.ensureValid();
        final long keyID = this.p11Key.getKeyID();
        try {
            if (this.session == null) {
                this.session = this.token.getOpSession();
            }
            this.token.p11.C_SignInit(this.session.id(), this.ckMechanism, keyID);
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw ex;
        }
        this.initialized = true;
    }
    
    @Override
    protected int engineGetMacLength() {
        return this.macLength;
    }
    
    @Override
    protected void engineReset() {
        this.reset(true);
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.reset(true);
        this.p11Key = P11SecretKeyFactory.convertKey(this.token, key, this.algorithm);
        try {
            this.initialize();
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("init() failed", ex);
        }
    }
    
    @Override
    protected byte[] engineDoFinal() {
        try {
            this.ensureInitialized();
            return this.token.p11.C_SignFinal(this.session.id(), 0);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("doFinal() failed", ex);
        }
        finally {
            this.reset(false);
        }
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        if (this.oneByte == null) {
            this.oneByte = new byte[1];
        }
        this.oneByte[0] = b;
        this.engineUpdate(this.oneByte, 0, 1);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        try {
            this.ensureInitialized();
            this.token.p11.C_SignUpdate(this.session.id(), 0L, array, n, n2);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("update() failed", ex);
        }
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        try {
            this.ensureInitialized();
            final int remaining = byteBuffer.remaining();
            if (remaining <= 0) {
                return;
            }
            if (!(byteBuffer instanceof DirectBuffer)) {
                super.engineUpdate(byteBuffer);
                return;
            }
            final long address = ((DirectBuffer)byteBuffer).address();
            final int position = byteBuffer.position();
            this.token.p11.C_SignUpdate(this.session.id(), address + position, null, 0, remaining);
            byteBuffer.position(position + remaining);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("update() failed", ex);
        }
    }
}
