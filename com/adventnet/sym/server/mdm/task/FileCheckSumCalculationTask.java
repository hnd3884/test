package com.adventnet.sym.server.mdm.task;

import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.MDMDefaultSecurityUtil;
import com.me.mdm.server.security.MDMBaseSecurityUtil;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import java.util.logging.Logger;

public class FileCheckSumCalculationTask
{
    private Logger logger;
    
    public FileCheckSumCalculationTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask() {
        this.executeTask(null);
    }
    
    public void executeTask(List<Long> customerIDs) {
        try {
            if (customerIDs == null) {
                customerIDs = Arrays.asList(CustomerInfoUtil.getInstance().getCustomerIdsFromDB());
            }
            for (final Long customerID : customerIDs) {
                MDMBaseSecurityUtil.getInstance().calculateCmFileChecksum(customerID);
                MDMBaseSecurityUtil.getInstance().calculateCheckSumForApps(customerID);
                new MDMDefaultSecurityUtil().notifyAgentsForCheckSumSettings(customerID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Issue on checksum task execution", e);
        }
    }
}
