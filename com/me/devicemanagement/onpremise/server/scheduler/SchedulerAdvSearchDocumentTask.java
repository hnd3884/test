package com.me.devicemanagement.onpremise.server.scheduler;

import java.util.List;
import com.me.devicemanagement.framework.server.search.CompleteSearchUtil;
import com.me.devicemanagement.framework.server.search.AdvSearchCommonUtil;
import com.me.devicemanagement.onpremise.server.search.AdvSearchIndexUpdater;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SchedulerAdvSearchDocumentTask implements SchedulerExecutionInterface
{
    private Logger logger;
    public final String methodName = "executeTask";
    
    public SchedulerAdvSearchDocumentTask() {
        this.logger = Logger.getLogger(SchedulerAdvSearchDocumentTask.class.getName());
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "**************** Executing SchedulerAdvSearchDocumentTask ****************");
        try {
            final JSONObject jObj = new JSONObject();
            final boolean searchDocsEnabled = SearchConfiguration.getConfiguration().isSearchDocsEnabled();
            if (searchDocsEnabled) {
                final AdvSearchIndexUpdater docIndexUpdater = AdvSearchIndexUpdater.getInstance();
                final String status = docIndexUpdater.docCRSupdate();
                final List<String> docVersionAvailableList = docIndexUpdater.getSubDirectoriesList(AdvSearchCommonUtil.doc_index_dir);
                jObj.put("docMainIndex", CompleteSearchUtil.getMainIndexDirJson().get("docMainIndex"));
                jObj.put("docVersionAvailableList", (Object)docVersionAvailableList.toArray());
                jObj.put("CRS Status", (Object)status);
                this.logger.log(Level.INFO, props.toString());
                this.logger.log(Level.INFO, jObj.toString());
                Logger.getLogger("AdvSearchLogger").log(Level.INFO, "# CRS update : " + jObj.toString());
            }
            else {
                this.logger.log(Level.INFO, "Document AdvSearch was DISABLED");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while execute Document CSR update", e);
        }
        this.logger.log(Level.INFO, "********************** Finished Execution of SchedulerAdvSearchDocumentTask **************************");
    }
}
