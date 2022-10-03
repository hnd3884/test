package org.apache.xmlbeans.impl.common;

import java.io.File;
import java.net.URI;
import java.io.Writer;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

public class IOUtil
{
    public static void copyCompletely(final InputStream input, final OutputStream output) throws IOException {
        if (output instanceof FileOutputStream && input instanceof FileInputStream) {
            try {
                final FileChannel target = ((FileOutputStream)output).getChannel();
                final FileChannel source = ((FileInputStream)input).getChannel();
                source.transferTo(0L, 2147483647L, target);
                source.close();
                target.close();
                return;
            }
            catch (final Exception ex) {}
        }
        final byte[] buf = new byte[8192];
        while (true) {
            final int length = input.read(buf);
            if (length < 0) {
                break;
            }
            output.write(buf, 0, length);
        }
        try {
            input.close();
        }
        catch (final IOException ex2) {}
        try {
            output.close();
        }
        catch (final IOException ex3) {}
    }
    
    public static void copyCompletely(final Reader input, final Writer output) throws IOException {
        final char[] buf = new char[8192];
        while (true) {
            final int length = input.read(buf);
            if (length < 0) {
                break;
            }
            output.write(buf, 0, length);
        }
        try {
            input.close();
        }
        catch (final IOException ex) {}
        try {
            output.close();
        }
        catch (final IOException ex2) {}
    }
    
    public static void copyCompletely(final URI input, final URI output) throws IOException {
        try {
            InputStream in = null;
            try {
                final File f = new File(input);
                if (f.exists()) {
                    in = new FileInputStream(f);
                }
            }
            catch (final Exception ex) {}
            final File out = new File(output);
            final File dir = out.getParentFile();
            dir.mkdirs();
            if (in == null) {
                in = input.toURL().openStream();
            }
            copyCompletely(in, new FileOutputStream(out));
        }
        catch (final IllegalArgumentException e) {
            throw new IOException("Cannot copy to " + output);
        }
    }
    
    public static File createDir(final File rootdir, final String subdir) {
        final File newdir = (subdir == null) ? rootdir : new File(rootdir, subdir);
        final boolean created = (newdir.exists() && newdir.isDirectory()) || newdir.mkdirs();
        assert created : "Could not create " + newdir.getAbsolutePath();
        return newdir;
    }
}
