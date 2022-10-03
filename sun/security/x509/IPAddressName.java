package sun.security.x509;

import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import sun.security.util.BitArray;
import java.net.InetAddress;
import java.io.IOException;
import sun.security.util.DerValue;

public class IPAddressName implements GeneralNameInterface
{
    private byte[] address;
    private boolean isIPv4;
    private String name;
    private static final int MASKSIZE = 16;
    
    public IPAddressName(final DerValue derValue) throws IOException {
        this(derValue.getOctetString());
    }
    
    public IPAddressName(final byte[] address) throws IOException {
        if (address.length == 4 || address.length == 8) {
            this.isIPv4 = true;
        }
        else {
            if (address.length != 16 && address.length != 32) {
                throw new IOException("Invalid IPAddressName");
            }
            this.isIPv4 = false;
        }
        this.address = address;
    }
    
    public IPAddressName(final String s) throws IOException {
        if (s == null || s.length() == 0) {
            throw new IOException("IPAddress cannot be null or empty");
        }
        if (s.charAt(s.length() - 1) == '/') {
            throw new IOException("Invalid IPAddress: " + s);
        }
        if (s.indexOf(58) >= 0) {
            this.parseIPv6(s);
            this.isIPv4 = false;
        }
        else {
            if (s.indexOf(46) < 0) {
                throw new IOException("Invalid IPAddress: " + s);
            }
            this.parseIPv4(s);
            this.isIPv4 = true;
        }
    }
    
    private void parseIPv4(final String s) throws IOException {
        final int index = s.indexOf(47);
        if (index == -1) {
            this.address = InetAddress.getByName(s).getAddress();
        }
        else {
            this.address = new byte[8];
            final byte[] address = InetAddress.getByName(s.substring(index + 1)).getAddress();
            System.arraycopy(InetAddress.getByName(s.substring(0, index)).getAddress(), 0, this.address, 0, 4);
            System.arraycopy(address, 0, this.address, 4, 4);
        }
    }
    
    private void parseIPv6(final String s) throws IOException {
        final int index = s.indexOf(47);
        if (index == -1) {
            this.address = InetAddress.getByName(s).getAddress();
        }
        else {
            this.address = new byte[32];
            System.arraycopy(InetAddress.getByName(s.substring(0, index)).getAddress(), 0, this.address, 0, 16);
            final int int1 = Integer.parseInt(s.substring(index + 1));
            if (int1 < 0 || int1 > 128) {
                throw new IOException("IPv6Address prefix length (" + int1 + ") in out of valid range [0,128]");
            }
            final BitArray bitArray = new BitArray(128);
            for (int i = 0; i < int1; ++i) {
                bitArray.set(i, true);
            }
            final byte[] byteArray = bitArray.toByteArray();
            for (int j = 0; j < 16; ++j) {
                this.address[16 + j] = byteArray[j];
            }
        }
    }
    
    @Override
    public int getType() {
        return 7;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putOctetString(this.address);
    }
    
    @Override
    public String toString() {
        try {
            return "IPAddress: " + this.getName();
        }
        catch (final IOException ex) {
            return "IPAddress: " + new HexDumpEncoder().encodeBuffer(this.address);
        }
    }
    
    public String getName() throws IOException {
        if (this.name != null) {
            return this.name;
        }
        if (this.isIPv4) {
            final byte[] array = new byte[4];
            System.arraycopy(this.address, 0, array, 0, 4);
            this.name = InetAddress.getByAddress(array).getHostAddress();
            if (this.address.length == 8) {
                final byte[] array2 = new byte[4];
                System.arraycopy(this.address, 4, array2, 0, 4);
                this.name = this.name + "/" + InetAddress.getByAddress(array2).getHostAddress();
            }
        }
        else {
            final byte[] array3 = new byte[16];
            System.arraycopy(this.address, 0, array3, 0, 16);
            this.name = InetAddress.getByAddress(array3).getHostAddress();
            if (this.address.length == 32) {
                final byte[] array4 = new byte[16];
                for (int i = 16; i < 32; ++i) {
                    array4[i - 16] = this.address[i];
                }
                BitArray bitArray;
                int j;
                for (bitArray = new BitArray(128, array4), j = 0; j < 128 && bitArray.get(j); ++j) {}
                this.name = this.name + "/" + j;
                while (j < 128) {
                    if (bitArray.get(j)) {
                        throw new IOException("Invalid IPv6 subdomain - set bit " + j + " not contiguous");
                    }
                    ++j;
                }
            }
        }
        return this.name;
    }
    
