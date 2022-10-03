package sun.security.krb5.internal;

import sun.security.krb5.KrbException;
import sun.security.krb5.Config;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;

public class KDCOptions extends KerberosFlags
{
    private static final int KDC_OPT_PROXIABLE = 268435456;
    private static final int KDC_OPT_RENEWABLE_OK = 16;
    private static final int KDC_OPT_FORWARDABLE = 1073741824;
    public static final int RESERVED = 0;
    public static final int FORWARDABLE = 1;
    public static final int FORWARDED = 2;
    public static final int PROXIABLE = 3;
    public static final int PROXY = 4;
    public static final int ALLOW_POSTDATE = 5;
    public static final int POSTDATED = 6;
    public static final int UNUSED7 = 7;
    public static final int RENEWABLE = 8;
    public static final int UNUSED9 = 9;
    public static final int UNUSED10 = 10;
    public static final int UNUSED11 = 11;
    public static final int CNAME_IN_ADDL_TKT = 14;
    public static final int CANONICALIZE = 15;
    public static final int RENEWABLE_OK = 27;
    public static final int ENC_TKT_IN_SKEY = 28;
    public static final int RENEW = 30;
    public static final int VALIDATE = 31;
    private static final String[] names;
    private boolean DEBUG;
    
    public static KDCOptions with(final int... array) {
        final KDCOptions kdcOptions = new KDCOptions();
        for (int length = array.length, i = 0; i < length; ++i) {
            kdcOptions.set(array[i], true);
        }
        return kdcOptions;
    }
    
    public KDCOptions() {
        super(32);
        this.DEBUG = Krb5.DEBUG;
        this.setDefault();
    }
    
    public KDCOptions(final int n, final byte[] array) throws Asn1Exception {
        super(n, array);
        this.DEBUG = Krb5.DEBUG;
        if (n > array.length * 8 || n > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public KDCOptions(final boolean[] array) throws Asn1Exception {
        super(array);
        this.DEBUG = Krb5.DEBUG;
        if (array.length > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public KDCOptions(final DerValue derValue) throws Asn1Exception, IOException {
        this(derValue.getUnalignedBitString(true).toBooleanArray());
    }
    
    public KDCOptions(final byte[] array) {
        super(array.length * 8, array);
        this.DEBUG = Krb5.DEBUG;
    }
    
    public static KDCOptions parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new KDCOptions(derValue.getData().getDerValue());
    }
    
    @Override
    public void set(final int n, final boolean b) throws ArrayIndexOutOfBoundsException {
        super.set(n, b);
    }
    
    @Override
    public boolean get(final int n) throws ArrayIndexOutOfBoundsException {
        return super.get(n);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("KDCOptions: ");
        for (int i = 0; i < 32; ++i) {
            if (this.get(i)) {
                if (KDCOptions.names[i] != null) {
                    sb.append(KDCOptions.names[i]).append(",");
                }
                else {
                    sb.append(i).append(",");
                }
            }
        }
        return sb.toString();
    }
    
    private void setDefault() {
        try {
            final Config instance = Config.getInstance();
            final int intValue = instance.getIntValue("libdefaults", "kdc_default_options");
            if ((intValue & 0x10) == 0x10) {
                this.set(27, true);
            }
            else if (instance.getBooleanValue("libdefaults", "renewable")) {
                this.set(27, true);
            }
            if ((intValue & 0x10000000) == 0x10000000) {
                this.set(3, true);
            }
            else if (instance.getBooleanValue("libdefaults", "proxiable")) {
                this.set(3, true);
            }
            if ((intValue & 0x40000000) == 0x40000000) {
                this.set(1, true);
            }
            else if (instance.getBooleanValue("libdefaults", "forwardable")) {
                this.set(1, true);
            }
        }
        catch (final KrbException ex) {
            if (this.DEBUG) {
                System.out.println("Exception in getting default values for KDC Options from the configuration ");
                ex.printStackTrace();
            }
        }
    }
    
    static {
        names = new String[] { "RESERVED", "FORWARDABLE", "FORWARDED", "PROXIABLE", "PROXY", "ALLOW_POSTDATE", "POSTDATED", "UNUSED7", "RENEWABLE", "UNUSED9", "UNUSED10", "UNUSED11", null, null, "CNAME_IN_ADDL_TKT", "CANONICALIZE", null, null, null, null, null, null, null, null, null, null, null, "RENEWABLE_OK", "ENC_TKT_IN_SKEY", null, "RENEW", "VALIDATE" };
    }
}
