package com.me.mdm.files.serve;

import java.util.Collection;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class FileDispatchController
{
    private ThreadPoolExecutor executor;
    private static final int PROCESSING_THREAD_COUNT = 1;
    private BlockingQueue<AsyncContextAuthorizer> asyncContexts;
    
    private FileDispatchController() {
        this.asyncContexts = new LinkedBlockingQueue<AsyncContextAuthorizer>(5000);
        if (this.executor == null) {
            (this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(20))).setCorePoolSize(1);
            this.executor.setMaximumPoolSize(1);
            this.executor.setKeepAliveTime(60L, TimeUnit.SECONDS);
            this.executor.allowCoreThreadTimeOut(false);
        }
    }
    
    public static FileDispatchController getInstance() {
        return Holder.INSTANCE;
    }
    
    void drainTo(final List<AsyncContextAuthorizer> asyncContextsBatch, final int pollSize) {
        if (this.asyncContexts != null) {
            if (this.asyncContexts.size() < 20) {
                try {
                    Thread.sleep(20L);
                }
                catch (final Exception ex) {
                    SyMLogger.log("FileServletLog", Level.WARNING, (String)null, (Throwable)ex);
                }
            }
            this.asyncContexts.drainTo(asyncContextsBatch, pollSize);
            if (this.asyncContexts.peek() != null) {
                this.processRequests();
            }
        }
    }
    
    private void processRequests() {
        if (this.executor.getQueue().size() < 5 || this.executor.getActiveCount() < 1) {
            this.executor.execute(new FileDispatchWorker());
        }
    }
    
    void dispatch(final AsyncContextAuthorizer asyncContextAuthorizer) throws InterruptedException {
        this.asyncContexts.put(asyncContextAuthorizer);
        this.processRequests();
    }
    
    public void shutdown() {
        try {
            this.executor.shutdown();
        }
        catch (final Exception e) {
            SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)e);
        }
    }
    
    private static class Holder
    {
        private static final FileDispatchController INSTANCE;
        
        static {
            INSTANCE = new FileDispatchController(null);
        }
    }
}
