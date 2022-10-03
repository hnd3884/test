package sun.security.mscapi;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Map;
import sun.security.action.PutAllAction;
import java.util.HashMap;
import java.security.Provider;

public final class SunMSCAPI extends Provider
{
    private static final long serialVersionUID = 8622598936488630849L;
    private static final String INFO = "Sun's Microsoft Crypto API provider";
    
    public SunMSCAPI() {
        super("SunMSCAPI", 1.8, "Sun's Microsoft Crypto API provider");
        final Cloneable cloneable = (System.getSecurityManager() == null) ? this : new HashMap<Object, Object>();
        ((Map<String, String>)cloneable).put("SecureRandom.Windows-PRNG", "sun.security.mscapi.PRNG");
        ((Map<String, String>)cloneable).put("KeyStore.Windows-MY", "sun.security.mscapi.CKeyStore$MY");
        ((Map<String, String>)cloneable).put("KeyStore.Windows-ROOT", "sun.security.mscapi.CKeyStore$ROOT");
        ((Map<String, String>)cloneable).put("Signature.NONEwithRSA", "sun.security.mscapi.CSignature$NONEwithRSA");
        ((Map<String, String>)cloneable).put("Signature.SHA1withRSA", "sun.security.mscapi.CSignature$SHA1withRSA");
        ((Map<String, String>)cloneable).put("Signature.SHA256withRSA", "sun.security.mscapi.CSignature$SHA256withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.113549.1.1.11", "SHA256withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.11", "SHA256withRSA");
        ((Map<String, String>)cloneable).put("Signature.SHA384withRSA", "sun.security.mscapi.CSignature$SHA384withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.113549.1.1.12", "SHA384withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.12", "SHA384withRSA");
        ((Map<String, String>)cloneable).put("Signature.SHA512withRSA", "sun.security.mscapi.CSignature$SHA512withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.113549.1.1.13", "SHA512withRSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.13", "SHA512withRSA");
        ((Map<String, String>)cloneable).put("Signature.MD5withRSA", "sun.security.mscapi.CSignature$MD5withRSA");
        ((Map<String, String>)cloneable).put("Signature.MD2withRSA", "sun.security.mscapi.CSignature$MD2withRSA");
        ((Map<String, String>)cloneable).put("Signature.RSASSA-PSS", "sun.security.mscapi.CSignature$PSS");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.113549.1.1.10", "RSASSA-PSS");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.10", "RSASSA-PSS");
        ((Map<String, String>)cloneable).put("Signature.SHA1withECDSA", "sun.security.mscapi.CSignature$SHA1withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.10045.4.1", "SHA1withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.10045.4.1", "SHA1withECDSA");
        ((Map<String, String>)cloneable).put("Signature.SHA224withECDSA", "sun.security.mscapi.CSignature$SHA224withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.10045.4.3.1", "SHA224withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.1", "SHA224withECDSA");
        ((Map<String, String>)cloneable).put("Signature.SHA256withECDSA", "sun.security.mscapi.CSignature$SHA256withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.10045.4.3.2", "SHA256withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.2", "SHA256withECDSA");
        ((Map<String, String>)cloneable).put("Signature.SHA384withECDSA", "sun.security.mscapi.CSignature$SHA384withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.10045.4.3.3", "SHA384withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.3", "SHA384withECDSA");
        ((Map<String, String>)cloneable).put("Signature.SHA512withECDSA", "sun.security.mscapi.CSignature$SHA512withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.1.2.840.10045.4.3.4", "SHA512withECDSA");
        ((Map<String, String>)cloneable).put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.4", "SHA512withECDSA");
        ((Map<String, String>)cloneable).put("Signature.NONEwithRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA1withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA256withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA384withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA512withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.MD5withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.MD2withRSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.RSASSA-PSS SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA1withECDSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA224withECDSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA256withECDSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA384withECDSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("Signature.SHA512withECDSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        ((Map<String, String>)cloneable).put("KeyPairGenerator.RSA", "sun.security.mscapi.CKeyPairGenerator$RSA");
        ((Map<String, String>)cloneable).put("KeyPairGenerator.RSA KeySize", "1024");
        ((Map<String, String>)cloneable).put("Cipher.RSA", "sun.security.mscapi.CRSACipher");
        ((Map<String, String>)cloneable).put("Cipher.RSA/ECB/PKCS1Padding", "sun.security.mscapi.CRSACipher");
        ((Map<String, String>)cloneable).put("Cipher.RSA SupportedModes", "ECB");
        ((Map<String, String>)cloneable).put("Cipher.RSA SupportedPaddings", "PKCS1PADDING");
        ((Map<String, String>)cloneable).put("Cipher.RSA SupportedKeyClasses", "sun.security.mscapi.CKey");
        if (cloneable != this) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PutAllAction(this, (Map<?, ?>)cloneable));
        }
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("sunmscapi");
                return null;
            }
        });
    }
}
