package sun.security.krb5.internal;

import sun.misc.HexDumpEncoder;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.krb5.internal.crypto.EType;
import java.util.Vector;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class PAData
{
    private int pADataType;
    private byte[] pADataValue;
    private static final byte TAG_PATYPE = 1;
    private static final byte TAG_PAVALUE = 2;
    
    private PAData() {
        this.pADataValue = null;
    }
    
    public PAData(final int paDataType, final byte[] array) {
        this.pADataValue = null;
        this.pADataType = paDataType;
        if (array != null) {
            this.pADataValue = array.clone();
        }
    }
    
    public Object clone() {
        final PAData paData = new PAData();
        paData.pADataType = this.pADataType;
        if (this.pADataValue != null) {
            paData.pADataValue = new byte[this.pADataValue.length];
            System.arraycopy(this.pADataValue, 0, paData.pADataValue, 0, this.pADataValue.length);
        }
        return paData;
    }
    
    public PAData(final DerValue derValue) throws Asn1Exception, IOException {
        this.pADataValue = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.pADataType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) == 0x2) {
            this.pADataValue = derValue3.getData().getOctetString();
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.pADataType);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(this.pADataValue);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public int getType() {
        return this.pADataType;
    }
    
    public byte[] getValue() {
        return (byte[])((this.pADataValue == null) ? null : ((byte[])this.pADataValue.clone()));
    }
    
    public static PAData[] parseSequence(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue().getData().getDerValue();
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final Vector vector = new Vector();
        while (derValue.getData().available() > 0) {
            vector.addElement(new PAData(derValue.getData().getDerValue()));
        }
        if (vector.size() > 0) {
            final PAData[] array = new PAData[vector.size()];
            vector.copyInto(array);
            return array;
        }
        return null;
    }
    
    public static int getPreferredEType(final PAData[] array, final int n) throws IOException, Asn1Exception {
        if (array == null) {
            return n;
        }
        DerValue derValue = null;
        DerValue derValue2 = null;
        for (final PAData paData : array) {
            if (paData.getValue() != null) {
                switch (paData.getType()) {
                    case 11: {
                        derValue = new DerValue(paData.getValue());
                        break;
                    }
                    case 19: {
                        derValue2 = new DerValue(paData.getValue());
                        break;
                    }
                }
            }
        }
        if (derValue2 != null) {
            while (derValue2.data.available() > 0) {
                final ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue2.data.getDerValue());
                if (EType.isNewer(eTypeInfo2.getEType()) || eTypeInfo2.getParams() == null) {
                    return eTypeInfo2.getEType();
                }
            }
        }
        if (derValue != null && derValue.data.available() > 0) {
            return new ETypeInfo(derValue.data.getDerValue()).getEType();
        }
        return n;
    }
    
    public static SaltAndParams getSaltAndParams(final int n, final PAData[] array) throws Asn1Exception, IOException {
        if (array == null) {
            return null;
        }
        DerValue derValue = null;
        DerValue derValue2 = null;
        String s = null;
        for (final PAData paData : array) {
            if (paData.getValue() != null) {
                switch (paData.getType()) {
                    case 3: {
                        s = new String(paData.getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
                        break;
                    }
                    case 11: {
                        derValue = new DerValue(paData.getValue());
                        break;
                    }
                    case 19: {
                        derValue2 = new DerValue(paData.getValue());
                        break;
                    }
                }
            }
        }
        if (derValue2 != null) {
            while (derValue2.data.available() > 0) {
                final ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue2.data.getDerValue());
                if (eTypeInfo2.getEType() == n && (EType.isNewer(n) || eTypeInfo2.getParams() == null)) {
                    return new SaltAndParams(eTypeInfo2.getSalt(), eTypeInfo2.getParams());
                }
            }
        }
        if (derValue != null) {
            while (derValue.data.available() > 0) {
                final ETypeInfo eTypeInfo3 = new ETypeInfo(derValue.data.getDerValue());
                if (eTypeInfo3.getEType() == n) {
                    return new SaltAndParams(eTypeInfo3.getSalt(), null);
                }
            }
        }
        if (s != null) {
            return new SaltAndParams(s, null);
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ").append(this.pADataType).append('\n');
        switch (this.pADataType) {
            case 2: {
                sb.append("\t PA-ENC-TIMESTAMP");
                break;
            }
            case 11: {
                if (this.pADataValue != null) {
                    try {
                        final DerValue derValue = new DerValue(this.pADataValue);
                        while (derValue.data.available() > 0) {
                            final ETypeInfo eTypeInfo = new ETypeInfo(derValue.data.getDerValue());
                            sb.append("\t PA-ETYPE-INFO etype = ").append(eTypeInfo.getEType()).append(", salt = ").append(eTypeInfo.getSalt()).append('\n');
                        }
                    }
                    catch (final IOException | Asn1Exception ex) {
                        sb.append("\t <Unparseable PA-ETYPE-INFO>\n");
                    }
                    break;
                }
                break;
            }
            case 19: {
                if (this.pADataValue != null) {
                    try {
                        final DerValue derValue2 = new DerValue(this.pADataValue);
                        while (derValue2.data.available() > 0) {
                            final ETypeInfo2 eTypeInfo2 = new ETypeInfo2(derValue2.data.getDerValue());
                            sb.append("\t PA-ETYPE-INFO2 etype = ").append(eTypeInfo2.getEType()).append(", salt = ").append(eTypeInfo2.getSalt()).append(", s2kparams = ");
                            final byte[] params = eTypeInfo2.getParams();
                            if (params == null) {
                                sb.append("null\n");
                            }
                            else if (params.length == 0) {
                                sb.append("empty\n");
                            }
                            else {
                                sb.append(new HexDumpEncoder().encodeBuffer(params));
                            }
                        }
                    }
                    catch (final IOException | Asn1Exception ex2) {
                        sb.append("\t <Unparseable PA-ETYPE-INFO>\n");
                    }
                    break;
                }
                break;
            }
            case 129: {
                sb.append("\t PA-FOR-USER\n");
                break;
            }
        }
        return sb.toString();
    }
    
    public static class SaltAndParams
    {
        public final String salt;
        public final byte[] params;
        
        public SaltAndParams(String salt, final byte[] params) {
            if (salt != null && salt.isEmpty()) {
                salt = null;
            }
            this.salt = salt;
            this.params = params;
        }
    }
}
