package sun.security.pkcs11;

import java.security.ProviderException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.jca.JCAUtil;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;

final class NativeKeyHolder
{
    private static long nativeKeyWrapperKeyID;
    private static CK_MECHANISM nativeKeyWrapperMechanism;
    private static long nativeKeyWrapperRefCount;
    private static Session nativeKeyWrapperSession;
    private final P11Key p11Key;
    private final byte[] nativeKeyInfo;
    private boolean wrapperKeyUsed;
    private long keyID;
    private SessionKeyRef ref;
    private int refCount;
    
    private static void createNativeKeyWrapper(final Token token) throws PKCS11Exception {
        assert NativeKeyHolder.nativeKeyWrapperKeyID == 0L;
        assert NativeKeyHolder.nativeKeyWrapperRefCount == 0L;
        assert NativeKeyHolder.nativeKeyWrapperSession == null;
        final CK_ATTRIBUTE[] attributes = token.getAttributes("generate", 4L, 31L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(353L, 32L) });
        Session objSession = null;
        try {
            objSession = token.getObjSession();
            NativeKeyHolder.nativeKeyWrapperKeyID = token.p11.C_GenerateKey(objSession.id(), new CK_MECHANISM(4224L), attributes);
            (NativeKeyHolder.nativeKeyWrapperSession = objSession).addObject();
            final byte[] array = new byte[16];
            JCAUtil.getSecureRandom().nextBytes(array);
            NativeKeyHolder.nativeKeyWrapperMechanism = new CK_MECHANISM(4229L, array);
        }
        catch (final PKCS11Exception ex) {}
        finally {
            token.releaseSession(objSession);
        }
    }
    
    private static void deleteNativeKeyWrapper() {
        final Token token = NativeKeyHolder.nativeKeyWrapperSession.token;
        if (token.isValid()) {
            Session opSession = null;
            try {
                opSession = token.getOpSession();
                token.p11.C_DestroyObject(opSession.id(), NativeKeyHolder.nativeKeyWrapperKeyID);
                NativeKeyHolder.nativeKeyWrapperSession.removeObject();
            }
            catch (final PKCS11Exception ex) {}
            finally {
                token.releaseSession(opSession);
            }
        }
        NativeKeyHolder.nativeKeyWrapperKeyID = 0L;
        NativeKeyHolder.nativeKeyWrapperMechanism = null;
        NativeKeyHolder.nativeKeyWrapperSession = null;
    }
    
    static void decWrapperKeyRef() {
        synchronized (NativeKeyHolder.class) {
            assert NativeKeyHolder.nativeKeyWrapperKeyID != 0L;
            assert NativeKeyHolder.nativeKeyWrapperRefCount > 0L;
            --NativeKeyHolder.nativeKeyWrapperRefCount;
            if (NativeKeyHolder.nativeKeyWrapperRefCount == 0L) {
                deleteNativeKeyWrapper();
            }
        }
    }
    
    NativeKeyHolder(final P11Key p11Key, final long keyID, final Session session, final boolean b, final boolean b2) {
        this.p11Key = p11Key;
        this.keyID = keyID;
        this.refCount = -1;
        byte[] nativeKeyInfo = null;
        if (b2) {
            this.ref = null;
        }
        else {
            final Token token = p11Key.token;
            if (b) {
                try {
                    if (p11Key.sensitive) {
                        synchronized (NativeKeyHolder.class) {
                            if (NativeKeyHolder.nativeKeyWrapperKeyID == 0L) {
                                createNativeKeyWrapper(token);
                            }
                            if (NativeKeyHolder.nativeKeyWrapperKeyID != 0L) {
                                ++NativeKeyHolder.nativeKeyWrapperRefCount;
                                this.wrapperKeyUsed = true;
                            }
                        }
                    }
                    Session opSession = null;
                    try {
                        opSession = token.getOpSession();
                        nativeKeyInfo = p11Key.token.p11.getNativeKeyInfo(opSession.id(), keyID, NativeKeyHolder.nativeKeyWrapperKeyID, NativeKeyHolder.nativeKeyWrapperMechanism);
                    }
                    catch (final PKCS11Exception ex) {}
                    finally {
                        token.releaseSession(opSession);
                    }
                }
                catch (final PKCS11Exception ex2) {}
            }
            this.ref = new SessionKeyRef(p11Key, keyID, this.wrapperKeyUsed, session);
        }
        this.nativeKeyInfo = (byte[])((nativeKeyInfo == null || nativeKeyInfo.length == 0) ? null : nativeKeyInfo);
    }
    
    long getKeyID() throws ProviderException {
        if (this.nativeKeyInfo != null) {
            synchronized (this.nativeKeyInfo) {
                if (this.refCount == -1) {
                    this.refCount = 0;
                }
                final int n = this.refCount++;
                if (this.keyID == 0L) {
                    if (n != 0) {
                        throw new RuntimeException("Error: null keyID with non-zero refCount " + n);
                    }
                    final Token token = this.p11Key.token;
                    Session objSession = null;
                    try {
                        objSession = token.getObjSession();
                        this.keyID = token.p11.createNativeKey(objSession.id(), this.nativeKeyInfo, NativeKeyHolder.nativeKeyWrapperKeyID, NativeKeyHolder.nativeKeyWrapperMechanism);
                        this.ref.registerNativeKey(this.keyID, objSession);
                    }
                    catch (final PKCS11Exception ex) {
                        --this.refCount;
                        throw new ProviderException("Error recreating native key", ex);
                    }
                    finally {
                        token.releaseSession(objSession);
                    }
                }
                else if (n < 0) {
                    throw new RuntimeException("ERROR: negative refCount");
                }
            }
        }
        return this.keyID;
    }
    
    void releaseKeyID() {
        if (this.nativeKeyInfo != null) {
            synchronized (this.nativeKeyInfo) {
                if (this.refCount == -1) {
                    throw new RuntimeException("Error: miss match getKeyID call");
                }
                final int refCount = this.refCount - 1;
                this.refCount = refCount;
                final int n = refCount;
                if (n == 0) {
                    if (this.keyID == 0L) {
                        throw new RuntimeException("ERROR: null keyID can't be destroyed");
                    }
                    this.keyID = 0L;
                    this.ref.removeNativeKey();
                }
                else if (n < 0) {
                    throw new RuntimeException("wrong refCount value: " + n);
                }
            }
        }
    }
    
    static {
        NativeKeyHolder.nativeKeyWrapperKeyID = 0L;
        NativeKeyHolder.nativeKeyWrapperMechanism = null;
        NativeKeyHolder.nativeKeyWrapperRefCount = 0L;
        NativeKeyHolder.nativeKeyWrapperSession = null;
    }
}
