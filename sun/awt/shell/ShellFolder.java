package sun.awt.shell;

import java.awt.Toolkit;
import java.util.Date;
import javax.swing.SortOrder;
import java.util.Iterator;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.List;
import java.io.IOException;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.io.ObjectStreamException;
import java.util.Comparator;
import java.io.File;

public abstract class ShellFolder extends File
{
    private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
    private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
    private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
    protected ShellFolder parent;
    private static final ShellFolderManager shellFolderManager;
    private static final Invoker invoker;
    private static final Comparator DEFAULT_COMPARATOR;
    private static final Comparator<File> FILE_COMPARATOR;
    
    ShellFolder(final ShellFolder parent, final String s) {
        super((s != null) ? s : "ShellFolder");
        this.parent = parent;
    }
    
    public boolean isFileSystem() {
        return !this.getPath().startsWith("ShellFolder");
    }
    
    protected abstract Object writeReplace() throws ObjectStreamException;
    
    @Override
    public String getParent() {
        if (this.parent == null && this.isFileSystem()) {
            return super.getParent();
        }
        if (this.parent != null) {
            return this.parent.getPath();
        }
        return null;
    }
    
    @Override
    public File getParentFile() {
        if (this.parent != null) {
            return this.parent;
        }
        if (this.isFileSystem()) {
            return super.getParentFile();
        }
        return null;
    }
    
    @Override
    public File[] listFiles() {
        return this.listFiles(true);
    }
    
    public File[] listFiles(final boolean b) {
        File[] listFiles = super.listFiles();
        if (!b) {
            final Vector vector = new Vector();
            for (int n = (listFiles == null) ? 0 : listFiles.length, i = 0; i < n; ++i) {
                if (!listFiles[i].isHidden()) {
                    vector.addElement(listFiles[i]);
                }
            }
            listFiles = vector.toArray(new File[vector.size()]);
        }
        return listFiles;
    }
    
    public abstract boolean isLink();
    
    public abstract ShellFolder getLinkLocation() throws FileNotFoundException;
    
    public abstract String getDisplayName();
    
    public abstract String getFolderType();
    
    public abstract String getExecutableType();
    
    @Override
    public int compareTo(final File file) {
        if (file == null || !(file instanceof ShellFolder) || (file instanceof ShellFolder && ((ShellFolder)file).isFileSystem())) {
            if (this.isFileSystem()) {
                return super.compareTo(file);
            }
            return -1;
        }
        else {
            if (this.isFileSystem()) {
                return 1;
            }
            return this.getName().compareTo(file.getName());
        }
    }
    
    public Image getIcon(final boolean b) {
        return null;
    }
    
    public static ShellFolder getShellFolder(final File file) throws FileNotFoundException {
        if (file instanceof ShellFolder) {
            return (ShellFolder)file;
        }
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return ShellFolder.shellFolderManager.createShellFolder(file);
    }
    
    public static Object get(final String s) {
        return ShellFolder.shellFolderManager.get(s);
    }
    
    public static boolean isComputerNode(final File file) {
        return ShellFolder.shellFolderManager.isComputerNode(file);
    }
    
    public static boolean isFileSystemRoot(final File file) {
        return ShellFolder.shellFolderManager.isFileSystemRoot(file);
    }
    
    public static File getNormalizedFile(final File file) throws IOException {
        final File canonicalFile = file.getCanonicalFile();
        if (file.equals(canonicalFile)) {
            return canonicalFile;
        }
        return new File(file.toURI().normalize());
    }
    