    public byte[] getBytes() {
        return this.address.clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IPAddressName)) {
            return false;
        }
        final byte[] address = ((IPAddressName)o).address;
        if (address.length != this.address.length) {
            return false;
        }
        if (this.address.length == 8 || this.address.length == 32) {
            final int n = this.address.length / 2;
            for (int i = 0; i < n; ++i) {
                if ((byte)(this.address[i] & this.address[i + n]) != (byte)(address[i] & address[i + n])) {
                    return false;
                }
            }
            for (int j = n; j < this.address.length; ++j) {
                if (this.address[j] != address[j]) {
                    return false;
                }
            }
            return true;
        }
        return Arrays.equals(address, this.address);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 0; b < this.address.length; ++b) {
            n += this.address[b] * b;
        }
        return n;
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else if (generalNameInterface.getType() != 7) {
            n = -1;
        }
        else if (((IPAddressName)generalNameInterface).equals(this)) {
            n = 0;
        }
        else {
            final byte[] address = ((IPAddressName)generalNameInterface).address;
            if (address.length == 4 && this.address.length == 4) {
                n = 3;
            }
            else if ((address.length == 8 && this.address.length == 8) || (address.length == 32 && this.address.length == 32)) {
                boolean b = true;
                boolean b2 = true;
                boolean b3 = false;
                boolean b4 = false;
                for (int n2 = this.address.length / 2, i = 0; i < n2; ++i) {
                    if ((byte)(this.address[i] & this.address[i + n2]) != this.address[i]) {
                        b3 = true;
                    }
                    if ((byte)(address[i] & address[i + n2]) != address[i]) {
                        b4 = true;
                    }
                    if ((byte)(this.address[i + n2] & address[i + n2]) != this.address[i + n2] || (byte)(this.address[i] & this.address[i + n2]) != (byte)(address[i] & this.address[i + n2])) {
                        b = false;
                    }
                    if ((byte)(address[i + n2] & this.address[i + n2]) != address[i + n2] || (byte)(address[i] & address[i + n2]) != (byte)(this.address[i] & address[i + n2])) {
                        b2 = false;
                    }
                }
                if (b3 || b4) {
                    if (b3 && b4) {
                        n = 0;
                    }
                    else if (b3) {
                        n = 2;
                    }
                    else {
                        n = 1;
                    }
                }
                else if (b) {
                    n = 1;
                }
                else if (b2) {
                    n = 2;
                }
                else {
                    n = 3;
                }
            }
            else if (address.length == 8 || address.length == 32) {
                int n3;
                int n4;
                for (n3 = 0, n4 = address.length / 2; n3 < n4 && (this.address[n3] & address[n3 + n4]) == address[n3]; ++n3) {}
                if (n3 == n4) {
                    n = 2;
                }
                else {
                    n = 3;
                }
            }
            else if (this.address.length == 8 || this.address.length == 32) {
                int n5;
                int n6;
                for (n5 = 0, n6 = this.address.length / 2; n5 < n6 && (address[n5] & this.address[n5 + n6]) == this.address[n5]; ++n5) {}
                if (n5 == n6) {
                    n = 1;
                }
                else {
                    n = 3;
                }
            }
            else {
                n = 3;
            }
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("subtreeDepth() not defined for IPAddressName");
    }
}
