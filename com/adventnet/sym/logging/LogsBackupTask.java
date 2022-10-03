package com.adventnet.sym.logging;

import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class LogsBackupTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties taskProps) {
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        if (frameworkConfigurations.has("backup_logs_task") && frameworkConfigurations.getJSONObject("backup_logs_task").get("enable").equals("true")) {
            new BackupLoggerUtil().performLogCleanUp();
        }
    }
}
