package java.io;

abstract class FileSystem
{
    public static final int BA_EXISTS = 1;
    public static final int BA_REGULAR = 2;
    public static final int BA_DIRECTORY = 4;
    public static final int BA_HIDDEN = 8;
    public static final int ACCESS_READ = 4;
    public static final int ACCESS_WRITE = 2;
    public static final int ACCESS_EXECUTE = 1;
    public static final int SPACE_TOTAL = 0;
    public static final int SPACE_FREE = 1;
    public static final int SPACE_USABLE = 2;
    static boolean useCanonCaches;
    static boolean useCanonPrefixCache;
    
    public abstract char getSeparator();
    
    public abstract char getPathSeparator();
    
    public abstract String normalize(final String p0);
    
    public abstract int prefixLength(final String p0);
    
    public abstract String resolve(final String p0, final String p1);
    
    public abstract String getDefaultParent();
    
    public abstract String fromURIPath(final String p0);
    
    public abstract boolean isAbsolute(final File p0);
    
    public abstract String resolve(final File p0);
    
    public abstract String canonicalize(final String p0) throws IOException;
    
    public abstract int getBooleanAttributes(final File p0);
    
    public abstract boolean checkAccess(final File p0, final int p1);
    
    public abstract boolean setPermission(final File p0, final int p1, final boolean p2, final boolean p3);
    
    public abstract long getLastModifiedTime(final File p0);
    
    public abstract long getLength(final File p0);
    
    public abstract boolean createFileExclusively(final String p0) throws IOException;
    
    public abstract boolean delete(final File p0);
    
    public abstract String[] list(final File p0);
    
    public abstract boolean createDirectory(final File p0);
    
    public abstract boolean rename(final File p0, final File p1);
    
    public abstract boolean setLastModifiedTime(final File p0, final long p1);
    
    public abstract boolean setReadOnly(final File p0);
    
    public abstract File[] listRoots();
    
    public abstract long getSpace(final File p0, final int p1);
    
    public abstract int compare(final File p0, final File p1);
    
    public abstract int hashCode(final File p0);
    
    private static boolean getBooleanProperty(final String s, final boolean b) {
        final String property = System.getProperty(s);
        if (property == null) {
            return b;
        }
        return property.equalsIgnoreCase("true");
    }
    
    static {
        FileSystem.useCanonCaches = true;
        FileSystem.useCanonPrefixCache = true;
        FileSystem.useCanonCaches = getBooleanProperty("sun.io.useCanonCaches", FileSystem.useCanonCaches);
        FileSystem.useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache", FileSystem.useCanonPrefixCache);
    }
}
