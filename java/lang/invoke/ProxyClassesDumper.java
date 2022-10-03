package java.lang.invoke;

import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import java.nio.file.InvalidPathException;
import sun.util.logging.PlatformLogger;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.io.FilePermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.nio.file.Paths;
import java.nio.file.Path;

final class ProxyClassesDumper
{
    private static final char[] HEX;
    private static final char[] BAD_CHARS;
    private static final String[] REPLACEMENT;
    private final Path dumpDir;
    
    public static ProxyClassesDumper getInstance(String trim) {
        if (null == trim) {
            return null;
        }
        try {
            trim = trim.trim();
            final Path value = Paths.get((trim.length() == 0) ? "." : trim, new String[0]);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    validateDumpDir(value);
                    return null;
                }
            }, null, new FilePermission("<<ALL FILES>>", "read, write"));
            return new ProxyClassesDumper(value);
        }
        catch (final InvalidPathException ex) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Path " + trim + " is not valid - dumping disabled", ex);
        }
        catch (final IllegalArgumentException ex2) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning(ex2.getMessage() + " - dumping disabled");
        }
        return null;
    }
    
    private ProxyClassesDumper(final Path path) {
        this.dumpDir = Objects.requireNonNull(path);
    }
    
    private static void validateDumpDir(final Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            throw new IllegalArgumentException("Directory " + path + " does not exist");
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + path + " is not a directory");
        }
        if (!Files.isWritable(path)) {
            throw new IllegalArgumentException("Directory " + path + " is not writable");
        }
    }
    
    public static String encodeForFilename(final String s) {
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 <= '\u001f') {
                sb.append('%');
                sb.append(ProxyClassesDumper.HEX[char1 >> 4 & 0xF]);
                sb.append(ProxyClassesDumper.HEX[char1 & '\u000f']);
            }
            else {
                int j;
                for (j = 0; j < ProxyClassesDumper.BAD_CHARS.length; ++j) {
                    if (char1 == ProxyClassesDumper.BAD_CHARS[j]) {
                        sb.append(ProxyClassesDumper.REPLACEMENT[j]);
                        break;
                    }
                }
                if (j >= ProxyClassesDumper.BAD_CHARS.length) {
                    sb.append(char1);
                }
            }
        }
        return sb.toString();
    }
    
    public void dumpClass(final String s, final byte[] array) {
        Path resolve;
        try {
            resolve = this.dumpDir.resolve(encodeForFilename(s) + ".class");
        }
        catch (final InvalidPathException ex) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Invalid path for class " + s);
            return;
        }
        try {
            Files.createDirectories(resolve.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
            Files.write(resolve, array, new OpenOption[0]);
        }
        catch (final Exception ex2) {
            PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Exception writing to path at " + resolve.toString());
        }
    }
    
    static {
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        BAD_CHARS = new char[] { '\\', ':', '*', '?', '\"', '<', '>', '|' };
        REPLACEMENT = new String[] { "%5C", "%3A", "%2A", "%3F", "%22", "%3C", "%3E", "%7C" };
    }
}
