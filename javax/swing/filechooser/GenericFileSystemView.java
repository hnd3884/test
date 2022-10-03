package javax.swing.filechooser;

import javax.swing.UIManager;
import java.io.IOException;
import java.io.File;

class GenericFileSystemView extends FileSystemView
{
    private static final String newFolderString;
    
    @Override
    public File createNewFolder(final File file) throws IOException {
        if (file == null) {
            throw new IOException("Containing directory is null:");
        }
        final File fileObject = this.createFileObject(file, GenericFileSystemView.newFolderString);
        if (fileObject.exists()) {
            throw new IOException("Directory already exists:" + fileObject.getAbsolutePath());
        }
        fileObject.mkdirs();
        return fileObject;
    }
    
    static {
        newFolderString = UIManager.getString("FileChooser.other.newFolder");
    }
}
