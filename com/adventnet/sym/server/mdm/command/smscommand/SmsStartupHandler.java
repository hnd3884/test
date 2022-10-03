package com.adventnet.sym.server.mdm.command.smscommand;

import java.util.Iterator;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SmsStartupHandler implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties properties) {
        final SmsDbHandler smsDbHandler = new SmsDbHandler();
        try {
            final ArrayList<HashMap> customerList = CustomerInfoUtil.getInstance().getCreatedCustomerInfoList();
            for (final HashMap singleCustomer : customerList) {
                final Object customerId = singleCustomer.get("CUSTOMER_ID");
                final Object customerName = singleCustomer.get("CUSTOMER_NAME");
                SmsStartupHandler.logger.log(Level.INFO, "The customer details are : Customer name = {0} Customer Id = {1}", new Object[] { customerName, customerId });
                final boolean isKeyPresentInDB = smsDbHandler.isSmsCommandKeyInDB((Long)customerId);
                if (isKeyPresentInDB) {
                    SmsStartupHandler.logger.log(Level.INFO, "Public and Private Key already present.");
                }
                else {
                    SmsStartupHandler.logger.log(Level.INFO, "Public key is not found, generating new pair of keys");
                    smsDbHandler.generateAndPublishKeys((Long)customerId);
                }
            }
        }
        catch (final SyMException excep) {
            SmsStartupHandler.logger.log(Level.WARNING, "Customer Info exception {0}", excep.toString());
        }
    }
    
    static {
        SmsStartupHandler.logger = Logger.getLogger(SmsStartupHandler.class.getName());
    }
}
