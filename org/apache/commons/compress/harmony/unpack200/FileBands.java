package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import org.apache.commons.compress.harmony.pack200.Codec;
import java.io.InputStream;

public class FileBands extends BandSet
{
    private byte[][] fileBits;
    private int[] fileModtime;
    private String[] fileName;
    private int[] fileOptions;
    private long[] fileSize;
    private final String[] cpUTF8;
    private InputStream in;
    
    public FileBands(final Segment segment) {
        super(segment);
        this.cpUTF8 = segment.getCpBands().getCpUTF8();
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
        final int numberOfFiles = this.header.getNumberOfFiles();
        final SegmentOptions options = this.header.getOptions();
        this.fileName = this.parseReferences("file_name", in, Codec.UNSIGNED5, numberOfFiles, this.cpUTF8);
        this.fileSize = this.parseFlags("file_size", in, numberOfFiles, Codec.UNSIGNED5, options.hasFileSizeHi());
        if (options.hasFileModtime()) {
            this.fileModtime = this.decodeBandInt("file_modtime", in, Codec.DELTA5, numberOfFiles);
        }
        else {
            this.fileModtime = new int[numberOfFiles];
        }
        if (options.hasFileOptions()) {
            this.fileOptions = this.decodeBandInt("file_options", in, Codec.UNSIGNED5, numberOfFiles);
        }
        else {
            this.fileOptions = new int[numberOfFiles];
        }
        this.in = in;
    }
    
    public void processFileBits() throws IOException, Pack200Exception {
        final int numberOfFiles = this.header.getNumberOfFiles();
        this.fileBits = new byte[numberOfFiles][];
        for (int i = 0; i < numberOfFiles; ++i) {
            final int size = (int)this.fileSize[i];
            this.fileBits[i] = new byte[size];
            final int read = this.in.read(this.fileBits[i]);
            if (size != 0 && read < size) {
                throw new Pack200Exception("Expected to read " + size + " bytes but read " + read);
            }
        }
    }
    
    @Override
    public void unpack() {
    }
    
    public byte[][] getFileBits() {
        return this.fileBits;
    }
    
    public int[] getFileModtime() {
        return this.fileModtime;
    }
    
    public String[] getFileName() {
        return this.fileName;
    }
    
    public int[] getFileOptions() {
        return this.fileOptions;
    }
    
    public long[] getFileSize() {
        return this.fileSize;
    }
}
