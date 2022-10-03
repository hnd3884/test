package sun.security.krb5;

import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KRBError;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.APRep;
import sun.security.krb5.internal.EncAPRepPart;

public class KrbApRep
{
    private byte[] obuf;
    private byte[] ibuf;
    private EncAPRepPart encPart;
    private APRep apRepMessg;
    
    public KrbApRep(final KrbApReq krbApReq, final boolean b, final EncryptionKey encryptionKey) throws KrbException, IOException {
        this.init(krbApReq, encryptionKey, new LocalSeqNumber());
    }
    
    public KrbApRep(final byte[] array, final Credentials credentials, final KrbApReq krbApReq) throws KrbException, IOException {
        this(array, credentials);
        this.authenticate(krbApReq);
    }
    
    private void init(final KrbApReq krbApReq, final EncryptionKey encryptionKey, final SeqNumber seqNumber) throws KrbException, IOException {
        this.createMessage(krbApReq.getCreds().key, krbApReq.getCtime(), krbApReq.cusec(), encryptionKey, seqNumber);
        this.obuf = this.apRepMessg.asn1Encode();
    }
    
    private KrbApRep(final byte[] array, final Credentials credentials) throws KrbException, IOException {
        this(new DerValue(array), credentials);
    }
    
    private KrbApRep(DerValue derValue, final Credentials credentials) throws KrbException, IOException {
        APRep apRep;
        try {
            apRep = new APRep(derValue);
        }
        catch (final Asn1Exception ex) {
            final KRBError krbError = new KRBError(derValue);
            final String errorString = krbError.getErrorString();
            String substring;
            if (errorString.charAt(errorString.length() - 1) == '\0') {
                substring = errorString.substring(0, errorString.length() - 1);
            }
            else {
                substring = errorString;
            }
            final KrbException ex2 = new KrbException(krbError.getErrorCode(), substring);
            ex2.initCause(ex);
            throw ex2;
        }
        derValue = new DerValue(apRep.encPart.reset(apRep.encPart.decrypt(credentials.key, 12)));
        this.encPart = new EncAPRepPart(derValue);
    }
    
    private void authenticate(final KrbApReq krbApReq) throws KrbException, IOException {
        if (this.encPart.ctime.getSeconds() != krbApReq.getCtime().getSeconds() || this.encPart.cusec != krbApReq.getCtime().getMicroSeconds()) {
            throw new KrbApErrException(46);
        }
    }
    
    public EncryptionKey getSubKey() {
        return this.encPart.getSubKey();
    }
    
    public Integer getSeqNumber() {
        return this.encPart.getSeqNumber();
    }
    
    public byte[] getMessage() {
        return this.obuf;
    }
    
    private void createMessage(final EncryptionKey encryptionKey, final KerberosTime kerberosTime, final int n, final EncryptionKey encryptionKey2, final SeqNumber seqNumber) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
        Integer n2 = null;
        if (seqNumber != null) {
            n2 = new Integer(seqNumber.current());
        }
        this.encPart = new EncAPRepPart(kerberosTime, n, encryptionKey2, n2);
        this.apRepMessg = new APRep(new EncryptedData(encryptionKey, this.encPart.asn1Encode(), 12));
    }
}
