package sun.misc;

import javax.crypto.SealedObject;
import java.security.Signature;
import java.util.zip.ZipFile;
import java.security.AccessController;
import java.security.ProtectionDomain;
import java.io.ObjectInputStream;
import java.io.FileDescriptor;
import java.io.Console;
import java.nio.ByteOrder;
import java.net.HttpCookie;
import java.util.jar.JarFile;

public class SharedSecrets
{
    private static final Unsafe unsafe;
    private static JavaUtilJarAccess javaUtilJarAccess;
    private static JavaLangAccess javaLangAccess;
    private static JavaLangRefAccess javaLangRefAccess;
    private static JavaIOAccess javaIOAccess;
    private static JavaNetAccess javaNetAccess;
    private static JavaNetHttpCookieAccess javaNetHttpCookieAccess;
    private static JavaNioAccess javaNioAccess;
    private static JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
    private static JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
    private static JavaSecurityAccess javaSecurityAccess;
    private static JavaUtilZipFileAccess javaUtilZipFileAccess;
    private static JavaAWTAccess javaAWTAccess;
    private static JavaOISAccess javaOISAccess;
    private static JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess;
    private static JavaObjectInputStreamReadString javaObjectInputStreamReadString;
    private static JavaObjectInputStreamAccess javaObjectInputStreamAccess;
    private static JavaSecuritySignatureAccess javaSecuritySignatureAccess;
    
    public static JavaUtilJarAccess javaUtilJarAccess() {
        if (SharedSecrets.javaUtilJarAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(JarFile.class);
        }
        return SharedSecrets.javaUtilJarAccess;
    }
    
    public static void setJavaUtilJarAccess(final JavaUtilJarAccess javaUtilJarAccess) {
        SharedSecrets.javaUtilJarAccess = javaUtilJarAccess;
    }
    
    public static void setJavaLangAccess(final JavaLangAccess javaLangAccess) {
        SharedSecrets.javaLangAccess = javaLangAccess;
    }
    
    public static JavaLangAccess getJavaLangAccess() {
        return SharedSecrets.javaLangAccess;
    }
    
    public static void setJavaLangRefAccess(final JavaLangRefAccess javaLangRefAccess) {
        SharedSecrets.javaLangRefAccess = javaLangRefAccess;
    }
    
    public static JavaLangRefAccess getJavaLangRefAccess() {
        return SharedSecrets.javaLangRefAccess;
    }
    
    public static void setJavaNetAccess(final JavaNetAccess javaNetAccess) {
        SharedSecrets.javaNetAccess = javaNetAccess;
    }
    
    public static JavaNetAccess getJavaNetAccess() {
        return SharedSecrets.javaNetAccess;
    }
    
    public static void setJavaNetHttpCookieAccess(final JavaNetHttpCookieAccess javaNetHttpCookieAccess) {
        SharedSecrets.javaNetHttpCookieAccess = javaNetHttpCookieAccess;
    }
    
