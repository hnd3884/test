package sun.awt.shell;

import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.ObjectStreamException;
import java.io.File;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.util.concurrent.Callable;
import java.io.IOException;
import java.util.Map;
import java.awt.Image;

final class Win32ShellFolder2 extends ShellFolder
{
    public static final int DESKTOP = 0;
    public static final int INTERNET = 1;
    public static final int PROGRAMS = 2;
    public static final int CONTROLS = 3;
    public static final int PRINTERS = 4;
    public static final int PERSONAL = 5;
    public static final int FAVORITES = 6;
    public static final int STARTUP = 7;
    public static final int RECENT = 8;
    public static final int SENDTO = 9;
    public static final int BITBUCKET = 10;
    public static final int STARTMENU = 11;
    public static final int DESKTOPDIRECTORY = 16;
    public static final int DRIVES = 17;
    public static final int NETWORK = 18;
    public static final int NETHOOD = 19;
    public static final int FONTS = 20;
    public static final int TEMPLATES = 21;
    public static final int COMMON_STARTMENU = 22;
    public static final int COMMON_PROGRAMS = 23;
    public static final int COMMON_STARTUP = 24;
    public static final int COMMON_DESKTOPDIRECTORY = 25;
    public static final int APPDATA = 26;
    public static final int PRINTHOOD = 27;
    public static final int ALTSTARTUP = 29;
    public static final int COMMON_ALTSTARTUP = 30;
    public static final int COMMON_FAVORITES = 31;
    public static final int INTERNET_CACHE = 32;
    public static final int COOKIES = 33;
    public static final int HISTORY = 34;
    public static final int ATTRIB_CANCOPY = 1;
    public static final int ATTRIB_CANMOVE = 2;
    public static final int ATTRIB_CANLINK = 4;
    public static final int ATTRIB_CANRENAME = 16;
    public static final int ATTRIB_CANDELETE = 32;
    public static final int ATTRIB_HASPROPSHEET = 64;
    public static final int ATTRIB_DROPTARGET = 256;
    public static final int ATTRIB_LINK = 65536;
    public static final int ATTRIB_SHARE = 131072;
    public static final int ATTRIB_READONLY = 262144;
    public static final int ATTRIB_GHOSTED = 524288;
    public static final int ATTRIB_HIDDEN = 524288;
    public static final int ATTRIB_FILESYSANCESTOR = 268435456;
    public static final int ATTRIB_FOLDER = 536870912;
    public static final int ATTRIB_FILESYSTEM = 1073741824;
    public static final int ATTRIB_HASSUBFOLDER = Integer.MIN_VALUE;
    public static final int ATTRIB_VALIDATE = 16777216;
    public static final int ATTRIB_REMOVABLE = 33554432;
    public static final int ATTRIB_COMPRESSED = 67108864;
    public static final int ATTRIB_BROWSABLE = 134217728;
    public static final int ATTRIB_NONENUMERATED = 1048576;
    public static final int ATTRIB_NEWCONTENT = 2097152;
    public static final int SHGDN_NORMAL = 0;
    public static final int SHGDN_INFOLDER = 1;
    public static final int SHGDN_INCLUDE_NONFILESYS = 8192;
    public static final int SHGDN_FORADDRESSBAR = 16384;
    public static final int SHGDN_FORPARSING = 32768;
    FolderDisposer disposer;
    private long pIShellIcon;
    private String folderType;
    private String displayName;
    private Image smallIcon;
    private Image largeIcon;
    private Boolean isDir;
    private boolean isPersonal;
    private volatile Boolean cachedIsFileSystem;
    private volatile Boolean cachedIsLink;
    private static Map smallSystemImages;
    private static Map largeSystemImages;
    private static Map smallLinkedSystemImages;
    private static Map largeLinkedSystemImages;
    private static final int LVCFMT_LEFT = 0;
    private static final int LVCFMT_RIGHT = 1;
    private static final int LVCFMT_CENTER = 2;
    
