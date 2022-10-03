package com.zoho.clustering.filerepl.slave;

import com.zoho.clustering.util.MyProperties;
import java.io.Closeable;
import java.io.InputStream;
import java.io.BufferedInputStream;
import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.filerepl.slave.api.APIException;
import com.zoho.clustering.filerepl.event.FileEvent;
import java.util.Iterator;
import java.util.List;
import com.zoho.clustering.filerepl.event.EventList;
import com.zoho.clustering.filerepl.slave.api.APIResourceException;
import java.util.logging.Level;
import java.io.File;
import com.zoho.clustering.filerepl.ErrorHandler;
import com.zoho.clustering.filerepl.slave.api.MasterStub;
import com.zoho.clustering.filerepl.DirectoryList;
import java.util.logging.Logger;

public class ReplSlave implements Runnable
{
    private static Logger logger;
    private Config config;
    private DirectoryList directoryList;
    private MasterStub masterStub;
    private ErrorHandler errorHandler;
    private LogPositionFile checkpointFile;
    private boolean running;
    private Thread slaveThread;
    
    public static Logger logger() {
        return ReplSlave.logger;
    }
    
    public ReplSlave(final Config config, final DirectoryList directoryList, final MasterStub masterStub, final ErrorHandler errorHandler) {
        this.config = config;
        this.directoryList = directoryList;
        this.checkpointFile = new LogPositionFile(new File(config.checkpointFilePath));
        this.masterStub = masterStub;
        this.errorHandler = errorHandler;
    }
    
    public Config getConfig() {
        return this.config;
    }
    
    public DirectoryList getDirectoryList() {
        return this.directoryList;
    }
    
