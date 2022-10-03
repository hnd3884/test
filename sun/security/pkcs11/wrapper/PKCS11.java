package sun.security.pkcs11.wrapper;

import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.util.Map;

public class PKCS11
{
    private static final String PKCS11_WRAPPER = "j2pkcs11";
    private final String pkcs11ModulePath;
    private long pNativeData;
    private static final Map<String, PKCS11> moduleMap;
    
    public static void loadNative() {
    }
    
    public static native long freeMechanism(final long p0);
    
    private static native void initializeLibrary();
    
    private static native void finalizeLibrary();
    
    PKCS11(final String pkcs11ModulePath, final String s) throws IOException {
        this.connect(pkcs11ModulePath, s);
        this.pkcs11ModulePath = pkcs11ModulePath;
    }
    
    public static synchronized PKCS11 getInstance(final String s, final String s2, final CK_C_INITIALIZE_ARGS ck_C_INITIALIZE_ARGS, final boolean b) throws IOException, PKCS11Exception {
        PKCS11 pkcs11 = PKCS11.moduleMap.get(s);
        if (pkcs11 == null) {
            if (ck_C_INITIALIZE_ARGS != null && (ck_C_INITIALIZE_ARGS.flags & 0x2L) != 0x0L) {
                pkcs11 = new PKCS11(s, s2);
            }
            else {
                pkcs11 = new SynchronizedPKCS11(s, s2);
            }
            if (!b) {
                try {
                    pkcs11.C_Initialize(ck_C_INITIALIZE_ARGS);
                }
                catch (final PKCS11Exception ex) {
                    if (ex.getErrorCode() != 401L) {
                        throw ex;
                    }
                }
            }
            PKCS11.moduleMap.put(s, pkcs11);
        }
        return pkcs11;
    }
    
    private native void connect(final String p0, final String p1) throws IOException;
    
    private native void disconnect();
    
    native void C_Initialize(final Object p0) throws PKCS11Exception;
    
    public native void C_Finalize(final Object p0) throws PKCS11Exception;
    
    public native CK_INFO C_GetInfo() throws PKCS11Exception;
    
    public native long[] C_GetSlotList(final boolean p0) throws PKCS11Exception;
    
    public native CK_SLOT_INFO C_GetSlotInfo(final long p0) throws PKCS11Exception;
    
    public native CK_TOKEN_INFO C_GetTokenInfo(final long p0) throws PKCS11Exception;
    
    public native long[] C_GetMechanismList(final long p0) throws PKCS11Exception;
    
    public native CK_MECHANISM_INFO C_GetMechanismInfo(final long p0, final long p1) throws PKCS11Exception;
    
    public native long C_OpenSession(final long p0, final long p1, final Object p2, final CK_NOTIFY p3) throws PKCS11Exception;
    
    public native void C_CloseSession(final long p0) throws PKCS11Exception;
    
    public native CK_SESSION_INFO C_GetSessionInfo(final long p0) throws PKCS11Exception;
    
    public native byte[] C_GetOperationState(final long p0) throws PKCS11Exception;
    
    public native void C_SetOperationState(final long p0, final byte[] p1, final long p2, final long p3) throws PKCS11Exception;
    
    public native void C_Login(final long p0, final long p1, final char[] p2) throws PKCS11Exception;
    
    public native void C_Logout(final long p0) throws PKCS11Exception;
    
    public native long C_CreateObject(final long p0, final CK_ATTRIBUTE[] p1) throws PKCS11Exception;
    
    public native long C_CopyObject(final long p0, final long p1, final CK_ATTRIBUTE[] p2) throws PKCS11Exception;
    
    public native void C_DestroyObject(final long p0, final long p1) throws PKCS11Exception;
    
    public native void C_GetAttributeValue(final long p0, final long p1, final CK_ATTRIBUTE[] p2) throws PKCS11Exception;
    
    public native void C_SetAttributeValue(final long p0, final long p1, final CK_ATTRIBUTE[] p2) throws PKCS11Exception;
    
    public native void C_FindObjectsInit(final long p0, final CK_ATTRIBUTE[] p1) throws PKCS11Exception;
    
    public native long[] C_FindObjects(final long p0, final long p1) throws PKCS11Exception;
    
    public native void C_FindObjectsFinal(final long p0) throws PKCS11Exception;
    
    public native void C_EncryptInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native int C_Encrypt(final long p0, final long p1, final byte[] p2, final int p3, final int p4, final long p5, final byte[] p6, final int p7, final int p8) throws PKCS11Exception;
    
    public native int C_EncryptUpdate(final long p0, final long p1, final byte[] p2, final int p3, final int p4, final long p5, final byte[] p6, final int p7, final int p8) throws PKCS11Exception;
    
