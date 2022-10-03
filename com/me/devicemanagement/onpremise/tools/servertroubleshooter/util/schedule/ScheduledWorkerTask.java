package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.schedule;

public interface ScheduledWorkerTask extends Runnable
{
    String getSchedulerName();
}
