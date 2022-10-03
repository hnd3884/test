package com.me.mdm.files.foldermigration;

import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ClientDataFolderMigrationTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties properties) {
        new ClientDataFolderMigration().copyProfileTask();
    }
}
