package sun.nio.fs;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.concurrent.TimeUnit;
import java.nio.file.attribute.FileTime;
import sun.misc.Unsafe;
import java.nio.file.attribute.DosFileAttributes;

class WindowsFileAttributes implements DosFileAttributes
{
    private static final Unsafe unsafe;
    private static final short SIZEOF_FILE_INFORMATION = 52;
    private static final short OFFSETOF_FILE_INFORMATION_ATTRIBUTES = 0;
    private static final short OFFSETOF_FILE_INFORMATION_CREATETIME = 4;
    private static final short OFFSETOF_FILE_INFORMATION_LASTACCESSTIME = 12;
    private static final short OFFSETOF_FILE_INFORMATION_LASTWRITETIME = 20;
    private static final short OFFSETOF_FILE_INFORMATION_VOLSERIALNUM = 28;
    private static final short OFFSETOF_FILE_INFORMATION_SIZEHIGH = 32;
    private static final short OFFSETOF_FILE_INFORMATION_SIZELOW = 36;
    private static final short OFFSETOF_FILE_INFORMATION_INDEXHIGH = 44;
    private static final short OFFSETOF_FILE_INFORMATION_INDEXLOW = 48;
    private static final short SIZEOF_FILE_ATTRIBUTE_DATA = 36;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_ATTRIBUTES = 0;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_CREATETIME = 4;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTACCESSTIME = 12;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTWRITETIME = 20;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZEHIGH = 28;
    private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZELOW = 32;
    private static final short SIZEOF_FIND_DATA = 592;
    private static final short OFFSETOF_FIND_DATA_ATTRIBUTES = 0;
    private static final short OFFSETOF_FIND_DATA_CREATETIME = 4;
    private static final short OFFSETOF_FIND_DATA_LASTACCESSTIME = 12;
    private static final short OFFSETOF_FIND_DATA_LASTWRITETIME = 20;
    private static final short OFFSETOF_FIND_DATA_SIZEHIGH = 28;
    private static final short OFFSETOF_FIND_DATA_SIZELOW = 32;
    private static final short OFFSETOF_FIND_DATA_RESERVED0 = 36;
    private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
    private static final boolean ensureAccurateMetadata;
    private final int fileAttrs;
    private final long creationTime;
    private final long lastAccessTime;
    private final long lastWriteTime;
    private final long size;
    private final int reparseTag;
    private final int volSerialNumber;
    private final int fileIndexHigh;
    private final int fileIndexLow;
    
    static FileTime toFileTime(long n) {
        n /= 10L;
        n -= 11644473600000000L;
        return FileTime.from(n, TimeUnit.MICROSECONDS);
    }
    
    static long toWindowsTime(final FileTime fileTime) {
        return (fileTime.to(TimeUnit.MICROSECONDS) + 11644473600000000L) * 10L;
    }
    
    private WindowsFileAttributes(final int fileAttrs, final long creationTime, final long lastAccessTime, final long lastWriteTime, final long size, final int reparseTag, final int volSerialNumber, final int fileIndexHigh, final int fileIndexLow) {
        this.fileAttrs = fileAttrs;
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
        this.lastWriteTime = lastWriteTime;
        this.size = size;
        this.reparseTag = reparseTag;
        this.volSerialNumber = volSerialNumber;
        this.fileIndexHigh = fileIndexHigh;
        this.fileIndexLow = fileIndexLow;
    }
    
    private static WindowsFileAttributes fromFileInformation(final long n, final int n2) {
        return new WindowsFileAttributes(WindowsFileAttributes.unsafe.getInt(n + 0L), WindowsFileAttributes.unsafe.getLong(n + 4L), WindowsFileAttributes.unsafe.getLong(n + 12L), WindowsFileAttributes.unsafe.getLong(n + 20L), ((long)WindowsFileAttributes.unsafe.getInt(n + 32L) << 32) + ((long)WindowsFileAttributes.unsafe.getInt(n + 36L) & 0xFFFFFFFFL), n2, WindowsFileAttributes.unsafe.getInt(n + 28L), WindowsFileAttributes.unsafe.getInt(n + 44L), WindowsFileAttributes.unsafe.getInt(n + 48L));
    }
    
    private static WindowsFileAttributes fromFileAttributeData(final long n, final int n2) {
        return new WindowsFileAttributes(WindowsFileAttributes.unsafe.getInt(n + 0L), WindowsFileAttributes.unsafe.getLong(n + 4L), WindowsFileAttributes.unsafe.getLong(n + 12L), WindowsFileAttributes.unsafe.getLong(n + 20L), ((long)WindowsFileAttributes.unsafe.getInt(n + 28L) << 32) + ((long)WindowsFileAttributes.unsafe.getInt(n + 32L) & 0xFFFFFFFFL), n2, 0, 0, 0);
    }
    
    static NativeBuffer getBufferForFindData() {
        return NativeBuffers.getNativeBuffer(592);
    }
    
    static WindowsFileAttributes fromFindData(final long n) {
        final int int1 = WindowsFileAttributes.unsafe.getInt(n + 0L);
        return new WindowsFileAttributes(int1, WindowsFileAttributes.unsafe.getLong(n + 4L), WindowsFileAttributes.unsafe.getLong(n + 12L), WindowsFileAttributes.unsafe.getLong(n + 20L), ((long)WindowsFileAttributes.unsafe.getInt(n + 28L) << 32) + ((long)WindowsFileAttributes.unsafe.getInt(n + 32L) & 0xFFFFFFFFL), isReparsePoint(int1) ? WindowsFileAttributes.unsafe.getInt(n + 36L) : 0, 0, 0, 0);
    }
    
