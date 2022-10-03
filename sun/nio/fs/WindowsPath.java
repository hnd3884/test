package sun.nio.fs;

import java.nio.file.FileSystem;
import com.sun.nio.file.ExtendedWatchEventModifier;
import java.util.Arrays;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.LinkOption;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.ArrayList;
import java.nio.file.ProviderMismatchException;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.nio.file.attribute.BasicFileAttributes;
import java.lang.ref.WeakReference;

class WindowsPath extends AbstractPath
{
    private static final int MAX_PATH = 247;
    private static final int MAX_LONG_PATH = 32000;
    private final WindowsFileSystem fs;
    private final WindowsPathType type;
    private final String root;
    private final String path;
    private volatile WeakReference<String> pathForWin32Calls;
    private volatile Integer[] offsets;
    private int hash;
    
    private WindowsPath(final WindowsFileSystem fs, final WindowsPathType type, final String root, final String path) {
        this.fs = fs;
        this.type = type;
        this.root = root;
        this.path = path;
    }
    
    static WindowsPath parse(final WindowsFileSystem windowsFileSystem, final String s) {
        final WindowsPathParser.Result parse = WindowsPathParser.parse(s);
        return new WindowsPath(windowsFileSystem, parse.type(), parse.root(), parse.path());
    }
    
