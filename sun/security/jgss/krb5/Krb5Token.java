package sun.security.jgss.krb5;

import java.io.IOException;
import sun.security.util.ObjectIdentifier;
import sun.security.jgss.GSSToken;

abstract class Krb5Token extends GSSToken
{
    public static final int AP_REQ_ID = 256;
    public static final int AP_REP_ID = 512;
    public static final int ERR_ID = 768;
    public static final int MIC_ID = 257;
    public static final int WRAP_ID = 513;
    public static final int MIC_ID_v2 = 1028;
    public static final int WRAP_ID_v2 = 1284;
    public static ObjectIdentifier OID;
    
    public static String getTokenName(final int n) {
        String s = null;
        switch (n) {
            case 256:
            case 512: {
                s = "Context Establishment Token";
                break;
            }
            case 257: {
                s = "MIC Token";
                break;
            }
            case 1028: {
                s = "MIC Token (new format)";
                break;
            }
            case 513: {
                s = "Wrap Token";
                break;
            }
            case 1284: {
                s = "Wrap Token (new format)";
                break;
            }
            default: {
                s = "Kerberos GSS-API Mechanism Token";
                break;
            }
        }
        return s;
    }
    
    static {
        try {
            Krb5Token.OID = new ObjectIdentifier(Krb5MechFactory.GSS_KRB5_MECH_OID.toString());
        }
        catch (final IOException ex) {}
    }
}
