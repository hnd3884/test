package java.net;

import java.util.Enumeration;
import java.io.IOException;

class Inet6AddressImpl implements InetAddressImpl
{
    private InetAddress anyLocalAddress;
    private InetAddress loopbackAddress;
    
    @Override
    public native String getLocalHostName() throws UnknownHostException;
    
    @Override
    public native InetAddress[] lookupAllHostAddr(final String p0) throws UnknownHostException;
    
    @Override
    public native String getHostByAddr(final byte[] p0) throws UnknownHostException;
    
    private native boolean isReachable0(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5) throws IOException;
    
    @Override
    public boolean isReachable(final InetAddress inetAddress, final int n, final NetworkInterface networkInterface, final int n2) throws IOException {
        byte[] address = null;
        int scopeId = -1;
        int scopeId2 = -1;
        if (networkInterface != null) {
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress2 = inetAddresses.nextElement();
                if (((Inet6Address)inetAddress2).getClass().isInstance(inetAddress)) {
                    address = inetAddress2.getAddress();
                    if (inetAddress2 instanceof Inet6Address) {
                        scopeId2 = ((Inet6Address)inetAddress2).getScopeId();
                        break;
                    }
                    break;
                }
            }
            if (address == null) {
                return false;
            }
        }
        if (inetAddress instanceof Inet6Address) {
            scopeId = ((Inet6Address)inetAddress).getScopeId();
        }
        return this.isReachable0(inetAddress.getAddress(), scopeId, n, address, n2, scopeId2);
    }
    
    @Override
    public synchronized InetAddress anyLocalAddress() {
        if (this.anyLocalAddress == null) {
            if (InetAddress.preferIPv6Address) {
                this.anyLocalAddress = new Inet6Address();
                this.anyLocalAddress.holder().hostName = "::";
            }
            else {
                this.anyLocalAddress = new Inet4AddressImpl().anyLocalAddress();
            }
        }
        return this.anyLocalAddress;
    }
    
    @Override
    public synchronized InetAddress loopbackAddress() {
        if (this.loopbackAddress == null) {
            if (InetAddress.preferIPv6Address) {
                this.loopbackAddress = new Inet6Address("localhost", new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 });
            }
            else {
                this.loopbackAddress = new Inet4AddressImpl().loopbackAddress();
            }
        }
        return this.loopbackAddress;
    }
}
