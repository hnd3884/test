package sun.awt.shell;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Callable;
import sun.misc.ThreadGroupUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import sun.awt.windows.WToolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Stream;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Arrays;
import sun.awt.OSInfo;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.File;
import java.awt.Image;
import sun.util.logging.PlatformLogger;

public class Win32ShellFolderManager2 extends ShellFolderManager
{
    private static final PlatformLogger log;
    private static final int VIEW_LIST = 2;
    private static final int VIEW_DETAILS = 3;
    private static final int VIEW_PARENTFOLDER = 8;
    private static final int VIEW_NEWFOLDER = 11;
    private static final Image[] STANDARD_VIEW_BUTTONS;
    private static Win32ShellFolder2 desktop;
    private static Win32ShellFolder2 drives;
    private static Win32ShellFolder2 recent;
    private static Win32ShellFolder2 network;
    private static Win32ShellFolder2 personal;
    private static File[] roots;
    private static List topFolderList;
    
    @Override
    public ShellFolder createShellFolder(final File file) throws FileNotFoundException {
        try {
            return createShellFolder(getDesktop(), file);
        }
        catch (final InterruptedException ex) {
            throw new FileNotFoundException("Execution was interrupted");
        }
    }
    
    static Win32ShellFolder2 createShellFolder(final Win32ShellFolder2 win32ShellFolder2, final File file) throws FileNotFoundException, InterruptedException {
        long displayName;
        try {
            displayName = win32ShellFolder2.parseDisplayName(file.getCanonicalPath());
        }
        catch (final IOException ex) {
            displayName = 0L;
        }
        if (displayName == 0L) {
            throw new FileNotFoundException("File " + file.getAbsolutePath() + " not found");
        }
        try {
            return createShellFolderFromRelativePIDL(win32ShellFolder2, displayName);
        }
        finally {
            Win32ShellFolder2.releasePIDL(displayName);
        }
    }
    
    static Win32ShellFolder2 createShellFolderFromRelativePIDL(Win32ShellFolder2 win32ShellFolder2, long nextPIDLEntry) throws InterruptedException {
        while (nextPIDLEntry != 0L) {
            final long copyFirstPIDLEntry = Win32ShellFolder2.copyFirstPIDLEntry(nextPIDLEntry);
            if (copyFirstPIDLEntry == 0L) {
                break;
            }
            win32ShellFolder2 = new Win32ShellFolder2(win32ShellFolder2, copyFirstPIDLEntry);
            nextPIDLEntry = Win32ShellFolder2.getNextPIDLEntry(nextPIDLEntry);
        }
        return win32ShellFolder2;
    }
    
    private static Image getStandardViewButton(final int n) {
        final Image image = Win32ShellFolderManager2.STANDARD_VIEW_BUTTONS[n];
        if (image != null) {
            return image;
        }
        final BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
        bufferedImage.setRGB(0, 0, 16, 16, Win32ShellFolder2.getStandardViewButton0(n), 0, 16);
        return Win32ShellFolderManager2.STANDARD_VIEW_BUTTONS[n] = bufferedImage;
    }
    
