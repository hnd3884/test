package sun.nio.fs;

import java.util.Iterator;
import java.nio.file.LinkOption;
import com.sun.nio.file.ExtendedOpenOption;
import java.nio.file.StandardOpenOption;
import sun.misc.SharedSecrets;
import java.io.FileDescriptor;
import java.io.IOException;
import sun.nio.ch.WindowsAsynchronousFileChannelImpl;
import java.nio.channels.AsynchronousFileChannel;
import sun.nio.ch.ThreadPool;
import sun.nio.ch.FileChannelImpl;
import java.nio.channels.FileChannel;
import java.util.Set;
import java.nio.file.OpenOption;
import sun.misc.JavaIOFileDescriptorAccess;

class WindowsChannelFactory
{
    private static final JavaIOFileDescriptorAccess fdAccess;
    static final OpenOption OPEN_REPARSE_POINT;
    
    private WindowsChannelFactory() {
    }
    
    static FileChannel newFileChannel(final String s, final String s2, final Set<? extends OpenOption> set, final long n) throws WindowsException {
        final Flags flags = Flags.toFlags(set);
        if (!flags.read && !flags.write) {
            if (flags.append) {
                flags.write = true;
            }
            else {
                flags.read = true;
            }
        }
        if (flags.read && flags.append) {
            throw new IllegalArgumentException("READ + APPEND not allowed");
        }
        if (flags.append && flags.truncateExisting) {
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
        }
        return FileChannelImpl.open(open(s, s2, flags, n), s, flags.read, flags.write, flags.append, null);
    }
    
    static AsynchronousFileChannel newAsynchronousFileChannel(final String s, final String s2, final Set<? extends OpenOption> set, final long n, final ThreadPool threadPool) throws IOException {
        final Flags flags = Flags.toFlags(set);
        flags.overlapped = true;
        if (!flags.read && !flags.write) {
            flags.read = true;
        }
        if (flags.append) {
            throw new UnsupportedOperationException("APPEND not allowed");
        }
        FileDescriptor open;
        try {
            open = open(s, s2, flags, n);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(s);
            return null;
        }
        try {
            return WindowsAsynchronousFileChannelImpl.open(open, flags.read, flags.write, threadPool);
        }
        catch (final IOException ex2) {
            WindowsNativeDispatcher.CloseHandle(WindowsChannelFactory.fdAccess.getHandle(open));
            throw ex2;
        }
    }
    