    private static native void initIDs();
    
    private void setIShellFolder(final long piShellFolder) {
        this.disposer.pIShellFolder = piShellFolder;
    }
    
    private void setRelativePIDL(final long relativePIDL) {
        this.disposer.relativePIDL = relativePIDL;
    }
    
    private static String composePathForCsidl(final int n) throws IOException, InterruptedException {
        final String fileSystemPath = getFileSystemPath(n);
        return (fileSystemPath == null) ? ("ShellFolder: 0x" + Integer.toHexString(n)) : fileSystemPath;
    }
    
    Win32ShellFolder2(final int n) throws IOException, InterruptedException {
        super((ShellFolder)null, composePathForCsidl(n));
        this.disposer = new FolderDisposer();
        this.pIShellIcon = -1L;
        this.folderType = null;
        this.displayName = null;
        this.smallIcon = null;
        this.largeIcon = null;
        this.isDir = null;
        ShellFolder.invoke((Callable<Object>)new Callable<Void>() {
            @Override
            public Void call() throws InterruptedException {
                if (n == 0) {
                    Win32ShellFolder2.this.initDesktop();
                }
                else {
                    Win32ShellFolder2.this.initSpecial(Win32ShellFolder2.this.getDesktop().getIShellFolder(), n);
                    long n = Win32ShellFolder2.this.disposer.relativePIDL;
                    Win32ShellFolder2.this.parent = Win32ShellFolder2.this.getDesktop();
                    while (n != 0L) {
                        final long copyFirstPIDLEntry = Win32ShellFolder2.copyFirstPIDLEntry(n);
                        if (copyFirstPIDLEntry == 0L) {
                            break;
                        }
                        n = Win32ShellFolder2.getNextPIDLEntry(n);
                        if (n != 0L) {
                            Win32ShellFolder2.this.parent = new Win32ShellFolder2((Win32ShellFolder2)Win32ShellFolder2.this.parent, copyFirstPIDLEntry);
                        }
                        else {
                            Win32ShellFolder2.this.disposer.relativePIDL = copyFirstPIDLEntry;
                        }
                    }
                }
                return null;
            }
        }, InterruptedException.class);
        Disposer.addRecord(this, this.disposer);
    }
    
    Win32ShellFolder2(final Win32ShellFolder2 win32ShellFolder2, final long piShellFolder, final long relativePIDL, final String s) {
        super(win32ShellFolder2, (s != null) ? s : "ShellFolder: ");
        this.disposer = new FolderDisposer();
        this.pIShellIcon = -1L;
        this.folderType = null;
        this.displayName = null;
        this.smallIcon = null;
        this.largeIcon = null;
        this.isDir = null;
        this.disposer.pIShellFolder = piShellFolder;
        this.disposer.relativePIDL = relativePIDL;
        Disposer.addRecord(this, this.disposer);
    }
    
    Win32ShellFolder2(final Win32ShellFolder2 win32ShellFolder2, final long relativePIDL) throws InterruptedException {
        super(win32ShellFolder2, ShellFolder.invoke((Callable<String>)new Callable<String>() {
            @Override
            public String call() {
                return getFileSystemPath(Win32ShellFolder2.this.getIShellFolder(), relativePIDL);
            }
        }, RuntimeException.class));
        this.disposer = new FolderDisposer();
        this.pIShellIcon = -1L;
        this.folderType = null;
        this.displayName = null;
        this.smallIcon = null;
        this.largeIcon = null;
        this.isDir = null;
        this.disposer.relativePIDL = relativePIDL;
        Disposer.addRecord(this, this.disposer);
    }
    
    private native void initDesktop();
    
    private native void initSpecial(final long p0, final int p1);
    