    static WindowsPath createFromNormalizedPath(final WindowsFileSystem windowsFileSystem, final String s, final BasicFileAttributes basicFileAttributes) {
        try {
            final WindowsPathParser.Result normalizedPath = WindowsPathParser.parseNormalizedPath(s);
            if (basicFileAttributes == null) {
                return new WindowsPath(windowsFileSystem, normalizedPath.type(), normalizedPath.root(), normalizedPath.path());
            }
            return new WindowsPathWithAttributes(windowsFileSystem, normalizedPath.type(), normalizedPath.root(), normalizedPath.path(), basicFileAttributes);
        }
        catch (final InvalidPathException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    static WindowsPath createFromNormalizedPath(final WindowsFileSystem windowsFileSystem, final String s) {
        return createFromNormalizedPath(windowsFileSystem, s, null);
    }
    
    String getPathForExceptionMessage() {
        return this.path;
    }
    
    String getPathForPermissionCheck() {
        return this.path;
    }
    
    String getPathForWin32Calls() throws WindowsException {
        if (this.isAbsolute() && this.path.length() <= 247) {
            return this.path;
        }
        final WeakReference<String> pathForWin32Calls = this.pathForWin32Calls;
        final String s = (pathForWin32Calls != null) ? pathForWin32Calls.get() : null;
        if (s != null) {
            return s;
        }
        String s2 = this.getAbsolutePath();
        if (s2.length() > 247) {
            if (s2.length() > 32000) {
                throw new WindowsException("Cannot access file with path exceeding 32000 characters");
            }
            s2 = addPrefixIfNeeded(WindowsNativeDispatcher.GetFullPathName(s2));
        }
        if (this.type != WindowsPathType.DRIVE_RELATIVE) {
            synchronized (this.path) {
                this.pathForWin32Calls = new WeakReference<String>(s2);
            }
        }
        return s2;
    }
    
    private String getAbsolutePath() throws WindowsException {
        if (this.isAbsolute()) {
            return this.path;
        }
        if (this.type == WindowsPathType.RELATIVE) {
            final String defaultDirectory = this.getFileSystem().defaultDirectory();
            if (this.isEmpty()) {
                return defaultDirectory;
            }
            if (defaultDirectory.endsWith("\\")) {
                return defaultDirectory + this.path;
            }
            return new StringBuilder(defaultDirectory.length() + this.path.length() + 1).append(defaultDirectory).append('\\').append(this.path).toString();
        }
        else {
            if (this.type == WindowsPathType.DIRECTORY_RELATIVE) {
                return this.getFileSystem().defaultRoot() + this.path.substring(1);
            }
            if (isSameDrive(this.root, this.getFileSystem().defaultRoot())) {
                final String substring = this.path.substring(this.root.length());
                final String defaultDirectory2 = this.getFileSystem().defaultDirectory();
                String s;
                if (defaultDirectory2.endsWith("\\")) {
                    s = defaultDirectory2 + substring;
                }
                else {
                    s = defaultDirectory2 + "\\" + substring;
                }
                return s;
            }
            String getFullPathName;
            try {
                final int getDriveType = WindowsNativeDispatcher.GetDriveType(this.root + "\\");
                if (getDriveType == 0 || getDriveType == 1) {
                    throw new WindowsException("");
                }
                getFullPathName = WindowsNativeDispatcher.GetFullPathName(this.root + ".");
            }
            catch (final WindowsException ex) {
                throw new WindowsException("Unable to get working directory of drive '" + Character.toUpperCase(this.root.charAt(0)) + "'");
            }
            String s2 = getFullPathName;
            if (getFullPathName.endsWith("\\")) {
                s2 += this.path.substring(this.root.length());
            }
            else if (this.path.length() > this.root.length()) {
                s2 = s2 + "\\" + this.path.substring(this.root.length());
            }
            return s2;
        }
    }
    
    private static boolean isSameDrive(final String s, final String s2) {
        return Character.toUpperCase(s.charAt(0)) == Character.toUpperCase(s2.charAt(0));
    }
    
    static String addPrefixIfNeeded(String s) {
        if (s.length() > 247) {
            if (s.startsWith("\\\\")) {
                s = "\\\\?\\UNC" + s.substring(1, s.length());
            }
            else {
                s = "\\\\?\\" + s;
            }
        }
        return s;
    }
    
    @Override
    public WindowsFileSystem getFileSystem() {
        return this.fs;
    }
    
    private boolean isEmpty() {
        return this.path.length() == 0;
    }
    
    private WindowsPath emptyPath() {
        return new WindowsPath(this.getFileSystem(), WindowsPathType.RELATIVE, "", "");
    }
    
    @Override
    public Path getFileName() {
        final int length = this.path.length();
        if (length == 0) {
            return this;
        }
        if (this.root.length() == length) {
            return null;
        }
        int n = this.path.lastIndexOf(92);
        if (n < this.root.length()) {
            n = this.root.length();
        }
        else {
            ++n;
        }
        return new WindowsPath(this.getFileSystem(), WindowsPathType.RELATIVE, "", this.path.substring(n));
    }
    
    @Override
    public WindowsPath getParent() {
        if (this.root.length() == this.path.length()) {
            return null;
        }
        final int lastIndex = this.path.lastIndexOf(92);
        if (lastIndex < this.root.length()) {
            return this.getRoot();
        }
        return new WindowsPath(this.getFileSystem(), this.type, this.root, this.path.substring(0, lastIndex));
    }
    
    @Override
    public WindowsPath getRoot() {
        if (this.root.length() == 0) {
            return null;
        }
        return new WindowsPath(this.getFileSystem(), this.type, this.root, this.root);
    }
    
    WindowsPathType type() {
        return this.type;
    }
    
    boolean isUnc() {
        return this.type == WindowsPathType.UNC;
    }
    
    boolean needsSlashWhenResolving() {
        return !this.path.endsWith("\\") && this.path.length() > this.root.length();
    }
    
    @Override
    public boolean isAbsolute() {
        return this.type == WindowsPathType.ABSOLUTE || this.type == WindowsPathType.UNC;
    }
    
    static WindowsPath toWindowsPath(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof WindowsPath)) {
            throw new ProviderMismatchException();
        }
        return (WindowsPath)path;
    }
    
