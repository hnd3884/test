package sun.management.jdp;

public abstract class JdpGenericPacket implements JdpPacket
{
    private static final int MAGIC = -1056969150;
    private static final short PROTOCOL_VERSION = 1;
    
    protected JdpGenericPacket() {
    }
    
    public static void checkMagic(final int n) throws JdpException {
        if (n != -1056969150) {
            throw new JdpException("Invalid JDP magic header: " + n);
        }
    }
    
    public static void checkVersion(final short n) throws JdpException {
        if (n > 1) {
            throw new JdpException("Unsupported protocol version: " + n);
        }
    }
    
    public static int getMagic() {
        return -1056969150;
    }
    
    public static short getVersion() {
        return 1;
    }
}
