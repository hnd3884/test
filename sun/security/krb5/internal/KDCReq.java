package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.KrbException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;

public class KDCReq
{
    public KDCReqBody reqBody;
    public PAData[] pAData;
    private int pvno;
    private int msgType;
    
    public KDCReq(final PAData[] array, final KDCReqBody reqBody, final int msgType) throws IOException {
        this.pAData = null;
        this.pvno = 5;
        this.msgType = msgType;
        if (array != null) {
            this.pAData = new PAData[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a KDCRep");
                }
                this.pAData[i] = (PAData)array[i].clone();
            }
        }
        this.reqBody = reqBody;
    }
    
    public KDCReq() {
        this.pAData = null;
    }
    
    public KDCReq(final byte[] array, final int n) throws Asn1Exception, IOException, KrbException {
        this.pAData = null;
        this.init(new DerValue(array), n);
    }
    
    public KDCReq(final DerValue derValue, final int n) throws Asn1Exception, IOException, KrbException {
        this.pAData = null;
        this.init(derValue, n);
    }
    
    protected void init(final DerValue derValue, final int n) throws Asn1Exception, IOException, KrbException {
        if ((derValue.getTag() & 0x1F) != n) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue3 = derValue2.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.pvno = derValue3.getData().getBigInteger().intValue();
        if (this.pvno != 5) {
            throw new KrbApErrException(39);
        }
        final DerValue derValue4 = derValue2.getData().getDerValue();
        if ((derValue4.getTag() & 0x1F) != 0x2) {
            throw new Asn1Exception(906);
        }
        this.msgType = derValue4.getData().getBigInteger().intValue();
        if (this.msgType != n) {
            throw new KrbApErrException(40);
        }
        this.pAData = PAData.parseSequence(derValue2.getData(), (byte)3, true);
        final DerValue derValue5 = derValue2.getData().getDerValue();
        if ((derValue5.getTag() & 0x1F) == 0x4) {
            this.reqBody = new KDCReqBody(derValue5.getData().getDerValue(), this.msgType);
            return;
        }
        throw new Asn1Exception(906);
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.pvno));
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putInteger(BigInteger.valueOf(this.msgType));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream3);
        if (this.pAData != null && this.pAData.length > 0) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            for (int i = 0; i < this.pAData.length; ++i) {
                derOutputStream4.write(this.pAData[i].asn1Encode());
            }
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.write((byte)48, derOutputStream4);
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream5);
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)4), this.reqBody.asn1Encode(this.msgType));
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write(DerValue.createTag((byte)64, true, (byte)this.msgType), derOutputStream6);
        return derOutputStream7.toByteArray();
    }
    
    public byte[] asn1EncodeReqBody() throws Asn1Exception, IOException {
        return this.reqBody.asn1Encode(this.msgType);
    }
}
