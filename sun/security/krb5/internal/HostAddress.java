package sun.security.krb5.internal;

import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.net.InetAddress;

public class HostAddress implements Cloneable
{
    int addrType;
    byte[] address;
    private static InetAddress localInetAddress;
    private static final boolean DEBUG;
    private volatile int hashCode;
    
    private HostAddress(final int n) {
        this.address = null;
        this.hashCode = 0;
    }
    
    public Object clone() {
        final HostAddress hostAddress = new HostAddress(0);
        hostAddress.addrType = this.addrType;
        if (this.address != null) {
            hostAddress.address = this.address.clone();
        }
        return hostAddress;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hashCode = 37 * 17 + this.addrType;
            if (this.address != null) {
                for (int i = 0; i < this.address.length; ++i) {
                    hashCode = 37 * hashCode + this.address[i];
                }
            }
            this.hashCode = hashCode;
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HostAddress)) {
            return false;
        }
        final HostAddress hostAddress = (HostAddress)o;
        if (this.addrType != hostAddress.addrType || (this.address != null && hostAddress.address == null) || (this.address == null && hostAddress.address != null)) {
            return false;
        }
        if (this.address != null && hostAddress.address != null) {
            if (this.address.length != hostAddress.address.length) {
                return false;
            }
            for (int i = 0; i < this.address.length; ++i) {
                if (this.address[i] != hostAddress.address[i]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static synchronized InetAddress getLocalInetAddress() throws UnknownHostException {
        if (HostAddress.localInetAddress == null) {
            HostAddress.localInetAddress = InetAddress.getLocalHost();
        }
        if (HostAddress.localInetAddress == null) {
            throw new UnknownHostException();
        }
        return HostAddress.localInetAddress;
    }
    
    public InetAddress getInetAddress() throws UnknownHostException {
        if (this.addrType == 2 || this.addrType == 24) {
            return InetAddress.getByAddress(this.address);
        }
        return null;
    }
    
    private int getAddrType(final InetAddress inetAddress) {
        int n = 0;
        if (inetAddress instanceof Inet4Address) {
            n = 2;
        }
        else if (inetAddress instanceof Inet6Address) {
            n = 24;
        }
        return n;
    }
    
    public HostAddress() throws UnknownHostException {
        this.address = null;
        this.hashCode = 0;
        final InetAddress localInetAddress = getLocalInetAddress();
        this.addrType = this.getAddrType(localInetAddress);
        this.address = localInetAddress.getAddress();
    }
    
    public HostAddress(final int addrType, final byte[] array) throws KrbApErrException, UnknownHostException {
        this.address = null;
        this.hashCode = 0;
        switch (addrType) {
            case 2: {
                if (array.length != 4) {
                    throw new KrbApErrException(0, "Invalid Internet address");
                }
                break;
            }
            case 5: {
                if (array.length != 2) {
                    throw new KrbApErrException(0, "Invalid CHAOSnet address");
                }
                break;
            }
            case 6: {
                if (array.length != 6) {
                    throw new KrbApErrException(0, "Invalid XNS address");
                }
                break;
            }
            case 16: {
                if (array.length != 3) {
                    throw new KrbApErrException(0, "Invalid DDP address");
                }
                break;
            }
            case 12: {
                if (array.length != 2) {
                    throw new KrbApErrException(0, "Invalid DECnet Phase IV address");
                }
                break;
            }
            case 24: {
                if (array.length != 16) {
                    throw new KrbApErrException(0, "Invalid Internet IPv6 address");
                }
                break;
            }
        }
        this.addrType = addrType;
        if (array != null) {
            this.address = array.clone();
        }
        if (HostAddress.DEBUG && (this.addrType == 2 || this.addrType == 24)) {
            System.out.println("Host address is " + InetAddress.getByAddress(this.address));
        }
    }
    
    public HostAddress(final InetAddress inetAddress) {
        this.address = null;
        this.hashCode = 0;
        this.addrType = this.getAddrType(inetAddress);
        this.address = inetAddress.getAddress();
    }
    
    public HostAddress(final DerValue derValue) throws Asn1Exception, IOException {
        this.address = null;
        this.hashCode = 0;
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.addrType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.address = derValue3.getData().getOctetString();
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.addrType);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(this.address);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public static HostAddress parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new HostAddress(derValue.getData().getDerValue());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(this.address));
        sb.append('(').append(this.addrType).append(')');
        return sb.toString();
    }
    
    static {
        DEBUG = Krb5.DEBUG;
    }
}
