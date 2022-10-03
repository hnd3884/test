package com.zoho.clustering.agent.filerepl;

import com.zoho.clustering.util.ClassUtil;
import com.zoho.clustering.filerepl.ErrorHandler;
import com.zoho.clustering.filerepl.event.EventLogger;
import com.zoho.clustering.util.logger.LogConfig;
import java.util.logging.Level;
import com.zoho.clustering.filerepl.DirectoryList;
import com.zoho.clustering.util.MyProperties;

public class ReplMasterModule
{
    private static ReplMaster replMaster;
    
    public static ReplMaster getInst() {
        if (ReplMasterModule.replMaster == null) {
            throw new IllegalStateException("ReplMaster is not yet initialized");
        }
        return ReplMasterModule.replMaster;
    }
    
    public static void initialize(final MyProperties props) {
        initialize("clustering.filerepl.master", props);
    }
    
    public static void initialize(final String prefix, final MyProperties props) {
        if (ReplMasterModule.replMaster != null) {
            throw new IllegalStateException("ReplMaster is already initialized");
        }
        initializeLogger(prefix + ".log", props);
        final String snapshotDir = props.value(prefix + ".snapshotDir");
        final DirectoryList directoryList = new DirectoryList(props.value(prefix + ".dirList"));
        ReplMasterModule.replMaster = new ReplMaster(directoryList, snapshotDir, createEventLogger(prefix + ".eventLogger", props));
        ReplMaster.logger().log(Level.INFO, "ReplMasterModule: initialized");
    }
    
    private static void initializeLogger(final String prefix, final MyProperties props) {
        if (props.optionalValue(prefix + ".fileName") != null) {
            new LogConfig(prefix, props).registerLogger("com.zoho.clustering.filerepl");
        }
    }
    
    private static EventLogger createEventLogger(final String prefix, final MyProperties props) {
        final String eventLogDir = props.value(prefix + ".dir");
        final int maxBytesPerLog = props.intValue(prefix + ".maxBytesPerLog");
        final boolean deleteOldEvents = props.boolValue(prefix + ".deleteOldEvents", false);
        final String errorHandlerClass = props.value(prefix + ".errorHandlerClass");
        final String errorHandlerPrefix = "eventLogger.errorHandler";
        final ErrorHandler errorHandler = (ErrorHandler)ClassUtil.New(errorHandlerClass, errorHandlerPrefix, props);
        return new EventLogger(eventLogDir, maxBytesPerLog, deleteOldEvents, errorHandler);
    }
    
    public static void logCreateEvent(final int baseDirId, final String fileName) {
        getInst().getEventLogger().logCreateEvent(baseDirId, fileName);
    }
    
    public static void logDeleteEvent(final int baseDirId, final String fileName) {
        getInst().getEventLogger().logDeleteEvent(baseDirId, fileName);
    }
    
    public static void logUpdateEvent(final int baseDirId, final String fileName) {
        getInst().getEventLogger().logUpdateEvent(baseDirId, fileName);
    }
    
    public static void logRenameEvent(final int baseDirId, final String oldName, final String newName) {
        getInst().getEventLogger().logRenameEvent(baseDirId, oldName, newName);
    }
    
    static {
        ReplMasterModule.replMaster = null;
    }
    
    public static class Test
    {
        public static void main(final String[] args) throws InterruptedException {
            ReplMasterModule.initialize(new MyProperties("conf/repl-master.conf"));
            testSnapshot(Integer.parseInt(args[0]));
        }
        
        private static void testSnapshot(final int count) throws InterruptedException {
            System.out.print("\nsnapshot test ...");
            for (int i = 0; i < count; ++i) {
                Thread.sleep(60000L);
                System.out.print("\nstart ...");
                ReplMasterModule.replMaster.takeSnapshot(true);
                System.out.print("\nover");
            }
        }
    }
}
