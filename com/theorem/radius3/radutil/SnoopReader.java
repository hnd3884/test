package com.theorem.radius3.radutil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;

public class SnoopReader
{
    private static final byte[] a;
    public static final int ETHERNET = 4;
    private DataInputStream b;
    
    public static void main(final String[] array) {
        try {
            final SnoopReader snoopReader = new SnoopReader(new BufferedInputStream(new FileInputStream(array[0])));
            int n = 0;
            SnoopRecord next;
            while ((next = snoopReader.next()) != null) {
                System.out.println("#" + n + " " + next);
                ++n;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public SnoopReader(final InputStream inputStream) throws IOException {
        this.b = new DataInputStream(inputStream);
        this.a();
    }
    
    private final void a() throws IOException {
        final byte[] array = new byte[8];
        this.b.read(array);
        if (!Util.cmp(array, SnoopReader.a)) {
            throw new IOException("Not a Snoop file");
        }
        final int n = this.b.readInt() & -1;
        if (n != 2) {
            throw new IOException("Incorrect Snoop file version - " + n + " expecting " + 2);
        }
        if ((this.b.readInt() & -1) != 0x4) {
            throw new IOException("Can't decode anything but Ethernet packets.");
        }
    }
    
    public final SnoopRecord next() throws IOException {
        SnoopRecord snoopRecord;
        try {
            final int n = this.b.readInt() & -1;
            final int n2 = this.b.readInt() & -1;
            final int n3 = this.b.readInt() & -1;
            snoopRecord = new SnoopRecord(n, n2, n3, this.b.readInt() & -1, this.b.readInt() & -1, this.b.readInt() & -1);
            final byte[] array = new byte[n3 - 24];
            this.b.read(array);
            snoopRecord.a(array);
        }
        catch (final EOFException ex) {
            return null;
        }
        return snoopRecord;
    }
    
    static {
        a = new byte[] { 115, 110, 111, 111, 112, 0, 0, 0 };
    }
}