    @Override
    public WindowsPath relativize(final Path path) {
        final WindowsPath windowsPath = toWindowsPath(path);
        if (this.equals(windowsPath)) {
            return this.emptyPath();
        }
        if (this.type != windowsPath.type) {
            throw new IllegalArgumentException("'other' is different type of Path");
        }
        if (!this.root.equalsIgnoreCase(windowsPath.root)) {
            throw new IllegalArgumentException("'other' has different root");
        }
        final int nameCount = this.getNameCount();
        final int nameCount2 = windowsPath.getNameCount();
        int n;
        int n2;
        for (n = ((nameCount > nameCount2) ? nameCount2 : nameCount), n2 = 0; n2 < n && this.getName(n2).equals(windowsPath.getName(n2)); ++n2) {}
        final StringBuilder sb = new StringBuilder();
        for (int i = n2; i < nameCount; ++i) {
            sb.append("..\\");
        }
        for (int j = n2; j < nameCount2; ++j) {
            sb.append(windowsPath.getName(j).toString());
            sb.append("\\");
        }
        sb.setLength(sb.length() - 1);
        return createFromNormalizedPath(this.getFileSystem(), sb.toString());
    }
    
    @Override
    public Path normalize() {
        final int nameCount = this.getNameCount();
        if (nameCount == 0 || this.isEmpty()) {
            return this;
        }
        final boolean[] array = new boolean[nameCount];
        int n = nameCount;
        int i;
        do {
            i = n;
            int n2 = -1;
            for (int j = 0; j < nameCount; ++j) {
                if (!array[j]) {
                    final String elementAsString = this.elementAsString(j);
                    if (elementAsString.length() > 2) {
                        n2 = j;
                    }
                    else if (elementAsString.length() == 1) {
                        if (elementAsString.charAt(0) == '.') {
                            array[j] = true;
                            --n;
                        }
                        else {
                            n2 = j;
                        }
                    }
                    else if (elementAsString.charAt(0) != '.' || elementAsString.charAt(1) != '.') {
                        n2 = j;
                    }
                    else if (n2 >= 0) {
                        array[j] = (array[n2] = true);
                        n -= 2;
                        n2 = -1;
                    }
                    else if (this.isAbsolute() || this.type == WindowsPathType.DIRECTORY_RELATIVE) {
                        boolean b = false;
                        for (int k = 0; k < j; ++k) {
                            if (!array[k]) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) {
                            array[j] = true;
                            --n;
                        }
                    }
                }
            }
        } while (i > n);
        if (n == nameCount) {
            return this;
        }
        if (n == 0) {
            return (this.root.length() == 0) ? this.emptyPath() : this.getRoot();
        }
        final StringBuilder sb = new StringBuilder();
        if (this.root != null) {
            sb.append(this.root);
        }
        for (int l = 0; l < nameCount; ++l) {
            if (!array[l]) {
                sb.append(this.getName(l));
                sb.append("\\");
            }
        }
        sb.setLength(sb.length() - 1);
        return createFromNormalizedPath(this.getFileSystem(), sb.toString());
    }
    
