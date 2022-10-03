package sun.security.krb5.internal.rcache;

import java.util.StringTokenizer;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class AuthTime
{
    final int ctime;
    final int cusec;
    final String client;
    final String server;
    
    public AuthTime(final String client, final String server, final int ctime, final int cusec) {
        this.ctime = ctime;
        this.cusec = cusec;
        this.client = client;
        this.server = server;
    }
    
    @Override
    public String toString() {
        return String.format("%d/%06d/----/%s", this.ctime, this.cusec, this.client);
    }
    
    private static String readStringWithLength(final SeekableByteChannel seekableByteChannel) throws IOException {
        final ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.order(ByteOrder.nativeOrder());
        seekableByteChannel.read(allocate);
        allocate.flip();
        final int int1 = allocate.getInt();
        if (int1 > 1024) {
            throw new IOException("Invalid string length");
        }
        final ByteBuffer allocate2 = ByteBuffer.allocate(int1);
        if (seekableByteChannel.read(allocate2) != int1) {
            throw new IOException("Not enough string");
        }
        final byte[] array = allocate2.array();
        return (array[int1 - 1] == 0) ? new String(array, 0, int1 - 1, StandardCharsets.UTF_8) : new String(array, StandardCharsets.UTF_8);
    }
    
    public static AuthTime readFrom(final SeekableByteChannel seekableByteChannel) throws IOException {
        final String stringWithLength = readStringWithLength(seekableByteChannel);
        final String stringWithLength2 = readStringWithLength(seekableByteChannel);
        final ByteBuffer allocate = ByteBuffer.allocate(8);
        seekableByteChannel.read(allocate);
        allocate.order(ByteOrder.nativeOrder());
        final int int1 = allocate.getInt(0);
        final int int2 = allocate.getInt(4);
        if (!stringWithLength.isEmpty()) {
            return new AuthTime(stringWithLength, stringWithLength2, int2, int1);
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(stringWithLength2, " :");
        if (stringTokenizer.countTokens() != 6) {
            throw new IOException("Incorrect rcache style");
        }
        stringTokenizer.nextToken();
        final String nextToken = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        final String nextToken2 = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        return new AuthTimeWithHash(nextToken2, stringTokenizer.nextToken(), int2, int1, nextToken);
    }
    
    protected byte[] encode0(final String s, final String s2) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes2 = s2.getBytes(StandardCharsets.UTF_8);
        final byte[] array = { 0 };
        final ByteBuffer order = ByteBuffer.allocate(4 + bytes.length + 1 + 4 + bytes2.length + 1 + 4 + 4).order(ByteOrder.nativeOrder());
        order.putInt(bytes.length + 1).put(bytes).put(array).putInt(bytes2.length + 1).put(bytes2).put(array).putInt(this.cusec).putInt(this.ctime);
        return order.array();
    }
    
    public byte[] encode(final boolean b) {
        return this.encode0(this.client, this.server);
    }
}