    static WindowsFileAttributes readAttributes(final long n) throws WindowsException {
        final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(52);
        try {
            final long address = nativeBuffer.address();
            WindowsNativeDispatcher.GetFileInformationByHandle(n, address);
            int n2 = 0;
            if (isReparsePoint(WindowsFileAttributes.unsafe.getInt(address + 0L))) {
                final int n3 = 16384;
                final NativeBuffer nativeBuffer2 = NativeBuffers.getNativeBuffer(n3);
                try {
                    WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(n, nativeBuffer2.address(), n3);
                    n2 = (int)WindowsFileAttributes.unsafe.getLong(nativeBuffer2.address());
                }
                finally {
                    nativeBuffer2.release();
                }
            }
            return fromFileInformation(address, n2);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    static WindowsFileAttributes get(final WindowsPath windowsPath, final boolean b) throws WindowsException {
        if (!WindowsFileAttributes.ensureAccurateMetadata) {
            WindowsException ex = null;
            final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(36);
            try {
                final long address = nativeBuffer.address();
                WindowsNativeDispatcher.GetFileAttributesEx(windowsPath.getPathForWin32Calls(), address);
                if (!isReparsePoint(WindowsFileAttributes.unsafe.getInt(address + 0L))) {
                    return fromFileAttributeData(address, 0);
                }
            }
            catch (final WindowsException ex2) {
                if (ex2.lastError() != 32) {
                    throw ex2;
                }
                ex = ex2;
            }
            finally {
                nativeBuffer.release();
            }
            if (ex != null) {
                final String pathForWin32Calls = windowsPath.getPathForWin32Calls();
                final char char1 = pathForWin32Calls.charAt(pathForWin32Calls.length() - 1);
                if (char1 == ':' || char1 == '\\') {
                    throw ex;
                }
                final NativeBuffer bufferForFindData = getBufferForFindData();
                try {
                    WindowsNativeDispatcher.FindClose(WindowsNativeDispatcher.FindFirstFile(pathForWin32Calls, bufferForFindData.address()));
                    final WindowsFileAttributes fromFindData = fromFindData(bufferForFindData.address());
                    if (fromFindData.isReparsePoint()) {
                        throw ex;
                    }
                    return fromFindData;
                }
                catch (final WindowsException ex3) {
                    throw ex;
                }
                finally {
                    bufferForFindData.release();
                }
            }
        }
        final long openForReadAttributeAccess = windowsPath.openForReadAttributeAccess(b);
        try {
            return readAttributes(openForReadAttributeAccess);
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(openForReadAttributeAccess);
        }
    }
    
    static boolean isSameFile(final WindowsFileAttributes windowsFileAttributes, final WindowsFileAttributes windowsFileAttributes2) {
        return windowsFileAttributes.volSerialNumber == windowsFileAttributes2.volSerialNumber && windowsFileAttributes.fileIndexHigh == windowsFileAttributes2.fileIndexHigh && windowsFileAttributes.fileIndexLow == windowsFileAttributes2.fileIndexLow;
    }
    
    static boolean isReparsePoint(final int n) {
        return (n & 0x400) != 0x0;
    }
    
    int attributes() {
        return this.fileAttrs;
    }
    
    int volSerialNumber() {
        return this.volSerialNumber;
    }
    
    int fileIndexHigh() {
        return this.fileIndexHigh;
    }
    
    int fileIndexLow() {
        return this.fileIndexLow;
    }
    
    @Override
    public long size() {
        return this.size;
    }
    
    @Override
    public FileTime lastModifiedTime() {
        return toFileTime(this.lastWriteTime);
    }
    
    @Override
    public FileTime lastAccessTime() {
        return toFileTime(this.lastAccessTime);
    }
    
    @Override
    public FileTime creationTime() {
        return toFileTime(this.creationTime);
    }
    
    @Override
    public Object fileKey() {
        return null;
    }
    
    boolean isReparsePoint() {
        return isReparsePoint(this.fileAttrs);
    }
    
    boolean isDirectoryLink() {
        return this.isSymbolicLink() && (this.fileAttrs & 0x10) != 0x0;
    }
    
    @Override
    public boolean isSymbolicLink() {
        return this.reparseTag == -1610612724;
    }
    
    @Override
    public boolean isDirectory() {
        return !this.isSymbolicLink() && (this.fileAttrs & 0x10) != 0x0;
    }
    
    @Override
    public boolean isOther() {
        return !this.isSymbolicLink() && (this.fileAttrs & 0x440) != 0x0;
    }
    
    @Override
    public boolean isRegularFile() {
        return !this.isSymbolicLink() && !this.isDirectory() && !this.isOther();
    }
    
    @Override
    public boolean isReadOnly() {
        return (this.fileAttrs & 0x1) != 0x0;
    }
    
    @Override
    public boolean isHidden() {
        return (this.fileAttrs & 0x2) != 0x0;
    }
    
    @Override
    public boolean isArchive() {
        return (this.fileAttrs & 0x20) != 0x0;
    }
    
    @Override
    public boolean isSystem() {
        return (this.fileAttrs & 0x4) != 0x0;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.nio.fs.ensureAccurateMetadata", "false"));
        ensureAccurateMetadata = (s.length() == 0 || Boolean.valueOf(s));
    }
}
