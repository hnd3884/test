package sun.security.pkcs11.wrapper;

import sun.security.pkcs11.P11Util;
import java.math.BigInteger;

public class CK_ATTRIBUTE
{
    public static final CK_ATTRIBUTE TOKEN_FALSE;
    public static final CK_ATTRIBUTE SENSITIVE_FALSE;
    public static final CK_ATTRIBUTE EXTRACTABLE_TRUE;
    public static final CK_ATTRIBUTE ENCRYPT_TRUE;
    public static final CK_ATTRIBUTE DECRYPT_TRUE;
    public static final CK_ATTRIBUTE WRAP_TRUE;
    public static final CK_ATTRIBUTE UNWRAP_TRUE;
    public static final CK_ATTRIBUTE SIGN_TRUE;
    public static final CK_ATTRIBUTE VERIFY_TRUE;
    public static final CK_ATTRIBUTE SIGN_RECOVER_TRUE;
    public static final CK_ATTRIBUTE VERIFY_RECOVER_TRUE;
    public static final CK_ATTRIBUTE DERIVE_TRUE;
    public static final CK_ATTRIBUTE ENCRYPT_NULL;
    public static final CK_ATTRIBUTE DECRYPT_NULL;
    public static final CK_ATTRIBUTE WRAP_NULL;
    public static final CK_ATTRIBUTE UNWRAP_NULL;
    public long type;
    public Object pValue;
    
    public CK_ATTRIBUTE() {
    }
    
    public CK_ATTRIBUTE(final long type) {
        this.type = type;
    }
    
    public CK_ATTRIBUTE(final long type, final Object pValue) {
        this.type = type;
        this.pValue = pValue;
    }
    
    public CK_ATTRIBUTE(final long type, final boolean b) {
        this.type = type;
        this.pValue = b;
    }
    
    public CK_ATTRIBUTE(final long type, final long n) {
        this.type = type;
        this.pValue = n;
    }
    
    public CK_ATTRIBUTE(final long type, final BigInteger bigInteger) {
        this.type = type;
        this.pValue = P11Util.getMagnitude(bigInteger);
    }
    
    public BigInteger getBigInteger() {
        if (!(this.pValue instanceof byte[])) {
            throw new RuntimeException("Not a byte[]");
        }
        return new BigInteger(1, (byte[])this.pValue);
    }
    
    public boolean getBoolean() {
        if (!(this.pValue instanceof Boolean)) {
            throw new RuntimeException("Not a Boolean: " + this.pValue.getClass().getName());
        }
        return (boolean)this.pValue;
    }
    
    public char[] getCharArray() {
        if (!(this.pValue instanceof char[])) {
            throw new RuntimeException("Not a char[]");
        }
        return (char[])this.pValue;
    }
    
    public byte[] getByteArray() {
        if (!(this.pValue instanceof byte[])) {
            throw new RuntimeException("Not a byte[]");
        }
        return (byte[])this.pValue;
    }
    
    public long getLong() {
        if (!(this.pValue instanceof Long)) {
            throw new RuntimeException("Not a Long: " + this.pValue.getClass().getName());
        }
        return (long)this.pValue;
    }
    
    @Override
    public String toString() {
        final String string = Functions.getAttributeName(this.type) + " = ";
        if (this.type == 0L) {
            return string + Functions.getObjectClassName(this.getLong());
        }
        if (this.type == 256L) {
            return string + Functions.getKeyName(this.getLong());
        }
        String s;
        if (this.pValue instanceof char[]) {
            s = new String((char[])this.pValue);
        }
        else if (this.pValue instanceof byte[]) {
            s = Functions.toHexString((byte[])this.pValue);
        }
        else {
            s = String.valueOf(this.pValue);
        }
        return string + s;
    }
    
    static {
        TOKEN_FALSE = new CK_ATTRIBUTE(1L, false);
        SENSITIVE_FALSE = new CK_ATTRIBUTE(259L, false);
        EXTRACTABLE_TRUE = new CK_ATTRIBUTE(354L, true);
        ENCRYPT_TRUE = new CK_ATTRIBUTE(260L, true);
        DECRYPT_TRUE = new CK_ATTRIBUTE(261L, true);
        WRAP_TRUE = new CK_ATTRIBUTE(262L, true);
        UNWRAP_TRUE = new CK_ATTRIBUTE(263L, true);
        SIGN_TRUE = new CK_ATTRIBUTE(264L, true);
        VERIFY_TRUE = new CK_ATTRIBUTE(266L, true);
        SIGN_RECOVER_TRUE = new CK_ATTRIBUTE(265L, true);
        VERIFY_RECOVER_TRUE = new CK_ATTRIBUTE(267L, true);
        DERIVE_TRUE = new CK_ATTRIBUTE(268L, true);
        ENCRYPT_NULL = new CK_ATTRIBUTE(260L);
        DECRYPT_NULL = new CK_ATTRIBUTE(261L);
        WRAP_NULL = new CK_ATTRIBUTE(262L);
        UNWRAP_NULL = new CK_ATTRIBUTE(263L);
    }
}
