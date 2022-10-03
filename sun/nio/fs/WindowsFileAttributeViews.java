package sun.nio.fs;

import java.nio.file.attribute.DosFileAttributes;
import java.util.Map;
import java.util.Set;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.io.IOException;

class WindowsFileAttributeViews
{
    static Basic createBasicView(final WindowsPath windowsPath, final boolean b) {
        return new Basic(windowsPath, b);
    }
    
    static Dos createDosView(final WindowsPath windowsPath, final boolean b) {
        return new Dos(windowsPath, b);
    }
    
    private static class Basic extends AbstractBasicFileAttributeView
    {
        final WindowsPath file;
        final boolean followLinks;
        
        Basic(final WindowsPath file, final boolean followLinks) {
            this.file = file;
            this.followLinks = followLinks;
        }
        
        @Override
        public WindowsFileAttributes readAttributes() throws IOException {
            this.file.checkRead();
            try {
                return WindowsFileAttributes.get(this.file, this.followLinks);
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(this.file);
                return null;
            }
        }
        
        private long adjustForFatEpoch(final long n) {
            if (n != -1L && n < 119600064000000000L) {
                return 119600064000000000L;
            }
            return n;
        }
        
        void setFileTimes(final long n, final long n2, final long n3) throws IOException {
            long createFile = -1L;
            try {
                int n4 = 33554432;
                if (!this.followLinks && this.file.getFileSystem().supportsLinks()) {
                    n4 |= 0x200000;
                }
                createFile = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), 256, 7, 3, n4);
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(this.file);
            }
            try {
                WindowsNativeDispatcher.SetFileTime(createFile, n, n2, n3);
            }
            catch (final WindowsException ex2) {
                if (this.followLinks && ex2.lastError() == 87) {
                    try {
                        if (WindowsFileStore.create(this.file).type().equals("FAT")) {
                            WindowsNativeDispatcher.SetFileTime(createFile, this.adjustForFatEpoch(n), this.adjustForFatEpoch(n2), this.adjustForFatEpoch(n3));
                            ex2 = null;
                        }
                    }
                    catch (final SecurityException ex3) {}
                    catch (final WindowsException ex4) {}
                    catch (final IOException ex5) {}
                }
                if (ex2 != null) {
                    ex2.rethrowAsIOException(this.file);
                }
            }
            finally {
                WindowsNativeDispatcher.CloseHandle(createFile);
            }
        }
        
        @Override
        public void setTimes(final FileTime fileTime, final FileTime fileTime2, final FileTime fileTime3) throws IOException {
            if (fileTime == null && fileTime2 == null && fileTime3 == null) {
                return;
            }
            this.file.checkWrite();
            this.setFileTimes((fileTime3 == null) ? -1L : WindowsFileAttributes.toWindowsTime(fileTime3), (fileTime2 == null) ? -1L : WindowsFileAttributes.toWindowsTime(fileTime2), (fileTime == null) ? -1L : WindowsFileAttributes.toWindowsTime(fileTime));
        }
    }
    
    static class Dos extends Basic implements DosFileAttributeView
    {
        private static final String READONLY_NAME = "readonly";
        private static final String ARCHIVE_NAME = "archive";
        private static final String SYSTEM_NAME = "system";
        private static final String HIDDEN_NAME = "hidden";
        private static final String ATTRIBUTES_NAME = "attributes";
        static final Set<String> dosAttributeNames;
        
        Dos(final WindowsPath windowsPath, final boolean b) {
            super(windowsPath, b);
        }
        
        @Override
        public String name() {
            return "dos";
        }
        
        @Override
        public void setAttribute(final String s, final Object o) throws IOException {
            if (s.equals("readonly")) {
                this.setReadOnly((boolean)o);
                return;
            }
            if (s.equals("archive")) {
                this.setArchive((boolean)o);
                return;
            }
            if (s.equals("system")) {
                this.setSystem((boolean)o);
                return;
            }
            if (s.equals("hidden")) {
                this.setHidden((boolean)o);
                return;
            }
            super.setAttribute(s, o);
        }
        
        @Override
        public Map<String, Object> readAttributes(final String[] array) throws IOException {
            final AttributesBuilder create = AttributesBuilder.create(Dos.dosAttributeNames, array);
            final WindowsFileAttributes attributes = this.readAttributes();
            this.addRequestedBasicAttributes(attributes, create);
            if (create.match("readonly")) {
                create.add("readonly", attributes.isReadOnly());
            }
            if (create.match("archive")) {
                create.add("archive", attributes.isArchive());
            }
            if (create.match("system")) {
                create.add("system", attributes.isSystem());
            }
            if (create.match("hidden")) {
                create.add("hidden", attributes.isHidden());
            }
            if (create.match("attributes")) {
                create.add("attributes", attributes.attributes());
            }
            return create.unmodifiableMap();
        }
        
        private void updateAttributes(final int n, final boolean b) throws IOException {
            this.file.checkWrite();
            final String finalPath = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
            try {
                final int getFileAttributes;
                final int n2 = getFileAttributes = WindowsNativeDispatcher.GetFileAttributes(finalPath);
                int n3;
                if (b) {
                    n3 = (getFileAttributes | n);
                }
                else {
                    n3 = (getFileAttributes & ~n);
                }
                if (n3 != n2) {
                    WindowsNativeDispatcher.SetFileAttributes(finalPath, n3);
                }
            }
            catch (final WindowsException ex) {
                ex.rethrowAsIOException(this.file);
            }
        }
        
        @Override
        public void setReadOnly(final boolean b) throws IOException {
            this.updateAttributes(1, b);
        }
        
        @Override
        public void setHidden(final boolean b) throws IOException {
            this.updateAttributes(2, b);
        }
        
        @Override
        public void setArchive(final boolean b) throws IOException {
            this.updateAttributes(32, b);
        }
        
        @Override
        public void setSystem(final boolean b) throws IOException {
            this.updateAttributes(4, b);
        }
        
        void setAttributes(final WindowsFileAttributes windowsFileAttributes) throws IOException {
            int n = 0;
            if (windowsFileAttributes.isReadOnly()) {
                n |= 0x1;
            }
            if (windowsFileAttributes.isHidden()) {
                n |= 0x2;
            }
            if (windowsFileAttributes.isArchive()) {
                n |= 0x20;
            }
            if (windowsFileAttributes.isSystem()) {
                n |= 0x4;
            }
            this.updateAttributes(n, true);
            this.setFileTimes(WindowsFileAttributes.toWindowsTime(windowsFileAttributes.creationTime()), WindowsFileAttributes.toWindowsTime(windowsFileAttributes.lastModifiedTime()), WindowsFileAttributes.toWindowsTime(windowsFileAttributes.lastAccessTime()));
        }
        
        static {
            dosAttributeNames = Util.newSet(Dos.basicAttributeNames, "readonly", "archive", "system", "hidden", "attributes");
        }
    }
}
