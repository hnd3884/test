package com.me.mdm.server.apps.appupdatepolicy;

import com.me.mdm.server.constants.MDMDeploymentTemplateConstants;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class MDMAppDeploymentTemplateHandler
{
    private static MDMAppDeploymentTemplateHandler mdmAppDeploymentTemplateHandler;
    
    public static MDMAppDeploymentTemplateHandler getInstance() {
        if (MDMAppDeploymentTemplateHandler.mdmAppDeploymentTemplateHandler == null) {
            MDMAppDeploymentTemplateHandler.mdmAppDeploymentTemplateHandler = new MDMAppDeploymentTemplateHandler();
        }
        return MDMAppDeploymentTemplateHandler.mdmAppDeploymentTemplateHandler;
    }
    
    public void addMDMAppDeploymentTemplate(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Row row = new Row("MdmDeploymentTemplate");
        row.set("DEPLOYMENT_TEMPLATE_NAME", (Object)(appUpdatePolicyModel.getPolicyName() + "App update"));
        row.set("DEPLOYMENT_TEMPLATE_DESC", (Object)"App Update Deployment Template");
        row.set("CREATED_BY", (Object)appUpdatePolicyModel.getUserId());
        row.set("CREATION_TIME", (Object)System.currentTimeMillis());
        row.set("CUSTOMER_ID", (Object)appUpdatePolicyModel.getCustomerId());
        row.set("MODIFIED_BY", (Object)appUpdatePolicyModel.getUserId());
        row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
        row.set("DEPLOYMENT_TEMPLATE_TYPE", (Object)MDMDeploymentTemplateConstants.APP_UPDATE_DEPLOYMENT_POLICY);
        dataObject.addRow(row);
        appUpdatePolicyModel.setDeploymentTemplateId(dataObject.getFirstValue("MdmDeploymentTemplate", "DEPLOYMENT_TEMPLATE_ID"));
    }
    
    static {
        MDMAppDeploymentTemplateHandler.mdmAppDeploymentTemplateHandler = null;
    }
}
