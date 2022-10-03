package org.apache.axiom.attachments.lifecycle.impl;

import org.apache.commons.logging.LogFactory;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.activation.DataSource;
import org.apache.axiom.attachments.CachedFileDataSource;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import java.io.File;
import org.apache.commons.logging.Log;
import java.util.Observable;

public class FileAccessor extends Observable
{
    private static final Log log;
    File file;
    LifecycleManager manager;
    private int accessCount;
    
    public FileAccessor(final LifecycleManager manager, final File file) {
        this.file = null;
        this.accessCount = 0;
        this.manager = manager;
        this.file = file;
    }
    
    @Deprecated
    public DataHandler getDataHandler(final String contentType) {
        if (FileAccessor.log.isDebugEnabled()) {
            FileAccessor.log.debug((Object)"getDataHandler()");
            FileAccessor.log.debug((Object)("accessCount =" + this.accessCount));
        }
        final CachedFileDataSource dataSource = new CachedFileDataSource(this.file);
        dataSource.setContentType(contentType);
        ++this.accessCount;
        this.setChanged();
        this.notifyObservers();
        final DataHandler dataHandler = new DataHandler(dataSource);
        return new DataHandlerExtImpl(dataHandler, this.manager);
    }
    
    public String getFileName() {
        if (FileAccessor.log.isDebugEnabled()) {
            FileAccessor.log.debug((Object)"getFileName()");
        }
        return this.file.getAbsolutePath();
    }
    
    public InputStream getInputStream() throws IOException {
        if (FileAccessor.log.isDebugEnabled()) {
            FileAccessor.log.debug((Object)"getInputStream()");
        }
        return new FileInputStream(this.file);
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
        if (FileAccessor.log.isDebugEnabled()) {
            FileAccessor.log.debug((Object)"getOutputStream()");
        }
        return new FileOutputStream(this.file);
    }
    
    public long getSize() {
        return this.file.length();
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public int getAccessCount() {
        return this.accessCount;
    }
    
    static {
        log = LogFactory.getLog((Class)FileAccessor.class);
    }
}
