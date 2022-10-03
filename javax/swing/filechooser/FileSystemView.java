package javax.swing.filechooser;

import java.util.ArrayList;
import java.io.IOException;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.io.FileNotFoundException;
import sun.awt.shell.ShellFolder;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.swing.UIManager;
import java.io.File;

public abstract class FileSystemView
{
    static FileSystemView windowsFileSystemView;
    static FileSystemView unixFileSystemView;
    static FileSystemView genericFileSystemView;
    private boolean useSystemExtensionHiding;
    
    public static FileSystemView getFileSystemView() {
        if (File.separatorChar == '\\') {
            if (FileSystemView.windowsFileSystemView == null) {
                FileSystemView.windowsFileSystemView = new WindowsFileSystemView();
            }
            return FileSystemView.windowsFileSystemView;
        }
        if (File.separatorChar == '/') {
            if (FileSystemView.unixFileSystemView == null) {
                FileSystemView.unixFileSystemView = new UnixFileSystemView();
            }
            return FileSystemView.unixFileSystemView;
        }
        if (FileSystemView.genericFileSystemView == null) {
            FileSystemView.genericFileSystemView = new GenericFileSystemView();
        }
        return FileSystemView.genericFileSystemView;
    }
    
    public FileSystemView() {
        this.useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            final /* synthetic */ WeakReference val$weakReference = new WeakReference((T)this);
            
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final FileSystemView fileSystemView = (FileSystemView)this.val$weakReference.get();
                if (fileSystemView == null) {
                    UIManager.removePropertyChangeListener(this);
                }
                else if (propertyChangeEvent.getPropertyName().equals("lookAndFeel")) {
                    fileSystemView.useSystemExtensionHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
                }
            }
        });
    }
    
    public boolean isRoot(final File file) {
        if (file == null || !file.isAbsolute()) {
            return false;
        }
        final File[] roots = this.getRoots();
        for (int length = roots.length, i = 0; i < length; ++i) {
            if (roots[i].equals(file)) {
                return true;
            }
        }
        return false;
    }
    
    public Boolean isTraversable(final File file) {
        return file.isDirectory();
    }
    
    public String getSystemDisplayName(final File file) {
        if (file == null) {
            return null;
        }
        String s = file.getName();
        if (!s.equals("..") && !s.equals(".") && (this.useSystemExtensionHiding || !this.isFileSystem(file) || this.isFileSystemRoot(file))) {
            if (!(file instanceof ShellFolder)) {
                if (!file.exists()) {
                    return s;
                }
            }
            try {
                s = this.getShellFolder(file).getDisplayName();
            }
            catch (final FileNotFoundException ex) {
                return null;
            }
            if (s == null || s.length() == 0) {
                s = file.getPath();
            }
        }
        return s;
    }
    
    public String getSystemTypeDescription(final File file) {
        return null;
    }
    
    public Icon getSystemIcon(final File file) {
        if (file == null) {
            return null;
        }
        ShellFolder shellFolder;
        try {
            shellFolder = this.getShellFolder(file);
        }
        catch (final FileNotFoundException ex) {
            return null;
        }
        final Image icon = shellFolder.getIcon(false);
        if (icon != null) {
            return new ImageIcon(icon, shellFolder.getFolderType());
        }
        return UIManager.getIcon(file.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
    }
    
    public boolean isParent(final File file, final File file2) {
        if (file == null || file2 == null) {
            return false;
        }
        if (!(file instanceof ShellFolder)) {
            return file.equals(file2.getParentFile());
        }
        final File parentFile = file2.getParentFile();
        if (parentFile != null && parentFile.equals(file)) {
            return true;
        }
        final File[] files = this.getFiles(file, false);
        for (int length = files.length, i = 0; i < length; ++i) {
            if (file2.equals(files[i])) {
                return true;
            }
        }
        return false;
    }
    
    public File getChild(final File file, final String s) {
        if (file instanceof ShellFolder) {
            for (final File file2 : this.getFiles(file, false)) {
                if (file2.getName().equals(s)) {
                    return file2;
                }
            }
        }
        return this.createFileObject(file, s);
    }
    
    public boolean isFileSystem(final File file) {
        if (file instanceof ShellFolder) {
            final ShellFolder shellFolder = (ShellFolder)file;
            return shellFolder.isFileSystem() && (!shellFolder.isLink() || !shellFolder.isDirectory());
        }
        return true;
    }
    
    public abstract File createNewFolder(final File p0) throws IOException;
    
    public boolean isHiddenFile(final File file) {
        return file.isHidden();
    }
    
    public boolean isFileSystemRoot(final File file) {
        return ShellFolder.isFileSystemRoot(file);
    }
    
    public boolean isDrive(final File file) {
        return false;
    }
    
    public boolean isFloppyDrive(final File file) {
        return false;
    }
    
    public boolean isComputerNode(final File file) {
        return ShellFolder.isComputerNode(file);
    }
    
    public File[] getRoots() {
        final File[] array = (File[])ShellFolder.get("roots");
        for (int i = 0; i < array.length; ++i) {
            if (this.isFileSystemRoot(array[i])) {
                array[i] = this.createFileSystemRoot(array[i]);
            }
        }
        return array;
    }
    
    public File getHomeDirectory() {
        return this.createFileObject(System.getProperty("user.home"));
    }
    
    public File getDefaultDirectory() {
        File fileSystemRoot = (File)ShellFolder.get("fileChooserDefaultFolder");
        if (this.isFileSystemRoot(fileSystemRoot)) {
            fileSystemRoot = this.createFileSystemRoot(fileSystemRoot);
        }
        return fileSystemRoot;
    }
    
    public File createFileObject(final File file, final String s) {
        if (file == null) {
            return new File(s);
        }
        return new File(file, s);
    }
    
    public File createFileObject(final String s) {
        File fileSystemRoot = new File(s);
        if (this.isFileSystemRoot(fileSystemRoot)) {
            fileSystemRoot = this.createFileSystemRoot(fileSystemRoot);
        }
        return fileSystemRoot;
    }
    
    public File[] getFiles(File shellFolder, final boolean b) {
        final ArrayList list = new ArrayList();
        if (!(shellFolder instanceof ShellFolder)) {
            try {
                shellFolder = this.getShellFolder(shellFolder);
            }
            catch (final FileNotFoundException ex) {
                return new File[0];
            }
        }
        final File[] listFiles = ((ShellFolder)shellFolder).listFiles(!b);
        if (listFiles == null) {
            return new File[0];
        }
        for (File file : listFiles) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            Label_0164: {
                if (!(file instanceof ShellFolder)) {
                    if (this.isFileSystemRoot(file)) {
                        file = this.createFileSystemRoot(file);
                    }
                    try {
                        file = ShellFolder.getShellFolder(file);
                    }
                    catch (final FileNotFoundException ex2) {
                        break Label_0164;
                    }
                    catch (final InternalError internalError) {
                        break Label_0164;
                    }
                }
                if (!b || !this.isHiddenFile(file)) {
                    list.add(file);
                }
            }
        }
        return (File[])list.toArray(new File[list.size()]);
    }
    
    public File getParentDirectory(final File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        ShellFolder shellFolder;
        try {
            shellFolder = this.getShellFolder(file);
        }
        catch (final FileNotFoundException ex) {
            return null;
        }
        final File parentFile = shellFolder.getParentFile();
        if (parentFile == null) {
            return null;
        }
        if (this.isFileSystem(parentFile)) {
            File fileSystemRoot = parentFile;
            if (!fileSystemRoot.exists()) {
                final File parentFile2 = parentFile.getParentFile();
                if (parentFile2 == null || !this.isFileSystem(parentFile2)) {
                    fileSystemRoot = this.createFileSystemRoot(fileSystemRoot);
                }
            }
            return fileSystemRoot;
        }
        return parentFile;
    }
    
    ShellFolder getShellFolder(File fileSystemRoot) throws FileNotFoundException {
        if (!(fileSystemRoot instanceof ShellFolder) && !(fileSystemRoot instanceof FileSystemRoot) && this.isFileSystemRoot(fileSystemRoot)) {
            fileSystemRoot = this.createFileSystemRoot(fileSystemRoot);
        }
        try {
            return ShellFolder.getShellFolder(fileSystemRoot);
        }
        catch (final InternalError internalError) {
            System.err.println("FileSystemView.getShellFolder: f=" + fileSystemRoot);
            internalError.printStackTrace();
            return null;
        }
    }
    
    protected File createFileSystemRoot(final File file) {
        return new FileSystemRoot(file);
    }
    
    static {
        FileSystemView.windowsFileSystemView = null;
        FileSystemView.unixFileSystemView = null;
        FileSystemView.genericFileSystemView = null;
    }
    
    static class FileSystemRoot extends File
    {
        public FileSystemRoot(final File file) {
            super(file, "");
        }
        
        public FileSystemRoot(final String s) {
            super(s);
        }
        
        @Override
        public boolean isDirectory() {
            return true;
        }
        
        @Override
        public String getName() {
            return this.getPath();
        }
    }
}
