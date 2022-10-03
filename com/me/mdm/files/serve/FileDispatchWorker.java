package com.me.mdm.files.serve;

import java.util.ArrayList;
import org.json.JSONException;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.idps.core.util.IdpsUtil;
import java.util.logging.Level;
import org.json.JSONArray;
import java.text.MessageFormat;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONObject;
import java.util.List;

class FileDispatchWorker implements Runnable
{
    private static final int BATCH_SIZE = 2000;
    
    private void log(final List<AsyncContextAuthorizer> asyncContextsBatch, final Long batchProcessingStartedAt, final Long batchProcessingEndedAt) throws JSONException {
        final JSONObject logMsg = new JSONObject();
        logMsg.put("MSG", (Object)MessageFormat.format("authenticated and authorized {0} request(s) in {1}", String.valueOf(asyncContextsBatch.size()), DurationFormatUtils.formatDurationHMS(batchProcessingEndedAt - batchProcessingStartedAt)));
        final JSONArray jsArray = new JSONArray();
        for (final AsyncContextAuthorizer asyncContextAuthorizer : asyncContextsBatch) {
            jsArray.put((Object)asyncContextAuthorizer.toString());
        }
        logMsg.put("requests", (Object)jsArray);
        SyMLogger.log("FileServletLog", Level.INFO, IdpsUtil.getPrettyJSON(logMsg));
    }
    
    @Override
    public void run() {
        final List<AsyncContextAuthorizer> asyncContextsBatch = new ArrayList<AsyncContextAuthorizer>(2000);
        try {
            boolean loop = true;
            while (loop) {
                loop = false;
                FileDispatchController.getInstance().drainTo(asyncContextsBatch, 2000);
                if (asyncContextsBatch.size() > 0) {
                    loop = true;
                    try {
                        final Long batchProcessingStartedAt = System.currentTimeMillis();
                        FileDownloadRegulator.AAA(asyncContextsBatch);
                        final Long batchProcessingEndedAt = System.currentTimeMillis();
                        this.log(asyncContextsBatch, batchProcessingStartedAt, batchProcessingEndedAt);
                    }
                    catch (final Exception ex) {
                        SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)ex);
                        try {
                            FileDownloadRegulator.close(asyncContextsBatch);
                        }
                        catch (final Exception ex2) {
                            SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)ex2);
                        }
                    }
                    finally {
                        asyncContextsBatch.clear();
                    }
                }
            }
        }
        catch (final Exception ex3) {
            SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)ex3);
        }
    }
}
