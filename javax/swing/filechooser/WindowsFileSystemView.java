package javax.swing.filechooser;

import javax.swing.UIManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

class WindowsFileSystemView extends FileSystemView
{
    private static final String newFolderString;
    private static final String newFolderNextString;
    
    @Override
    public Boolean isTraversable(final File file) {
        return this.isFileSystemRoot(file) || this.isComputerNode(file) || file.isDirectory();
    }
    
    @Override
    public File getChild(final File file, final String s) {
        if (s.startsWith("\\") && !s.startsWith("\\\\") && this.isFileSystem(file)) {
            final String absolutePath = file.getAbsolutePath();
            if (absolutePath.length() >= 2 && absolutePath.charAt(1) == ':' && Character.isLetter(absolutePath.charAt(0))) {
                return this.createFileObject(absolutePath.substring(0, 2) + s);
            }
        }
        return super.getChild(file, s);
    }
    
    @Override
    public String getSystemTypeDescription(final File file) {
        if (file == null) {
            return null;
        }
        try {
            return this.getShellFolder(file).getFolderType();
        }
        catch (final FileNotFoundException ex) {
            return null;
        }
    }
    
    @Override
    public File getHomeDirectory() {
        final File[] roots = this.getRoots();
        return (roots.length == 0) ? null : roots[0];
    }
    
    @Override
    public File createNewFolder(final File file) throws IOException {
        if (file == null) {
            throw new IOException("Containing directory is null:");
        }
        File file2 = this.createFileObject(file, WindowsFileSystemView.newFolderString);
        for (int n = 2; file2.exists() && n < 100; file2 = this.createFileObject(file, MessageFormat.format(WindowsFileSystemView.newFolderNextString, new Integer(n))), ++n) {}
        if (file2.exists()) {
            throw new IOException("Directory already exists:" + file2.getAbsolutePath());
        }
        file2.mkdirs();
        return file2;
    }
    
    @Override
    public boolean isDrive(final File file) {
        return this.isFileSystemRoot(file);
    }
    
    @Override
    public boolean isFloppyDrive(final File file) {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return file.getAbsolutePath();
            }
        });
        return s != null && (s.equals("A:\\") || s.equals("B:\\"));
    }
    
    @Override
    public File createFileObject(String s) {
        if (s.length() >= 2 && s.charAt(1) == ':' && Character.isLetter(s.charAt(0))) {
            if (s.length() == 2) {
                s += "\\";
            }
            else if (s.charAt(2) != '\\') {
                s = s.substring(0, 2) + "\\" + s.substring(2);
            }
        }
        return super.createFileObject(s);
    }
    
    @Override
    protected File createFileSystemRoot(final File file) {
        return new FileSystemRoot(file) {
            @Override
            public boolean exists() {
                return true;
            }
        };
    }
    
    static {
        newFolderString = UIManager.getString("FileChooser.win32.newFolder");
        newFolderNextString = UIManager.getString("FileChooser.win32.newFolder.subsequent");
    }
}
