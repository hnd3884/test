package sun.awt.shell;

import java.io.ObjectStreamException;
import java.io.File;

class DefaultShellFolder extends ShellFolder
{
    DefaultShellFolder(final ShellFolder shellFolder, final File file) {
        super(shellFolder, file.getAbsolutePath());
    }
    
    @Override
    protected Object writeReplace() throws ObjectStreamException {
        return new File(this.getPath());
    }
    
    @Override
    public File[] listFiles() {
        final File[] listFiles = super.listFiles();
        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; ++i) {
                listFiles[i] = new DefaultShellFolder(this, listFiles[i]);
            }
        }
        return listFiles;
    }
    
    @Override
    public boolean isLink() {
        return false;
    }
    
    @Override
    public boolean isHidden() {
        final String name = this.getName();
        return name.length() > 0 && name.charAt(0) == '.';
    }
    
    @Override
    public ShellFolder getLinkLocation() {
        return null;
    }
    
    @Override
    public String getDisplayName() {
        return this.getName();
    }
    
    @Override
    public String getFolderType() {
        if (this.isDirectory()) {
            return "File Folder";
        }
        return "File";
    }
    
    @Override
    public String getExecutableType() {
        return null;
    }
}
