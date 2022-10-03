package org.apache.axiom.attachments.lifecycle.impl;

import org.apache.commons.logging.LogFactory;
import javax.activation.DataSource;
import org.apache.axiom.attachments.CachedFileDataSource;
import java.util.Observable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.commons.logging.Log;
import java.util.Observer;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import javax.activation.DataHandler;

public class DataHandlerExtImpl extends DataHandler implements DataHandlerExt, Observer
{
    private static final Log log;
    private DataHandler dataHandler;
    private LifecycleManager manager;
    private static int READ_COUNT;
    private boolean deleteOnreadOnce;
    
    public DataHandlerExtImpl(final DataHandler dataHandler, final LifecycleManager manager) {
        super(dataHandler.getDataSource());
        this.dataHandler = null;
        this.manager = null;
        this.deleteOnreadOnce = false;
        this.dataHandler = dataHandler;
        this.manager = manager;
    }
    
    public InputStream readOnce() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void deleteWhenReadOnce() throws IOException {
        this.deleteOnreadOnce = true;
        final FileAccessor fa = this.manager.getFileAccessor(this.getName());
        if (fa == null) {
            DataHandlerExtImpl.log.warn((Object)"Could not find FileAccessor, delete on readOnce Failed");
            return;
        }
        if (fa.getAccessCount() >= DataHandlerExtImpl.READ_COUNT) {
            this.purgeDataSource();
        }
        else {
            fa.addObserver(this);
        }
    }
    
    public void purgeDataSource() throws IOException {
        if (DataHandlerExtImpl.log.isDebugEnabled()) {
            DataHandlerExtImpl.log.debug((Object)"Start purgeDataSource");
        }
        final File file = this.getFile();
        if (file != null) {
            this.manager.delete(file);
        }
        else if (DataHandlerExtImpl.log.isDebugEnabled()) {
            DataHandlerExtImpl.log.debug((Object)"DataSource is not a CachedFileDataSource, Unable to Purge.");
        }
        if (DataHandlerExtImpl.log.isDebugEnabled()) {
            DataHandlerExtImpl.log.debug((Object)"End purgeDataSource");
        }
    }
    
    public void update(final Observable o, final Object arg) {
        try {
            if (DataHandlerExtImpl.log.isDebugEnabled()) {
                DataHandlerExtImpl.log.debug((Object)"Start update in Observer");
            }
            if (o instanceof FileAccessor) {
                final FileAccessor fa = (FileAccessor)o;
                if (this.deleteOnreadOnce && fa.getAccessCount() >= DataHandlerExtImpl.READ_COUNT) {
                    this.purgeDataSource();
                }
            }
        }
        catch (final IOException e) {
            if (DataHandlerExtImpl.log.isDebugEnabled()) {
                DataHandlerExtImpl.log.debug((Object)"delete on readOnce Failed");
            }
            DataHandlerExtImpl.log.warn((Object)("delete on readOnce Failed with IOException in Observer" + e.getMessage()));
        }
        if (DataHandlerExtImpl.log.isDebugEnabled()) {
            DataHandlerExtImpl.log.debug((Object)"End update in Observer");
        }
    }
    
    private File getFile() {
        final DataSource dataSource = this.dataHandler.getDataSource();
        if (dataSource instanceof CachedFileDataSource) {
            final CachedFileDataSource cds = (CachedFileDataSource)dataSource;
            return cds.getFile();
        }
        return null;
    }
    
    static {
        log = LogFactory.getLog((Class)DataHandlerExtImpl.class);
        DataHandlerExtImpl.READ_COUNT = 1;
    }
}
