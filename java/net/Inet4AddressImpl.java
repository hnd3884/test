package java.net;

import java.util.Enumeration;
import java.io.IOException;

class Inet4AddressImpl implements InetAddressImpl
{
    private InetAddress anyLocalAddress;
    private InetAddress loopbackAddress;
    
    @Override
    public native String getLocalHostName() throws UnknownHostException;
    
    @Override
    public native InetAddress[] lookupAllHostAddr(final String p0) throws UnknownHostException;
    
    @Override
    public native String getHostByAddr(final byte[] p0) throws UnknownHostException;
    
    private native boolean isReachable0(final byte[] p0, final int p1, final byte[] p2, final int p3) throws IOException;
    
    @Override
    public synchronized InetAddress anyLocalAddress() {
        if (this.anyLocalAddress == null) {
            this.anyLocalAddress = new Inet4Address();
            this.anyLocalAddress.holder().hostName = "0.0.0.0";
        }
        return this.anyLocalAddress;
    }
    
    @Override
    public synchronized InetAddress loopbackAddress() {
        if (this.loopbackAddress == null) {
            this.loopbackAddress = new Inet4Address("localhost", new byte[] { 127, 0, 0, 1 });
        }
        return this.loopbackAddress;
    }
    
    @Override
    public boolean isReachable(final InetAddress inetAddress, final int n, final NetworkInterface networkInterface, final int n2) throws IOException {
        byte[] address = null;
        if (networkInterface != null) {
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress2;
            for (inetAddresses = networkInterface.getInetAddresses(), inetAddress2 = null; !(inetAddress2 instanceof Inet4Address) && inetAddresses.hasMoreElements(); inetAddress2 = inetAddresses.nextElement()) {}
            if (inetAddress2 instanceof Inet4Address) {
                address = inetAddress2.getAddress();
            }
        }
        return this.isReachable0(inetAddress.getAddress(), n, address, n2);
    }
}
