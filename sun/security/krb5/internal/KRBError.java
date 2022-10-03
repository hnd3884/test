package sun.security.krb5.internal;

import java.util.Arrays;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.internal.util.KerberosString;
import java.util.ArrayList;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import java.io.ObjectOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.io.ObjectInputStream;
import sun.security.krb5.Checksum;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import java.io.Serializable;

public class KRBError implements Serializable
{
    static final long serialVersionUID = 3643809337475284503L;
    private int pvno;
    private int msgType;
    private KerberosTime cTime;
    private Integer cuSec;
    private KerberosTime sTime;
    private Integer suSec;
    private int errorCode;
    private Realm crealm;
    private PrincipalName cname;
    private PrincipalName sname;
    private String eText;
    private byte[] eData;
    private Checksum eCksum;
    private PAData[] pa;
    private static boolean DEBUG;
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        try {
            this.init(new DerValue((byte[])objectInputStream.readObject()));
            this.parseEData(this.eData);
        }
        catch (final Exception ex) {
            throw new IOException(ex);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        try {
            objectOutputStream.writeObject(this.asn1Encode());
        }
        catch (final Exception ex) {
            throw new IOException(ex);
        }
    }
    
    public KRBError(final APOptions apOptions, final KerberosTime cTime, final Integer cuSec, final KerberosTime sTime, final Integer suSec, final int errorCode, final PrincipalName cname, final PrincipalName sname, final String eText, final byte[] eData) throws IOException, Asn1Exception {
        this.pvno = 5;
        this.msgType = 30;
        this.cTime = cTime;
        this.cuSec = cuSec;
        this.sTime = sTime;
        this.suSec = suSec;
        this.errorCode = errorCode;
        this.crealm = ((cname != null) ? cname.getRealm() : null);
        this.cname = cname;
        this.sname = sname;
        this.eText = eText;
        this.parseEData(this.eData = eData);
    }
    
    public KRBError(final APOptions apOptions, final KerberosTime cTime, final Integer cuSec, final KerberosTime sTime, final Integer suSec, final int errorCode, final PrincipalName cname, final PrincipalName sname, final String eText, final byte[] eData, final Checksum eCksum) throws IOException, Asn1Exception {
        this.pvno = 5;
        this.msgType = 30;
        this.cTime = cTime;
        this.cuSec = cuSec;
        this.sTime = sTime;
        this.suSec = suSec;
        this.errorCode = errorCode;
        this.crealm = ((cname != null) ? cname.getRealm() : null);
        this.cname = cname;
        this.sname = sname;
        this.eText = eText;
        this.eData = eData;
        this.eCksum = eCksum;
        this.parseEData(this.eData);
    }
    
    public KRBError(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(new DerValue(array));
        this.parseEData(this.eData);
    }
    
