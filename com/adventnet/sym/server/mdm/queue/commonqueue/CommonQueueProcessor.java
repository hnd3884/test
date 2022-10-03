package com.adventnet.sym.server.mdm.queue.commonqueue;

import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class CommonQueueProcessor extends DCQueueDataProcessor
{
    Logger mdmCommonQueueLog;
    String separator;
    
    public CommonQueueProcessor() {
        this.mdmCommonQueueLog = Logger.getLogger("MDMCommonQueueLogger");
        this.separator = "\t";
    }
    
    public void processData(final DCQueueData qData) {
        final Long sysTime = System.currentTimeMillis();
        this.mdmCommonQueueLog.log(Level.INFO, "CommandProcessingStarted {0}{1}{2}TimeSpentInQueue : {3}", new Object[] { this.separator, qData.fileName, this.separator, String.valueOf(sysTime - qData.postTime) });
        final CommonQueueData data = new CommonQueueData(qData.queueExtnTableData);
        this.mdmCommonQueueLog.log(Level.INFO, "The command name is {0}", data.getTaskName());
        this.mdmCommonQueueLog.log(Level.INFO, "The class name is {0}", data.getClassName());
        try {
            final Object processorClass = Class.forName(data.getClassName()).newInstance();
            if (!(processorClass instanceof CommonQueueProcessorInterface)) {
                throw new Exception("The processor class does not extend CommonQueueProcessorInterface");
            }
            final CommonQueueProcessorInterface processor = (CommonQueueProcessorInterface)processorClass;
            processor.processData(new CommonQueueData(qData.queueExtnTableData, (String)qData.queueData));
        }
        catch (final ClassNotFoundException exp) {
            this.mdmCommonQueueLog.log(Level.SEVERE, "Cannot find the specified class ", exp);
        }
        catch (final NoClassDefFoundError exp2) {
            this.mdmCommonQueueLog.log(Level.SEVERE, "Cannot find definition for the specified class ", exp2);
        }
        catch (final Exception exp3) {
            this.mdmCommonQueueLog.log(Level.SEVERE, "Exception occured while executing common queue data ", exp3);
        }
        this.mdmCommonQueueLog.log(Level.INFO, "CommandProcessingEnded {0}{1}{2} TimeSpentInQueue : {3}", new Object[] { this.separator, qData.fileName, this.separator, String.valueOf(System.currentTimeMillis() - sysTime) });
    }
}
