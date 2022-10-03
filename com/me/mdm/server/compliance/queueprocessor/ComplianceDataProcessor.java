package com.me.mdm.server.compliance.queueprocessor;

import java.util.List;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Level;
import com.me.mdm.server.compliance.ComplianceProfileAssociationDataHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class ComplianceDataProcessor extends DCQueueDataProcessor
{
    public Logger logger;
    
    public ComplianceDataProcessor() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            final JSONObject distributionJSON = new JSONObject(qData.queueData.toString());
            DMSecurityLogger.info(this.logger, ComplianceDataProcessor.class.getName(), "processData", "input JSON for queue" + distributionJSON.toString(), (Object)null);
            final int distributionType = qData.queueDataType;
            switch (distributionType) {
                case 160: {
                    ComplianceProfileAssociationDataHandler.getInstance().associateProfileForDevice(distributionJSON);
                    break;
                }
                case 161: {
                    ComplianceProfileAssociationDataHandler.getInstance().disassociateProfileForDevice(distributionJSON);
                    break;
                }
                case 164: {
                    ComplianceProfileAssociationDataHandler.getInstance().associateProfileForGroup(distributionJSON);
                    break;
                }
                case 165: {
                    ComplianceProfileAssociationDataHandler.getInstance().disassociateProfileForGroup(distributionJSON);
                    break;
                }
                case 162: {
                    ComplianceProfileAssociationDataHandler.getInstance().associateProfileToMDMResource(distributionJSON);
                    break;
                }
                case 163: {
                    ComplianceProfileAssociationDataHandler.getInstance().disassociateProfileToMDMResource(distributionJSON);
                    break;
                }
                case 166: {
                    this.processDeviceRenameCommandRequest(distributionJSON);
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error -- processData()\t", e);
        }
    }
    
    private void processDeviceRenameCommandRequest(final JSONObject deviceJSON) {
        try {
            final String deviceName = deviceJSON.optString("NAME");
            final Long resourceId = JSONUtil.optLongForUVH(deviceJSON, "RESOURCE_ID", Long.valueOf(-1L));
            if (!MDMStringUtils.isEmpty(deviceName)) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceId);
                MDCustomDetailsRequestHandler.getInstance().checkAndSendDeviceNameUpdateCommand(resourceList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in processDeviceRenameCommandRequest()", e);
        }
    }
}
