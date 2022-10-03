package java.security;

import java.io.ObjectStreamException;
import java.io.NotSerializableException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Locale;
import java.io.Serializable;

public class KeyRep implements Serializable
{
    private static final long serialVersionUID = -4757683898830641853L;
    private static final String PKCS8 = "PKCS#8";
    private static final String X509 = "X.509";
    private static final String RAW = "RAW";
    private Type type;
    private String algorithm;
    private String format;
    private byte[] encoded;
    
    public KeyRep(final Type type, final String algorithm, final String s, final byte[] array) {
        if (type == null || algorithm == null || s == null || array == null) {
            throw new NullPointerException("invalid null input(s)");
        }
        this.type = type;
        this.algorithm = algorithm;
        this.format = s.toUpperCase(Locale.ENGLISH);
        this.encoded = array.clone();
    }
    
    protected Object readResolve() throws ObjectStreamException {
        try {
            if (this.type == Type.SECRET && "RAW".equals(this.format)) {
                return new SecretKeySpec(this.encoded, this.algorithm);
            }
            if (this.type == Type.PUBLIC && "X.509".equals(this.format)) {
                return KeyFactory.getInstance(this.algorithm).generatePublic(new X509EncodedKeySpec(this.encoded));
            }
            if (this.type == Type.PRIVATE && "PKCS#8".equals(this.format)) {
                return KeyFactory.getInstance(this.algorithm).generatePrivate(new PKCS8EncodedKeySpec(this.encoded));
            }
            throw new NotSerializableException("unrecognized type/format combination: " + this.type + "/" + this.format);
        }
        catch (final NotSerializableException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            final NotSerializableException ex3 = new NotSerializableException("java.security.Key: [" + this.type + "] [" + this.algorithm + "] [" + this.format + "]");
            ex3.initCause(ex2);
            throw ex3;
        }
    }
    
    public enum Type
    {
        SECRET, 
        PUBLIC, 
        PRIVATE;
    }
}