    public static void sort(final List<? extends File> list) {
        if (list == null || list.size() <= 1) {
            return;
        }
        invoke((Callable<Object>)new Callable<Void>() {
            @Override
            public Void call() {
                ShellFolder shellFolder = null;
                for (final File file : list) {
                    final File parentFile = file.getParentFile();
                    if (parentFile == null || !(file instanceof ShellFolder)) {
                        shellFolder = null;
                        break;
                    }
                    if (shellFolder == null) {
                        shellFolder = (ShellFolder)parentFile;
                    }
                    else {
                        if (shellFolder != parentFile && !shellFolder.equals(parentFile)) {
                            shellFolder = null;
                            break;
                        }
                        continue;
                    }
                }
                if (shellFolder instanceof ShellFolder) {
                    shellFolder.sortChildren(list);
                }
                else {
                    Collections.sort((List<Object>)list, ShellFolder.FILE_COMPARATOR);
                }
                return null;
            }
        });
    }
    
    public void sortChildren(final List<? extends File> list) {
        invoke((Callable<Object>)new Callable<Void>() {
            @Override
            public Void call() {
                Collections.sort((List<Object>)list, ShellFolder.FILE_COMPARATOR);
                return null;
            }
        });
    }
    
    @Override
    public boolean isAbsolute() {
        return !this.isFileSystem() || super.isAbsolute();
    }
    
    @Override
    public File getAbsoluteFile() {
        return this.isFileSystem() ? super.getAbsoluteFile() : this;
    }
    
    @Override
    public boolean canRead() {
        return !this.isFileSystem() || super.canRead();
    }
    
    @Override
    public boolean canWrite() {
        return this.isFileSystem() && super.canWrite();
    }
    
    @Override
    public boolean exists() {
        return !this.isFileSystem() || isFileSystemRoot(this) || super.exists();
    }
    
    @Override
    public boolean isDirectory() {
        return !this.isFileSystem() || super.isDirectory();
    }
    
    @Override
    public boolean isFile() {
        return this.isFileSystem() ? super.isFile() : (!this.isDirectory());
    }
    
    @Override
    public long lastModified() {
        return this.isFileSystem() ? super.lastModified() : 0L;
    }
    
    @Override
    public long length() {
        return this.isFileSystem() ? super.length() : 0L;
    }
    
    @Override
    public boolean createNewFile() throws IOException {
        return this.isFileSystem() && super.createNewFile();
    }
    
    @Override
    public boolean delete() {
        return this.isFileSystem() && super.delete();
    }
    
    @Override
    public void deleteOnExit() {
        if (this.isFileSystem()) {
            super.deleteOnExit();
        }
    }
    
    @Override
    public boolean mkdir() {
        return this.isFileSystem() && super.mkdir();
    }
    
    @Override
    public boolean mkdirs() {
        return this.isFileSystem() && super.mkdirs();
    }
    
    @Override
    public boolean renameTo(final File file) {
        return this.isFileSystem() && super.renameTo(file);
    }
    
    @Override
    public boolean setLastModified(final long lastModified) {
        return this.isFileSystem() && super.setLastModified(lastModified);
    }
    
    @Override
    public boolean setReadOnly() {
        return this.isFileSystem() && super.setReadOnly();
    }
    
    @Override
    public String toString() {
        return this.isFileSystem() ? super.toString() : this.getDisplayName();
    }
    
    public static ShellFolderColumnInfo[] getFolderColumns(final File file) {
        ShellFolderColumnInfo[] folderColumns = null;
        if (file instanceof ShellFolder) {
            folderColumns = ((ShellFolder)file).getFolderColumns();
        }
        if (folderColumns == null) {
            folderColumns = new ShellFolderColumnInfo[] { new ShellFolderColumnInfo("FileChooser.fileNameHeaderText", 150, 10, true, null, ShellFolder.FILE_COMPARATOR), new ShellFolderColumnInfo("FileChooser.fileSizeHeaderText", 75, 4, true, null, ShellFolder.DEFAULT_COMPARATOR, true), new ShellFolderColumnInfo("FileChooser.fileDateHeaderText", 130, 10, true, null, ShellFolder.DEFAULT_COMPARATOR, true) };
        }
        return folderColumns;
    }
    
    public ShellFolderColumnInfo[] getFolderColumns() {
        return null;
    }
    
