package org.apache.commons.compress.harmony.unpack200;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.io.InputStream;
import org.apache.commons.compress.java.util.jar.Pack200;
import org.apache.commons.compress.harmony.pack200.Pack200Adapter;

public class Pack200UnpackerAdapter extends Pack200Adapter implements Pack200.Unpacker
{
    @Override
    public void unpack(final InputStream in, final JarOutputStream out) throws IOException {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Must specify both input and output streams");
        }
        this.completed(0.0);
        try {
            new Archive(in, out).unpack();
        }
        catch (final Pack200Exception e) {
            throw new IOException("Failed to unpack Jar:" + String.valueOf(e));
        }
        this.completed(1.0);
        in.close();
    }
    
    @Override
    public void unpack(final File file, final JarOutputStream out) throws IOException {
        if (file == null || out == null) {
            throw new IllegalArgumentException("Must specify both input and output streams");
        }
        final int size = (int)file.length();
        final int bufferSize = (size > 0 && size < 8192) ? size : 8192;
        final InputStream in = new BufferedInputStream(new FileInputStream(file), bufferSize);
        this.unpack(in, out);
    }
}
