package java.io;

import java.util.Locale;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

class WinNTFileSystem extends FileSystem
{
    private final char slash;
    private final char altSlash;
    private final char semicolon;
    private static String[] driveDirCache;
    private ExpiringCache cache;
    private ExpiringCache prefixCache;
    
    public WinNTFileSystem() {
        this.cache = new ExpiringCache();
        this.prefixCache = new ExpiringCache();
        this.slash = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("file.separator")).charAt(0);
        this.semicolon = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("path.separator")).charAt(0);
        this.altSlash = ((this.slash == '\\') ? '/' : '\\');
    }
    
    private boolean isSlash(final char c) {
        return c == '\\' || c == '/';
    }
    
    private boolean isLetter(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private String slashify(final String s) {
        if (s.length() > 0 && s.charAt(0) != this.slash) {
            return this.slash + s;
        }
        return s;
    }
    
    @Override
    public char getSeparator() {
        return this.slash;
    }
    
    @Override
    public char getPathSeparator() {
        return this.semicolon;
    }
    
    @Override
    public String normalize(final String s) {
        final int length = s.length();
        final char slash = this.slash;
        final char altSlash = this.altSlash;
        char c = '\0';
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == altSlash) {
                return this.normalize(s, length, (c == slash) ? (i - 1) : i);
            }
            if (char1 == slash && c == slash && i > 1) {
                return this.normalize(s, length, i - 1);
            }
            if (char1 == ':' && i > 1) {
                return this.normalize(s, length, 0);
            }
            c = char1;
        }
        if (c == slash) {
            return this.normalize(s, length, length - 1);
        }
        return s;
    }
    
    private String normalize(final String s, final int n, int n2) {
        if (n == 0) {
            return s;
        }
        if (n2 < 3) {
            n2 = 0;
        }
        final char slash = this.slash;
        final StringBuffer sb = new StringBuffer(n);
        int i;
        if (n2 == 0) {
            i = this.normalizePrefix(s, n, sb);
        }
        else {
            i = n2;
            sb.append(s.substring(0, n2));
        }
        while (i < n) {
            final char char1 = s.charAt(i++);
            if (this.isSlash(char1)) {
                while (i < n && this.isSlash(s.charAt(i))) {
                    ++i;
                }
                if (i == n) {
                    final int length = sb.length();
                    if (length == 2 && sb.charAt(1) == ':') {
                        sb.append(slash);
                        break;
                    }
                    if (length == 0) {
                        sb.append(slash);
                        break;
                    }
                    if (length == 1 && this.isSlash(sb.charAt(0))) {
                        sb.append(slash);
                        break;
                    }
                    break;
                }
                else {
                    sb.append(slash);
                }
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private int normalizePrefix(final String s, final int n, final StringBuffer sb) {
        int n2;
        for (n2 = 0; n2 < n && this.isSlash(s.charAt(n2)); ++n2) {}
        final char char1;
        if (n - n2 >= 2 && this.isLetter(char1 = s.charAt(n2)) && s.charAt(n2 + 1) == ':') {
            sb.append(char1);
            sb.append(':');
            n2 += 2;
        }
        else {
            n2 = 0;
            if (n >= 2 && this.isSlash(s.charAt(0)) && this.isSlash(s.charAt(1))) {
                n2 = 1;
                sb.append(this.slash);
            }
        }
        return n2;
    }
    
    @Override
    public int prefixLength(final String s) {
        final char slash = this.slash;
        final int length = s.length();
        if (length == 0) {
            return 0;
        }
        final char char1 = s.charAt(0);
        final char c = (length > 1) ? s.charAt(1) : '\0';
        if (char1 == slash) {
            if (c == slash) {
                return 2;
            }
            return 1;
        }
        else {
            if (!this.isLetter(char1) || c != ':') {
                return 0;
            }
            if (length > 2 && s.charAt(2) == slash) {
                return 3;
            }
            return 2;
        }
    }
    
    @Override
    public String resolve(final String s, final String s2) {
        final int length = s.length();
        if (length == 0) {
            return s2;
        }
        final int length2 = s2.length();
        if (length2 == 0) {
            return s;
        }
        int n = 0;
        int n2 = length;
        if (length2 > 1 && s2.charAt(0) == this.slash) {
            if (s2.charAt(1) == this.slash) {
                n = 2;
            }
            else {
                n = 1;
            }
            if (length2 == n) {
                if (s.charAt(length - 1) == this.slash) {
                    return s.substring(0, length - 1);
                }
                return s;
            }
        }
        if (s.charAt(length - 1) == this.slash) {
            --n2;
        }
        final int n3 = n2 + length2 - n;
        char[] array;
        if (s2.charAt(n) == this.slash) {
            array = new char[n3];
            s.getChars(0, n2, array, 0);
            s2.getChars(n, length2, array, n2);
        }
        else {
            array = new char[n3 + 1];
            s.getChars(0, n2, array, 0);
            array[n2] = this.slash;
            s2.getChars(n, length2, array, n2 + 1);
        }
        return new String(array);
    }
    
    @Override
    public String getDefaultParent() {
        return "" + this.slash;
    }
    
    @Override
    public String fromURIPath(final String s) {
        String s2 = s;
        if (s2.length() > 2 && s2.charAt(2) == ':') {
            s2 = s2.substring(1);
            if (s2.length() > 3 && s2.endsWith("/")) {
                s2 = s2.substring(0, s2.length() - 1);
            }
        }
        else if (s2.length() > 1 && s2.endsWith("/")) {
            s2 = s2.substring(0, s2.length() - 1);
        }
        return s2;
    }
    
    @Override
    public boolean isAbsolute(final File file) {
        final int prefixLength = file.getPrefixLength();
        return (prefixLength == 2 && file.getPath().charAt(0) == this.slash) || prefixLength == 3;
    }
    
    @Override
    public String resolve(final File file) {
        final String path = file.getPath();
        final int prefixLength = file.getPrefixLength();
        if (prefixLength == 2 && path.charAt(0) == this.slash) {
            return path;
        }
        if (prefixLength == 3) {
            return path;
        }
        if (prefixLength == 0) {
            return this.getUserPath() + this.slashify(path);
        }
        if (prefixLength == 1) {
            final String userPath = this.getUserPath();
            final String drive = this.getDrive(userPath);
            if (drive != null) {
                return drive + path;
            }
            return userPath + path;
        }
        else {
            if (prefixLength != 2) {
                throw new InternalError("Unresolvable path: " + path);
            }
            final String userPath2 = this.getUserPath();
            final String drive2 = this.getDrive(userPath2);
            if (drive2 != null && path.startsWith(drive2)) {
                return userPath2 + this.slashify(path.substring(2));
            }
            final char char1 = path.charAt(0);
            final String driveDirectory = this.getDriveDirectory(char1);
            if (driveDirectory != null) {
                final String string = new StringBuilder().append(char1).append(':').append(driveDirectory).append(this.slashify(path.substring(2))).toString();
                final SecurityManager securityManager = System.getSecurityManager();
                try {
                    if (securityManager != null) {
                        securityManager.checkRead(string);
                    }
                }
                catch (final SecurityException ex) {
                    throw new SecurityException("Cannot resolve path " + path);
                }
                return string;
            }
            return char1 + ":" + this.slashify(path.substring(2));
        }
    }
    
    private String getUserPath() {
        return this.normalize(System.getProperty("user.dir"));
    }
    
    private String getDrive(final String s) {
        return (this.prefixLength(s) == 3) ? s.substring(0, 2) : null;
    }
    
    private static int driveIndex(final char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        return -1;
    }
    
    private native String getDriveDirectory(final int p0);
    
    private String getDriveDirectory(final char c) {
        final int driveIndex = driveIndex(c);
        if (driveIndex < 0) {
            return null;
        }
        final String s = WinNTFileSystem.driveDirCache[driveIndex];
        if (s != null) {
            return s;
        }
        return WinNTFileSystem.driveDirCache[driveIndex] = this.getDriveDirectory(driveIndex + 1);
    }
    
    @Override
    public String canonicalize(final String s) throws IOException {
        final int length = s.length();
        if (length == 2 && this.isLetter(s.charAt(0)) && s.charAt(1) == ':') {
            final char char1 = s.charAt(0);
            if (char1 >= 'A' && char1 <= 'Z') {
                return s;
            }
            return "" + (char)(char1 - ' ') + ':';
        }
        else if (length == 3 && this.isLetter(s.charAt(0)) && s.charAt(1) == ':' && s.charAt(2) == '\\') {
            final char char2 = s.charAt(0);
            if (char2 >= 'A' && char2 <= 'Z') {
                return s;
            }
            return "" + (char)(char2 - ' ') + ':' + '\\';
        }
        else {
            if (!WinNTFileSystem.useCanonCaches) {
                return this.canonicalize0(s);
            }
            String s2 = this.cache.get(s);
            if (s2 == null) {
                String parentOrNull = null;
                if (WinNTFileSystem.useCanonPrefixCache) {
                    parentOrNull = parentOrNull(s);
                    if (parentOrNull != null) {
                        final String value = this.prefixCache.get(parentOrNull);
                        if (value != null) {
                            final String substring = s.substring(1 + parentOrNull.length());
                            s2 = this.canonicalizeWithPrefix(value, substring);
                            this.cache.put(parentOrNull + File.separatorChar + substring, s2);
                        }
                    }
                }
                if (s2 == null) {
                    s2 = this.canonicalize0(s);
                    this.cache.put(s, s2);
                    if (WinNTFileSystem.useCanonPrefixCache && parentOrNull != null) {
                        final String parentOrNull2 = parentOrNull(s2);
                        if (parentOrNull2 != null) {
                            final File file = new File(s2);
                            if (file.exists() && !file.isDirectory()) {
                                this.prefixCache.put(parentOrNull, parentOrNull2);
                            }
                        }
                    }
                }
            }
            return s2;
        }
    }
    
    private native String canonicalize0(final String p0) throws IOException;
    
    private String canonicalizeWithPrefix(final String s, final String s2) throws IOException {
        return this.canonicalizeWithPrefix0(s, s + File.separatorChar + s2);
    }
    
    private native String canonicalizeWithPrefix0(final String p0, final String p1) throws IOException;
    
    private static String parentOrNull(final String s) {
        if (s == null) {
            return null;
        }
        final char separatorChar = File.separatorChar;
        final char c = '/';
        int i;
        final int n = i = s.length() - 1;
        int n2 = 0;
        int n3 = 0;
        while (i > 0) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                if (++n2 >= 2) {
                    return null;
                }
                if (n3 == 0) {
                    return null;
                }
            }
            else if (char1 == separatorChar) {
                if (n2 == 1 && n3 == 0) {
                    return null;
                }
                if (i == 0 || i >= n - 1 || s.charAt(i - 1) == separatorChar || s.charAt(i - 1) == c) {
                    return null;
                }
                return s.substring(0, i);
            }
            else {
                if (char1 == c) {
                    return null;
                }
                if (char1 == '*' || char1 == '?') {
                    return null;
                }
                ++n3;
                n2 = 0;
            }
            --i;
        }
        return null;
    }
    
    @Override
    public native int getBooleanAttributes(final File p0);
    
    @Override
    public native boolean checkAccess(final File p0, final int p1);
    
    @Override
    public native long getLastModifiedTime(final File p0);
    
    @Override
    public native long getLength(final File p0);
    
    @Override
    public native boolean setPermission(final File p0, final int p1, final boolean p2, final boolean p3);
    
    @Override
    public native boolean createFileExclusively(final String p0) throws IOException;
    
    @Override
    public native String[] list(final File p0);
    
    @Override
    public native boolean createDirectory(final File p0);
    
    @Override
    public native boolean setLastModifiedTime(final File p0, final long p1);
    
    @Override
    public native boolean setReadOnly(final File p0);
    
    @Override
    public boolean delete(final File file) {
        this.cache.clear();
        this.prefixCache.clear();
        return this.delete0(file);
    }
    
    private native boolean delete0(final File p0);
    
    @Override
    public boolean rename(final File file, final File file2) {
        this.cache.clear();
        this.prefixCache.clear();
        return this.rename0(file, file2);
    }
    
    private native boolean rename0(final File p0, final File p1);
    
    @Override
    public File[] listRoots() {
        int listRoots0 = listRoots0();
        int n = 0;
        for (int i = 0; i < 26; ++i) {
            if ((listRoots0 >> i & 0x1) != 0x0) {
                if (!this.access((char)(65 + i) + ":" + this.slash)) {
                    listRoots0 &= ~(1 << i);
                }
                else {
                    ++n;
                }
            }
        }
        final File[] array = new File[n];
        int n2 = 0;
        final char slash = this.slash;
        for (int j = 0; j < 26; ++j) {
            if ((listRoots0 >> j & 0x1) != 0x0) {
                array[n2++] = new File((char)(65 + j) + ":" + slash);
            }
        }
        return array;
    }
    
    private static native int listRoots0();
    
    private boolean access(final String s) {
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkRead(s);
            }
            return true;
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    @Override
    public long getSpace(final File file, final int n) {
        if (file.exists()) {
            return this.getSpace0(file, n);
        }
        return 0L;
    }
    
    private native long getSpace0(final File p0, final int p1);
    
    @Override
    public int compare(final File file, final File file2) {
        return file.getPath().compareToIgnoreCase(file2.getPath());
    }
    
    @Override
    public int hashCode(final File file) {
        return file.getPath().toLowerCase(Locale.ENGLISH).hashCode() ^ 0x12D591;
    }
    
    private static native void initIDs();
    
    static {
        WinNTFileSystem.driveDirCache = new String[26];
        initIDs();
    }
}
