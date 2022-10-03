package org.apache.commons.compress.compressors.pack200;

import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import org.apache.commons.compress.java.util.jar.Pack200;
import java.util.jar.JarFile;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;
import java.io.File;

public class Pack200Utils
{
    private Pack200Utils() {
    }
    
    public static void normalize(final File jar) throws IOException {
        normalize(jar, jar, null);
    }
    
    public static void normalize(final File jar, final Map<String, String> props) throws IOException {
        normalize(jar, jar, props);
    }
    
    public static void normalize(final File from, final File to) throws IOException {
        normalize(from, to, null);
    }
    
    public static void normalize(final File from, final File to, Map<String, String> props) throws IOException {
        if (props == null) {
            props = new HashMap<String, String>();
        }
        props.put("pack.segment.limit", "-1");
        final File tempFile = File.createTempFile("commons-compress", "pack200normalize");
        try {
            try (final OutputStream fos = Files.newOutputStream(tempFile.toPath(), new OpenOption[0]);
                 final JarFile jarFile = new JarFile(from)) {
                final Pack200.Packer packer = Pack200.newPacker();
                packer.properties().putAll((Map<?, ?>)props);
                packer.pack(jarFile, fos);
            }
            final Pack200.Unpacker unpacker = Pack200.newUnpacker();
            try (final JarOutputStream jos = new JarOutputStream(Files.newOutputStream(to.toPath(), new OpenOption[0]))) {
                unpacker.unpack(tempFile, jos);
            }
        }
        finally {
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }
}
