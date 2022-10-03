package sun.nio.fs;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.FileSystemException;
import java.io.IOException;
import java.nio.file.FileStore;

class WindowsFileStore extends FileStore
{
    private final String root;
    private final WindowsNativeDispatcher.VolumeInformation volInfo;
    private final int volType;
    private final String displayName;
    
    private WindowsFileStore(final String root) throws WindowsException {
        assert root.charAt(root.length() - 1) == '\\';
        this.root = root;
        this.volInfo = WindowsNativeDispatcher.GetVolumeInformation(root);
        this.volType = WindowsNativeDispatcher.GetDriveType(root);
        final String volumeName = this.volInfo.volumeName();
        if (volumeName.length() > 0) {
            this.displayName = volumeName;
        }
        else {
            this.displayName = ((this.volType == 2) ? "Removable Disk" : "");
        }
    }
    
    static WindowsFileStore create(final String s, final boolean b) throws IOException {
        try {
            return new WindowsFileStore(s);
        }
        catch (final WindowsException ex) {
            if (b && ex.lastError() == 21) {
                return null;
            }
            ex.rethrowAsIOException(s);
            return null;
        }
    }
    
    static WindowsFileStore create(final WindowsPath windowsPath) throws IOException {
        try {
            String s;
            if (windowsPath.getFileSystem().supportsLinks()) {
                s = WindowsLinkSupport.getFinalPath(windowsPath, true);
            }
            else {
                WindowsFileAttributes.get(windowsPath, true);
                s = windowsPath.getPathForWin32Calls();
            }
            try {
                return createFromPath(s);
            }
            catch (final WindowsException ex) {
                if (ex.lastError() != 144) {
                    throw ex;
                }
                final String finalPath = WindowsLinkSupport.getFinalPath(windowsPath);
                if (finalPath == null) {
                    throw new FileSystemException(windowsPath.getPathForExceptionMessage(), null, "Couldn't resolve path");
                }
                return createFromPath(finalPath);
            }
        }
        catch (final WindowsException ex2) {
            ex2.rethrowAsIOException(windowsPath);
            return null;
        }
    }
    
    private static WindowsFileStore createFromPath(final String s) throws WindowsException {
        return new WindowsFileStore(WindowsNativeDispatcher.GetVolumePathName(s));
    }
    
    WindowsNativeDispatcher.VolumeInformation volumeInformation() {
        return this.volInfo;
    }
    
    int volumeType() {
        return this.volType;
    }
    
    @Override
    public String name() {
        return this.volInfo.volumeName();
    }
    
    @Override
    public String type() {
        return this.volInfo.fileSystemName();
    }
    
    @Override
    public boolean isReadOnly() {
        return (this.volInfo.flags() & 0x80000) != 0x0;
    }
    
    private WindowsNativeDispatcher.DiskFreeSpace readDiskFreeSpace() throws IOException {
        try {
            return WindowsNativeDispatcher.GetDiskFreeSpaceEx(this.root);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.root);
            return null;
        }
    }
    
    @Override
    public long getTotalSpace() throws IOException {
        return this.readDiskFreeSpace().totalNumberOfBytes();
    }
    
    @Override
    public long getUsableSpace() throws IOException {
        return this.readDiskFreeSpace().freeBytesAvailable();
    }
    
    @Override
    public long getUnallocatedSpace() throws IOException {
        return this.readDiskFreeSpace().freeBytesAvailable();
    }
    
    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        return null;
    }
    
    @Override
    public Object getAttribute(final String s) throws IOException {
        if (s.equals("totalSpace")) {
            return this.getTotalSpace();
        }
        if (s.equals("usableSpace")) {
            return this.getUsableSpace();
        }
        if (s.equals("unallocatedSpace")) {
            return this.getUnallocatedSpace();
        }
        if (s.equals("volume:vsn")) {
            return this.volInfo.volumeSerialNumber();
        }
        if (s.equals("volume:isRemovable")) {
            return this.volType == 2;
        }
        if (s.equals("volume:isCdrom")) {
            return this.volType == 5;
        }
        throw new UnsupportedOperationException("'" + s + "' not recognized");
    }
    
    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        if (clazz == BasicFileAttributeView.class || clazz == DosFileAttributeView.class) {
            return true;
        }
        if (clazz == AclFileAttributeView.class || clazz == FileOwnerAttributeView.class) {
            return (this.volInfo.flags() & 0x8) != 0x0;
        }
        return clazz == UserDefinedFileAttributeView.class && (this.volInfo.flags() & 0x40000) != 0x0;
    }
    
    @Override
    public boolean supportsFileAttributeView(final String s) {
        if (s.equals("basic") || s.equals("dos")) {
            return true;
        }
        if (s.equals("acl")) {
            return this.supportsFileAttributeView(AclFileAttributeView.class);
        }
        if (s.equals("owner")) {
            return this.supportsFileAttributeView(FileOwnerAttributeView.class);
        }
        return s.equals("user") && this.supportsFileAttributeView(UserDefinedFileAttributeView.class);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof WindowsFileStore && this.root.equals(((WindowsFileStore)o).root));
    }
    
    @Override
    public int hashCode() {
        return this.root.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.displayName);
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append("(");
        sb.append(this.root.subSequence(0, this.root.length() - 1));
        sb.append(")");
        return sb.toString();
    }
}
