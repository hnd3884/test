package org.apache.commons.compress.java.util.jar;

import java.util.jar.JarInputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.io.InputStream;
import java.util.SortedMap;
import java.security.AccessController;
import org.apache.commons.compress.harmony.archive.internal.nls.Messages;
import java.security.PrivilegedAction;

public abstract class Pack200
{
    private static final String SYSTEM_PROPERTY_PACKER = "java.util.jar.Pack200.Packer";
    private static final String SYSTEM_PROPERTY_UNPACKER = "java.util.jar.Pack200.Unpacker";
    
    private Pack200() {
    }
    
    public static Packer newPacker() {
        return AccessController.doPrivileged((PrivilegedAction<Packer>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                final String className = System.getProperty("java.util.jar.Pack200.Packer", "org.apache.commons.compress.harmony.pack200.Pack200PackerAdapter");
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
                }
                catch (final Exception e) {
                    throw new Error(Messages.getString("archive.3E", className), e);
                }
            }
        });
    }
    
    public static Unpacker newUnpacker() {
        return AccessController.doPrivileged((PrivilegedAction<Unpacker>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                final String className = System.getProperty("java.util.jar.Pack200.Unpacker", "org.apache.commons.compress.harmony.unpack200.Pack200UnpackerAdapter");
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
                }
                catch (final Exception e) {
                    throw new Error(Messages.getString("archive.3E", className), e);
                }
            }
        });
    }
    
    public interface Unpacker
    {
        public static final String DEFLATE_HINT = "unpack.deflate.hint";
        public static final String FALSE = "false";
        public static final String KEEP = "keep";
        public static final String PROGRESS = "unpack.progress";
        public static final String TRUE = "true";
        
        SortedMap<String, String> properties();
        
        void unpack(final InputStream p0, final JarOutputStream p1) throws IOException;
        
        void unpack(final File p0, final JarOutputStream p1) throws IOException;
        
        void addPropertyChangeListener(final PropertyChangeListener p0);
        
        void removePropertyChangeListener(final PropertyChangeListener p0);
    }
    
    public interface Packer
    {
        public static final String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
        public static final String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
        public static final String DEFLATE_HINT = "pack.deflate.hint";
        public static final String EFFORT = "pack.effort";
        public static final String ERROR = "error";
        public static final String FALSE = "false";
        public static final String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
        public static final String KEEP = "keep";
        public static final String KEEP_FILE_ORDER = "pack.keep.file.order";
        public static final String LATEST = "latest";
        public static final String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
        public static final String MODIFICATION_TIME = "pack.modification.time";
        public static final String PASS = "pass";
        public static final String PASS_FILE_PFX = "pack.pass.file.";
        public static final String PROGRESS = "pack.progress";
        public static final String SEGMENT_LIMIT = "pack.segment.limit";
        public static final String STRIP = "strip";
        public static final String TRUE = "true";
        public static final String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";
        
        SortedMap<String, String> properties();
        
        void pack(final JarFile p0, final OutputStream p1) throws IOException;
        
        void pack(final JarInputStream p0, final OutputStream p1) throws IOException;
        
        void addPropertyChangeListener(final PropertyChangeListener p0);
        
        void removePropertyChangeListener(final PropertyChangeListener p0);
    }
}
