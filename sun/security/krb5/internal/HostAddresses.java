package sun.security.krb5.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.net.NetworkInterface;
import sun.security.krb5.Config;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Asn1Exception;
import java.util.Vector;
import sun.security.util.DerValue;
import java.net.InetAddress;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import java.net.UnknownHostException;
import java.io.IOException;

public class HostAddresses implements Cloneable
{
    private static boolean DEBUG;
    private HostAddress[] addresses;
    private volatile int hashCode;
    
    public HostAddresses(final HostAddress[] array) throws IOException {
        this.addresses = null;
        this.hashCode = 0;
        if (array != null) {
            this.addresses = new HostAddress[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a HostAddress");
                }
                this.addresses[i] = (HostAddress)array[i].clone();
            }
        }
    }
    
    public HostAddresses() throws UnknownHostException {
        this.addresses = null;
        this.hashCode = 0;
        (this.addresses = new HostAddress[1])[0] = new HostAddress();
    }
    
    private HostAddresses(final int n) {
        this.addresses = null;
        this.hashCode = 0;
    }
    
    public HostAddresses(final PrincipalName principalName) throws UnknownHostException, KrbException {
        this.addresses = null;
        this.hashCode = 0;
        final String[] nameStrings = principalName.getNameStrings();
        if (principalName.getNameType() != 3 || nameStrings.length < 2) {
            throw new KrbException(60, "Bad name");
        }
        final InetAddress[] allByName = InetAddress.getAllByName(nameStrings[1]);
        final HostAddress[] addresses = new HostAddress[allByName.length];
        for (int i = 0; i < allByName.length; ++i) {
            addresses[i] = new HostAddress(allByName[i]);
        }
        this.addresses = addresses;
    }
    
    public Object clone() {
        final HostAddresses hostAddresses = new HostAddresses(0);
        if (this.addresses != null) {
            hostAddresses.addresses = new HostAddress[this.addresses.length];
            for (int i = 0; i < this.addresses.length; ++i) {
                hostAddresses.addresses[i] = (HostAddress)this.addresses[i].clone();
            }
        }
        return hostAddresses;
    }
    
    public boolean inList(final HostAddress hostAddress) {
        if (this.addresses != null) {
            for (int i = 0; i < this.addresses.length; ++i) {
                if (this.addresses[i].equals(hostAddress)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hashCode = 17;
            if (this.addresses != null) {
                for (int i = 0; i < this.addresses.length; ++i) {
                    hashCode = 37 * hashCode + this.addresses[i].hashCode();
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
        if (!(o instanceof HostAddresses)) {
            return false;
        }
        final HostAddresses hostAddresses = (HostAddresses)o;
        if ((this.addresses == null && hostAddresses.addresses != null) || (this.addresses != null && hostAddresses.addresses == null)) {
            return false;
        }
        if (this.addresses != null && hostAddresses.addresses != null) {
            if (this.addresses.length != hostAddresses.addresses.length) {
                return false;
            }
            for (int i = 0; i < this.addresses.length; ++i) {
                if (!this.addresses[i].equals(hostAddresses.addresses[i])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public HostAddresses(final DerValue derValue) throws Asn1Exception, IOException {
        this.addresses = null;
        this.hashCode = 0;
        final Vector vector = new Vector();
        while (derValue.getData().available() > 0) {
            vector.addElement(new HostAddress(derValue.getData().getDerValue()));
        }
        if (vector.size() > 0) {
            vector.copyInto(this.addresses = new HostAddress[vector.size()]);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.addresses != null && this.addresses.length > 0) {
            for (int i = 0; i < this.addresses.length; ++i) {
                derOutputStream.write(this.addresses[i].asn1Encode());
            }
        }
        derOutputStream2.write((byte)48, derOutputStream);
        return derOutputStream2.toByteArray();
    }
    
    public static HostAddresses parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new HostAddresses(derValue.getData().getDerValue());
    }
    
    public void writeAddrs(final CCacheOutputStream cCacheOutputStream) throws IOException {
        if (this.addresses == null || this.addresses.length == 0) {
            cCacheOutputStream.write32(0);
            return;
        }
        cCacheOutputStream.write32(this.addresses.length);
        for (int i = 0; i < this.addresses.length; ++i) {
            cCacheOutputStream.write16(this.addresses[i].addrType);
            cCacheOutputStream.write32(this.addresses[i].address.length);
            cCacheOutputStream.write(this.addresses[i].address, 0, this.addresses[i].address.length);
        }
    }
    
    public InetAddress[] getInetAddresses() {
        if (this.addresses == null || this.addresses.length == 0) {
            return null;
        }
        final ArrayList list = new ArrayList(this.addresses.length);
        for (int i = 0; i < this.addresses.length; ++i) {
            try {
                if (this.addresses[i].addrType == 2 || this.addresses[i].addrType == 24) {
                    list.add(this.addresses[i].getInetAddress());
                }
            }
            catch (final UnknownHostException ex) {
                return null;
            }
        }
        return list.toArray(new InetAddress[list.size()]);
    }
    
    public static HostAddresses getLocalAddresses() throws IOException {
        final LinkedHashSet set = new LinkedHashSet();
        try {
            if (HostAddresses.DEBUG) {
                System.out.println(">>> KrbKdcReq local addresses are:");
            }
            final String all = Config.getInstance().getAll("libdefaults", "extra_addresses");
            if (all != null) {
                for (final String s : all.split("\\s+")) {
                    set.add(InetAddress.getByName(s));
                    if (HostAddresses.DEBUG) {
                        System.out.println("   extra_addresses: " + InetAddress.getByName(s));
                    }
                }
            }
            for (final NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (HostAddresses.DEBUG) {
                    System.out.println("   NetworkInterface " + networkInterface + ":");
                    System.out.println("      " + Collections.list(networkInterface.getInetAddresses()));
                }
                set.addAll(Collections.list(networkInterface.getInetAddresses()));
            }
            return new HostAddresses((InetAddress[])set.toArray(new InetAddress[set.size()]));
        }
        catch (final Exception ex) {
            throw new IOException(ex.toString());
        }
    }
    
    public HostAddresses(final InetAddress[] array) {
        this.addresses = null;
        this.hashCode = 0;
        if (array == null) {
            this.addresses = null;
            return;
        }
        this.addresses = new HostAddress[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.addresses[i] = new HostAddress(array[i]);
        }
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.addresses);
    }
    
    static {
        HostAddresses.DEBUG = Krb5.DEBUG;
    }
}
