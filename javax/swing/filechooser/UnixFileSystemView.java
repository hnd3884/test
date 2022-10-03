package javax.swing.filechooser;

import javax.swing.UIManager;
import java.text.MessageFormat;
import java.io.IOException;
import java.io.File;

class UnixFileSystemView extends FileSystemView
{
    private static final String newFolderString;
    private static final String newFolderNextString;
    
    @Override
    public File createNewFolder(final File file) throws IOException {
        if (file == null) {
            throw new IOException("Containing directory is null:");
        }
        File file2 = this.createFileObject(file, UnixFileSystemView.newFolderString);
        for (int n = 1; file2.exists() && n < 100; file2 = this.createFileObject(file, MessageFormat.format(UnixFileSystemView.newFolderNextString, new Integer(n))), ++n) {}
        if (file2.exists()) {
            throw new IOException("Directory already exists:" + file2.getAbsolutePath());
        }
        file2.mkdirs();
        return file2;
    }
    
    @Override
    public boolean isFileSystemRoot(final File file) {
        return file != null && file.getAbsolutePath().equals("/");
    }
    
    @Override
    public boolean isDrive(final File file) {
        return this.isFloppyDrive(file);
    }
    
    @Override
    public boolean isFloppyDrive(final File file) {
        return false;
    }
    
    @Override
    public boolean isComputerNode(final File file) {
        if (file != null) {
            final String parent = file.getParent();
            if (parent != null && parent.equals("/net")) {
                return true;
            }
        }
        return false;
    }
    
    static {
        newFolderString = UIManager.getString("FileChooser.other.newFolder");
        newFolderNextString = UIManager.getString("FileChooser.other.newFolder.subsequent");
    }
}