    private static FileDescriptor open(final String s, final String s2, final Flags flags, final long n) throws WindowsException {
        boolean b = false;
        int n2 = 0;
        if (flags.read) {
            n2 |= Integer.MIN_VALUE;
        }
        if (flags.write) {
            n2 |= 0x40000000;
        }
        int n3 = 0;
        if (flags.shareRead) {
            n3 |= 0x1;
        }
        if (flags.shareWrite) {
            n3 |= 0x2;
        }
        if (flags.shareDelete) {
            n3 |= 0x4;
        }
        int n4 = 128;
        int n5 = 3;
        if (flags.write) {
            if (flags.createNew) {
                n5 = 1;
                n4 |= 0x200000;
            }
            else {
                if (flags.create) {
                    n5 = 4;
                }
                if (flags.truncateExisting) {
                    if (n5 == 4) {
                        b = true;
                    }
                    else {
                        n5 = 5;
                    }
                }
            }
        }
        if (flags.dsync || flags.sync) {
            n4 |= Integer.MIN_VALUE;
        }
        if (flags.overlapped) {
            n4 |= 0x40000000;
        }
        if (flags.deleteOnClose) {
            n4 |= 0x4000000;
        }
        boolean b2 = true;
        if (n5 != 1 && (flags.noFollowLinks || flags.openReparsePoint || flags.deleteOnClose)) {
            if (flags.noFollowLinks || flags.deleteOnClose) {
                b2 = false;
            }
            n4 |= 0x200000;
        }
        if (s2 != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                if (flags.read) {
                    securityManager.checkRead(s2);
                }
                if (flags.write) {
                    securityManager.checkWrite(s2);
                }
                if (flags.deleteOnClose) {
                    securityManager.checkDelete(s2);
                }
            }
        }
        final long createFile = WindowsNativeDispatcher.CreateFile(s, n2, n3, n, n5, n4);
        if (!b2) {
            try {
                if (WindowsFileAttributes.readAttributes(createFile).isSymbolicLink()) {
                    throw new WindowsException("File is symbolic link");
                }
            }
            catch (final WindowsException ex) {
                WindowsNativeDispatcher.CloseHandle(createFile);
                throw ex;
            }
        }
        if (b) {
            try {
                WindowsNativeDispatcher.SetEndOfFile(createFile);
            }
            catch (final WindowsException ex2) {
                WindowsNativeDispatcher.CloseHandle(createFile);
                throw ex2;
            }
        }
        if (n5 == 1 && flags.sparse) {
            try {
                WindowsNativeDispatcher.DeviceIoControlSetSparse(createFile);
            }
            catch (final WindowsException ex3) {}
        }
        final FileDescriptor fileDescriptor = new FileDescriptor();
        WindowsChannelFactory.fdAccess.setHandle(fileDescriptor, createFile);
        return fileDescriptor;
    }
    
    static {
        fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        OPEN_REPARSE_POINT = new OpenOption() {};
    }
    
    private static class Flags
    {
        boolean read;
        boolean write;
        boolean append;
        boolean truncateExisting;
        boolean create;
        boolean createNew;
        boolean deleteOnClose;
        boolean sparse;
        boolean overlapped;
        boolean sync;
        boolean dsync;
        boolean shareRead;
        boolean shareWrite;
        boolean shareDelete;
        boolean noFollowLinks;
        boolean openReparsePoint;
        
        private Flags() {
            this.shareRead = true;
            this.shareWrite = true;
            this.shareDelete = true;
        }
        
        static Flags toFlags(final Set<? extends OpenOption> set) {
            final Flags flags = new Flags();
            for (final OpenOption openOption : set) {
                if (openOption instanceof StandardOpenOption) {
                    switch ((StandardOpenOption)openOption) {
                        case READ: {
                            flags.read = true;
                            continue;
                        }
                        case WRITE: {
                            flags.write = true;
                            continue;
                        }
                        case APPEND: {
                            flags.append = true;
                            continue;
                        }
                        case TRUNCATE_EXISTING: {
                            flags.truncateExisting = true;
                            continue;
                        }
                        case CREATE: {
                            flags.create = true;
                            continue;
                        }
                        case CREATE_NEW: {
                            flags.createNew = true;
                            continue;
                        }
                        case DELETE_ON_CLOSE: {
                            flags.deleteOnClose = true;
                            continue;
                        }
                        case SPARSE: {
                            flags.sparse = true;
                            continue;
                        }
                        case SYNC: {
                            flags.sync = true;
                            continue;
                        }
                        case DSYNC: {
                            flags.dsync = true;
                            continue;
                        }
                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }
                }
                else if (openOption instanceof ExtendedOpenOption) {
                    switch ((ExtendedOpenOption)openOption) {
                        case NOSHARE_READ: {
                            flags.shareRead = false;
                            continue;
                        }
                        case NOSHARE_WRITE: {
                            flags.shareWrite = false;
                            continue;
                        }
                        case NOSHARE_DELETE: {
                            flags.shareDelete = false;
                            continue;
                        }
                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }
                }
                else if (openOption == LinkOption.NOFOLLOW_LINKS) {
                    flags.noFollowLinks = true;
                }
                else if (openOption == WindowsChannelFactory.OPEN_REPARSE_POINT) {
                    flags.openReparsePoint = true;
                }
                else {
                    if (openOption == null) {
                        throw new NullPointerException();
                    }
                    throw new UnsupportedOperationException();
                }
            }
            return flags;
        }
    }
}
