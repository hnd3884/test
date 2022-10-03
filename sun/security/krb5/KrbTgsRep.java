package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import java.io.IOException;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KDCReq;
import sun.security.krb5.internal.EncTGSRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.util.DerValue;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TGSRep;

public class KrbTgsRep extends KrbKdcRep
{
    private TGSRep rep;
    private Credentials creds;
    private Ticket secondTicket;
    private static final boolean DEBUG;
    
    KrbTgsRep(final byte[] array, final KrbTgsReq krbTgsReq) throws KrbException, IOException {
        final DerValue derValue = new DerValue(array);
        final TGSReq message = krbTgsReq.getMessage();
        TGSRep rep;
        try {
            rep = new TGSRep(derValue);
        }
        catch (final Asn1Exception ex) {
            final KRBError krbError = new KRBError(derValue);
            final String errorString = krbError.getErrorString();
            String substring = null;
            if (errorString != null && errorString.length() > 0) {
                if (errorString.charAt(errorString.length() - 1) == '\0') {
                    substring = errorString.substring(0, errorString.length() - 1);
                }
                else {
                    substring = errorString;
                }
            }
            KrbException ex2;
            if (substring == null) {
                ex2 = new KrbException(krbError.getErrorCode());
            }
            else {
                ex2 = new KrbException(krbError.getErrorCode(), substring);
            }
            ex2.initCause(ex);
            throw ex2;
        }
        final EncTGSRepPart encKDCRepPart = new EncTGSRepPart(new DerValue(rep.encPart.reset(rep.encPart.decrypt(krbTgsReq.tgsReqKey, krbTgsReq.usedSubkey() ? 9 : 8))));
        rep.encKDCRepPart = encKDCRepPart;
        KrbKdcRep.check(false, message, rep, krbTgsReq.tgsReqKey);
        PrincipalName serverAlias = krbTgsReq.getServerAlias();
        if (serverAlias != null) {
            final PrincipalName sname = encKDCRepPart.sname;
            if (serverAlias.equals(sname) || isReferralSname(sname)) {
                serverAlias = null;
            }
        }
        PrincipalName clientAlias = null;
        if (rep.cname.equals(message.reqBody.cname)) {
            clientAlias = krbTgsReq.getClientAlias();
        }
        this.creds = new Credentials(rep.ticket, rep.cname, clientAlias, encKDCRepPart.sname, serverAlias, encKDCRepPart.key, encKDCRepPart.flags, encKDCRepPart.authtime, encKDCRepPart.starttime, encKDCRepPart.endtime, encKDCRepPart.renewTill, encKDCRepPart.caddr);
        this.rep = rep;
        this.secondTicket = krbTgsReq.getSecondTicket();
    }
    
    public Credentials getCreds() {
        return this.creds;
    }
    
    sun.security.krb5.internal.ccache.Credentials setCredentials() {
        return new sun.security.krb5.internal.ccache.Credentials(this.rep, this.secondTicket);
    }
    
    private static boolean isReferralSname(final PrincipalName principalName) {
        if (principalName != null) {
            final String[] nameStrings = principalName.getNameStrings();
            if (nameStrings.length == 2 && nameStrings[0].equals("krbtgt")) {
                return true;
            }
        }
        return false;
    }
    
    static {
        DEBUG = Krb5.DEBUG;
    }
}
