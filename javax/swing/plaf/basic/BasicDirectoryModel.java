package javax.swing.plaf.basic;

import java.util.Iterator;
import javax.swing.filechooser.FileSystemView;
import java.util.concurrent.Callable;
import java.util.Collection;
import javax.swing.SwingUtilities;
import java.util.List;
import sun.awt.shell.ShellFolder;
import javax.swing.event.ListDataEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Vector;
import javax.swing.JFileChooser;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractListModel;

public class BasicDirectoryModel extends AbstractListModel<Object> implements PropertyChangeListener
{
    private JFileChooser filechooser;
    private Vector<File> fileCache;
    private LoadFilesThread loadThread;
    private Vector<File> files;
    private Vector<File> directories;
    private int fetchID;
    private PropertyChangeSupport changeSupport;
    private boolean busy;
    
    public BasicDirectoryModel(final JFileChooser filechooser) {
        this.filechooser = null;
        this.fileCache = new Vector<File>(50);
        this.loadThread = null;
        this.files = null;
        this.directories = null;
        this.fetchID = 0;
        this.busy = false;
        this.filechooser = filechooser;
        this.validateFileCache();
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName == "directoryChanged" || propertyName == "fileViewChanged" || propertyName == "fileFilterChanged" || propertyName == "FileHidingChanged" || propertyName == "fileSelectionChanged") {
            this.validateFileCache();
        }
        else if ("UI".equals(propertyName)) {
            final Object oldValue = propertyChangeEvent.getOldValue();
            if (oldValue instanceof BasicFileChooserUI) {
                final BasicDirectoryModel model = ((BasicFileChooserUI)oldValue).getModel();
                if (model != null) {
                    model.invalidateFileCache();
                }
            }
        }
        else if ("JFileChooserDialogIsClosingProperty".equals(propertyName)) {
            this.invalidateFileCache();
        }
    }
    
    public void invalidateFileCache() {
        if (this.loadThread != null) {
            this.loadThread.interrupt();
            this.loadThread.cancelRunnables();
            this.loadThread = null;
        }
    }
    
    public Vector<File> getDirectories() {
        synchronized (this.fileCache) {
            if (this.directories != null) {
                return this.directories;
            }
            this.getFiles();
            return this.directories;
        }
    }
    
    public Vector<File> getFiles() {
        synchronized (this.fileCache) {
            if (this.files != null) {
                return this.files;
            }
            this.files = new Vector<File>();
            (this.directories = new Vector<File>()).addElement(this.filechooser.getFileSystemView().createFileObject(this.filechooser.getCurrentDirectory(), ".."));
            for (int i = 0; i < this.getSize(); ++i) {
                final File file = this.fileCache.get(i);
                if (this.filechooser.isTraversable(file)) {
                    this.directories.add(file);
                }
                else {
                    this.files.add(file);
                }
            }
            return this.files;
        }
    }
    
    public void validateFileCache() {
        final File currentDirectory = this.filechooser.getCurrentDirectory();
        if (currentDirectory == null) {
            return;
        }
        if (this.loadThread != null) {
            this.loadThread.interrupt();
            this.loadThread.cancelRunnables();
        }
        this.setBusy(true, ++this.fetchID);
        (this.loadThread = new LoadFilesThread(currentDirectory, this.fetchID)).start();
    }
    
    public boolean renameFile(final File file, final File file2) {
        synchronized (this.fileCache) {
            if (file.renameTo(file2)) {
                this.validateFileCache();
                return true;
            }
            return false;
        }
    }
    
    public void fireContentsChanged() {
        this.fireContentsChanged(this, 0, this.getSize() - 1);
    }
    
    @Override
    public int getSize() {
        return this.fileCache.size();
    }
    
    public boolean contains(final Object o) {
        return this.fileCache.contains(o);
    }
    
    public int indexOf(final Object o) {
        return this.fileCache.indexOf(o);
    }
    
    @Override
    public Object getElementAt(final int n) {
        return this.fileCache.get(n);
    }
    
    public void intervalAdded(final ListDataEvent listDataEvent) {
    }
    
    public void intervalRemoved(final ListDataEvent listDataEvent) {
    }
    
    protected void sort(final Vector<? extends File> vector) {
        ShellFolder.sort(vector);
    }
    
    protected boolean lt(final File file, final File file2) {
        final int compareTo = file.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
        if (compareTo != 0) {
            return compareTo < 0;
        }
        return file.getName().compareTo(file2.getName()) < 0;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new PropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport != null) {
            this.changeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (this.changeSupport != null) {
            this.changeSupport.firePropertyChange(s, o, o2);
        }
    }
    
    private synchronized void setBusy(final boolean busy, final int n) {
        if (n == this.fetchID) {
            final boolean busy2 = this.busy;
            this.busy = busy;
            if (this.changeSupport != null && busy != busy2) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BasicDirectoryModel.this.firePropertyChange("busy", !busy, busy);
                    }
                });
            }
        }
    }
    
    class LoadFilesThread extends Thread
    {
        File currentDirectory;
        int fid;
        Vector<DoChangeContents> runnables;
        
        public LoadFilesThread(final File currentDirectory, final int fid) {
            super("Basic L&F File Loading Thread");
            this.currentDirectory = null;
            this.runnables = new Vector<DoChangeContents>(10);
            this.currentDirectory = currentDirectory;
            this.fid = fid;
        }
        
        @Override
        public void run() {
            this.run0();
            BasicDirectoryModel.this.setBusy(false, this.fid);
        }
        
        public void run0() {
            final FileSystemView fileSystemView = BasicDirectoryModel.this.filechooser.getFileSystemView();
            if (this.isInterrupted()) {
                return;
            }
            final File[] files = fileSystemView.getFiles(this.currentDirectory, BasicDirectoryModel.this.filechooser.isFileHidingEnabled());
            if (this.isInterrupted()) {
                return;
            }
            final Vector vector = new Vector();
            final Vector vector2 = new Vector();
            for (final File file : files) {
                if (BasicDirectoryModel.this.filechooser.accept(file)) {
                    if (BasicDirectoryModel.this.filechooser.isTraversable(file)) {
                        vector.addElement(file);
                    }
                    else if (BasicDirectoryModel.this.filechooser.isFileSelectionEnabled()) {
                        vector2.addElement(file);
                    }
                    if (this.isInterrupted()) {
                        return;
                    }
                }
            }
            BasicDirectoryModel.this.sort(vector);
            BasicDirectoryModel.this.sort(vector2);
            vector.addAll(vector2);
            final DoChangeContents doChangeContents = ShellFolder.invoke((Callable<DoChangeContents>)new Callable<DoChangeContents>() {
                @Override
                public DoChangeContents call() {
                    final int size = vector.size();
                    final int size2 = BasicDirectoryModel.this.fileCache.size();
                    if (size > size2) {
                        int n = size2;
                        int n2 = size;
                        for (int i = 0; i < size2; ++i) {
                            if (!((File)vector.get(i)).equals(BasicDirectoryModel.this.fileCache.get(i))) {
                                n = i;
                                for (int j = i; j < size; ++j) {
                                    if (((File)vector.get(j)).equals(BasicDirectoryModel.this.fileCache.get(i))) {
                                        n2 = j;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        if (n >= 0 && n2 > n && vector.subList(n2, size).equals(BasicDirectoryModel.this.fileCache.subList(n, size2))) {
                            if (LoadFilesThread.this.isInterrupted()) {
                                return null;
                            }
                            return new DoChangeContents(vector.subList(n, n2), n, null, 0, LoadFilesThread.this.fid);
                        }
                    }
                    else if (size < size2) {
                        int n3 = -1;
                        int n4 = -1;
                        for (int k = 0; k < size; ++k) {
                            if (!((File)vector.get(k)).equals(BasicDirectoryModel.this.fileCache.get(k))) {
                                n3 = k;
                                n4 = k + size2 - size;
                                break;
                            }
                        }
                        if (n3 >= 0 && n4 > n3 && BasicDirectoryModel.this.fileCache.subList(n4, size2).equals(vector.subList(n3, size))) {
                            if (LoadFilesThread.this.isInterrupted()) {
                                return null;
                            }
                            return new DoChangeContents(null, 0, new Vector<File>(BasicDirectoryModel.this.fileCache.subList(n3, n4)), n3, LoadFilesThread.this.fid);
                        }
                    }
                    if (!BasicDirectoryModel.this.fileCache.equals(vector)) {
                        if (LoadFilesThread.this.isInterrupted()) {
                            LoadFilesThread.this.cancelRunnables(LoadFilesThread.this.runnables);
                        }
                        return new DoChangeContents(vector, 0, BasicDirectoryModel.this.fileCache, 0, LoadFilesThread.this.fid);
                    }
                    return null;
                }
            });
            if (doChangeContents != null) {
                this.runnables.addElement(doChangeContents);
                SwingUtilities.invokeLater(doChangeContents);
            }
        }
        
        public void cancelRunnables(final Vector<DoChangeContents> vector) {
            final Iterator<DoChangeContents> iterator = vector.iterator();
            while (iterator.hasNext()) {
                iterator.next().cancel();
            }
        }
        
        public void cancelRunnables() {
            this.cancelRunnables(this.runnables);
        }
    }
    
    class DoChangeContents implements Runnable
    {
        private List<File> addFiles;
        private List<File> remFiles;
        private boolean doFire;
        private int fid;
        private int addStart;
        private int remStart;
        
        public DoChangeContents(final List<File> addFiles, final int addStart, final List<File> remFiles, final int remStart, final int fid) {
            this.doFire = true;
            this.addStart = 0;
            this.remStart = 0;
            this.addFiles = addFiles;
            this.addStart = addStart;
            this.remFiles = remFiles;
            this.remStart = remStart;
            this.fid = fid;
        }
        
        synchronized void cancel() {
            this.doFire = false;
        }
        
        @Override
        public synchronized void run() {
            if (BasicDirectoryModel.this.fetchID == this.fid && this.doFire) {
                final int n = (this.remFiles == null) ? 0 : this.remFiles.size();
                final int n2 = (this.addFiles == null) ? 0 : this.addFiles.size();
                synchronized (BasicDirectoryModel.this.fileCache) {
                    if (n > 0) {
                        BasicDirectoryModel.this.fileCache.removeAll(this.remFiles);
                    }
                    if (n2 > 0) {
                        BasicDirectoryModel.this.fileCache.addAll(this.addStart, this.addFiles);
                    }
                    BasicDirectoryModel.this.files = null;
                    BasicDirectoryModel.this.directories = null;
                }
                if (n > 0 && n2 == 0) {
                    AbstractListModel.this.fireIntervalRemoved(BasicDirectoryModel.this, this.remStart, this.remStart + n - 1);
                }
                else if (n2 > 0 && n == 0 && this.addStart + n2 <= BasicDirectoryModel.this.fileCache.size()) {
                    AbstractListModel.this.fireIntervalAdded(BasicDirectoryModel.this, this.addStart, this.addStart + n2 - 1);
                }
                else {
                    BasicDirectoryModel.this.fireContentsChanged();
                }
            }
        }
    }
}