    public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
        if (SharedSecrets.javaNetHttpCookieAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(HttpCookie.class);
        }
        return SharedSecrets.javaNetHttpCookieAccess;
    }
    
    public static void setJavaNioAccess(final JavaNioAccess javaNioAccess) {
        SharedSecrets.javaNioAccess = javaNioAccess;
    }
    
    public static JavaNioAccess getJavaNioAccess() {
        if (SharedSecrets.javaNioAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ByteOrder.class);
        }
        return SharedSecrets.javaNioAccess;
    }
    
    public static void setJavaIOAccess(final JavaIOAccess javaIOAccess) {
        SharedSecrets.javaIOAccess = javaIOAccess;
    }
    
    public static JavaIOAccess getJavaIOAccess() {
        if (SharedSecrets.javaIOAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(Console.class);
        }
        return SharedSecrets.javaIOAccess;
    }
    
    public static void setJavaIOFileDescriptorAccess(final JavaIOFileDescriptorAccess javaIOFileDescriptorAccess) {
        SharedSecrets.javaIOFileDescriptorAccess = javaIOFileDescriptorAccess;
    }
    
    public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
        if (SharedSecrets.javaIOFileDescriptorAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(FileDescriptor.class);
        }
        return SharedSecrets.javaIOFileDescriptorAccess;
    }
    
    public static void setJavaOISAccess(final JavaOISAccess javaOISAccess) {
        SharedSecrets.javaOISAccess = javaOISAccess;
    }
    
    public static JavaOISAccess getJavaOISAccess() {
        if (SharedSecrets.javaOISAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ObjectInputStream.class);
        }
        return SharedSecrets.javaOISAccess;
    }
    
    public static void setJavaSecurityProtectionDomainAccess(final JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess) {
        SharedSecrets.javaSecurityProtectionDomainAccess = javaSecurityProtectionDomainAccess;
    }
    
    public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess() {
        if (SharedSecrets.javaSecurityProtectionDomainAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ProtectionDomain.class);
        }
        return SharedSecrets.javaSecurityProtectionDomainAccess;
    }
    
    public static void setJavaSecurityAccess(final JavaSecurityAccess javaSecurityAccess) {
        SharedSecrets.javaSecurityAccess = javaSecurityAccess;
    }
    
    public static JavaSecurityAccess getJavaSecurityAccess() {
        if (SharedSecrets.javaSecurityAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(AccessController.class);
        }
        return SharedSecrets.javaSecurityAccess;
    }
    
    public static JavaUtilZipFileAccess getJavaUtilZipFileAccess() {
        if (SharedSecrets.javaUtilZipFileAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ZipFile.class);
        }
        return SharedSecrets.javaUtilZipFileAccess;
    }
    
    public static void setJavaUtilZipFileAccess(final JavaUtilZipFileAccess javaUtilZipFileAccess) {
        SharedSecrets.javaUtilZipFileAccess = javaUtilZipFileAccess;
    }
    
    public static void setJavaAWTAccess(final JavaAWTAccess javaAWTAccess) {
        SharedSecrets.javaAWTAccess = javaAWTAccess;
    }
    
    public static JavaAWTAccess getJavaAWTAccess() {
        if (SharedSecrets.javaAWTAccess == null) {
            return null;
        }
        return SharedSecrets.javaAWTAccess;
    }
    
    public static JavaObjectInputStreamReadString getJavaObjectInputStreamReadString() {
        if (SharedSecrets.javaObjectInputStreamReadString == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ObjectInputStream.class);
        }
        return SharedSecrets.javaObjectInputStreamReadString;
    }
    
    public static void setJavaObjectInputStreamReadString(final JavaObjectInputStreamReadString javaObjectInputStreamReadString) {
        SharedSecrets.javaObjectInputStreamReadString = javaObjectInputStreamReadString;
    }
    
    public static JavaObjectInputStreamAccess getJavaObjectInputStreamAccess() {
        if (SharedSecrets.javaObjectInputStreamAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(ObjectInputStream.class);
        }
        return SharedSecrets.javaObjectInputStreamAccess;
    }
    
    public static void setJavaObjectInputStreamAccess(final JavaObjectInputStreamAccess javaObjectInputStreamAccess) {
        SharedSecrets.javaObjectInputStreamAccess = javaObjectInputStreamAccess;
    }
    
    public static void setJavaSecuritySignatureAccess(final JavaSecuritySignatureAccess javaSecuritySignatureAccess) {
        SharedSecrets.javaSecuritySignatureAccess = javaSecuritySignatureAccess;
    }
    
    public static JavaSecuritySignatureAccess getJavaSecuritySignatureAccess() {
        if (SharedSecrets.javaSecuritySignatureAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(Signature.class);
        }
        return SharedSecrets.javaSecuritySignatureAccess;
    }
    
    public static void setJavaxCryptoSealedObjectAccess(final JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess) {
        SharedSecrets.javaxCryptoSealedObjectAccess = javaxCryptoSealedObjectAccess;
    }
    
    public static JavaxCryptoSealedObjectAccess getJavaxCryptoSealedObjectAccess() {
        if (SharedSecrets.javaxCryptoSealedObjectAccess == null) {
            SharedSecrets.unsafe.ensureClassInitialized(SealedObject.class);
        }
        return SharedSecrets.javaxCryptoSealedObjectAccess;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
