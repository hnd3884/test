package com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue;

public class AssociationQueueConstants
{
    public static final String DATA_ADDED_TO_QUEUE = "AddedToQueue";
    public static final String DATA_PROCESSING_QUEUE_STARTED = "ProcessingStarted";
    public static final String DATA_PROCESSING_QUEUE_ENDED = "ProcessingEnded";
    public static final String DEVICES = "DEVICES";
    public static final String PROFILES = "PROFILES";
    public static final String COMMAND_NAME = "commandName";
    public static final String CUSTOMER_ID = "customerId";
    public static final String PROPS_FILE = "propsFile";
    public static final String IS_USER_COMMAND = "isAssignUserCommand";
    public static final String COMMAND_TYPE = "commandType";
    public static final String INMEMORY = " In Memory";
    public static final String INDB = " In DB";
    public static final int Q_DATA_IN_MEMORY = 1;
    public static final int Q_DATA_IN_DB = 2;
    public static final String IS_Q_SUSPENDED = "isQueueSuspended:";
    public static final int DEVICE_COMMAND = 1;
    public static final int USER_COMMAND = 2;
    public static final Integer QUEUED_COUNT;
    
    static {
        QUEUED_COUNT = new Integer(310);
    }
}
