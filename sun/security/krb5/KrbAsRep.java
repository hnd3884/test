package sun.security.krb5;

import java.util.Objects;
import sun.security.krb5.internal.ASReq;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KDCReq;
import sun.security.krb5.internal.EncASRepPart;
import sun.security.krb5.internal.crypto.EType;
import sun.security.jgss.krb5.Krb5Util;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.internal.PAData;
import java.io.IOException;
import sun.security.krb5.internal.KRBError;
import sun.security.util.DerValue;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ASRep;

class KrbAsRep extends KrbKdcRep
{
    private ASRep rep;
    private Credentials creds;
    private boolean DEBUG;
    
    KrbAsRep(final byte[] array) throws KrbException, Asn1Exception, IOException {
        this.DEBUG = Krb5.DEBUG;
        final DerValue derValue = new DerValue(array);
        try {
            this.rep = new ASRep(derValue);
        }
        catch (final Asn1Exception ex) {
            this.rep = null;
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
                ex2 = new KrbException(krbError);
            }
            else {
                if (this.DEBUG) {
                    System.out.println("KRBError received: " + substring);
                }
                ex2 = new KrbException(krbError, substring);
            }
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    PAData[] getPA() {
        return this.rep.pAData;
    }
    
    void decryptUsingKeyTab(final KeyTab keyTab, final KrbAsReq krbAsReq, final PrincipalName principalName) throws KrbException, Asn1Exception, IOException {
        EncryptionKey encryptionKey = null;
        final int eType = this.rep.encPart.getEType();
        final Integer kvno = this.rep.encPart.kvno;
        try {
            encryptionKey = EncryptionKey.findKey(eType, kvno, Krb5Util.keysFromJavaxKeyTab(keyTab, principalName));
        }
        catch (final KrbException ex) {
            if (ex.returnCode() == 44) {
                encryptionKey = EncryptionKey.findKey(eType, Krb5Util.keysFromJavaxKeyTab(keyTab, principalName));
            }
        }
        if (encryptionKey == null) {
            throw new KrbException(400, "Cannot find key for type/kvno to decrypt AS REP - " + EType.toString(eType) + "/" + kvno);
        }
        this.decrypt(encryptionKey, krbAsReq, principalName);
    }
    
    void decryptUsingPassword(final char[] array, final KrbAsReq krbAsReq, final PrincipalName principalName) throws KrbException, Asn1Exception, IOException {
        final int eType = this.rep.encPart.getEType();
        this.decrypt(EncryptionKey.acquireSecretKey(principalName, array, eType, PAData.getSaltAndParams(eType, this.rep.pAData)), krbAsReq, principalName);
    }
    
    private void decrypt(final EncryptionKey encryptionKey, final KrbAsReq krbAsReq, final PrincipalName principalName) throws KrbException, Asn1Exception, IOException {
        final EncASRepPart encKDCRepPart = new EncASRepPart(new DerValue(this.rep.encPart.reset(this.rep.encPart.decrypt(encryptionKey, 3))));
        this.rep.encKDCRepPart = encKDCRepPart;
        final ASReq message = krbAsReq.getMessage();
        KrbKdcRep.check(true, message, this.rep, encryptionKey);
        PrincipalName principalName2 = principalName;
        if (principalName2.equals(this.rep.cname)) {
            principalName2 = null;
        }
        this.creds = new Credentials(this.rep.ticket, this.rep.cname, principalName2, encKDCRepPart.sname, null, encKDCRepPart.key, encKDCRepPart.flags, encKDCRepPart.authtime, encKDCRepPart.starttime, encKDCRepPart.endtime, encKDCRepPart.renewTill, encKDCRepPart.caddr);
        if (this.DEBUG) {
            System.out.println(">>> KrbAsRep cons in KrbAsReq.getReply " + message.reqBody.cname.getNameString());
        }
    }
    
    Credentials getCreds() {
        return Objects.requireNonNull(this.creds, "Creds not available yet.");
    }
    
    sun.security.krb5.internal.ccache.Credentials getCCreds() {
        return new sun.security.krb5.internal.ccache.Credentials(this.rep);
    }
}