    public void setIsPersonal() {
        this.isPersonal = true;
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        return ShellFolder.invoke((Callable<Object>)new Callable<File>() {
            @Override
            public File call() {
                if (Win32ShellFolder2.this.isFileSystem()) {
                    return new File(Win32ShellFolder2.this.getPath());
                }
                final Win32ShellFolder2 drives = Win32ShellFolderManager2.getDrives();
                if (drives != null) {
                    final File[] listFiles = drives.listFiles();
                    if (listFiles != null) {
                        for (int i = 0; i < listFiles.length; ++i) {
                            if (listFiles[i] instanceof Win32ShellFolder2) {
                                final Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)listFiles[i];
                                if (win32ShellFolder2.isFileSystem() && !win32ShellFolder2.hasAttribute(33554432)) {
                                    return new File(win32ShellFolder2.getPath());
                                }
                            }
                        }
                    }
                }
                return new File("C:\\");
            }
        });
    }
    
    protected void dispose() {
        this.disposer.dispose();
    }
    
    static native long getNextPIDLEntry(final long p0);
    
    static native long copyFirstPIDLEntry(final long p0);
    
    private static native long combinePIDLs(final long p0, final long p1);
    
    static native void releasePIDL(final long p0);
    
    private static native void releaseIShellFolder(final long p0);
    
    private long getIShellFolder() {
        if (this.disposer.pIShellFolder == 0L) {
            try {
                this.disposer.pIShellFolder = ShellFolder.invoke((Callable<Long>)new Callable<Long>() {
                    @Override
                    public Long call() {
                        assert Win32ShellFolder2.this.isDirectory();
                        assert Win32ShellFolder2.this.parent != null;
                        final long parentIShellFolder = Win32ShellFolder2.this.getParentIShellFolder();
                        if (parentIShellFolder == 0L) {
                            throw new InternalError("Parent IShellFolder was null for " + Win32ShellFolder2.this.getAbsolutePath());
                        }
                        final long access$500 = bindToObject(parentIShellFolder, Win32ShellFolder2.this.disposer.relativePIDL);
                        if (access$500 == 0L) {
                            throw new InternalError("Unable to bind " + Win32ShellFolder2.this.getAbsolutePath() + " to parent");
                        }
                        return access$500;
                    }
                }, RuntimeException.class);
            }
            catch (final InterruptedException ex) {}
        }
        return this.disposer.pIShellFolder;
    }
    
    public long getParentIShellFolder() {
        final Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)this.getParentFile();
        if (win32ShellFolder2 == null) {
            return this.getIShellFolder();
        }
        return win32ShellFolder2.getIShellFolder();
    }
    
    public long getRelativePIDL() {
        if (this.disposer.relativePIDL == 0L) {
            throw new InternalError("Should always have a relative PIDL");
        }
        return this.disposer.relativePIDL;
    }
    
    private long getAbsolutePIDL() {
        if (this.parent == null) {
            return this.getRelativePIDL();
        }
        if (this.disposer.absolutePIDL == 0L) {
            this.disposer.absolutePIDL = combinePIDLs(((Win32ShellFolder2)this.parent).getAbsolutePIDL(), this.getRelativePIDL());
        }
        return this.disposer.absolutePIDL;
    }
    
    public Win32ShellFolder2 getDesktop() {
        return Win32ShellFolderManager2.getDesktop();
    }
    
    public long getDesktopIShellFolder() {
        return this.getDesktop().getIShellFolder();
    }
    
    private static boolean pathsEqual(final String s, final String s2) {
        return s.equalsIgnoreCase(s2);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Win32ShellFolder2)) {
            if (!(o instanceof File)) {
                return super.equals(o);
            }
            return pathsEqual(this.getPath(), ((File)o).getPath());
        }
        else {
            final Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)o;
            if ((this.parent == null && win32ShellFolder2.parent != null) || (this.parent != null && win32ShellFolder2.parent == null)) {
                return false;
            }
            if (this.isFileSystem() && win32ShellFolder2.isFileSystem()) {
                return pathsEqual(this.getPath(), win32ShellFolder2.getPath()) && (this.parent == win32ShellFolder2.parent || this.parent.equals(win32ShellFolder2.parent));
            }
            if (this.parent != win32ShellFolder2.parent) {
                if (!this.parent.equals(win32ShellFolder2.parent)) {
                    return false;
                }
            }
            try {
                return pidlsEqual(this.getParentIShellFolder(), this.disposer.relativePIDL, win32ShellFolder2.disposer.relativePIDL);
            }
            catch (final InterruptedException ex) {
                return false;
            }
            return false;
        }
    }
    
    private static boolean pidlsEqual(final long n, final long n2, final long n3) throws InterruptedException {
        return ShellFolder.invoke((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return compareIDs(n, n2, n3) == 0;
            }
        }, RuntimeException.class);
    }
    
    private static native int compareIDs(final long p0, final long p1, final long p2);
    
    @Override
    public boolean isFileSystem() {
        if (this.cachedIsFileSystem == null) {
            this.cachedIsFileSystem = this.hasAttribute(1073741824);
        }
        return this.cachedIsFileSystem;
    }
    
    public boolean hasAttribute(final int n) {
        final Boolean b = ShellFolder.invoke((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return (getAttributes0(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), n) & n) != 0x0;
            }
        });
        return b != null && b;
    }
    
    private static native int getAttributes0(final long p0, final long p1, final int p2);
    
    private static String getFileSystemPath(final long n, final long n2) {
        final int n3 = 536936448;
        if (n == Win32ShellFolderManager2.getNetwork().getIShellFolder() && getAttributes0(n, n2, n3) == n3) {
            final String fileSystemPath = getFileSystemPath(Win32ShellFolderManager2.getDesktop().getIShellFolder(), getLinkLocation(n, n2, false));
            if (fileSystemPath != null && fileSystemPath.startsWith("\\\\")) {
                return fileSystemPath;
            }
        }
        return getDisplayNameOf(n, n2, 32768);
    }
    
    static String getFileSystemPath(final int n) throws IOException, InterruptedException {
        final String s = ShellFolder.invoke((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws IOException {
                return getFileSystemPath0(n);
            }
        }, IOException.class);
        if (s != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkRead(s);
            }
        }
        return s;
    }
    
    private static native String getFileSystemPath0(final int p0) throws IOException;
    
    private static boolean isNetworkRoot(final String s) {
        return s.equals("\\\\") || s.equals("\\") || s.equals("//") || s.equals("/");
    }
    
    @Override
    public File getParentFile() {
        return this.parent;
    }
    
    @Override
    public boolean isDirectory() {
        if (this.isDir == null) {
            if (this.hasAttribute(536870912) && !this.hasAttribute(134217728)) {
                this.isDir = Boolean.TRUE;
            }
            else if (this.isLink()) {
                final ShellFolder linkLocation = this.getLinkLocation(false);
                this.isDir = (linkLocation != null && linkLocation.isDirectory());
            }
            else {
                this.isDir = Boolean.FALSE;
            }
        }
        return this.isDir;
    }
    
    private long getEnumObjects(final boolean b) throws InterruptedException {
        return ShellFolder.invoke((Callable<Long>)new Callable<Long>() {
            @Override
            public Long call() {
                return Win32ShellFolder2.this.getEnumObjects(Win32ShellFolder2.this.disposer.pIShellFolder, Win32ShellFolder2.this.disposer.pIShellFolder == Win32ShellFolder2.this.getDesktopIShellFolder(), b);
            }
        }, RuntimeException.class);
    }
    
    private native long getEnumObjects(final long p0, final boolean p1, final boolean p2);
    
    private native long getNextChild(final long p0);
    
    private native void releaseEnumObjects(final long p0);
    
    private static native long bindToObject(final long p0, final long p1);
    
    @Override
    public File[] listFiles(final boolean b) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkRead(this.getPath());
        }
        try {
            return Win32ShellFolderManager2.checkFiles(ShellFolder.invoke((Callable<File[]>)new Callable<File[]>() {
                @Override
                public File[] call() throws InterruptedException {
                    if (!Win32ShellFolder2.this.isDirectory()) {
                        return null;
                    }
                    if (Win32ShellFolder2.this.isLink() && !Win32ShellFolder2.this.hasAttribute(536870912)) {
                        return new File[0];
                    }
                    final Win32ShellFolder2 desktop = Win32ShellFolderManager2.getDesktop();
                    final Win32ShellFolder2 personal = Win32ShellFolderManager2.getPersonal();
                    final long access$200 = Win32ShellFolder2.this.getIShellFolder();
                    final ArrayList list = new ArrayList();
                    final long access$201 = Win32ShellFolder2.this.getEnumObjects(b);
                    if (access$201 != 0L) {
                        try {
                            final int n = 1342177280;
                            long access$202;
                            do {
                                access$202 = Win32ShellFolder2.this.getNextChild(access$201);
                                boolean b = true;
                                if (access$202 != 0L && (getAttributes0(access$200, access$202, n) & n) != 0x0) {
                                    Win32ShellFolder2 win32ShellFolder2;
                                    if (Win32ShellFolder2.this.equals(desktop) && personal != null && pidlsEqual(access$200, access$202, personal.disposer.relativePIDL)) {
                                        win32ShellFolder2 = personal;
                                    }
                                    else {
                                        win32ShellFolder2 = new Win32ShellFolder2(Win32ShellFolder2.this, access$202);
                                        b = false;
                                    }
                                    list.add(win32ShellFolder2);
                                }
                                if (b) {
                                    Win32ShellFolder2.releasePIDL(access$202);
                                }
                            } while (access$202 != 0L && !Thread.currentThread().isInterrupted());
                        }
                        finally {
                            Win32ShellFolder2.this.releaseEnumObjects(access$201);
                        }
                    }
                    return Thread.currentThread().isInterrupted() ? new File[0] : list.toArray(new ShellFolder[list.size()]);
                }
            }, InterruptedException.class));
        }
        catch (final InterruptedException ex) {
            return new File[0];
        }
    }
    
    Win32ShellFolder2 getChildByPath(final String s) throws InterruptedException {
        return ShellFolder.invoke((Callable<Win32ShellFolder2>)new Callable<Win32ShellFolder2>() {
            @Override
            public Win32ShellFolder2 call() throws InterruptedException {
                final long access$200 = Win32ShellFolder2.this.getIShellFolder();
                final long access$201 = Win32ShellFolder2.this.getEnumObjects(true);
                Win32ShellFolder2 win32ShellFolder2 = null;
                long access$202;
                while ((access$202 = Win32ShellFolder2.this.getNextChild(access$201)) != 0L) {
                    if (getAttributes0(access$200, access$202, 1073741824) != 0) {
                        final String access$203 = getFileSystemPath(access$200, access$202);
                        if (access$203 != null && access$203.equalsIgnoreCase(s)) {
                            win32ShellFolder2 = new Win32ShellFolder2(Win32ShellFolder2.this, bindToObject(access$200, access$202), access$202, access$203);
                            break;
                        }
                    }
                    Win32ShellFolder2.releasePIDL(access$202);
                }
                Win32ShellFolder2.this.releaseEnumObjects(access$201);
                return win32ShellFolder2;
            }
        }, InterruptedException.class);
    }
    
    @Override
    public boolean isLink() {
        if (this.cachedIsLink == null) {
            this.cachedIsLink = this.hasAttribute(65536);
        }
        return this.cachedIsLink;
    }
    
    @Override
    public boolean isHidden() {
        return this.hasAttribute(524288);
    }
    
    private static native long getLinkLocation(final long p0, final long p1, final boolean p2);
    
    @Override
    public ShellFolder getLinkLocation() {
        return this.getLinkLocation(true);
    }
    
    private ShellFolder getLinkLocation(final boolean b) {
        return ShellFolder.invoke((Callable<ShellFolder>)new Callable<ShellFolder>() {
            @Override
            public ShellFolder call() {
                if (!Win32ShellFolder2.this.isLink()) {
                    return null;
                }
                ShellFolder shellFolderFromRelativePIDL = null;
                final long access$1400 = getLinkLocation(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), b);
                if (access$1400 != 0L) {
                    try {
                        shellFolderFromRelativePIDL = Win32ShellFolderManager2.createShellFolderFromRelativePIDL(Win32ShellFolder2.this.getDesktop(), access$1400);
                    }
                    catch (final InterruptedException ex) {}
                    catch (final InternalError internalError) {}
                }
                return shellFolderFromRelativePIDL;
            }
        });
    }
    
    long parseDisplayName(final String s) throws IOException, InterruptedException {
        return ShellFolder.invoke((Callable<Long>)new Callable<Long>() {
            @Override
            public Long call() throws IOException {
                return parseDisplayName0(Win32ShellFolder2.this.getIShellFolder(), s);
            }
        }, IOException.class);
    }
    
    private static native long parseDisplayName0(final long p0, final String p1) throws IOException;
    
    private static native String getDisplayNameOf(final long p0, final long p1, final int p2);
    
    @Override
    public String getDisplayName() {
        if (this.displayName == null) {
            this.displayName = ShellFolder.invoke((Callable<String>)new Callable<String>() {
                @Override
                public String call() {
                    return getDisplayNameOf(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), 0);
                }
            });
        }
        return this.displayName;
    }
    
    private static native String getFolderType(final long p0);
    
    @Override
    public String getFolderType() {
        if (this.folderType == null) {
            this.folderType = ShellFolder.invoke((Callable<String>)new Callable<String>() {
                final /* synthetic */ long val$absolutePIDL = Win32ShellFolder2.this.getAbsolutePIDL();
                
                @Override
                public String call() {
                    return getFolderType(this.val$absolutePIDL);
                }
            });
        }
        return this.folderType;
    }
    
    private native String getExecutableType(final String p0);
    
    @Override
    public String getExecutableType() {
        if (!this.isFileSystem()) {
            return null;
        }
        return this.getExecutableType(this.getAbsolutePath());
    }
    
    private static native long getIShellIcon(final long p0);
    
    private static native int getIconIndex(final long p0, final long p1);
    
    private static native long getIcon(final String p0, final boolean p1);
    
    private static native long extractIcon(final long p0, final long p1, final boolean p2);
    
    private static native long getSystemIcon(final int p0);
    
    private static native long getIconResource(final String p0, final int p1, final int p2, final int p3, final boolean p4);
    
    private static native int[] getIconBits(final long p0, final int p1);
    
    private static native void disposeIcon(final long p0);
    
    static native int[] getStandardViewButton0(final int p0);
    
    private long getIShellIcon() {
        if (this.pIShellIcon == -1L) {
            this.pIShellIcon = getIShellIcon(this.getIShellFolder());
        }
        return this.pIShellIcon;
    }
    
    private static Image makeIcon(final long n, final boolean b) {
        if (n != 0L && n != -1L) {
            final int n2 = b ? 32 : 16;
            final int[] iconBits = getIconBits(n, n2);
            if (iconBits != null) {
                final BufferedImage bufferedImage = new BufferedImage(n2, n2, 2);
                bufferedImage.setRGB(0, 0, n2, n2, iconBits, 0, n2);
                return bufferedImage;
            }
        }
        return null;
    }
    
    @Override
    public Image getIcon(final boolean b) {
        Image image = b ? this.largeIcon : this.smallIcon;
        if (image == null) {
            image = ShellFolder.invoke((Callable<Image>)new Callable<Image>() {
                @Override
                public Image call() {
                    Image image = null;
                    if (Win32ShellFolder2.this.isFileSystem()) {
                        final int access$1900 = getIconIndex((Win32ShellFolder2.this.parent != null) ? ((Win32ShellFolder2)Win32ShellFolder2.this.parent).getIShellIcon() : 0L, Win32ShellFolder2.this.getRelativePIDL());
                        if (access$1900 > 0) {
                            Map map;
                            if (Win32ShellFolder2.this.isLink()) {
                                map = (b ? Win32ShellFolder2.largeLinkedSystemImages : Win32ShellFolder2.smallLinkedSystemImages);
                            }
                            else {
                                map = (b ? Win32ShellFolder2.largeSystemImages : Win32ShellFolder2.smallSystemImages);
                            }
                            image = (Image)map.get(access$1900);
                            if (image == null) {
                                final long access$1901 = getIcon(Win32ShellFolder2.this.getAbsolutePath(), b);
                                image = makeIcon(access$1901, b);
                                disposeIcon(access$1901);
                                if (image != null) {
                                    map.put(access$1900, image);
                                }
                            }
                        }
                    }
                    if (image == null) {
                        final long access$1902 = extractIcon(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), b);
                        image = makeIcon(access$1902, b);
                        disposeIcon(access$1902);
                    }
                    if (image == null) {
                        image = ShellFolder.this.getIcon(b);
                    }
                    return image;
                }
            });
            if (b) {
                this.largeIcon = image;
            }
            else {
                this.smallIcon = image;
            }
        }
        return image;
    }
    
    static Image getSystemIcon(final SystemIcon systemIcon) {
        final long systemIcon2 = getSystemIcon(systemIcon.getIconID());
        final Image icon = makeIcon(systemIcon2, true);
        disposeIcon(systemIcon2);
        return icon;
    }
    
    static Image getShell32Icon(final int n, final boolean b) {
        boolean equals = true;
        final int n2 = b ? 32 : 16;
        final String s = (String)Toolkit.getDefaultToolkit().getDesktopProperty("win.icon.shellIconBPP");
        if (s != null) {
            equals = s.equals("4");
        }
        final long iconResource = getIconResource("shell32.dll", n, n2, n2, equals);
        if (iconResource != 0L) {
            final Image icon = makeIcon(iconResource, b);
            disposeIcon(iconResource);
            return icon;
        }
        return null;
    }
    
    @Override
    public File getCanonicalFile() throws IOException {
        return this;
    }
    
    public boolean isSpecial() {
        return this.isPersonal || !this.isFileSystem() || this == this.getDesktop();
    }
    
    @Override
    public int compareTo(final File file) {
        if (file instanceof Win32ShellFolder2) {
            return Win32ShellFolderManager2.compareShellFolders(this, (Win32ShellFolder2)file);
        }
        if (this.isFileSystem() && !this.isSpecial()) {
            return super.compareTo(file);
        }
        return -1;
    }
    
    @Override
    public ShellFolderColumnInfo[] getFolderColumns() {
        return ShellFolder.invoke((Callable<ShellFolderColumnInfo[]>)new Callable<ShellFolderColumnInfo[]>() {
            @Override
            public ShellFolderColumnInfo[] call() {
                ShellFolderColumnInfo[] access$2900 = Win32ShellFolder2.this.doGetColumnInfo(Win32ShellFolder2.this.getIShellFolder());
                if (access$2900 != null) {
                    final ArrayList list = new ArrayList();
                    for (int i = 0; i < access$2900.length; ++i) {
                        final ShellFolderColumnInfo shellFolderColumnInfo = access$2900[i];
                        if (shellFolderColumnInfo != null) {
                            shellFolderColumnInfo.setAlignment((shellFolderColumnInfo.getAlignment() == 1) ? 4 : ((shellFolderColumnInfo.getAlignment() == 2) ? 0 : 10));
                            shellFolderColumnInfo.setComparator(new ColumnComparator(Win32ShellFolder2.this, i));
                            list.add(shellFolderColumnInfo);
                        }
                    }
                    access$2900 = new ShellFolderColumnInfo[list.size()];
                    list.toArray(access$2900);
                }
                return access$2900;
            }
        });
    }
    
    @Override
    public Object getFolderColumnValue(final int n) {
        return ShellFolder.invoke((Callable<Object>)new Callable<Object>() {
            @Override
            public Object call() {
                return Win32ShellFolder2.this.doGetColumnValue(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), n);
            }
        });
    }
    
    private native ShellFolderColumnInfo[] doGetColumnInfo(final long p0);
    
    private native Object doGetColumnValue(final long p0, final long p1, final int p2);
    
    private static native int compareIDsByColumn(final long p0, final long p1, final long p2, final int p3);
    
    @Override
    public void sortChildren(final List<? extends File> list) {
        ShellFolder.invoke((Callable<Object>)new Callable<Void>() {
            @Override
            public Void call() {
                Collections.sort((List<Object>)list, (Comparator<? super Object>)new ColumnComparator(Win32ShellFolder2.this, 0));
                return null;
            }
        });
    }
    
    static {
        initIDs();
        Win32ShellFolder2.smallSystemImages = new HashMap();
        Win32ShellFolder2.largeSystemImages = new HashMap();
        Win32ShellFolder2.smallLinkedSystemImages = new HashMap();
        Win32ShellFolder2.largeLinkedSystemImages = new HashMap();
    }
    
    public enum SystemIcon
    {
        IDI_APPLICATION(32512), 
        IDI_HAND(32513), 
        IDI_ERROR(32513), 
        IDI_QUESTION(32514), 
        IDI_EXCLAMATION(32515), 
        IDI_WARNING(32515), 
        IDI_ASTERISK(32516), 
        IDI_INFORMATION(32516), 
        IDI_WINLOGO(32517);
        
        private final int iconID;
        
        private SystemIcon(final int iconID) {
            this.iconID = iconID;
        }
        
        public int getIconID() {
            return this.iconID;
        }
    }
    
    static class FolderDisposer implements DisposerRecord
    {
        long absolutePIDL;
        long pIShellFolder;
        long relativePIDL;
        boolean disposed;
        
        @Override
        public void dispose() {
            if (this.disposed) {
                return;
            }
            ShellFolder.invoke((Callable<Object>)new Callable<Void>() {
                @Override
                public Void call() {
                    if (FolderDisposer.this.relativePIDL != 0L) {
                        Win32ShellFolder2.releasePIDL(FolderDisposer.this.relativePIDL);
                    }
                    if (FolderDisposer.this.absolutePIDL != 0L) {
                        Win32ShellFolder2.releasePIDL(FolderDisposer.this.absolutePIDL);
                    }
                    if (FolderDisposer.this.pIShellFolder != 0L) {
                        releaseIShellFolder(FolderDisposer.this.pIShellFolder);
                    }
                    return null;
                }
            });
            this.disposed = true;
        }
    }
    
    private static class ColumnComparator implements Comparator<File>
    {
        private final Win32ShellFolder2 shellFolder;
        private final int columnIdx;
        
        public ColumnComparator(final Win32ShellFolder2 shellFolder, final int columnIdx) {
            this.shellFolder = shellFolder;
            this.columnIdx = columnIdx;
        }
        
        @Override
        public int compare(final File file, final File file2) {
            final Integer n = ShellFolder.invoke((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() {
                    if (file instanceof Win32ShellFolder2 && file2 instanceof Win32ShellFolder2) {
                        return compareIDsByColumn(ColumnComparator.this.shellFolder.getIShellFolder(), ((Win32ShellFolder2)file).getRelativePIDL(), ((Win32ShellFolder2)file2).getRelativePIDL(), ColumnComparator.this.columnIdx);
                    }
                    return 0;
                }
            });
            return (n == null) ? 0 : n;
        }
    }
}
