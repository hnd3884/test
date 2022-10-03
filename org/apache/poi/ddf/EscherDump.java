package org.apache.poi.ddf;

import java.io.PrintStream;

public final class EscherDump
{
    public void dump(final byte[] data, final int offset, final int size, final PrintStream out) {
        final EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int bytesRead;
        for (int pos = offset; pos < offset + size; pos += bytesRead) {
            final EscherRecord r = recordFactory.createRecord(data, pos);
            bytesRead = r.fillFields(data, pos, recordFactory);
            out.println(r);
        }
    }
    
    public void dump(final int recordSize, final byte[] data, final PrintStream out) {
        this.dump(data, 0, recordSize, out);
    }
}