    public MasterStub getMasterStub() {
        return this.masterStub;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void start() {
        if (this.running) {
            throw new IllegalStateException("ReplicationSlave is already running");
        }
        this.running = true;
        (this.slaveThread = new Thread(this, "Replication-Slave")).start();
        ReplSlave.logger.log(Level.INFO, "ReplicationSlave: started");
    }
    
    @Override
    public void run() {
        while (this.running) {
            ReplSlave.logger.log(Level.FINE, this.checkpointFile.getValue().toString());
            final int fetchIntervalMillis = this.config.fetchIntervalInSecs * 1000;
            try {
                EventList eventList = null;
                try {
                    eventList = this.masterStub.getEvents(this.checkpointFile.getValue(), this.config.batchSize);
                }
                catch (final APIResourceException exp) {
                    ReplSlave.logger.log(Level.SEVERE, "Problem while fetching the events. {0}", exp.toString());
                    ReplSlave.logger.log(Level.FINE, "", exp);
                    Thread.sleep(fetchIntervalMillis * 2);
                    continue;
                }
                final List<String> events = eventList.getEvents();
                if (events.size() == 0) {
                    Thread.sleep(fetchIntervalMillis);
                }
                else {
                    for (final String eventStr : events) {
                        this.processEvent(eventStr);
                    }
                    this.checkpointFile.setValue(eventList.getNextPos());
                }
            }
            catch (final InterruptedException ignored) {
                break;
            }
            catch (final RuntimeException exp2) {
                ReplSlave.logger.log(Level.SEVERE, "ReplSlave error.", exp2);
                this.errorHandler.handleError(exp2);
            }
        }
    }
    
    public void stop() {
        if (!this.running) {
            ReplSlave.logger.log(Level.WARNING, "ReplicationSlave: already stopped");
            return;
        }
        this.running = false;
        if (this.slaveThread != null && this.slaveThread.isAlive()) {
            this.slaveThread.interrupt();
        }
        ReplSlave.logger.log(Level.INFO, "ReplicationSlave: stopped");
    }
    
    public void processEvent(final String eventStr) throws APIException {
        try {
            final FileEvent event = new FileEvent(eventStr);
            final FileEvent.Type type = event.getType();
            if (type == FileEvent.Type.CREATE) {
                this.handleCreateEvent(event);
            }
            else if (type == FileEvent.Type.UPDATE) {
                this.handleUpdateEvent(event);
            }
            else if (type == FileEvent.Type.DELETE) {
                this.handleDeleteEvent(event);
            }
            else if (type == FileEvent.Type.RENAME) {
                this.handleRenameEvent(event);
            }
        }
        catch (final APIException exp) {
            throw exp;
        }
        catch (final RuntimeException exp2) {
            ReplSlave.logger.log(Level.WARNING, "Error while processing the event [" + eventStr + "]", exp2);
        }
    }
    
    private void handleCreateEvent(final FileEvent event) {
        final boolean success = this.copyFileFromMaster(event.getBaseDirId(), event.getFilePath());
        if (!success) {
            ReplSlave.logger.log(Level.WARNING, "No such resource [" + event.getBaseDirId() + "," + event.getFilePath() + "]. Event [" + event + "]");
        }
    }
    
    private void handleDeleteEvent(final FileEvent event) {
        final File file = new File(this.directoryList.getNameForId(event.getBaseDirId()), event.getFilePath());
        if (file.isDirectory()) {
            if (!FileUtil.deleteDirectory(file)) {
                ReplSlave.logger.log(Level.WARNING, "Not able to delete [" + file.getAbsolutePath() + "].Event [" + event + "]");
            }
        }
        else if (file.isFile() && !file.delete()) {
            ReplSlave.logger.log(Level.WARNING, "Not able to delete [" + file.getAbsolutePath() + "].Event [" + event + "]");
        }
    }
    
    private void handleUpdateEvent(final FileEvent event) {
        final String localBaseDir = this.directoryList.getNameForId(event.getBaseDirId());
        final File currVersion = new File(localBaseDir, event.getFilePath());
        final File currVersion_renamed = new File(localBaseDir, event.getFilePath() + "~");
        if (currVersion.exists()) {
            currVersion.renameTo(currVersion_renamed);
        }
        final boolean success = this.copyFileFromMaster(event.getBaseDirId(), event.getFilePath());
        if (success) {
            if (currVersion_renamed.exists()) {
                currVersion_renamed.delete();
            }
        }
        else {
            ReplSlave.logger.log(Level.WARNING, "No such resource [" + event.getBaseDirId() + "," + event.getFilePath() + "]. Event [" + event + "]");
            if (currVersion_renamed.exists()) {
                currVersion_renamed.renameTo(currVersion);
            }
        }
    }
    
    private void handleRenameEvent(final FileEvent event) {
        final String localBaseDir = this.directoryList.getNameForId(event.getBaseDirId());
        final File oldFile = new File(localBaseDir, event.getFilePath());
        final File newFile = new File(localBaseDir, event.getNewName());
        if (oldFile.exists()) {
            FileUtil.createParentDirectories(newFile);
            if (!oldFile.renameTo(newFile)) {
                ReplSlave.logger.log(Level.SEVERE, "Problem while handling the Rename-Event. Rename [{0} => {1}] failed.", new Object[] { oldFile.getAbsolutePath(), newFile.getAbsoluteFile() });
            }
        }
        else {
            final boolean success = this.copyFileFromMaster(event.getBaseDirId(), event.getNewName());
            if (!success) {
                ReplSlave.logger.log(Level.WARNING, "No such resource [" + event.getBaseDirId() + "/" + event.getNewName() + "]. Event [" + event + "]");
            }
        }
    }
    
    private boolean copyFileFromMaster(final int baseDirId, final String fileName) {
        BufferedInputStream bin = null;
        try {
            final InputStream masterCopy = this.masterStub.downloadFile(baseDirId, fileName);
            if (masterCopy == null) {
                return false;
            }
            bin = new BufferedInputStream(masterCopy);
            final File localCopy = new File(this.directoryList.getNameForId(baseDirId), fileName);
            FileUtil.createParentDirectories(localCopy);
            FileUtil.copyToFile(bin, localCopy);
        }
        finally {
            FileUtil.Close(bin);
        }
        return true;
    }
    
    static {
        ReplSlave.logger = Logger.getLogger(ReplSlave.class.getName());
    }
    
    public static class Config
    {
        public final int batchSize;
        public final int fetchIntervalInSecs;
        public final String checkpointFilePath;
        
        public Config(final int batchSize, final int fetchIntervalInSecs, final String checkpointFilePath) {
            this.batchSize = batchSize;
            this.fetchIntervalInSecs = fetchIntervalInSecs;
            this.checkpointFilePath = checkpointFilePath;
        }
        
        public Config(final String prefix, final MyProperties props) {
            this.batchSize = props.intValue(prefix + ".batchSize", 10);
            this.fetchIntervalInSecs = props.intValue(prefix + ".fetchIntervalInSecs", 3);
            this.checkpointFilePath = props.value(prefix + ".checkpointFile");
        }
    }
}