    public KRBError(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue);
        this.showDebug();
        this.parseEData(this.eData);
    }
    
    private void parseEData(final byte[] array) throws IOException {
        if (array == null) {
            return;
        }
        Label_0091: {
            if (this.errorCode != 25) {
                if (this.errorCode != 24) {
                    break Label_0091;
                }
            }
            try {
                this.parsePAData(array);
                return;
            }
            catch (final Exception ex) {
                if (KRBError.DEBUG) {
                    System.out.println("Unable to parse eData field of KRB-ERROR:\n" + new HexDumpEncoder().encodeBuffer(array));
                }
                final IOException ex2 = new IOException("Unable to parse eData field of KRB-ERROR");
                ex2.initCause(ex);
                throw ex2;
            }
        }
        if (KRBError.DEBUG) {
            System.out.println("Unknown eData field of KRB-ERROR:\n" + new HexDumpEncoder().encodeBuffer(array));
        }
    }
    
    private void parsePAData(final byte[] array) throws IOException, Asn1Exception {
        final DerValue derValue = new DerValue(array);
        final ArrayList list = new ArrayList();
        while (derValue.data.available() > 0) {
            final PAData paData = new PAData(derValue.data.getDerValue());
            list.add(paData);
            if (KRBError.DEBUG) {
                System.out.println(paData);
            }
        }
        this.pa = (PAData[])list.toArray(new PAData[list.size()]);
    }
    
    public final Realm getClientRealm() {
        return this.crealm;
    }
    
    public final KerberosTime getServerTime() {
        return this.sTime;
    }
    
    public final KerberosTime getClientTime() {
        return this.cTime;
    }
    
    public final Integer getServerMicroSeconds() {
        return this.suSec;
    }
    
    public final Integer getClientMicroSeconds() {
        return this.cuSec;
    }
    
    public final int getErrorCode() {
        return this.errorCode;
    }
    
    public final PAData[] getPA() {
        return this.pa;
    }
    
    public final String getErrorString() {
        return this.eText;
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        if ((derValue.getTag() & 0x1F) != 0x1E || !derValue.isApplication() || !derValue.isConstructed()) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue3 = derValue2.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.pvno = derValue3.getData().getBigInteger().intValue();
        if (this.pvno != 5) {
            throw new KrbApErrException(39);
        }
        final DerValue derValue4 = derValue2.getData().getDerValue();
        if ((derValue4.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.msgType = derValue4.getData().getBigInteger().intValue();
        if (this.msgType != 30) {
            throw new KrbApErrException(40);
        }
        this.cTime = KerberosTime.parse(derValue2.getData(), (byte)2, true);
        if ((derValue2.getData().peekByte() & 0x1F) == 0x3) {
            this.cuSec = new Integer(derValue2.getData().getDerValue().getData().getBigInteger().intValue());
        }
        else {
            this.cuSec = null;
        }
        this.sTime = KerberosTime.parse(derValue2.getData(), (byte)4, false);
        final DerValue derValue5 = derValue2.getData().getDerValue();
        if ((derValue5.getTag() & 0x1F) != 0x5) {
            throw new Asn1Exception(906);
        }
        this.suSec = new Integer(derValue5.getData().getBigInteger().intValue());
        final DerValue derValue6 = derValue2.getData().getDerValue();
        if ((derValue6.getTag() & 0x1F) != 0x6) {
            throw new Asn1Exception(906);
        }
        this.errorCode = derValue6.getData().getBigInteger().intValue();
        this.crealm = Realm.parse(derValue2.getData(), (byte)7, true);
        this.cname = PrincipalName.parse(derValue2.getData(), (byte)8, true, this.crealm);
        this.sname = PrincipalName.parse(derValue2.getData(), (byte)10, false, Realm.parse(derValue2.getData(), (byte)9, false));
        this.eText = null;
        this.eData = null;
        this.eCksum = null;
        if (derValue2.getData().available() > 0 && (derValue2.getData().peekByte() & 0x1F) == 0xB) {
            this.eText = new KerberosString(derValue2.getData().getDerValue().getData().getDerValue()).toString();
        }
        if (derValue2.getData().available() > 0 && (derValue2.getData().peekByte() & 0x1F) == 0xC) {
            this.eData = derValue2.getData().getDerValue().getData().getOctetString();
        }
        if (derValue2.getData().available() > 0) {
            this.eCksum = Checksum.parse(derValue2.getData(), (byte)13, true);
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    private void showDebug() {
        if (KRBError.DEBUG) {
            System.out.println(">>>KRBError:");
            if (this.cTime != null) {
                System.out.println("\t cTime is " + this.cTime.toDate().toString() + " " + this.cTime.toDate().getTime());
            }
            if (this.cuSec != null) {
                System.out.println("\t cuSec is " + (int)this.cuSec);
            }
            System.out.println("\t sTime is " + this.sTime.toDate().toString() + " " + this.sTime.toDate().getTime());
            System.out.println("\t suSec is " + this.suSec);
            System.out.println("\t error code is " + this.errorCode);
            System.out.println("\t error Message is " + Krb5.getErrorMessage(this.errorCode));
            if (this.crealm != null) {
                System.out.println("\t crealm is " + this.crealm.toString());
            }
            if (this.cname != null) {
                System.out.println("\t cname is " + this.cname.toString());
            }
            if (this.sname != null) {
                System.out.println("\t sname is " + this.sname.toString());
            }
            if (this.eData != null) {
                System.out.println("\t eData provided.");
            }
            if (this.eCksum != null) {
                System.out.println("\t checksum provided.");
            }
            System.out.println("\t msgType is " + this.msgType);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.pvno));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putInteger(BigInteger.valueOf(this.msgType));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        if (this.cTime != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), this.cTime.asn1Encode());
        }
        if (this.cuSec != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putInteger(BigInteger.valueOf(this.cuSec));
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream4);
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)4), this.sTime.asn1Encode());
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.putInteger(BigInteger.valueOf(this.suSec));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)5), derOutputStream5);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.putInteger(BigInteger.valueOf(this.errorCode));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)6), derOutputStream6);
        if (this.crealm != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)7), this.crealm.asn1Encode());
        }
        if (this.cname != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)8), this.cname.asn1Encode());
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)9), this.sname.getRealm().asn1Encode());
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)10), this.sname.asn1Encode());
        if (this.eText != null) {
            final DerOutputStream derOutputStream7 = new DerOutputStream();
            derOutputStream7.putDerValue(new KerberosString(this.eText).toDerValue());
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)11), derOutputStream7);
        }
        if (this.eData != null) {
            final DerOutputStream derOutputStream8 = new DerOutputStream();
            derOutputStream8.putOctetString(this.eData);
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)12), derOutputStream8);
        }
        if (this.eCksum != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)13), this.eCksum.asn1Encode());
        }
        final DerOutputStream derOutputStream9 = new DerOutputStream();
        derOutputStream9.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream10 = new DerOutputStream();
        derOutputStream10.write(DerValue.createTag((byte)64, true, (byte)30), derOutputStream9);
        return derOutputStream10.toByteArray();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KRBError)) {
            return false;
        }
        final KRBError krbError = (KRBError)o;
        return this.pvno == krbError.pvno && this.msgType == krbError.msgType && isEqual(this.cTime, krbError.cTime) && isEqual(this.cuSec, krbError.cuSec) && isEqual(this.sTime, krbError.sTime) && isEqual(this.suSec, krbError.suSec) && this.errorCode == krbError.errorCode && isEqual(this.crealm, krbError.crealm) && isEqual(this.cname, krbError.cname) && isEqual(this.sname, krbError.sname) && isEqual(this.eText, krbError.eText) && Arrays.equals(this.eData, krbError.eData) && isEqual(this.eCksum, krbError.eCksum);
    }
    
    private static boolean isEqual(final Object o, final Object o2) {
        return (o == null) ? (o2 == null) : o.equals(o2);
    }
    
    @Override
    public int hashCode() {
        int n = 37 * (37 * 17 + this.pvno) + this.msgType;
        if (this.cTime != null) {
            n = 37 * n + this.cTime.hashCode();
        }
        if (this.cuSec != null) {
            n = 37 * n + this.cuSec.hashCode();
        }
        if (this.sTime != null) {
            n = 37 * n + this.sTime.hashCode();
        }
        if (this.suSec != null) {
            n = 37 * n + this.suSec.hashCode();
        }
        int n2 = 37 * n + this.errorCode;
        if (this.crealm != null) {
            n2 = 37 * n2 + this.crealm.hashCode();
        }
        if (this.cname != null) {
            n2 = 37 * n2 + this.cname.hashCode();
        }
        if (this.sname != null) {
            n2 = 37 * n2 + this.sname.hashCode();
        }
        if (this.eText != null) {
            n2 = 37 * n2 + this.eText.hashCode();
        }
        int n3 = 37 * n2 + Arrays.hashCode(this.eData);
        if (this.eCksum != null) {
            n3 = 37 * n3 + this.eCksum.hashCode();
        }
        return n3;
    }
    
    static {
        KRBError.DEBUG = Krb5.DEBUG;
    }
}