    static Win32ShellFolder2 getDesktop() {
        if (Win32ShellFolderManager2.desktop == null) {
            try {
                Win32ShellFolderManager2.desktop = new Win32ShellFolder2(0);
            }
            catch (final SecurityException ex) {}
            catch (final IOException | InterruptedException ex2) {
                if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                    Win32ShellFolderManager2.log.warning("Cannot access 'Desktop'", (Throwable)ex2);
                }
            }
        }
        return Win32ShellFolderManager2.desktop;
    }
    
    static Win32ShellFolder2 getDrives() {
        if (Win32ShellFolderManager2.drives == null) {
            try {
                Win32ShellFolderManager2.drives = new Win32ShellFolder2(17);
            }
            catch (final SecurityException ex) {}
            catch (final IOException | InterruptedException ex2) {
                if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                    Win32ShellFolderManager2.log.warning("Cannot access 'Drives'", (Throwable)ex2);
                }
            }
        }
        return Win32ShellFolderManager2.drives;
    }
    
    static Win32ShellFolder2 getRecent() {
        if (Win32ShellFolderManager2.recent == null) {
            try {
                final String fileSystemPath = Win32ShellFolder2.getFileSystemPath(8);
                if (fileSystemPath != null) {
                    Win32ShellFolderManager2.recent = createShellFolder(getDesktop(), new File(fileSystemPath));
                }
            }
            catch (final SecurityException ex) {}
            catch (final InterruptedException | IOException ex2) {
                if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                    Win32ShellFolderManager2.log.warning("Cannot access 'Recent'", (Throwable)ex2);
                }
            }
        }
        return Win32ShellFolderManager2.recent;
    }
    
    static Win32ShellFolder2 getNetwork() {
        if (Win32ShellFolderManager2.network == null) {
            try {
                Win32ShellFolderManager2.network = new Win32ShellFolder2(18);
            }
            catch (final SecurityException ex) {}
            catch (final IOException | InterruptedException ex2) {
                if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                    Win32ShellFolderManager2.log.warning("Cannot access 'Network'", (Throwable)ex2);
                }
            }
        }
        return Win32ShellFolderManager2.network;
    }
    
    static Win32ShellFolder2 getPersonal() {
        if (Win32ShellFolderManager2.personal == null) {
            try {
                final String fileSystemPath = Win32ShellFolder2.getFileSystemPath(5);
                if (fileSystemPath != null) {
                    Win32ShellFolderManager2.personal = getDesktop().getChildByPath(fileSystemPath);
                    if (Win32ShellFolderManager2.personal == null) {
                        Win32ShellFolderManager2.personal = createShellFolder(getDesktop(), new File(fileSystemPath));
                    }
                    if (Win32ShellFolderManager2.personal != null) {
                        Win32ShellFolderManager2.personal.setIsPersonal();
                    }
                }
            }
            catch (final SecurityException ex) {}
            catch (final InterruptedException | IOException ex2) {
                if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                    Win32ShellFolderManager2.log.warning("Cannot access 'Personal'", (Throwable)ex2);
                }
            }
        }
        return Win32ShellFolderManager2.personal;
    }
    
    @Override
    public Object get(final String s) {
        if (s.equals("fileChooserDefaultFolder")) {
            Win32ShellFolder2 win32ShellFolder2 = getPersonal();
            if (win32ShellFolder2 == null) {
                win32ShellFolder2 = getDesktop();
            }
            return checkFile(win32ShellFolder2);
        }
        if (s.equals("roots")) {
            if (Win32ShellFolderManager2.roots == null) {
                final Win32ShellFolder2 desktop = getDesktop();
                if (desktop != null) {
                    Win32ShellFolderManager2.roots = new File[] { desktop };
                }
                else {
                    Win32ShellFolderManager2.roots = (File[])super.get(s);
                }
            }
            return checkFiles(Win32ShellFolderManager2.roots);
        }
        if (s.equals("fileChooserComboBoxFolders")) {
            final Win32ShellFolder2 desktop2 = getDesktop();
            if (desktop2 != null && checkFile(desktop2) != null) {
                final ArrayList list = new ArrayList();
                final Win32ShellFolder2 drives = getDrives();
                final Win32ShellFolder2 recent = getRecent();
                if (recent != null && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_2000) >= 0) {
                    list.add(recent);
                }
                list.add(desktop2);
                final File[] checkFiles = checkFiles(desktop2.listFiles());
                Arrays.sort(checkFiles);
                final File[] array = checkFiles;
                for (int length = array.length, i = 0; i < length; ++i) {
                    final Win32ShellFolder2 win32ShellFolder3 = (Win32ShellFolder2)array[i];
                    if (!win32ShellFolder3.isFileSystem() || (win32ShellFolder3.isDirectory() && !win32ShellFolder3.isLink())) {
                        list.add(win32ShellFolder3);
                        if (win32ShellFolder3.equals(drives)) {
                            final File[] checkFiles2 = checkFiles(win32ShellFolder3.listFiles());
                            if (checkFiles2 != null && checkFiles2.length > 0) {
                                final List<File> list2 = Arrays.asList(checkFiles2);
                                win32ShellFolder3.sortChildren(list2);
                                list.addAll(list2);
                            }
                        }
                    }
                }
                return checkFiles(list);
            }
            return super.get(s);
        }
        else {
            if (s.equals("fileChooserShortcutPanelFolders")) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                final ArrayList list3 = new ArrayList();
                int n = 0;
                Object desktopProperty;
                do {
                    desktopProperty = defaultToolkit.getDesktopProperty("win.comdlg.placesBarPlace" + n++);
                    try {
                        if (desktopProperty instanceof Integer) {
                            list3.add(new Win32ShellFolder2((int)desktopProperty));
                        }
                        else {
                            if (!(desktopProperty instanceof String)) {
                                continue;
                            }
                            list3.add(this.createShellFolder(new File((String)desktopProperty)));
                        }
                    }
                    catch (final IOException ex) {
                        if (!Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                            continue;
                        }
                        Win32ShellFolderManager2.log.warning("Cannot read value = " + desktopProperty, ex);
                    }
                    catch (final InterruptedException ex2) {
                        if (Win32ShellFolderManager2.log.isLoggable(PlatformLogger.Level.WARNING)) {
                            Win32ShellFolderManager2.log.warning("Cannot read value = " + desktopProperty, ex2);
                        }
                        return new File[0];
                    }
                } while (desktopProperty != null);
                if (list3.size() == 0) {
                    for (final File file : new File[] { getRecent(), getDesktop(), getPersonal(), getDrives(), getNetwork() }) {
                        if (file != null) {
                            list3.add(file);
                        }
                    }
                }
                return checkFiles(list3);
            }
            if (s.startsWith("fileChooserIcon ")) {
                final String substring = s.substring(s.indexOf(" ") + 1);
                int n2;
                if (substring.equals("ListView") || substring.equals("ViewMenu")) {
                    n2 = 2;
                }
                else if (substring.equals("DetailsView")) {
                    n2 = 3;
                }
                else if (substring.equals("UpFolder")) {
                    n2 = 8;
                }
                else {
                    if (!substring.equals("NewFolder")) {
                        return null;
                    }
                    n2 = 11;
                }
                return getStandardViewButton(n2);
            }
            if (s.startsWith("optionPaneIcon ")) {
                Win32ShellFolder2.SystemIcon systemIcon;
                if (s == "optionPaneIcon Error") {
                    systemIcon = Win32ShellFolder2.SystemIcon.IDI_ERROR;
                }
                else if (s == "optionPaneIcon Information") {
                    systemIcon = Win32ShellFolder2.SystemIcon.IDI_INFORMATION;
                }
                else if (s == "optionPaneIcon Question") {
                    systemIcon = Win32ShellFolder2.SystemIcon.IDI_QUESTION;
                }
                else {
                    if (s != "optionPaneIcon Warning") {
                        return null;
                    }
                    systemIcon = Win32ShellFolder2.SystemIcon.IDI_EXCLAMATION;
                }
                return Win32ShellFolder2.getSystemIcon(systemIcon);
            }
            if (s.startsWith("shell32Icon ") || s.startsWith("shell32LargeIcon ")) {
                final String substring2 = s.substring(s.indexOf(" ") + 1);
                try {
                    final int int1 = Integer.parseInt(substring2);
                    if (int1 >= 0) {
                        return Win32ShellFolder2.getShell32Icon(int1, s.startsWith("shell32LargeIcon "));
                    }
                }
                catch (final NumberFormatException ex3) {}
            }
            return null;
        }
    }
    
    private static File checkFile(final File file) {
        final SecurityManager securityManager = System.getSecurityManager();
        return (securityManager == null || file == null) ? file : checkFile(file, securityManager);
    }
    
    private static File checkFile(final File file, final SecurityManager securityManager) {
        try {
            securityManager.checkRead(file.getPath());
            if (file instanceof Win32ShellFolder2) {
                final Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)file;
                if (win32ShellFolder2.isLink()) {
                    final Win32ShellFolder2 win32ShellFolder3 = (Win32ShellFolder2)win32ShellFolder2.getLinkLocation();
                    if (win32ShellFolder3 != null) {
                        securityManager.checkRead(win32ShellFolder3.getPath());
                    }
                }
            }
            return file;
        }
        catch (final SecurityException ex) {
            return null;
        }
    }
    
    static File[] checkFiles(final File[] array) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null || array == null || array.length == 0) {
            return array;
        }
        return checkFiles(Arrays.stream(array), securityManager);
    }
    
    private static File[] checkFiles(final List<File> list) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null || list.isEmpty()) {
            return list.toArray(new File[list.size()]);
        }
        return checkFiles(list.stream(), securityManager);
    }
    
    private static File[] checkFiles(final Stream<File> stream, final SecurityManager securityManager) {
        return stream.filter(file -> checkFile(file, securityManager2) != null).toArray(File[]::new);
    }
    
    @Override
    public boolean isComputerNode(final File file) {
        if (file != null && file == getDrives()) {
            return true;
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return file.getAbsolutePath();
            }
        });
        return s.startsWith("\\\\") && s.indexOf("\\", 2) < 0;
    }
    
    @Override
    public boolean isFileSystemRoot(final File file) {
        if (file == null) {
            return false;
        }
        final Win32ShellFolder2 drives = getDrives();
        if (file instanceof Win32ShellFolder2) {
            final Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)file;
            if (!win32ShellFolder2.isFileSystem()) {
                return false;
            }
            if (win32ShellFolder2.parent != null) {
                return win32ShellFolder2.parent.equals(drives);
            }
        }
        final String path = file.getPath();
        if (path.length() != 3 || path.charAt(1) != ':') {
            return false;
        }
        final File[] listFiles = drives.listFiles();
        return listFiles != null && Arrays.asList(listFiles).contains(file);
    }
    
    static int compareShellFolders(final Win32ShellFolder2 win32ShellFolder2, final Win32ShellFolder2 win32ShellFolder3) {
        final boolean special = win32ShellFolder2.isSpecial();
        final boolean special2 = win32ShellFolder3.isSpecial();
        if (special || special2) {
            if (Win32ShellFolderManager2.topFolderList == null) {
                final ArrayList topFolderList = new ArrayList();
                topFolderList.add(getPersonal());
                topFolderList.add(getDesktop());
                topFolderList.add(getDrives());
                topFolderList.add(getNetwork());
                Win32ShellFolderManager2.topFolderList = topFolderList;
            }
            final int index = Win32ShellFolderManager2.topFolderList.indexOf(win32ShellFolder2);
            final int index2 = Win32ShellFolderManager2.topFolderList.indexOf(win32ShellFolder3);
            if (index >= 0 && index2 >= 0) {
                return index - index2;
            }
            if (index >= 0) {
                return -1;
            }
            if (index2 >= 0) {
                return 1;
            }
        }
        if (special && !special2) {
            return -1;
        }
        if (special2 && !special) {
            return 1;
        }
        return compareNames(win32ShellFolder2.getAbsolutePath(), win32ShellFolder3.getAbsolutePath());
    }
    
    static int compareNames(final String s, final String s2) {
        final int compareToIgnoreCase = s.compareToIgnoreCase(s2);
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        return s.compareTo(s2);
    }
    
    @Override
    protected ShellFolder.Invoker createInvoker() {
        return new ComInvoker();
    }
    
    static native void initializeCom();
    
    static native void uninitializeCom();
    
    static {
        log = PlatformLogger.getLogger("sun.awt.shell.Win32ShellFolderManager2");
        WToolkit.loadLibraries();
        STANDARD_VIEW_BUTTONS = new Image[12];
        Win32ShellFolderManager2.topFolderList = null;
    }
    
    private static class ComInvoker extends ThreadPoolExecutor implements ThreadFactory, ShellFolder.Invoker
    {
        private static Thread comThread;
        
        private ComInvoker() {
            super(1, 1, 0L, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>());
            this.allowCoreThreadTimeOut(false);
            this.setThreadFactory(this);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                final /* synthetic */ Runnable val$shutdownHook = new Runnable(this) {
                    @Override
                    public void run() {
                        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>(this) {
                            @Override
                            public Void run() {
                                ComInvoker.this.shutdownNow();
                                return null;
                            }
                        });
                    }
                };
                
                @Override
                public Void run() {
                    Runtime.getRuntime().addShutdownHook(new Thread(this.val$shutdownHook));
                    return null;
                }
            });
        }
        
        @Override
        public synchronized Thread newThread(final Runnable runnable) {
            return ComInvoker.comThread = AccessController.doPrivileged(() -> {
                final Object o = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Win32ShellFolderManager2.initializeCom();
                            runnable3.run();
                        }
                        finally {
                            Win32ShellFolderManager2.uninitializeCom();
                        }
                    }
                };
                final Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), runnable2, "Swing-Shell");
                thread.setDaemon(true);
                return thread;
            });
        }
        
        @Override
        public <T> T invoke(final Callable<T> callable) throws Exception {
            if (Thread.currentThread() == ComInvoker.comThread) {
                return callable.call();
            }
            Future<T> submit;
            try {
                submit = this.submit(callable);
            }
            catch (final RejectedExecutionException ex) {
                throw new InterruptedException(ex.getMessage());
            }
            try {
                return submit.get();
            }
            catch (final InterruptedException ex2) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        submit.cancel(true);
                        return null;
                    }
                });
                throw ex2;
            }
            catch (final ExecutionException ex3) {
                final Throwable cause = ex3.getCause();
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException("Unexpected error", cause);
            }
        }
    }
}
