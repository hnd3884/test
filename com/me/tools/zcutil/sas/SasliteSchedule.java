package com.me.tools.zcutil.sas;

import com.me.tools.zcutil.METrack;
import com.me.tools.zcutil.ZCDataHandler;
import com.zoho.scheduler.RunnableJob;

public class SasliteSchedule implements RunnableJob
{
    public void run(final long jobid) throws Exception {
        final ZCDataHandler customDataHandler = (ZCDataHandler)Class.forName(METrack.getZCUtil().getConfValue().getProperty("dataHandler")).newInstance();
        customDataHandler.uploadODData(jobid);
    }
}