    public static Object getFolderColumnValue(final File file, final int n) {
        if (file instanceof ShellFolder) {
            final Object folderColumnValue = ((ShellFolder)file).getFolderColumnValue(n);
            if (folderColumnValue != null) {
                return folderColumnValue;
            }
        }
        if (file == null || !file.exists()) {
            return null;
        }
        switch (n) {
            case 0: {
                return file;
            }
            case 1: {
                return file.isDirectory() ? null : Long.valueOf(file.length());
            }
            case 2: {
                if (isFileSystemRoot(file)) {
                    return null;
                }
                final long lastModified = file.lastModified();
                return (lastModified == 0L) ? null : new Date(lastModified);
            }
            default: {
                return null;
            }
        }
    }
    
    public Object getFolderColumnValue(final int n) {
        return null;
    }
    
    public static <T> T invoke(final Callable<T> callable) {
        try {
            return invoke(callable, RuntimeException.class);
        }
        catch (final InterruptedException ex) {
            return null;
        }
    }
    
    public static <T, E extends Throwable> T invoke(final Callable<T> callable, final Class<E> clazz) throws InterruptedException, E, Throwable {
        try {
            return ShellFolder.invoker.invoke(callable);
        }
        catch (final Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw (InterruptedException)ex;
            }
            if (clazz.isInstance(ex)) {
                throw (Throwable)clazz.cast(ex);
            }
            throw new RuntimeException("Unexpected error", ex);
        }
    }
    
    static {
        final String s = (String)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
        Class<?> forName = null;
        try {
            forName = Class.forName(s, false, null);
            if (!ShellFolderManager.class.isAssignableFrom(forName)) {
                forName = null;
            }
        }
        catch (final ClassNotFoundException ex) {}
        catch (final NullPointerException ex2) {}
        catch (final SecurityException ex3) {}
        if (forName == null) {
            forName = ShellFolderManager.class;
        }
        try {
            shellFolderManager = (ShellFolderManager)forName.newInstance();
        }
        catch (final InstantiationException ex4) {
            throw new Error("Could not instantiate Shell Folder Manager: " + forName.getName());
        }
        catch (final IllegalAccessException ex5) {
            throw new Error("Could not access Shell Folder Manager: " + forName.getName());
        }
        invoker = ShellFolder.shellFolderManager.createInvoker();
        DEFAULT_COMPARATOR = new Comparator() {
            @Override
            public int compare(final Object o, final Object o2) {
                int compareTo;
                if (o == null && o2 == null) {
                    compareTo = 0;
                }
                else if (o != null && o2 == null) {
                    compareTo = 1;
                }
                else if (o == null && o2 != null) {
                    compareTo = -1;
                }
                else if (o instanceof Comparable) {
                    compareTo = ((Comparable)o).compareTo(o2);
                }
                else {
                    compareTo = 0;
                }
                return compareTo;
            }
        };
        FILE_COMPARATOR = new Comparator<File>() {
            @Override
            public int compare(final File file, final File file2) {
                ShellFolder shellFolder = null;
                File file3 = null;
                if (file instanceof ShellFolder) {
                    shellFolder = (ShellFolder)file;
                    if (shellFolder.isFileSystem()) {
                        shellFolder = null;
                    }
                }
                if (file2 instanceof ShellFolder) {
                    file3 = file2;
                    if (((ShellFolder)file3).isFileSystem()) {
                        file3 = null;
                    }
                }
                if (shellFolder != null && file3 != null) {
                    return shellFolder.compareTo(file3);
                }
                if (shellFolder != null) {
                    return -1;
                }
                if (file3 != null) {
                    return 1;
                }
                final String name = file.getName();
                final String name2 = file2.getName();
                final int compareToIgnoreCase = name.compareToIgnoreCase(name2);
                if (compareToIgnoreCase != 0) {
                    return compareToIgnoreCase;
                }
                return name.compareTo(name2);
            }
        };
    }
    
    public interface Invoker
    {
         <T> T invoke(final Callable<T> p0) throws Exception;
    }
}
