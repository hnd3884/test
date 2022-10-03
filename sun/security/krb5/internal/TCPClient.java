package sun.security.krb5.internal;

import java.io.InputStream;
import sun.misc.IOUtils;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

class TCPClient extends NetClient
{
    private Socket tcpSocket;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    
    TCPClient(final String s, final int n, final int soTimeout) throws IOException {
        (this.tcpSocket = new Socket()).connect(new InetSocketAddress(s, n), soTimeout);
        this.out = new BufferedOutputStream(this.tcpSocket.getOutputStream());
        this.in = new BufferedInputStream(this.tcpSocket.getInputStream());
        this.tcpSocket.setSoTimeout(soTimeout);
    }
    
    @Override
    public void send(final byte[] array) throws IOException {
        final byte[] array2 = new byte[4];
        intToNetworkByteOrder(array.length, array2, 0, 4);
        this.out.write(array2);
        this.out.write(array);
        this.out.flush();
    }
    
    @Override
    public byte[] receive() throws IOException {
        final byte[] array = new byte[4];
        final int fully = this.readFully(array, 4);
        if (fully != 4) {
            if (Krb5.DEBUG) {
                System.out.println(">>>DEBUG: TCPClient could not read length field");
            }
            return null;
        }
        final int networkByteOrderToInt = networkByteOrderToInt(array, 0, 4);
        if (Krb5.DEBUG) {
            System.out.println(">>>DEBUG: TCPClient reading " + networkByteOrderToInt + " bytes");
        }
        if (networkByteOrderToInt <= 0) {
            if (Krb5.DEBUG) {
                System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + networkByteOrderToInt);
            }
            return null;
        }
        try {
            return IOUtils.readExactlyNBytes(this.in, networkByteOrderToInt);
        }
        catch (final IOException ex) {
            if (Krb5.DEBUG) {
                System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + networkByteOrderToInt + "/" + fully + ")");
            }
            return null;
        }
    }
    
    @Override
    public void close() throws IOException {
        this.tcpSocket.close();
    }
    
    private int readFully(final byte[] array, int i) throws IOException {
        int n = 0;
        while (i > 0) {
            final int read = this.in.read(array, n, i);
            if (read == -1) {
                return (n == 0) ? -1 : n;
            }
            n += read;
            i -= read;
        }
        return n;
    }
    
    private static int networkByteOrderToInt(final byte[] array, final int n, final int n2) {
        if (n2 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            n3 = (n3 << 8 | (array[n + i] & 0xFF));
        }
        return n3;
    }
    
    private static void intToNetworkByteOrder(int n, final byte[] array, final int n2, final int n3) {
        if (n3 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        for (int i = n3 - 1; i >= 0; --i) {
            array[n2 + i] = (byte)(n & 0xFF);
            n >>>= 8;
        }
    }
}
