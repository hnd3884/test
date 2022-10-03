package sun.awt.shell;

import java.util.concurrent.Callable;
import java.io.FileNotFoundException;
import java.io.File;

class ShellFolderManager
{
    public ShellFolder createShellFolder(final File file) throws FileNotFoundException {
        return new DefaultShellFolder((ShellFolder)null, file);
    }
    
    public Object get(final String s) {
        if (s.equals("fileChooserDefaultFolder")) {
            final File file = new File(System.getProperty("user.home"));
            try {
                return this.createShellFolder(file);
            }
            catch (final FileNotFoundException ex) {
                return file;
            }
        }
        if (s.equals("roots")) {
            return File.listRoots();
        }
        if (s.equals("fileChooserComboBoxFolders")) {
            return this.get("roots");
        }
        if (s.equals("fileChooserShortcutPanelFolders")) {
            return new File[] { (File)this.get("fileChooserDefaultFolder") };
        }
        return null;
    }
    
    public boolean isComputerNode(final File file) {
        return false;
    }
    
    public boolean isFileSystemRoot(final File file) {
        return (!(file instanceof ShellFolder) || ((ShellFolder)file).isFileSystem()) && file.getParentFile() == null;
    }
    
    protected ShellFolder.Invoker createInvoker() {
        return new DirectInvoker();
    }
    
    private static class DirectInvoker implements ShellFolder.Invoker
    {
        @Override
        public <T> T invoke(final Callable<T> callable) throws Exception {
            return callable.call();
        }
    }
}
