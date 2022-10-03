package com.sun.corba.se.spi.ior.iiop;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public class GIOPVersion
{
    public static final GIOPVersion V1_0;
    public static final GIOPVersion V1_1;
    public static final GIOPVersion V1_2;
    public static final GIOPVersion V1_3;
    public static final GIOPVersion V13_XX;
    public static final GIOPVersion DEFAULT_VERSION;
    public static final int VERSION_1_0 = 256;
    public static final int VERSION_1_1 = 257;
    public static final int VERSION_1_2 = 258;
    public static final int VERSION_1_3 = 259;
    public static final int VERSION_13_XX = 3329;
    private byte major;
    private byte minor;
    
    public GIOPVersion() {
        this.major = 0;
        this.minor = 0;
    }
    
    public GIOPVersion(final byte major, final byte minor) {
        this.major = 0;
        this.minor = 0;
        this.major = major;
        this.minor = minor;
    }
    
    public GIOPVersion(final int n, final int n2) {
        this.major = 0;
        this.minor = 0;
        this.major = (byte)n;
        this.minor = (byte)n2;
    }
    
    public byte getMajor() {
        return this.major;
    }
    
    public byte getMinor() {
        return this.minor;
    }
    
    public boolean equals(final GIOPVersion giopVersion) {
        return giopVersion.major == this.major && giopVersion.minor == this.minor;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof GIOPVersion && this.equals((GIOPVersion)o);
    }
    
    @Override
    public int hashCode() {
        return 37 * this.major + this.minor;
    }
    
    public boolean lessThan(final GIOPVersion giopVersion) {
        return this.major < giopVersion.major || (this.major == giopVersion.major && this.minor < giopVersion.minor);
    }
    
    public int intValue() {
        return this.major << 8 | this.minor;
    }
    
    @Override
    public String toString() {
        return this.major + "." + this.minor;
    }
    
    public static GIOPVersion getInstance(final byte b, final byte b2) {
        switch (b << 8 | b2) {
            case 256: {
                return GIOPVersion.V1_0;
            }
            case 257: {
                return GIOPVersion.V1_1;
            }
            case 258: {
                return GIOPVersion.V1_2;
            }
            case 259: {
                return GIOPVersion.V1_3;
            }
            case 3329: {
                return GIOPVersion.V13_XX;
            }
            default: {
                return new GIOPVersion(b, b2);
            }
        }
    }
    
    public static GIOPVersion parseVersion(final String s) {
        final int index = s.indexOf(46);
        if (index < 1 || index == s.length() - 1) {
            throw new NumberFormatException("GIOP major, minor, and decimal point required: " + s);
        }
        return getInstance((byte)Integer.parseInt(s.substring(0, index)), (byte)Integer.parseInt(s.substring(index + 1, s.length())));
    }
    
    public static GIOPVersion chooseRequestVersion(final ORB orb, final IOR ior) {
        final GIOPVersion giopVersion = orb.getORBData().getGIOPVersion();
        final IIOPProfile profile = ior.getProfile();
        final GIOPVersion giopVersion2 = profile.getGIOPVersion();
        final ORBVersion orbVersion = profile.getORBVersion();
        if (!orbVersion.equals(ORBVersionFactory.getFOREIGN()) && orbVersion.lessThan(ORBVersionFactory.getNEWER())) {
            return GIOPVersion.V1_0;
        }
        final byte major = giopVersion2.getMajor();
        final byte minor = giopVersion2.getMinor();
        final byte major2 = giopVersion.getMajor();
        final byte minor2 = giopVersion.getMinor();
        if (major2 < major) {
            return giopVersion;
        }
        if (major2 > major) {
            return giopVersion2;
        }
        if (minor2 <= minor) {
            return giopVersion;
        }
        return giopVersion2;
    }
    
    public boolean supportsIORIIOPProfileComponents() {
        return this.getMinor() > 0 || this.getMajor() > 1;
    }
    
    public void read(final InputStream inputStream) {
        this.major = inputStream.read_octet();
        this.minor = inputStream.read_octet();
    }
    
    public void write(final OutputStream outputStream) {
        outputStream.write_octet(this.major);
        outputStream.write_octet(this.minor);
    }
    
    static {
        V1_0 = new GIOPVersion((byte)1, (byte)0);
        V1_1 = new GIOPVersion((byte)1, (byte)1);
        V1_2 = new GIOPVersion((byte)1, (byte)2);
        V1_3 = new GIOPVersion((byte)1, (byte)3);
        V13_XX = new GIOPVersion((byte)13, (byte)1);
        DEFAULT_VERSION = GIOPVersion.V1_2;
    }
}
