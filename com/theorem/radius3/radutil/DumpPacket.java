package com.theorem.radius3.radutil;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileReader;
import com.theorem.radius3.Ascend;
import com.theorem.radius3.WISPr;
import com.theorem.radius3.Microsoft;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.PacketType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import com.theorem.radius3.A;
import com.theorem.radius3.AttributeList;
import java.util.TreeMap;

public class DumpPacket
{
    private TreeMap a;
    private byte[] b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private byte[] h;
    private AttributeList i;
    private A j;
    private String k;
    
    public DumpPacket() {
        this.a = a();
    }
    
    public DumpPacket(final byte[] b) {
        this.a = a();
        this.b = b;
        this.b();
    }
    
    public final byte[] toByteArray(final BufferedReader bufferedReader) throws IOException {
        final StringBuffer sb = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final String trim = line.trim();
            sb.append(trim).append('\u0001');
            if (trim.equals("")) {
                break;
            }
        }
        final byte[] a = this.a(sb);
        this.b();
        return a;
    }
    
    public final void setData(final byte[] b) {
        this.b = b;
    }
    
    private final byte[] a(final StringBuffer sb) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final StringBuffer sb2 = new StringBuffer();
        final int length = sb.length();
        int n = 0;
        for (int i = 0; i < length; ++i) {
            if (n >= 32) {
                while (i < length && sb.charAt(i) != '\u0001') {
                    ++i;
                }
                n = 0;
            }
            final char char1 = sb.charAt(i);
            if (Character.isLetterOrDigit(char1)) {
                sb2.append(char1);
                ++n;
            }
        }
        final int length2 = sb2.length();
        final char[] array = new char[2];
        for (int j = 0; j < length2; j += 2) {
            try {
                sb2.getChars(j, j + 2, array, 0);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                System.out.println("Error at " + array[0] + " " + array[1]);
                System.exit(0);
            }
            final String lowerCase = new String(array).toLowerCase();
            try {
                byteArrayOutputStream.write((byte)this.a.get(lowerCase) & 0xFF);
            }
            catch (final NullPointerException ex2) {
                final char[] array2 = new char[20];
                sb2.getChars(0, array2.length, array2, 0);
                throw new IOException("Unable to parse the line(s) containing: " + new String(array2));
            }
        }
        this.b = byteArrayOutputStream.toByteArray();
        this.c();
        return this.b;
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("------------------------ Packet -----------------------\n");
        this.b();
        final StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < this.h.length; ++i) {
            sb2.append(Integer.toHexString(this.h[i] & 0xFF)).append(' ');
        }
        final String upperCase = sb2.toString().toUpperCase();
        final StringBuffer sb3 = new StringBuffer();
        sb3.append("Physical Length: ").append(this.g).append('\n');
        if (this.f > this.g) {
            sb3.append("Warning: Packet physical length is less than length field value.\n");
        }
        sb3.append("Code: ").append(new PacketType().getName(this.d)).append('\n');
        sb3.append("Identifier: ").append(this.e).append('\n');
        sb3.append("Length: ").append(this.f).append('\n');
        sb3.append("Authenticator: ").append(upperCase).append('\n');
        sb3.append("Attributes:\n").append(this.i.toString());
        if (this.c > 0) {
            sb3.append(this.c).append(" bytes at end of dump ignored.").append('\n');
        }
        sb.append(sb3.toString());
        if (this.k != null) {
            sb.append("\nError: " + this.k).append('\n');
        }
        sb.append("-------------------- End of Packet --------------------\n");
        return sb.toString();
    }
    
    private final void b() {
        this.k = null;
        final ByteIterator byteIterator = new ByteIterator(this.b);
        this.d = byteIterator.readUnsignedByte();
        this.e = (byteIterator.readUnsignedByte() & 0xFF);
        this.f = byteIterator.readShort();
        byteIterator.read(this.h = new byte[16]);
        final int n = (int)(byteIterator.length() - byteIterator.current());
        final byte[] array = new byte[n];
        byteIterator.read(array);
        this.g = (int)byteIterator.length();
        if (this.j == null) {
            this.j = new A();
            new Cisco();
            new Microsoft();
            new WISPr();
            new Ascend();
        }
        this.i = new AttributeList();
        try {
            this.i.loadRadiusAttributes(array, 0, n, false);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            this.k = ex.getMessage();
        }
    }
    
    public final String getErrorMessage() {
        return this.k;
    }
    
    public final int getCode() {
        return this.d;
    }
    
    public final int getIdentifier() {
        return this.e;
    }
    
    public final int getLength() {
        return this.f;
    }
    
    public final int getPhysicalLength() {
        return this.g;
    }
    
    public final byte[] getAuthenticator() {
        return this.h;
    }
    
    public final AttributeList getAttributeList() {
        return this.i;
    }
    
    static TreeMap a() {
        final TreeMap treeMap = new TreeMap();
        for (int i = 0; i < 256; ++i) {
            String s = Integer.toHexString(i);
            if (s.length() < 2) {
                s = "0" + s;
            }
            treeMap.put(s, new Byte((byte)i));
        }
        return treeMap;
    }
    
    private final void c() {
        final ByteIterator byteIterator = new ByteIterator(this.b);
        byteIterator.readUnsignedByte();
        byteIterator.readUnsignedByte();
        byteIterator.readShort();
        byteIterator.read(new byte[16]);
        final byte[] array = new byte[(int)(byteIterator.length() - byteIterator.current())];
        byteIterator.read(array);
        this.c = 0;
        final AttributeList list = new AttributeList();
        int i = array.length;
        while (true) {
            while (i > 0) {
                try {
                    list.loadRadiusAttributes(array, 0, i, true);
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    --i;
                    ++this.c;
                    continue;
                }
                final byte[] b = new byte[this.b.length - this.c];
                System.arraycopy(this.b, 0, b, 0, b.length);
                this.b = b;
                return;
            }
            final byte[] array2 = new byte[0];
            continue;
        }
    }
    
    public static void main(final String[] array) {
        final DumpPacket dumpPacket = new DumpPacket();
        try {
            BufferedReader bufferedReader;
            if (array.length > 0) {
                bufferedReader = new BufferedReader(new FileReader(array[0]));
            }
            else {
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            }
            dumpPacket.toByteArray(bufferedReader);
            bufferedReader.close();
            System.out.println(dumpPacket);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
