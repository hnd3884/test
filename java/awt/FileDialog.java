package java.awt;

import sun.awt.AWTAccessor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.peer.FileDialogPeer;
import java.io.FilenameFilter;
import java.io.File;

public class FileDialog extends Dialog
{
    public static final int LOAD = 0;
    public static final int SAVE = 1;
    int mode;
    String dir;
    String file;
    private File[] files;
    private boolean multipleMode;
    FilenameFilter filter;
    private static final String base = "filedlg";
    private static int nameCounter;
    private static final long serialVersionUID = 5035145889651310422L;
    
    private static native void initIDs();
    
    public FileDialog(final Frame frame) {
        this(frame, "", 0);
    }
    
    public FileDialog(final Frame frame, final String s) {
        this(frame, s, 0);
    }
    
    public FileDialog(final Frame frame, final String s, final int mode) {
        super(frame, s, true);
        this.multipleMode = false;
        this.setMode(mode);
        this.setLayout(null);
    }
    
    public FileDialog(final Dialog dialog) {
        this(dialog, "", 0);
    }
    
    public FileDialog(final Dialog dialog, final String s) {
        this(dialog, s, 0);
    }
    
    public FileDialog(final Dialog dialog, final String s, final int mode) {
        super(dialog, s, true);
        this.multipleMode = false;
        this.setMode(mode);
        this.setLayout(null);
    }
    
    @Override
    String constructComponentName() {
        synchronized (FileDialog.class) {
            return "filedlg" + FileDialog.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.parent != null && this.parent.getPeer() == null) {
                this.parent.addNotify();
            }
            if (this.peer == null) {
                this.peer = this.getToolkit().createFileDialog(this);
            }
            super.addNotify();
        }
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setMode(final int mode) {
        switch (mode) {
            case 0:
            case 1: {
                this.mode = mode;
                return;
            }
            default: {
                throw new IllegalArgumentException("illegal file dialog mode");
            }
        }
    }
    
    public String getDirectory() {
        return this.dir;
    }
    
    public void setDirectory(final String s) {
        this.dir = ((s != null && s.equals("")) ? null : s);
        final FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
        if (fileDialogPeer != null) {
            fileDialogPeer.setDirectory(this.dir);
        }
    }
    
    public String getFile() {
        return this.file;
    }
    
    public File[] getFiles() {
        synchronized (this.getObjectLock()) {
            if (this.files != null) {
                return this.files.clone();
            }
            return new File[0];
        }
    }
    
    private void setFiles(final File[] files) {
        synchronized (this.getObjectLock()) {
            this.files = files;
        }
    }
    
    public void setFile(final String s) {
        this.file = ((s != null && s.equals("")) ? null : s);
        final FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
        if (fileDialogPeer != null) {
            fileDialogPeer.setFile(this.file);
        }
    }
    
    public void setMultipleMode(final boolean multipleMode) {
        synchronized (this.getObjectLock()) {
            this.multipleMode = multipleMode;
        }
    }
    
    public boolean isMultipleMode() {
        synchronized (this.getObjectLock()) {
            return this.multipleMode;
        }
    }
    
    public FilenameFilter getFilenameFilter() {
        return this.filter;
    }
    
    public synchronized void setFilenameFilter(final FilenameFilter filenameFilter) {
        this.filter = filenameFilter;
        final FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
        if (fileDialogPeer != null) {
            fileDialogPeer.setFilenameFilter(filenameFilter);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (this.dir != null && this.dir.equals("")) {
            this.dir = null;
        }
        if (this.file != null && this.file.equals("")) {
            this.file = null;
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",dir= " + this.dir + ",file= " + this.file + ((this.mode == 0) ? ",load" : ",save");
    }
    
    @Override
    boolean postsOldMouseEvents() {
        return false;
    }
    
    static {
        FileDialog.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setFileDialogAccessor(new AWTAccessor.FileDialogAccessor() {
            @Override
            public void setFiles(final FileDialog fileDialog, final File[] array) {
                fileDialog.setFiles(array);
            }
            
            @Override
            public void setFile(final FileDialog fileDialog, final String s) {
                fileDialog.file = ("".equals(s) ? null : s);
            }
            
            @Override
            public void setDirectory(final FileDialog fileDialog, final String s) {
                fileDialog.dir = ("".equals(s) ? null : s);
            }
            
            @Override
            public boolean isMultipleMode(final FileDialog fileDialog) {
                synchronized (fileDialog.getObjectLock()) {
                    return fileDialog.multipleMode;
                }
            }
        });
    }
}