    public native int C_EncryptFinal(final long p0, final long p1, final byte[] p2, final int p3, final int p4) throws PKCS11Exception;
    
    public native void C_DecryptInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native int C_Decrypt(final long p0, final long p1, final byte[] p2, final int p3, final int p4, final long p5, final byte[] p6, final int p7, final int p8) throws PKCS11Exception;
    
    public native int C_DecryptUpdate(final long p0, final long p1, final byte[] p2, final int p3, final int p4, final long p5, final byte[] p6, final int p7, final int p8) throws PKCS11Exception;
    
    public native int C_DecryptFinal(final long p0, final long p1, final byte[] p2, final int p3, final int p4) throws PKCS11Exception;
    
    public native void C_DigestInit(final long p0, final CK_MECHANISM p1) throws PKCS11Exception;
    
    public native int C_DigestSingle(final long p0, final CK_MECHANISM p1, final byte[] p2, final int p3, final int p4, final byte[] p5, final int p6, final int p7) throws PKCS11Exception;
    
    public native void C_DigestUpdate(final long p0, final long p1, final byte[] p2, final int p3, final int p4) throws PKCS11Exception;
    
    public native void C_DigestKey(final long p0, final long p1) throws PKCS11Exception;
    
    public native int C_DigestFinal(final long p0, final byte[] p1, final int p2, final int p3) throws PKCS11Exception;
    
    public native void C_SignInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native byte[] C_Sign(final long p0, final byte[] p1) throws PKCS11Exception;
    
    public native void C_SignUpdate(final long p0, final long p1, final byte[] p2, final int p3, final int p4) throws PKCS11Exception;
    
    public native byte[] C_SignFinal(final long p0, final int p1) throws PKCS11Exception;
    
    public native void C_SignRecoverInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native int C_SignRecover(final long p0, final byte[] p1, final int p2, final int p3, final byte[] p4, final int p5, final int p6) throws PKCS11Exception;
    
    public native void C_VerifyInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native void C_Verify(final long p0, final byte[] p1, final byte[] p2) throws PKCS11Exception;
    
    public native void C_VerifyUpdate(final long p0, final long p1, final byte[] p2, final int p3, final int p4) throws PKCS11Exception;
    
    public native void C_VerifyFinal(final long p0, final byte[] p1) throws PKCS11Exception;
    
    public native void C_VerifyRecoverInit(final long p0, final CK_MECHANISM p1, final long p2) throws PKCS11Exception;
    
    public native int C_VerifyRecover(final long p0, final byte[] p1, final int p2, final int p3, final byte[] p4, final int p5, final int p6) throws PKCS11Exception;
    
    public native byte[] getNativeKeyInfo(final long p0, final long p1, final long p2, final CK_MECHANISM p3) throws PKCS11Exception;
    
    public native long createNativeKey(final long p0, final byte[] p1, final long p2, final CK_MECHANISM p3) throws PKCS11Exception;
    
    public native long C_GenerateKey(final long p0, final CK_MECHANISM p1, final CK_ATTRIBUTE[] p2) throws PKCS11Exception;
    
    public native long[] C_GenerateKeyPair(final long p0, final CK_MECHANISM p1, final CK_ATTRIBUTE[] p2, final CK_ATTRIBUTE[] p3) throws PKCS11Exception;
    
    public native byte[] C_WrapKey(final long p0, final CK_MECHANISM p1, final long p2, final long p3) throws PKCS11Exception;
    
    public native long C_UnwrapKey(final long p0, final CK_MECHANISM p1, final long p2, final byte[] p3, final CK_ATTRIBUTE[] p4) throws PKCS11Exception;
    
    public native long C_DeriveKey(final long p0, final CK_MECHANISM p1, final long p2, final CK_ATTRIBUTE[] p3) throws PKCS11Exception;
    
    public native void C_SeedRandom(final long p0, final byte[] p1) throws PKCS11Exception;
    
    public native void C_GenerateRandom(final long p0, final byte[] p1) throws PKCS11Exception;
    
