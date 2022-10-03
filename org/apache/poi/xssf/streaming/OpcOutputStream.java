package org.apache.poi.xssf.streaming;

import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

class OpcOutputStream extends DeflaterOutputStream
{
    private final Zip64Impl spec;
    private final List<Zip64Impl.Entry> entries;
    private final CRC32 crc;
    private Zip64Impl.Entry current;
    private int written;
    private boolean finished;
    
    public OpcOutputStream(final OutputStream out) {
        super(out, new Deflater(-1, true));
        this.entries = new ArrayList<Zip64Impl.Entry>();
        this.crc = new CRC32();
        this.written = 0;
        this.finished = false;
        this.spec = new Zip64Impl(out);
    }
    
    public void setLevel(final int level) {
        super.def.setLevel(level);
    }
    
    public void putNextEntry(final String name) throws IOException {
        if (this.current != null) {
            this.closeEntry();
        }
        this.current = new Zip64Impl.Entry(name);
        this.current.offset = this.written;
        this.written += this.spec.writeLFH(this.current);
        this.entries.add(this.current);
    }
    
    public void closeEntry() throws IOException {
        if (this.current == null) {
            throw new IllegalStateException("not current zip current");
        }
        this.def.finish();
        while (!this.def.finished()) {
            this.deflate();
        }
        this.current.size = this.def.getBytesRead();
        this.current.compressedSize = Math.toIntExact(this.def.getBytesWritten());
        this.current.crc = this.crc.getValue();
        this.written += this.current.compressedSize;
        this.written += this.spec.writeDAT(this.current);
        this.current = null;
        this.def.reset();
        this.crc.reset();
    }
    
    @Override
    public void finish() throws IOException {
        if (this.finished) {
            return;
        }
        if (this.current != null) {
            this.closeEntry();
        }
        final int offset = this.written;
        for (final Zip64Impl.Entry entry : this.entries) {
            this.written += this.spec.writeCEN(entry);
        }
        this.written += this.spec.writeEND(this.entries.size(), offset, this.written - offset);
        this.finished = true;
    }
    
    @Override
    public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        super.write(b, off, len);
        this.crc.update(b, off, len);
    }
    
    @Override
    public void close() throws IOException {
        this.finish();
        this.out.close();
    }
}