    @Override
    public WindowsPath resolve(final Path path) {
        final WindowsPath windowsPath = toWindowsPath(path);
        if (windowsPath.isEmpty()) {
            return this;
        }
        if (windowsPath.isAbsolute()) {
            return windowsPath;
        }
        switch (windowsPath.type) {
            case RELATIVE: {
                String s;
                if (this.path.endsWith("\\") || this.root.length() == this.path.length()) {
                    s = this.path + windowsPath.path;
                }
                else {
                    s = this.path + "\\" + windowsPath.path;
                }
                return new WindowsPath(this.getFileSystem(), this.type, this.root, s);
            }
            case DIRECTORY_RELATIVE: {
                String s2;
                if (this.root.endsWith("\\")) {
                    s2 = this.root + windowsPath.path.substring(1);
                }
                else {
                    s2 = this.root + windowsPath.path;
                }
                return createFromNormalizedPath(this.getFileSystem(), s2);
            }
            case DRIVE_RELATIVE: {
                if (!this.root.endsWith("\\")) {
                    return windowsPath;
                }
                if (!this.root.substring(0, this.root.length() - 1).equalsIgnoreCase(windowsPath.root)) {
                    return windowsPath;
                }
                final String substring = windowsPath.path.substring(windowsPath.root.length());
                String s3;
                if (this.path.endsWith("\\")) {
                    s3 = this.path + substring;
                }
                else {
                    s3 = this.path + "\\" + substring;
                }
                return createFromNormalizedPath(this.getFileSystem(), s3);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private void initOffsets() {
        if (this.offsets == null) {
            final ArrayList list = new ArrayList();
            if (this.isEmpty()) {
                list.add(0);
            }
            else {
                int length = this.root.length();
                int i = this.root.length();
                while (i < this.path.length()) {
                    if (this.path.charAt(i) != '\\') {
                        ++i;
                    }
                    else {
                        list.add(length);
                        length = ++i;
                    }
                }
                if (length != i) {
                    list.add(length);
                }
            }
            synchronized (this) {
                if (this.offsets == null) {
                    this.offsets = list.toArray(new Integer[list.size()]);
                }
            }
        }
    }
    
    @Override
    public int getNameCount() {
        this.initOffsets();
        return this.offsets.length;
    }
    
    private String elementAsString(final int n) {
        this.initOffsets();
        if (n == this.offsets.length - 1) {
            return this.path.substring(this.offsets[n]);
        }
        return this.path.substring(this.offsets[n], this.offsets[n + 1] - 1);
    }
    
    @Override
    public WindowsPath getName(final int n) {
        this.initOffsets();
        if (n < 0 || n >= this.offsets.length) {
            throw new IllegalArgumentException();
        }
        return new WindowsPath(this.getFileSystem(), WindowsPathType.RELATIVE, "", this.elementAsString(n));
    }
    
    @Override
    public WindowsPath subpath(final int n, final int n2) {
        this.initOffsets();
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        if (n >= this.offsets.length) {
            throw new IllegalArgumentException();
        }
        if (n2 > this.offsets.length) {
            throw new IllegalArgumentException();
        }
        if (n >= n2) {
            throw new IllegalArgumentException();
        }
        final StringBuilder sb = new StringBuilder();
        final Integer[] array = new Integer[n2 - n];
        for (int i = n; i < n2; ++i) {
            array[i - n] = sb.length();
            sb.append(this.elementAsString(i));
            if (i != n2 - 1) {
                sb.append("\\");
            }
        }
        return new WindowsPath(this.getFileSystem(), WindowsPathType.RELATIVE, "", sb.toString());
    }
    
    @Override
    public boolean startsWith(final Path path) {
        if (!(Objects.requireNonNull(path) instanceof WindowsPath)) {
            return false;
        }
        final WindowsPath windowsPath = (WindowsPath)path;
        if (!this.root.equalsIgnoreCase(windowsPath.root)) {
            return false;
        }
        if (windowsPath.isEmpty()) {
            return this.isEmpty();
        }
        final int nameCount = this.getNameCount();
        int nameCount2 = windowsPath.getNameCount();
        if (nameCount2 <= nameCount) {
            while (--nameCount2 >= 0) {
                if (!this.elementAsString(nameCount2).equalsIgnoreCase(windowsPath.elementAsString(nameCount2))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean endsWith(final Path path) {
        if (!(Objects.requireNonNull(path) instanceof WindowsPath)) {
            return false;
        }
        final WindowsPath windowsPath = (WindowsPath)path;
        if (windowsPath.path.length() > this.path.length()) {
            return false;
        }
        if (windowsPath.isEmpty()) {
            return this.isEmpty();
        }
        final int nameCount = this.getNameCount();
        int nameCount2 = windowsPath.getNameCount();
        if (nameCount2 > nameCount) {
            return false;
        }
        if (windowsPath.root.length() > 0) {
            if (nameCount2 < nameCount) {
                return false;
            }
            if (!this.root.equalsIgnoreCase(windowsPath.root)) {
                return false;
            }
        }
        final int n = nameCount - nameCount2;
        while (--nameCount2 >= 0) {
            if (!this.elementAsString(n + nameCount2).equalsIgnoreCase(windowsPath.elementAsString(nameCount2))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int compareTo(final Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        final String path2 = this.path;
        final String path3 = ((WindowsPath)path).path;
        final int length = path2.length();
        final int length2 = path3.length();
        for (int min = Math.min(length, length2), i = 0; i < min; ++i) {
            final char char1 = path2.charAt(i);
            final char char2 = path3.charAt(i);
            if (char1 != char2) {
                final char upperCase = Character.toUpperCase(char1);
                final char upperCase2 = Character.toUpperCase(char2);
                if (upperCase != upperCase2) {
                    return upperCase - upperCase2;
                }
            }
        }
        return length - length2;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof WindowsPath && this.compareTo((Path)o) == 0;
    }
    
    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            for (int i = 0; i < this.path.length(); ++i) {
                hash = 31 * hash + Character.toUpperCase(this.path.charAt(i));
            }
            this.hash = hash;
        }
        return hash;
    }
    
    @Override
    public String toString() {
        return this.path;
    }
    
    long openForReadAttributeAccess(final boolean b) throws WindowsException {
        int n = 33554432;
        if (!b && this.getFileSystem().supportsLinks()) {
            n |= 0x200000;
        }
        return WindowsNativeDispatcher.CreateFile(this.getPathForWin32Calls(), 128, 7, 0L, 3, n);
    }
    
    void checkRead() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkRead(this.getPathForPermissionCheck());
        }
    }
    
    void checkWrite() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkWrite(this.getPathForPermissionCheck());
        }
    }
    
    void checkDelete() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkDelete(this.getPathForPermissionCheck());
        }
    }
    
    @Override
    public URI toUri() {
        return WindowsUriSupport.toUri(this);
    }
    
    @Override
    public WindowsPath toAbsolutePath() {
        if (this.isAbsolute()) {
            return this;
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertyAccess("user.dir");
        }
        try {
            return createFromNormalizedPath(this.getFileSystem(), this.getAbsolutePath());
        }
        catch (final WindowsException ex) {
            throw new IOError(new IOException(ex.getMessage()));
        }
    }
    
    @Override
    public WindowsPath toRealPath(final LinkOption... array) throws IOException {
        this.checkRead();
        return createFromNormalizedPath(this.getFileSystem(), WindowsLinkSupport.getRealPath(this, Util.followLinks(array)));
    }
    
    @Override
    public WatchKey register(final WatchService watchService, final WatchEvent.Kind<?>[] array, WatchEvent.Modifier... array2) throws IOException {
        if (watchService == null) {
            throw new NullPointerException();
        }
        if (!(watchService instanceof WindowsWatchService)) {
            throw new ProviderMismatchException();
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            boolean b = false;
            final int length = array2.length;
            if (length > 0) {
                array2 = Arrays.copyOf(array2, length);
                int i = 0;
                while (i < length) {
                    if (array2[i++] == ExtendedWatchEventModifier.FILE_TREE) {
                        b = true;
                        break;
                    }
                }
            }
            final String pathForPermissionCheck = this.getPathForPermissionCheck();
            securityManager.checkRead(pathForPermissionCheck);
            if (b) {
                securityManager.checkRead(pathForPermissionCheck + "\\-");
            }
        }
        return ((WindowsWatchService)watchService).register(this, array, array2);
    }
    
    private static class WindowsPathWithAttributes extends WindowsPath implements BasicFileAttributesHolder
    {
        final WeakReference<BasicFileAttributes> ref;
        
        WindowsPathWithAttributes(final WindowsFileSystem windowsFileSystem, final WindowsPathType windowsPathType, final String s, final String s2, final BasicFileAttributes basicFileAttributes) {
            super(windowsFileSystem, windowsPathType, s, s2, null);
            this.ref = new WeakReference<BasicFileAttributes>(basicFileAttributes);
        }
        
        @Override
        public BasicFileAttributes get() {
            return this.ref.get();
        }
        
        @Override
        public void invalidate() {
            this.ref.clear();
        }
    }
}