    @Override
    public String toString() {
        return "Module name: " + this.pkcs11ModulePath;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.disconnect();
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                System.loadLibrary("j2pkcs11");
                return null;
            }
        });
        initializeLibrary();
        moduleMap = new HashMap<String, PKCS11>();
    }
    
    static class SynchronizedPKCS11 extends PKCS11
    {
        SynchronizedPKCS11(final String s, final String s2) throws IOException {
            super(s, s2);
        }
        
        @Override
        synchronized void C_Initialize(final Object o) throws PKCS11Exception {
            super.C_Initialize(o);
        }
        
        @Override
        public synchronized void C_Finalize(final Object o) throws PKCS11Exception {
            super.C_Finalize(o);
        }
        
        @Override
        public synchronized CK_INFO C_GetInfo() throws PKCS11Exception {
            return super.C_GetInfo();
        }
        
        @Override
        public synchronized long[] C_GetSlotList(final boolean b) throws PKCS11Exception {
            return super.C_GetSlotList(b);
        }
        
        @Override
        public synchronized CK_SLOT_INFO C_GetSlotInfo(final long n) throws PKCS11Exception {
            return super.C_GetSlotInfo(n);
        }
        
        @Override
        public synchronized CK_TOKEN_INFO C_GetTokenInfo(final long n) throws PKCS11Exception {
            return super.C_GetTokenInfo(n);
        }
        
        @Override
        public synchronized long[] C_GetMechanismList(final long n) throws PKCS11Exception {
            return super.C_GetMechanismList(n);
        }
        
        @Override
        public synchronized CK_MECHANISM_INFO C_GetMechanismInfo(final long n, final long n2) throws PKCS11Exception {
            return super.C_GetMechanismInfo(n, n2);
        }
        
        @Override
        public synchronized long C_OpenSession(final long n, final long n2, final Object o, final CK_NOTIFY ck_NOTIFY) throws PKCS11Exception {
            return super.C_OpenSession(n, n2, o, ck_NOTIFY);
        }
        
        @Override
        public synchronized void C_CloseSession(final long n) throws PKCS11Exception {
            super.C_CloseSession(n);
        }
        
        @Override
        public synchronized CK_SESSION_INFO C_GetSessionInfo(final long n) throws PKCS11Exception {
            return super.C_GetSessionInfo(n);
        }
        
        @Override
        public synchronized void C_Login(final long n, final long n2, final char[] array) throws PKCS11Exception {
            super.C_Login(n, n2, array);
        }
        
        @Override
        public synchronized void C_Logout(final long n) throws PKCS11Exception {
            super.C_Logout(n);
        }
        
        @Override
        public synchronized long C_CreateObject(final long n, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            return super.C_CreateObject(n, array);
        }
        
        @Override
        public synchronized long C_CopyObject(final long n, final long n2, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            return super.C_CopyObject(n, n2, array);
        }
        
        @Override
        public synchronized void C_DestroyObject(final long n, final long n2) throws PKCS11Exception {
            super.C_DestroyObject(n, n2);
        }
        
        @Override
        public synchronized void C_GetAttributeValue(final long n, final long n2, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            super.C_GetAttributeValue(n, n2, array);
        }
        
        @Override
        public synchronized void C_SetAttributeValue(final long n, final long n2, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            super.C_SetAttributeValue(n, n2, array);
        }
        
        @Override
        public synchronized void C_FindObjectsInit(final long n, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            super.C_FindObjectsInit(n, array);
        }
        
        @Override
        public synchronized long[] C_FindObjects(final long n, final long n2) throws PKCS11Exception {
            return super.C_FindObjects(n, n2);
        }
        
        @Override
        public synchronized void C_FindObjectsFinal(final long n) throws PKCS11Exception {
            super.C_FindObjectsFinal(n);
        }
        
        @Override
        public synchronized void C_EncryptInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_EncryptInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized int C_Encrypt(final long n, final long n2, final byte[] array, final int n3, final int n4, final long n5, final byte[] array2, final int n6, final int n7) throws PKCS11Exception {
            return super.C_Encrypt(n, n2, array, n3, n4, n5, array2, n6, n7);
        }
        
        @Override
        public synchronized int C_EncryptUpdate(final long n, final long n2, final byte[] array, final int n3, final int n4, final long n5, final byte[] array2, final int n6, final int n7) throws PKCS11Exception {
            return super.C_EncryptUpdate(n, n2, array, n3, n4, n5, array2, n6, n7);
        }
        
        @Override
        public synchronized int C_EncryptFinal(final long n, final long n2, final byte[] array, final int n3, final int n4) throws PKCS11Exception {
            return super.C_EncryptFinal(n, n2, array, n3, n4);
        }
        
        @Override
        public synchronized void C_DecryptInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_DecryptInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized int C_Decrypt(final long n, final long n2, final byte[] array, final int n3, final int n4, final long n5, final byte[] array2, final int n6, final int n7) throws PKCS11Exception {
            return super.C_Decrypt(n, n2, array, n3, n4, n5, array2, n6, n7);
        }
        
        @Override
        public synchronized int C_DecryptUpdate(final long n, final long n2, final byte[] array, final int n3, final int n4, final long n5, final byte[] array2, final int n6, final int n7) throws PKCS11Exception {
            return super.C_DecryptUpdate(n, n2, array, n3, n4, n5, array2, n6, n7);
        }
        
        @Override
        public synchronized int C_DecryptFinal(final long n, final long n2, final byte[] array, final int n3, final int n4) throws PKCS11Exception {
            return super.C_DecryptFinal(n, n2, array, n3, n4);
        }
        
        @Override
        public synchronized void C_DigestInit(final long n, final CK_MECHANISM ck_MECHANISM) throws PKCS11Exception {
            super.C_DigestInit(n, ck_MECHANISM);
        }
        
        @Override
        public synchronized int C_DigestSingle(final long n, final CK_MECHANISM ck_MECHANISM, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4, final int n5) throws PKCS11Exception {
            return super.C_DigestSingle(n, ck_MECHANISM, array, n2, n3, array2, n4, n5);
        }
        
        @Override
        public synchronized void C_DigestUpdate(final long n, final long n2, final byte[] array, final int n3, final int n4) throws PKCS11Exception {
            super.C_DigestUpdate(n, n2, array, n3, n4);
        }
        
        @Override
        public synchronized void C_DigestKey(final long n, final long n2) throws PKCS11Exception {
            super.C_DigestKey(n, n2);
        }
        
        @Override
        public synchronized int C_DigestFinal(final long n, final byte[] array, final int n2, final int n3) throws PKCS11Exception {
            return super.C_DigestFinal(n, array, n2, n3);
        }
        
        @Override
        public synchronized void C_SignInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_SignInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized byte[] C_Sign(final long n, final byte[] array) throws PKCS11Exception {
            return super.C_Sign(n, array);
        }
        
        @Override
        public synchronized void C_SignUpdate(final long n, final long n2, final byte[] array, final int n3, final int n4) throws PKCS11Exception {
            super.C_SignUpdate(n, n2, array, n3, n4);
        }
        
        @Override
        public synchronized byte[] C_SignFinal(final long n, final int n2) throws PKCS11Exception {
            return super.C_SignFinal(n, n2);
        }
        
        @Override
        public synchronized void C_SignRecoverInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_SignRecoverInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized int C_SignRecover(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4, final int n5) throws PKCS11Exception {
            return super.C_SignRecover(n, array, n2, n3, array2, n4, n5);
        }
        
        @Override
        public synchronized void C_VerifyInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_VerifyInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized void C_Verify(final long n, final byte[] array, final byte[] array2) throws PKCS11Exception {
            super.C_Verify(n, array, array2);
        }
        
        @Override
        public synchronized void C_VerifyUpdate(final long n, final long n2, final byte[] array, final int n3, final int n4) throws PKCS11Exception {
            super.C_VerifyUpdate(n, n2, array, n3, n4);
        }
        
        @Override
        public synchronized void C_VerifyFinal(final long n, final byte[] array) throws PKCS11Exception {
            super.C_VerifyFinal(n, array);
        }
        
        @Override
        public synchronized void C_VerifyRecoverInit(final long n, final CK_MECHANISM ck_MECHANISM, final long n2) throws PKCS11Exception {
            super.C_VerifyRecoverInit(n, ck_MECHANISM, n2);
        }
        
        @Override
        public synchronized int C_VerifyRecover(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4, final int n5) throws PKCS11Exception {
            return super.C_VerifyRecover(n, array, n2, n3, array2, n4, n5);
        }
        
        @Override
        public synchronized long C_GenerateKey(final long n, final CK_MECHANISM ck_MECHANISM, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            return super.C_GenerateKey(n, ck_MECHANISM, array);
        }
        
        @Override
        public synchronized long[] C_GenerateKeyPair(final long n, final CK_MECHANISM ck_MECHANISM, final CK_ATTRIBUTE[] array, final CK_ATTRIBUTE[] array2) throws PKCS11Exception {
            return super.C_GenerateKeyPair(n, ck_MECHANISM, array, array2);
        }
        
        @Override
        public synchronized byte[] C_WrapKey(final long n, final CK_MECHANISM ck_MECHANISM, final long n2, final long n3) throws PKCS11Exception {
            return super.C_WrapKey(n, ck_MECHANISM, n2, n3);
        }
        
        @Override
        public synchronized long C_UnwrapKey(final long n, final CK_MECHANISM ck_MECHANISM, final long n2, final byte[] array, final CK_ATTRIBUTE[] array2) throws PKCS11Exception {
            return super.C_UnwrapKey(n, ck_MECHANISM, n2, array, array2);
        }
        
        @Override
        public synchronized long C_DeriveKey(final long n, final CK_MECHANISM ck_MECHANISM, final long n2, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
            return super.C_DeriveKey(n, ck_MECHANISM, n2, array);
        }
        
        @Override
        public synchronized void C_SeedRandom(final long n, final byte[] array) throws PKCS11Exception {
            super.C_SeedRandom(n, array);
        }
        
        @Override
        public synchronized void C_GenerateRandom(final long n, final byte[] array) throws PKCS11Exception {
            super.C_GenerateRandom(n, array);
        }
    }
}
