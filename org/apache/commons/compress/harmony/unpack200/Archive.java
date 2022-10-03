package org.apache.commons.compress.harmony.unpack200;

import java.util.jar.JarEntry;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarOutputStream;
import java.io.InputStream;

public class Archive
{
    private InputStream inputStream;
    private final JarOutputStream outputStream;
    private boolean removePackFile;
    private int logLevel;
    private FileOutputStream logFile;
    private boolean overrideDeflateHint;
    private boolean deflateHint;
    private String inputFileName;
    private String outputFileName;
    
    public Archive(final String inputFile, final String outputFile) throws FileNotFoundException, IOException {
        this.logLevel = 1;
        this.inputFileName = inputFile;
        this.outputFileName = outputFile;
        this.inputStream = new FileInputStream(inputFile);
        this.outputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
    }
    
    public Archive(final InputStream inputStream, final JarOutputStream outputStream) throws IOException {
        this.logLevel = 1;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
    public void unpack() throws Pack200Exception, IOException {
        this.outputStream.setComment("PACK200");
        try {
            if (!this.inputStream.markSupported()) {
                this.inputStream = new BufferedInputStream(this.inputStream);
                if (!this.inputStream.markSupported()) {
                    throw new IllegalStateException();
                }
            }
            this.inputStream.mark(2);
            if (((this.inputStream.read() & 0xFF) | (this.inputStream.read() & 0xFF) << 8) == 0x8B1F) {
                this.inputStream.reset();
                this.inputStream = new BufferedInputStream(new GZIPInputStream(this.inputStream));
            }
            else {
                this.inputStream.reset();
            }
            this.inputStream.mark(4);
            final int[] magic = { 202, 254, 208, 13 };
            final int[] word = new int[4];
            for (int i = 0; i < word.length; ++i) {
                word[i] = this.inputStream.read();
            }
            boolean compressedWithE0 = false;
            for (int m = 0; m < magic.length; ++m) {
                if (word[m] != magic[m]) {
                    compressedWithE0 = true;
                }
            }
            this.inputStream.reset();
            if (compressedWithE0) {
                final JarInputStream jarInputStream = new JarInputStream(this.inputStream);
                JarEntry jarEntry;
                while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                    this.outputStream.putNextEntry(jarEntry);
                    final byte[] bytes = new byte[16384];
                    for (int bytesRead = jarInputStream.read(bytes); bytesRead != -1; bytesRead = jarInputStream.read(bytes)) {
                        this.outputStream.write(bytes, 0, bytesRead);
                    }
                    this.outputStream.closeEntry();
                }
            }
            else {
                int j = 0;
                while (this.available(this.inputStream)) {
                    ++j;
                    final Segment segment = new Segment();
                    segment.setLogLevel(this.logLevel);
                    segment.setLogStream((this.logFile != null) ? this.logFile : System.out);
                    segment.setPreRead(false);
                    if (j == 1) {
                        segment.log(2, "Unpacking from " + this.inputFileName + " to " + this.outputFileName);
                    }
                    segment.log(2, "Reading segment " + j);
                    if (this.overrideDeflateHint) {
                        segment.overrideDeflateHint(this.deflateHint);
                    }
                    segment.unpack(this.inputStream, this.outputStream);
                    this.outputStream.flush();
                    if (this.inputStream instanceof FileInputStream) {
                        this.inputFileName = ((FileInputStream)this.inputStream).getFD().toString();
                    }
                }
            }
        }
        finally {
            try {
                this.inputStream.close();
            }
            catch (final Exception ex) {}
            try {
                this.outputStream.close();
            }
            catch (final Exception ex2) {}
            if (this.logFile != null) {
                try {
                    this.logFile.close();
                }
                catch (final Exception ex3) {}
            }
        }
        if (this.removePackFile) {
            boolean deleted = false;
            if (this.inputFileName != null) {
                final File file = new File(this.inputFileName);
                deleted = file.delete();
            }
            if (!deleted) {
                throw new Pack200Exception("Failed to delete the input file.");
            }
        }
    }
    
    private boolean available(final InputStream inputStream) throws IOException {
        inputStream.mark(1);
        final int check = inputStream.read();
        inputStream.reset();
        return check != -1;
    }
    
    public void setRemovePackFile(final boolean removePackFile) {
        this.removePackFile = removePackFile;
    }
    
    public void setVerbose(final boolean verbose) {
        if (verbose) {
            this.logLevel = 2;
        }
        else if (this.logLevel == 2) {
            this.logLevel = 1;
        }
    }
    
    public void setQuiet(final boolean quiet) {
        if (quiet) {
            this.logLevel = 0;
        }
        else if (this.logLevel == 0) {
            this.logLevel = 0;
        }
    }
    
    public void setLogFile(final String logFileName) throws FileNotFoundException {
        this.logFile = new FileOutputStream(logFileName);
    }
    
    public void setLogFile(final String logFileName, final boolean append) throws FileNotFoundException {
        this.logFile = new FileOutputStream(logFileName, append);
    }
    
    public void setDeflateHint(final boolean deflateHint) {
        this.overrideDeflateHint = true;
        this.deflateHint = deflateHint;
    }
}
