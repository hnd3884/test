package sun.security.krb5.internal;

import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;

public class TicketFlags extends KerberosFlags
{
    public TicketFlags() {
        super(32);
    }
    
    public TicketFlags(final boolean[] array) throws Asn1Exception {
        super(array);
        if (array.length > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public TicketFlags(final int n, final byte[] array) throws Asn1Exception {
        super(n, array);
        if (n > array.length * 8 || n > 32) {
            throw new Asn1Exception(502);
        }
    }
    
    public TicketFlags(final DerValue derValue) throws IOException, Asn1Exception {
        this(derValue.getUnalignedBitString(true).toBooleanArray());
    }
    
    public static TicketFlags parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new TicketFlags(derValue.getData().getDerValue());
    }
    
    public Object clone() {
        try {
            return new TicketFlags(this.toBooleanArray());
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public boolean match(final LoginOptions loginOptions) {
        boolean b = false;
        if (this.get(1) == loginOptions.get(1) && this.get(3) == loginOptions.get(3) && this.get(8) == loginOptions.get(8)) {
            b = true;
        }
        return b;
    }
    
    public boolean match(final TicketFlags ticketFlags) {
        final boolean b = true;
        for (int i = 0; i <= 31; ++i) {
            if (this.get(i) != ticketFlags.get(i)) {
                return false;
            }
        }
        return b;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final boolean[] booleanArray = this.toBooleanArray();
        for (int i = 0; i < booleanArray.length; ++i) {
            if (booleanArray[i]) {
                switch (i) {
                    case 0: {
                        sb.append("RESERVED;");
                        break;
                    }
                    case 1: {
                        sb.append("FORWARDABLE;");
                        break;
                    }
                    case 2: {
                        sb.append("FORWARDED;");
                        break;
                    }
                    case 3: {
                        sb.append("PROXIABLE;");
                        break;
                    }
                    case 4: {
                        sb.append("PROXY;");
                        break;
                    }
                    case 5: {
                        sb.append("MAY-POSTDATE;");
                        break;
                    }
                    case 6: {
                        sb.append("POSTDATED;");
                        break;
                    }
                    case 7: {
                        sb.append("INVALID;");
                        break;
                    }
                    case 8: {
                        sb.append("RENEWABLE;");
                        break;
                    }
                    case 9: {
                        sb.append("INITIAL;");
                        break;
                    }
                    case 10: {
                        sb.append("PRE-AUTHENT;");
                        break;
                    }
                    case 11: {
                        sb.append("HW-AUTHENT;");
                        break;
                    }
                    case 15: {
                        sb.append("ENC-PA-REP;");
                        break;
                    }
                }
            }
        }
        String s = sb.toString();
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
